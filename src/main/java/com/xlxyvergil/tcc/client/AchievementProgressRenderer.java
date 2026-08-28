package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * 客户端专用：在饰品 tooltip 中显示玩家当前的成就进度。
 * 第一行显示 "进度：" 标题，后续每行显示一个条件的进度：
 * stat → "stat名称: current/threshold"、kills → "entity名称: step/value"、
 * biome → "群系: name"。数据从 TccPlayerDataCapability 读取，由服务端同步至客户端。
 */
@OnlyIn(Dist.CLIENT)
public final class AchievementProgressRenderer {

    private AchievementProgressRenderer() {}

    public static void appendProgress(ItemStack stack, List<Component> tooltip) {
        try {
            doAppendProgress(stack, tooltip);
        } catch (Exception ignored) {
            // 防御性：玩家统计数据未就绪、注册表查询异常等情况下不崩溃
        }
    }

    /**
     * 显示当前饰品的下一级进化条件：按 achievement_definitions.json 的 reward.item（或 linkedEvolves.item）
     * 匹配进化来源；未按 Shift 提示「按住Shift显示进化条件」，按住则直接显示其 display.description 文本。
     */
    public static void appendNextEvolutionCondition(ItemStack stack, List<Component> tooltip) {
        try {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (itemId == null) return;
            String currentId = itemId.toString();

            for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.all()) {
                if (!def.isEnabled()) continue;
                AchievementDefinitions.Reward reward = def.reward();
                if (reward == null || !reward.isEvolve()) continue;

                // 当前物品是否为该成就的进化来源（reward.item 或 linkedEvolves.item）
                boolean isSource = currentId.equals(reward.item());
                if (!isSource && reward.linkedEvolves() != null) {
                    for (AchievementDefinitions.LinkedEvolveRef ref : reward.linkedEvolves()) {
                        if (ref.item() != null && currentId.equals(ref.item())) {
                            isSource = true;
                            break;
                        }
                    }
                }
                if (!isSource) continue;

                if (def.display() == null || def.display().description() == null) return;
                tooltip.add(Component.literal(""));

                // 未按 Shift：仅显示提示
                if (!Screen.hasShiftDown()) {
                    tooltip.add(Component.translatable("tcc.tooltip.next_evolution_hint")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                    return;
                }

                // 按住 Shift：直接显示条件文本
                String locale = TaczCuriosClientTooltip.getClientLocale();
                String text = def.display().description().get(locale);
                if (text == null) text = def.display().description().get("en_us");
                if (text == null) return;

                tooltip.add(Component.literal(text)
                        .withStyle(ChatFormatting.GRAY));
                return;
            }
        } catch (Exception ignored) {
            // 防御性：注册表未就绪等情况不崩溃
        }
    }

