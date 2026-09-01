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


@OnlyIn(Dist.CLIENT)
public final class AchievementProgressRenderer {

    private AchievementProgressRenderer() {}

    public static void appendProgress(ItemStack stack, List<Component> tooltip) {
        try {
            doAppendProgress(stack, tooltip);
        } catch (Exception ignored) {
            
        }
    }

    
    public static void appendNextEvolutionCondition(ItemStack stack, List<Component> tooltip) {
        try {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (itemId == null) return;
            String currentId = itemId.toString();

            for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.all()) {
                if (!def.isEnabled()) continue;
                AchievementDefinitions.Reward reward = def.reward();
                if (reward == null || !reward.isEvolve()) continue;

                
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

                
                if (!Screen.hasShiftDown()) {
                    tooltip.add(Component.translatable("tcc.tooltip.next_evolution_hint")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                    return;
                }

                
                String locale = TaczCuriosClientTooltip.getClientLocale();
                String text = def.display().description().get(locale);
                if (text == null) text = def.display().description().get("en_us");
                if (text == null) return;

                tooltip.add(Component.literal(text)
                        .withStyle(ChatFormatting.GRAY));
                return;
            }
        } catch (Exception ignored) {
            
        }
    }

    private static void doAppendProgress(ItemStack stack, List<Component> tooltip) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null) return;

        AchievementDefinitions.AchievementDef def = TaczCuriosClientTooltip.getAchievementForItem(itemId.toString());
        if (def == null) return;
        if (def.targetCount() <= 0) return;
        if (def.reward() == null) return;

        
        if (isAchievementCompleted(def)) return;

        var player = Minecraft.getInstance().player;
        if (player == null) return;

        
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.achievement_progress")
                .withStyle(ChatFormatting.GRAY));

        
        appendConditionProgress(player, def, tooltip);
    }

    private static void appendConditionProgress(net.minecraft.client.player.LocalPlayer player,
                                                AchievementDefinitions.AchievementDef def,
                                                List<Component> tooltip) {
        AchievementDefinitions.AchievementConditions conds = def.conditions();
        int nbtStep = TccPlayerDataCapability.getAchievementProgress(player, def.id());

        
        if (conds == null) {
            addConditionLine(tooltip,
                    Component.translatable("tcc.tooltip.achievement_cond_progress"),
                    Component.literal(String.valueOf(nbtStep))
                            .withStyle(ChatFormatting.GREEN));
            return;
        }

        boolean hasDisplayable = false;

        
        if (conds.stat() != null) {
            hasDisplayable = true;
            int current = resolveStatValue(player, conds.stat());
            addConditionLine(tooltip,
                    Component.literal(resolveStatName(conds.stat())),
                    Component.literal(String.valueOf(current))
                            .withStyle(ChatFormatting.GREEN));
        }

        
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
                
                AchievementDefinitions.KillCondition kc = conds.kills().get(0);
                String entityName = "*".equals(kc.entity())
                        ? Component.translatable("tcc.tooltip.achievement_cond_any_entity").getString()
                        : AchievementDefinitions.entityDisplayName(kc.entity());
                addConditionLine(tooltip,
                        Component.literal(entityName),
                        Component.literal(String.valueOf(nbtStep))
                                .withStyle(ChatFormatting.GREEN));
            } else if ("and".equals(conds.mode())) {
                
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

        
        

        
        if (!hasDisplayable) {
            addConditionLine(tooltip,
                    Component.translatable("tcc.tooltip.achievement_cond_progress"),
                    Component.literal(String.valueOf(nbtStep))
                            .withStyle(ChatFormatting.GREEN));
        }
    }

    
    private static void addConditionLine(List<Component> tooltip, Component label, Component value) {
        tooltip.add(Component.literal("  ")
                .append(label)
                .append(Component.literal(": "))
                .append(value)
                .withStyle(ChatFormatting.GRAY));
    }

    
    private static boolean isAchievementCompleted(AchievementDefinitions.AchievementDef def) {
        var mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.getConnection() == null) return false;

        try {
            ResourceLocation id = ResourceLocation.tryParse(def.id());
            if (id == null) return false;

            ClientAdvancements manager = mc.getConnection().getAdvancements();
            
            Advancement adv = manager.getAdvancements().get(id);
            if (adv == null) return false;

            
            
            AdvancementProgress progress = manager.progress.get(adv);
            return progress != null && progress.isDone();
        } catch (Exception ignored) {
            
            return false;
        }
    }

    
    private static String resolveStatName(String statId) {
        String key = "stat." + statId.replace(':', '.');
        String localized = I18n.get(key);
        return localized.equals(key) ? statId : localized;
    }

    
    private static int resolveStatValue(net.minecraft.client.player.LocalPlayer player, String statId) {
        ResourceLocation statRl = ResourceLocation.tryParse(statId);
        if (statRl == null) return 0;

        if ("tcc".equals(statRl.getNamespace())) {
            return TccPlayerDataCapability.getCustomStat(player, statRl.toString());
        }

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

