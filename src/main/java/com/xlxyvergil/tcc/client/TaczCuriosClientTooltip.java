package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TaczCuriosClientTooltip {

    
    private static Map<String, AchievementDefinitions.AchievementDef> rewardToAchievement;

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        
        appendAchievementCondition(tooltip, stack);

        
        AchievementProgressRenderer.appendProgress(stack, tooltip);

        
        AchievementProgressRenderer.appendNextEvolutionCondition(stack, tooltip);

        
        appendEvolutionCondition(tooltip, stack);

        

        
        if (stack.getItem() instanceof BoundCurioItem curio && curio.requiresCollapseCrystal()) {
            tooltip.add(Component.translatable("tcc.tooltip.requires_collapse_crystal")
                    .withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
        }
    }

    

    
    private static Map<String, AchievementDefinitions.AchievementDef> getRewardMap() {
        if (rewardToAchievement == null) {
            rewardToAchievement = new HashMap<>();
            for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.all()) {
                if (!def.isEnabled()) continue;
                AchievementDefinitions.Reward reward = def.reward();
                if (reward == null) continue;

                
                if (reward.isGrant() && reward.item() != null) {
                    rewardToAchievement.put(reward.item(), def);
                }
                
                if (reward.isEvolve() && reward.to() != null) {
                    rewardToAchievement.put(reward.to(), def);
                }
                
                if (reward.linkedEvolves() != null) {
                    for (AchievementDefinitions.LinkedEvolveRef ref : reward.linkedEvolves()) {
                        if (ref.to() != null) {
                            rewardToAchievement.putIfAbsent(ref.to(), def);
                        }
                    }
                }
                
                if (def.conditions() != null && def.conditions().equippedCurios() != null) {
                    for (String curio : def.conditions().equippedCurios()) {
                        rewardToAchievement.putIfAbsent(curio, def);
                    }
                }
            }
        }
        return rewardToAchievement;
    }

    
    public static AchievementDefinitions.AchievementDef getAchievementForItem(String itemId) {
        return getRewardMap().get(itemId);
    }

    private static void appendAchievementCondition(List<Component> tooltip, ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        AchievementDefinitions.AchievementDef def = getRewardMap().get(itemId.toString());
        if (def == null) return;

        String locale = getClientLocale();

        
        if (def.display() == null || def.display().description() == null) return;
        String text = def.display().description().get(locale);
        if (text == null) text = def.display().description().get("en_us");
        if (text == null) return;

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.how_to_obtain", def.title(locale), text)
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    
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

    public static String getClientLocale() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.getLanguageManager() != null) {
                return mc.getLanguageManager().getSelected();
            }
        } catch (Exception ignored) {}
        return "en_us";
    }

    
    public static LivingEntity resolveWearer(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) {
            return null;
        }
        
        LivingEntity maidWearer = MaidCompat.findWearingMaid(mc.level, s -> ItemStack.isSameItem(s, stack));
        if (maidWearer != null) {
            return maidWearer;
        }
        Player player = mc.player;
        
        if (player != null
                && !CurioSearchHelper.findFirstEquippedStack(player, s -> ItemStack.isSameItem(s, stack)).isEmpty()) {
            return player;
        }
        
        return player;
    }
}