    private static void doAppendProgress(ItemStack stack, List<Component> tooltip) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null) return;

        AchievementDefinitions.AchievementDef def = TaczCuriosClientTooltip.getAchievementForItem(itemId.toString());
        if (def == null) return;
        if (def.targetCount() <= 0) return;
        if (def.reward() == null) return;

        // 成就达成后不再显示进度
        if (isAchievementCompleted(def)) return;

        var player = Minecraft.getInstance().player;
        if (player == null) return;

        // 第一行：进度标题
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.achievement_progress")
                .withStyle(ChatFormatting.GRAY));

        // 第二行起：各条件进度
        appendConditionProgress(player, def, tooltip);
    }

    private static void appendConditionProgress(net.minecraft.client.player.LocalPlayer player,
                                                AchievementDefinitions.AchievementDef def,
                                                List<Component> tooltip) {
        AchievementDefinitions.AchievementConditions conds = def.conditions();
        int nbtStep = TccPlayerDataCapability.getAchievementProgress(player, def.id());

        // 无显式条件：显示总体进度
        if (conds == null) {
            addConditionLine(tooltip,
                    Component.translatable("tcc.tooltip.achievement_cond_progress"),
                    Component.literal(String.valueOf(nbtStep))
                            .withStyle(ChatFormatting.GREEN));
            return;
        }

        boolean hasDisplayable = false;

        // stat 条件（仅显示当前值，不显示阈值）
        if (conds.stat() != null) {
            hasDisplayable = true;
            int current = resolveStatValue(player, conds.stat());
            addConditionLine(tooltip,
                    Component.literal(resolveStatName(conds.stat())),
                    Component.literal(String.valueOf(current))
                            .withStyle(ChatFormatting.GREEN));
        }

        // extraStats 条件（仅显示当前值）
        if (conds.extraStats() != null) {
            hasDisplayable = true;
            for (AchievementDefinitions.StatCondition sc : conds.extraStats()) {
                int current = resolveStatValue(player, sc.stat());
                addConditionLine(tooltip,
                        Component.literal(resolveStatName(sc.stat())),
                        Component.literal(String.valueOf(current))
                                .withStyle(ChatFormatting.GREEN));
            }
        }

        if (conds.kills() != null && !conds.kills().isEmpty()) {
            hasDisplayable = true;

            if (conds.kills().size() == 1) {
                // 单类型：直接显示进度
                AchievementDefinitions.KillCondition kc = conds.kills().get(0);
                String entityName = "*".equals(kc.entity())
                        ? Component.translatable("tcc.tooltip.achievement_cond_any_entity").getString()
                        : AchievementDefinitions.entityDisplayName(kc.entity());
                addConditionLine(tooltip,
                        Component.literal(entityName),
                        Component.literal(String.valueOf(nbtStep))
                                .withStyle(ChatFormatting.GREEN));
            } else if ("and".equals(conds.mode())) {
                // AND 多类型击杀：逐类型显示子进度
                for (var kc : conds.kills()) {
                    String subKey = def.id() + "|" + kc.entity();
                    int sub = TccPlayerDataCapability.getAchievementProgress(player, subKey);
                    int target = kc.criteriaCount();

                    String entityName = "*".equals(kc.entity())
                            ? Component.translatable("tcc.tooltip.achievement_cond_any_entity").getString()
                            : AchievementDefinitions.entityDisplayName(kc.entity());
                    addConditionLine(tooltip,
                            Component.literal(entityName),
                            Component.literal(sub + "/" + target)
                                    .withStyle(ChatFormatting.GREEN));
                }
            } else {
                // OR 多类型击杀：逐类型显示独立子进度
                for (var kc : conds.kills()) {
                    String subKey = def.id() + "|" + kc.entity();
                    int sub = TccPlayerDataCapability.getAchievementProgress(player, subKey);
                    int target = kc.criteriaCount();

                    String entityName = "*".equals(kc.entity())
                            ? Component.translatable("tcc.tooltip.achievement_cond_any_entity").getString()
                            : AchievementDefinitions.entityDisplayName(kc.entity());
                    addConditionLine(tooltip,
                            Component.literal(entityName),
                            Component.literal(sub + "/" + target)
                                    .withStyle(ChatFormatting.GREEN));
                }
            }
        }

        // biome 条件
        if (conds.biome() != null) {
            hasDisplayable = true;
            ResourceLocation biomeId = ResourceLocation.tryParse(conds.biome());
            if (biomeId != null) {
                String name = I18n.get(Util.makeDescriptionId("biome", biomeId));
                boolean visited = TccPlayerDataCapability.hasVisitedBiome(player, biomeId.toString());
                addConditionLine(tooltip,
                        Component.translatable("tcc.tooltip.achievement_cond_biome"),
                        visited ? Component.literal(name).withStyle(ChatFormatting.GREEN)
                                : Component.translatable("tcc.tooltip.achievement_cond_unknown")
                                        .withStyle(ChatFormatting.RED));
            }
        }

        // attributes 条件（显示当前属性修饰符总值）
        if (conds.attributes() != null && !conds.attributes().isEmpty()) {
            hasDisplayable = true;
            for (AchievementDefinitions.AttributeCondition ac : conds.attributes()) {
                ResourceLocation attrId = ResourceLocation.tryParse(ac.attribute());
                if (attrId == null) continue;
                Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(attrId);
                if (attr == null) continue;
                double current = player.getAttributeValue(attr);
                addConditionLine(tooltip,
                        Component.translatable(attr.getDescriptionId()),
                        Component.literal(String.format("%.0f", current))
                                .withStyle(ChatFormatting.GREEN));
            }
        }

        // 维度条件：不在进度中单独展示一行（维度信息已由 display.description 文本说明承载），
        // 服务端在统计击杀次数前仍会按维度过滤。

        // 无可显示的进度条件（仅有 equippedCurios/attributes 等二元判定）：显示总体进度
        if (!hasDisplayable) {
            addConditionLine(tooltip,
                    Component.translatable("tcc.tooltip.achievement_cond_progress"),
                    Component.literal(String.valueOf(nbtStep))
                            .withStyle(ChatFormatting.GREEN));
        }
    }

    /** 添加一行进度：缩进 + 灰色标签 + 着色值。 */
    private static void addConditionLine(List<Component> tooltip, Component label, Component value) {
        tooltip.add(Component.literal("  ")
                .append(label)
                .append(Component.literal(": "))
                .append(value)
                .withStyle(ChatFormatting.GRAY));
    }

    /** 通过客户端 advancement 系统判断成就是否已完成（ClientAdvancements 持有服务端同步的树与进度）。 */
    private static boolean isAchievementCompleted(AchievementDefinitions.AchievementDef def) {
        var mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.getConnection() == null) return false;

        try {
            ResourceLocation id = ResourceLocation.tryParse(def.id());
            if (id == null) return false;

            ClientAdvancements manager = mc.getConnection().getAdvancements();
            // ClientAdvancements.getAdvancements() 返回 AdvancementList
            Advancement adv = manager.getAdvancements().get(id);
            if (adv == null) return false;

            // ClientAdvancements.progress 字段通过 Access Transformer 暴露，
            // 持有从服务端同步的 AdvancementProgress
            AdvancementProgress progress = manager.progress.get(adv);
            return progress != null && progress.isDone();
        } catch (Exception ignored) {
            // 防御性：advancement 未同步、注册表未就绪等情况
            return false;
        }
    }

    /**
     * 解析 stat 的本地化名称（stat.minecraft.damage_dealt → "伤害造成"）。
     * 与 Minecraft Stat.getDisplayName() 一致，把 ResourceLocation 中的 ':' 替换为 '.' 构造翻译键。
     */
    private static String resolveStatName(String statId) {
        String key = "stat." + statId.replace(':', '.');
        String localized = I18n.get(key);
        return localized.equals(key) ? statId : localized;
    }

    /** 从客户端 Statistics 读取 stat 当前值，与服务端 StatPollingEventHandler.checkStat 一致：先经 CUSTOM_STAT 验证。 */
    private static int resolveStatValue(net.minecraft.client.player.LocalPlayer player, String statId) {
        ResourceLocation statRl = ResourceLocation.tryParse(statId);
        if (statRl == null) return 0;

        ResourceLocation registered = BuiltInRegistries.CUSTOM_STAT.get(statRl);
        if (registered == null) {
            registered = BuiltInRegistries.CUSTOM_STAT.get(new ResourceLocation(statRl.getPath()));
        }
        if (registered == null) return 0;

        try {
            return player.getStats().getValue(Stats.CUSTOM.get(registered));
        } catch (Exception e) {
            return 0;
        }
    }

}

