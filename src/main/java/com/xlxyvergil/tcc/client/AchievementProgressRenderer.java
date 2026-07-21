package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * 客户端专用：在饰品 tooltip 中显示玩家当前的成就进度。
 * <p>
 * 进度数据从玩家 NBT 中读取（由网络数据包同步自服务端），
 * 实时更新。
 */
@OnlyIn(Dist.CLIENT)
public final class AchievementProgressRenderer {

    private static final String PROGRESS_PREFIX = "tcc_ach_progress_";
    private static final String TRIGGER_BIOME = "biome_visit";

    private AchievementProgressRenderer() {}

    /**
     * 检查物品是否与某个成就关联，若有则从玩家 NBT 读取进度并追加到 tooltip。
     */
    public static void appendProgress(ItemStack stack, List<Component> tooltip) {
        try {
            doAppendProgress(stack, tooltip);
        } catch (Exception ignored) {
            // 防御性：玩家统计数据未就绪、注册表查询异常等情况下不崩溃
        }
    }

    private static void doAppendProgress(ItemStack stack, List<Component> tooltip) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null) return;

        AchievementDefinitions.AchievementDef def = TaczCuriosClientTooltip.getAchievementForItem(itemId.toString());
        if (def == null) return;
        if (def.criteriaCount() <= 0) return;
        if (def.reward() == null) return;

        var player = Minecraft.getInstance().player;
        if (player == null) return;
        CompoundTag data = player.getPersistentData();

        if (TRIGGER_BIOME.equals(def.trigger())) {
            appendBiomeVisit(def, tooltip);
            return;
        }

        int current;
        if ("stat_polling".equals(def.trigger())) {
            // stat_polling 直接从客户端 Stats 读取（Minecraft 自动同步）
            current = resolveStatProgress(player, def);
        } else {
            // 击杀类从网络同步的玩家 NBT 读取
            String nbtKey = PROGRESS_PREFIX + def.id().replace(':', '_');
            current = data.getInt(nbtKey);
        }

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.achievement_progress")
                .append(Component.literal(String.valueOf(current)).withStyle(ChatFormatting.GREEN))
                .withStyle(ChatFormatting.GRAY));
    }

    /**
     * 对 biome_visit 类型：从玩家 NBT 读取访问记录并显示。
     * 使用 NaturesCompass 的方式获取翻译名称（Util.makeDescriptionId + I18n）。
     */
    private static void appendBiomeVisit(AchievementDefinitions.AchievementDef def, List<Component> tooltip) {
        AchievementDefinitions.AchievementConditions conds = def.conditions();
        if (conds == null) return;

        String type;
        ResourceLocation id;
        if (conds.dimension() != null) {
            id = ResourceLocation.tryParse(conds.dimension());
            if (id == null) return;
            type = "dimension";
        } else if (conds.biome() != null) {
            id = ResourceLocation.tryParse(conds.biome());
            if (id == null) return;
            type = "biome";
        } else {
            return;
        }

        // 维度翻译键格式："dimension.路径"（不带命名空间），与 Minecraft 实际格式一致
        // 群系翻译键格式："biome.命名空间.路径"，用 Util.makeDescriptionId 生成
        String key = "dimension".equals(type) ? "dimension." + id.getPath() : Util.makeDescriptionId(type, id);
        String localized = I18n.get(key);
        Component nameComponent;
        if ("biome".equals(type)) {
            // 群系：不做回退，直接显示（与 NaturesCompass 一致）
            nameComponent = Component.literal(localized).withStyle(ChatFormatting.GREEN);
        } else {
            // 维度：翻译找不到时做回退（与 NaturesCompass 一致）
            if (!localized.equals(key)) {
                nameComponent = Component.literal(localized).withStyle(ChatFormatting.GREEN);
            } else {
                // 回退：去掉命名空间，下划线转空格，单词首字母大写
                String fallback = id.getPath().replace('_', ' ');
                fallback = org.apache.commons.lang3.text.WordUtils.capitalize(fallback);
                nameComponent = Component.literal(fallback).withStyle(ChatFormatting.GREEN);
            }
        }

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.achievement_biome")
                .append(nameComponent)
                .withStyle(ChatFormatting.GRAY));
    }

    /**
     * 对 stat_polling 类型：从客户端 Statistics 读取原始数据计算进度。
     * 服务端在每次 stat 变动后主动同步给客户端，客户端直接读取本地缓存。
     */
    private static int resolveStatProgress(net.minecraft.client.player.LocalPlayer player,
                                            AchievementDefinitions.AchievementDef def) {
        AchievementDefinitions.AchievementConditions conds = def.conditions();
        if (conds == null || conds.stat() == null) return 0;

        ResourceLocation statId = ResourceLocation.tryParse(conds.stat());
        if (statId == null) return 0;

        // 先尝试通过 BuiltInRegistries.CUSTOM_STAT 验证（与服务端一致），
        // 某些 stat 可能未在客户端注册，此时回退直接读取。
        ResourceLocation registered = BuiltInRegistries.CUSTOM_STAT.get(statId);
        if (registered == null) {
            registered = BuiltInRegistries.CUSTOM_STAT.get(new ResourceLocation(statId.getPath()));
        }

        // 与服务端一致（StatPollingEventHandler.checkStat）：未注册的 stat 直接跳过
        if (registered == null) return 0;

        int current;
        try {
            current = player.getStats().getValue(Stats.CUSTOM.get(registered));
        } catch (Exception e) {
            // 防御性：Stat 构造函数/注册表尚未就绪时可能抛异常
            return 0;
        }
        // 返回原始 stat 值用于进度显示（与击杀类成就保持一致），
        // 服务端按 threshold 分步判定，但客户端直接显示原始值让玩家了解实际进度
        return current;
    }
}
