package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.items.ItemBaseCurio;
import com.xlxyvergil.tcc.items.curios.HeavenFireApocalypse;
import com.xlxyvergil.tcc.items.curios.HeavenFireApocalypseEndless;
import com.xlxyvergil.tcc.items.curios.HeavenFireJudgment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户端 Tooltip 工具类
 * 仅处理需要访问 Minecraft.getInstance().player 的动态计算逻辑，
 * 避免在物品类（服务端也会加载）中出现客户端类的字节码引用。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TaczCuriosClientTooltip {

    /** 奖励物品 ID → 成就定义的懒加载反向映射 */
    private static Map<String, AchievementDefinitions.AchievementDef> rewardToAchievement;

    /**
     * 根据玩家当前虚数抗性，计算最终伤害保留率
     * @return double[] {totalRetentionPct, resistanceBonusPct}
     */
    public static double[] getImaginaryResistanceRetention(double baseRetentionPct, double bonusPerPoint) {
        double resistanceBonusPct = 0;
        double totalRetentionPct = baseRetentionPct;
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            double resistance = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            resistanceBonusPct = resistance * bonusPerPoint;
            totalRetentionPct = Math.max(0, baseRetentionPct + resistanceBonusPct);
        }
        return new double[]{totalRetentionPct, resistanceBonusPct};
    }

    /**
     * 监听 ItemTooltipEvent，为所有饰品追加动态信息
     */
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        // 天火系列动态伤害信息
        if (stack.getItem() instanceof HeavenFireApocalypse) {
            appendApocalypseDynamicInfo(tooltip);
        } else if (stack.getItem() instanceof HeavenFireJudgment) {
            appendJudgmentDynamicInfo(tooltip);
        } else if (stack.getItem() instanceof HeavenFireApocalypseEndless) {
            appendEndlessDynamicInfo(tooltip);
        }

        // 奖励物品的成就达成方式
        appendAchievementCondition(tooltip, stack);

        // 逐火之蛾饰品的虚数抗性成长条件
        appendEvolutionCondition(tooltip, stack);

        // 奖励物品的成就进度（玩家 NBT 中的累计值）
        AchievementProgressRenderer.appendProgress(stack, tooltip);

        // 绑定饰品（需要崩坏结晶才能卸下）
        if (stack.getItem() instanceof ItemBaseCurio curio && curio.requiresCollapseCrystal()) {
            tooltip.add(Component.translatable("tcc.tooltip.requires_collapse_crystal")
                    .withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
        }
    }

    private static void appendApocalypseDynamicInfo(List<Component> tooltip) {
        double baseRetentionPct = TaczCuriosConfig.COMMON.heavenFireApocalypseDamageConversionRatio.get() * 100;
        double bonusPerPoint = TaczCuriosConfig.COMMON.imaginaryDamageResistanceBonusPerPoint.get() * 100;
        double[] retention = getImaginaryResistanceRetention(baseRetentionPct, bonusPerPoint);
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse.damage_conversion",
                String.format("%.0f", retention[0]),
                String.format("%.0f", baseRetentionPct),
                String.format("%.0f", retention[1])));
    }

    private static void appendJudgmentDynamicInfo(List<Component> tooltip) {
        double baseRetentionPct = TaczCuriosConfig.COMMON.heavenFireJudgmentDamageConversionRatio.get() * 100;
        double bonusPerPoint = TaczCuriosConfig.COMMON.imaginaryDamageResistanceBonusPerPoint.get() * 100;
        double[] retention = getImaginaryResistanceRetention(baseRetentionPct, bonusPerPoint);
        tooltip.add(Component.translatable("item.tcc.heaven_fire_judgment.damage_conversion",
                String.format("%.0f", retention[0]),
                String.format("%.0f", baseRetentionPct),
                String.format("%.0f", retention[1])));
    }

    private static void appendEndlessDynamicInfo(List<Component> tooltip) {
        double baseRetentionPct = TaczCuriosConfig.COMMON.endlessDamageConversionRatio.get() * 100;
        double bonusPerPoint = TaczCuriosConfig.COMMON.imaginaryDamageResistanceBonusPerPoint.get() * 100;
        double[] retention = getImaginaryResistanceRetention(baseRetentionPct, bonusPerPoint);
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse_endless.damage_conversion",
                String.format("%.0f", retention[0]),
                String.format("%.0f", baseRetentionPct),
                String.format("%.0f", retention[1])));
    }

    // ==================== 成就达成方式 tooltip ====================

    /** 构建奖励/佩戴物品 → 成就定义的映射 */
    private static Map<String, AchievementDefinitions.AchievementDef> getRewardMap() {
        if (rewardToAchievement == null) {
            rewardToAchievement = new HashMap<>();
            for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.all()) {
                if (!def.isEnabled()) continue;
                AchievementDefinitions.Reward reward = def.reward();
                if (reward == null) continue;

                // grant → 用 reward.item 匹配
                if (reward.isGrant() && reward.item() != null) {
                    rewardToAchievement.put(reward.item(), def);
                }
                // evolve → 用 reward.to 匹配
                if (reward.isEvolve() && reward.to() != null) {
                    rewardToAchievement.put(reward.to(), def);
                }
                // linkedEvolves 的 to 也映射到该成就
                if (reward.linkedEvolves() != null) {
                    for (AchievementDefinitions.LinkedEvolveRef ref : reward.linkedEvolves()) {
                        if (ref.to() != null) {
                            rewardToAchievement.putIfAbsent(ref.to(), def);
                        }
                    }
                }
                // equippedCurios → 佩戴物品也映射到该成就（用于在任务道具上显示进度）
                if (def.conditions() != null && def.conditions().equippedCurios() != null) {
                    for (String curio : def.conditions().equippedCurios()) {
                        rewardToAchievement.putIfAbsent(curio, def);
                    }
                }
            }
        }
        return rewardToAchievement;
    }

    /** 公开访问：根据物品 ID 获取关联的成就定义（用于进度读取等场景） */
    public static AchievementDefinitions.AchievementDef getAchievementForItem(String itemId) {
        return getRewardMap().get(itemId);
    }

    /** 如果物品是某成就的奖励，追加达成条件描述 */
    private static void appendAchievementCondition(List<Component> tooltip, ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        AchievementDefinitions.AchievementDef def = getRewardMap().get(itemId.toString());
        if (def == null) return;

        // 获取当前 locale
        String locale = getClientLocale();

        // 从 achievement_definitions.json 读取 display.description
        if (def.display() == null || def.display().description() == null) return;
        String text = def.display().description().get(locale);
        if (text == null) text = def.display().description().get("en_us");
        if (text == null) return;

        // 追加到 tooltip
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.how_to_obtain", def.title(locale), text)
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    /** 如果物品有 ATTRIBUTE 类型的进化规则且包含 description，追加成长条件 */
    private static void appendEvolutionCondition(List<Component> tooltip, ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        List<EvolutionRegistry.Rule> rules = EvolutionRegistry.getRulesByTypeAndItemOrEmpty(
                EvolutionRegistry.RuleType.ATTRIBUTE, itemId.toString());
        if (rules.isEmpty()) return;

        String locale = getClientLocale();

        for (EvolutionRegistry.Rule rule : rules) {
            if (!rule.enabled) continue;
            if (rule.description.isEmpty()) continue;

            String text = rule.description.get(locale);
            if (text == null) text = rule.description.get("en_us");
            if (text == null) continue;

            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.growth_condition", text)
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }

    private static String getClientLocale() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.getLanguageManager() != null) {
                return mc.getLanguageManager().getSelected();
            }
        } catch (Exception ignored) {}
        return "en_us";
    }
}
