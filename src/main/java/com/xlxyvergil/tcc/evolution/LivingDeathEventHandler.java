package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LivingDeathEventHandler {
    public static final String TRIGGER_LIVING_DEATH = "living_death";

    private LivingDeathEventHandler() {}

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed.level().isClientSide) return;

        DamageSource source = event.getSource();
        Entity sourceEntity = source.getEntity();

        if (sourceEntity instanceof Player killerPlayer) {
            String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
            handleTrigger(killerPlayer, killed, killedKey, sourceEntity, source,
                    TRIGGER_LIVING_DEATH, false, false);
        }

        if (killed instanceof Player victimPlayer) {
            String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
            handleTrigger(victimPlayer, killed, killedKey, sourceEntity, source,
                    TRIGGER_LIVING_DEATH, true, false);
        }
    }

    public static void triggerLivingDeath(Player player, LivingEntity killed, Entity otherEntity,
                                           DamageSource source, boolean playerKilled, boolean ignoreEnabled) {
        if (player == null || killed == null) return;
        String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
        handleTrigger(player, killed, killedKey, otherEntity, source,
                TRIGGER_LIVING_DEATH, playerKilled, ignoreEnabled);
    }

    // ===== API for external callers (scripting) =====

    public static boolean tryGrantRule(Player player, Entity otherEntity, DamageSource source,
                                        String achievementId) {
        var def = AchievementDefinitions.get(achievementId).orElse(null);
        if (def == null || def.reward() == null || !def.reward().isGrant()) return false;
        if (!def.isPlayerKilled()) return false;
        ServerPlayer sp = player instanceof ServerPlayer s ? s : null;
        if (sp == null) return false;
        if (RuleAdvancementMapping.isAdvancementDone(sp, def.id())) return false;
        if (!RuleAdvancementMapping.arePrerequisitesMet(sp, def)) return false;
        if (!AchievementConditionMatcher.matchesDeathConditions(player, null, otherEntity, def)) return false;
        RuleAdvancementMapping.awardAll(sp, def.id(), def.criteriaCount());
        return true;
    }

    public static boolean applyAttributeRule(Player player, LivingEntity killed, DamageSource source,
                                              String ruleId, boolean ignoreEnabled) {
        EvolutionRegistry.Rule rule = EvolutionRegistry.getRule(ruleId).orElse(null);
        if (rule == null || rule.type != EvolutionRegistry.RuleType.ATTRIBUTE) return false;
        if (!ignoreEnabled && !rule.enabled) return false;
        if (rule.playerKilled || rule.item == null || rule.item.isBlank() || rule.progress == null) return false;
        if (!matchesDamageSource(rule, source)) return false;
        if (killed == null) return false;

        String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
        ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
        if (tracked.isEmpty()) return false;

        boolean changed = false;
        for (EvolutionRegistry.KillGain k : rule.kills) {
            if (killedKey.equals(k.entity.key)
                    && EntityConditionHelper.matchesNbtFilters(killed, k.entity.nbt)) {
                changed |= incrementProgress(tracked, rule.progress.nbtKey,
                        rule.progress.capCounterKey, rule.progress.cap, k.value);
            }
        }
        if (changed && tracked.getItem() instanceof BaseCurioItem curio) {
            curio.refreshEffects(player);
        }
        return changed;
    }

    // ===== Core handler =====

    private static void handleTrigger(Player player, LivingEntity killed, String killedKey,
                                       Entity otherEntity, DamageSource source, String trigger,
                                       boolean playerKilled, boolean ignoreEnabled) {
        ServerPlayer serverPlayer = player instanceof ServerPlayer sp ? sp : null;

        // ===== Achievement-driven GRANT (death trigger) =====
        if (serverPlayer != null) {
            for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(trigger)) {
                if (def.reward() == null) continue;
                if (def.isPlayerKilled() != playerKilled) continue;

                // Skip disabled achievements
                if (!def.isEnabled()) continue;

                if (RuleAdvancementMapping.isAdvancementDone(serverPlayer, def.id())) continue;
                if (!RuleAdvancementMapping.arePrerequisitesMet(serverPlayer, def)) continue;
                if (!AchievementConditionMatcher.matchesDeathConditions(player, killed, otherEntity, def)) continue;

                RuleAdvancementMapping.awardNextCriterion(
                        serverPlayer, def.id(), def.criteriaCount());
            }
        }

        // ===== ATTRIBUTE rules (from evolution_rules.json) =====
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTriggerOrEmpty(trigger)) {
            if (!ignoreEnabled && !rule.enabled) continue;
            if (rule.playerKilled != playerKilled) continue;
            if (rule.type != EvolutionRegistry.RuleType.ATTRIBUTE) continue;
            if (!matchesDamageSource(rule, source)) continue;

            if (rule.item == null || rule.item.isBlank() || rule.progress == null) continue;

            ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
            if (tracked.isEmpty()) continue;

            boolean changed = false;
            for (EvolutionRegistry.KillGain k : rule.kills) {
                if (killedKey.equals(k.entity.key)
                        && EntityConditionHelper.matchesNbtFilters(killed, k.entity.nbt)) {
                    if (!passesExtraRequirements(player, killed, rule.requirements)) continue;
                    changed |= incrementProgress(tracked, rule.progress.nbtKey,
                            rule.progress.capCounterKey, rule.progress.cap, k.value);
                }
            }
            if (changed && tracked.getItem() instanceof BaseCurioItem curio) {
                curio.refreshEffects(player);
            }
        }
    }

    // ===== Utility methods (public/shared) =====

    static void bindToPlayer(ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("BoundPlayer", player.getStringUUID());
        tag.putString("BoundPlayerName", player.getName().getString());
        tag.putBoolean("IsBound", true);
    }

    static void resetCapCountersForItem(String itemId, ItemStack stack) {
        if (itemId == null || itemId.isBlank()) return;
        CompoundTag tag = stack.getOrCreateTag();
        for (EvolutionRegistry.Rule rule :
                EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.ATTRIBUTE, itemId)) {
            EvolutionRegistry.Progress progress = rule.progress;
            if (progress == null || progress.capCounterKey == null || progress.capCounterKey.isBlank()) continue;
            tag.putDouble(progress.capCounterKey, 0.0);
        }
    }

    static boolean hasEquipped(Player player, String itemId) {
        if (itemId == null || itemId.isBlank()) return false;
        return !findFirstEquippedStack(player, stack -> itemId.equals(itemId(stack))).isEmpty();
    }

    static boolean passesExtraRequirements(Player player, LivingEntity killed, EvolutionRegistry.Requirements req) {
        if (!req.requiredEffects.isEmpty()) {
            for (String effectId : req.requiredEffects) {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectId));
                if (effect == null || !player.hasEffect(effect)) return false;
            }
        }
        if (!req.holdingGunTypes.isEmpty()) {
            if (!GunTypeChecker.isHoldingConfiguredGunTypes(player, req.holdingGunTypes)) return false;
        }
        if (req.minDistance != null) {
            if (killed == null) return false;
            if (player.distanceToSqr(killed) < req.minDistance * req.minDistance) return false;
        }
        return true;
    }

    static boolean matchesDamageSource(EvolutionRegistry.Rule rule, DamageSource source) {
        if (source == null) return rule.damageSourceTags.isEmpty();
        if (rule.damageSourceTags.isEmpty()) return true;
        for (String tagId : rule.damageSourceTags) {
            TagKey<DamageType> tag = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(tagId));
            if (!source.is(tag)) return false;
        }
        return true;
    }

    // ===== Private helpers =====

    private static ItemStack findFirstEquippedStack(LivingEntity livingEntity, Predicate<ItemStack> predicate) {
        if (livingEntity == null) return ItemStack.EMPTY;
        ICuriosItemHandler inv = CuriosApi.getCuriosInventory(livingEntity).orElse(null);
        if (inv == null) return ItemStack.EMPTY;
        for (var entry : inv.getCurios().entrySet()) {
            ICurioStacksHandler stacksHandler = entry.getValue();
            if (stacksHandler == null) continue;
            var handler = stacksHandler.getStacks();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && predicate.test(stack)) return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static boolean incrementProgress(ItemStack stack, String progressKey,
                                              String capCounterKey, double cap, double value) {
        if (value == 0.0) return false;
        CompoundTag tag = stack.getOrCreateTag();
        if (progressKey == null || progressKey.isBlank() || capCounterKey == null || capCounterKey.isBlank())
            return false;
        double counter = tag.getDouble(capCounterKey);
        if (counter >= cap) return false;
        double delta = Math.min(value, cap - counter);
        if (delta == 0.0) return false;
        tag.putDouble(capCounterKey, counter + delta);
        tag.putDouble(progressKey, tag.getDouble(progressKey) + delta);
        return true;
    }

    private static String itemId(ItemStack stack) {
        if (stack.isEmpty()) return "";
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key != null ? key.toString() : "";
    }
}
