package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioGrantHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.EvolutionNbtKeys;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Collections;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LivingDeathEventHandler {
    public static final String TRIGGER_LIVING_DEATH = "living_death";

    private LivingDeathEventHandler() {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed.level().isClientSide) return;

        DamageSource source = event.getSource();
        Entity sourceEntity = source.getEntity();

        if (sourceEntity instanceof Player killerPlayer) {
            String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
            handleTrigger(killerPlayer, killed, killedKey, sourceEntity, source, TRIGGER_LIVING_DEATH, false, false);
        }

        if (killed instanceof Player victimPlayer) {
            String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
            handleTrigger(victimPlayer, killed, killedKey, sourceEntity, source, TRIGGER_LIVING_DEATH, true, false);
        }
    }

    public static void triggerLivingDeath(Player player, LivingEntity killed, Entity otherEntity, DamageSource source, boolean playerKilled, boolean ignoreEnabled) {
        if (player == null || killed == null) {
            return;
        }
        String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
        handleTrigger(player, killed, killedKey, otherEntity, source, TRIGGER_LIVING_DEATH, playerKilled, ignoreEnabled);
    }

    public static boolean tryEvolveRule(Player player, String ruleId, boolean ignoreEnabled) {
        EvolutionRegistry.Rule rule = EvolutionRegistry.getRule(ruleId).orElse(null);
        if (rule == null || rule.type != EvolutionRegistry.RuleType.EVOLVE) {
            return false;
        }
        if (!ignoreEnabled && !rule.enabled) {
            return false;
        }
        boolean evolved = tryEvolve(player, null, rule);
        if (evolved) {
            for (EvolutionRegistry.LinkedEvolve linked : rule.requirementsRef) {
                tryLinkedEvolve(player, linked);
            }
        }
        return evolved;
    }

    public static boolean applyAttributeRule(Player player, LivingEntity killed, DamageSource source, String ruleId, boolean ignoreEnabled) {
        EvolutionRegistry.Rule rule = EvolutionRegistry.getRule(ruleId).orElse(null);
        if (rule == null || rule.type != EvolutionRegistry.RuleType.ATTRIBUTE) {
            return false;
        }
        if (!ignoreEnabled && !rule.enabled) {
            return false;
        }
        if (rule.playerKilled) {
            return false;
        }
        if (rule.item == null || rule.item.isBlank()) {
            return false;
        }
        if (rule.progress == null) {
            return false;
        }
        if (!matchesDamageSource(rule, source)) {
            return false;
        }
        if (killed == null) {
            return false;
        }

        String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
        ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
        if (tracked.isEmpty()) {
            return false;
        }

        boolean changed = false;
        for (EvolutionRegistry.KillGain k : rule.kills) {
            if (killedKey.equals(k.entity.key) && EntityConditionHelper.matchesNbtFilters(killed, k.entity.nbt)) {
                changed |= incrementProgress(tracked, rule.progress.nbtKey, rule.progress.capCounterKey, rule.progress.cap, k.value);
            }
        }

        if (changed && tracked.getItem() instanceof BaseCurioItem curio) {
            curio.refreshEffects(player);
        }
        return changed;
    }

    public static boolean tryGrantRule(Player player, Entity otherEntity, DamageSource source, String ruleId, boolean ignoreEnabled) {
        EvolutionRegistry.Rule rule = EvolutionRegistry.getRule(ruleId).orElse(null);
        if (rule == null || rule.type != EvolutionRegistry.RuleType.GRANT) {
            return false;
        }
        if (!ignoreEnabled && !rule.enabled) {
            return false;
        }
        if (!LivingDeathGrantRuleMatcher.matches(player, player, otherEntity, source, rule, ignoreEnabled)) {
            return false;
        }
        executeGrant(player, rule);
        return true;
    }

    private static void handleTrigger(Player player, LivingEntity killed, String killedKey, Entity otherEntity, DamageSource source, String trigger, boolean playerKilled, boolean ignoreEnabled) {
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTriggerOrEmpty(trigger)) {
            if (!ignoreEnabled && !rule.enabled) continue;
            if (rule.playerKilled != playerKilled) continue;
            if (!matchesDamageSource(rule, source)) continue;

            if (rule.type == EvolutionRegistry.RuleType.EVOLVE) {
                if (playerKilled) continue;
                if (rule.item == null || rule.item.isBlank()) continue;
                if (rule.to == null || rule.to.isBlank()) continue;

                ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
                if (tracked.isEmpty()) {
                    continue;
                }

                boolean changed = false;
                for (EvolutionRegistry.KillRequirement req : rule.requirements.kills) {
                    if (killedKey.equals(req.entity.key) && EntityConditionHelper.matchesNbtFilters(killed, req.entity.nbt)) {
                    if (!passesExtraRequirements(player, killed, rule.requirements)) {
                        continue;
                    }
                        String matchKey = EntityConditionHelper.getMatchKey(killedKey, req.entity.nbt);
                        changed |= incrementKillCount(tracked, matchKey, req.count);
                    }
                }

                if (changed && tracked.getItem() instanceof BaseCurioItem curio) {
                    curio.refreshEffects(player);
                }

            if (tryEvolve(player, killed, rule)) {
                    for (EvolutionRegistry.LinkedEvolve linked : rule.requirementsRef) {
                        tryLinkedEvolve(player, linked);
                    }
                }
                continue;
            }

            if (rule.type == EvolutionRegistry.RuleType.ATTRIBUTE) {
                if (playerKilled) continue;
                if (rule.item == null || rule.item.isBlank()) continue;
                if (rule.progress == null) continue;

                ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
                if (tracked.isEmpty()) {
                    continue;
                }

                boolean changed = false;
                for (EvolutionRegistry.KillGain k : rule.kills) {
                    if (killedKey.equals(k.entity.key) && EntityConditionHelper.matchesNbtFilters(killed, k.entity.nbt)) {
                        if (!passesExtraRequirements(player, killed, rule.requirements)) {
                            continue;
                        }
                        changed |= incrementProgress(tracked, rule.progress.nbtKey, rule.progress.capCounterKey, rule.progress.cap, k.value);
                    }
                }

                if (changed && tracked.getItem() instanceof BaseCurioItem curio) {
                    curio.refreshEffects(player);
                }
                continue;
            }

            if (rule.type == EvolutionRegistry.RuleType.GRANT) {
                if (!LivingDeathGrantRuleMatcher.matches(player, killed, otherEntity, source, rule, ignoreEnabled)) {
                    continue;
                }
                executeGrant(player, rule);
            }
        }
    }

    static boolean passesExtraRequirements(Player player, LivingEntity killed, EvolutionRegistry.Requirements req) {
        if (!req.requiredEffects.isEmpty()) {
            for (String effectId : req.requiredEffects) {
                MobEffect effect;
                try {
                    effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectId));
                } catch (Exception e) {
                    return false;
                }
                if (effect == null || !player.hasEffect(effect)) {
                    return false;
                }
            }
        }

        if (!req.holdingGunTypes.isEmpty()) {
            if (!GunTypeChecker.isHoldingConfiguredGunTypes(player, req.holdingGunTypes)) {
                return false;
            }
        }

        if (req.minDistance != null) {
            if (killed == null) {
                return false;
            }
            double min = req.minDistance;
            if (player.distanceToSqr(killed) < min * min) {
                return false;
            }
        }
        return true;
    }

    static boolean matchesDamageSource(EvolutionRegistry.Rule rule, DamageSource source) {
        if (source == null) {
            return rule.damageSourceTags.isEmpty();
        }
        if (rule.damageSourceTags.isEmpty()) {
            return true;
        }
        for (String tagId : rule.damageSourceTags) {
            TagKey<DamageType> tag;
            try {
                tag = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(tagId));
            } catch (Exception e) {
                return false;
            }
            if (!source.is(tag)) {
                return false;
            }
        }
        return true;
    }

    static void executeGrant(Player player, EvolutionRegistry.Rule rule) {
        if (rule.grant == null) {
            return;
        }
        Item item = resolveItem(rule.grant.item);
        if (item == null) {
            return;
        }

        String onceTag = LivingDeathGrantRuleMatcher.oncePerPlayerKey(rule);
        if (onceTag != null && !onceTag.isBlank()) {
            CompoundTag data = player.getPersistentData();
            if (data.getBoolean(onceTag)) {
                return;
            }
            data.putBoolean(onceTag, true);
        }

        ItemStack stack = new ItemStack(item);
        if (rule.grant.bindToPlayer) {
            bindToPlayer(stack, player);
        }
        CurioGrantHelper.give(player, stack, rule.grant.overflowMode);
    }

    private static void bindToPlayer(ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("BoundPlayer", player.getStringUUID());
        tag.putString("BoundPlayerName", player.getName().getString());
        tag.putBoolean("IsBound", true);
    }

    private static boolean tryEvolve(Player player, LivingEntity killed, EvolutionRegistry.Rule rule) {
        ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
        if (tracked.isEmpty()) {
            return false;
        }

        if (!LivingDeathEvolutionRuleMatcher.matches(player, tracked, killed, rule)) {
            return false;
        }

        Item toItem = resolveItem(rule.to);
        if (toItem == null) {
            return false;
        }

        return EvolutionExecutor.evolve(player, stack -> rule.item.equals(itemId(stack)),
                () -> new ItemStack(toItem),
                EvolutionExecutor.NbtMode.COPY_ALL, rule.excludeNbtKeys, (oldStack, newStack) -> resetCapCountersForItem(rule.to, newStack), false);
    }

    private static void tryLinkedEvolve(Player player, EvolutionRegistry.LinkedEvolve linked) {
        Item toItem = resolveItem(linked.to);
        if (toItem == null) {
            return;
        }
        EvolutionExecutor.evolve(player, stack -> linked.item.equals(itemId(stack)),
                () -> new ItemStack(toItem),
                EvolutionExecutor.NbtMode.COPY_ALL, Collections.emptyList(), (oldStack, newStack) -> resetCapCountersForItem(linked.to, newStack), true);
    }

    static void resetCapCountersForItem(String itemId, ItemStack stack) {
        if (itemId == null || itemId.isBlank()) {
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.ATTRIBUTE, itemId)) {
            EvolutionRegistry.Progress progress = rule.progress;
            if (progress == null || progress.capCounterKey == null || progress.capCounterKey.isBlank()) {
                continue;
            }
            tag.putDouble(progress.capCounterKey, 0.0);
        }
    }

    static boolean hasEquipped(Player player, String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return false;
        }
        return !findFirstEquippedStack(player, stack -> itemId.equals(itemId(stack))).isEmpty();
    }

    private static ItemStack findFirstEquippedStack(LivingEntity livingEntity, Predicate<ItemStack> predicate) {
        if (livingEntity == null) {
            return ItemStack.EMPTY;
        }
        ICuriosItemHandler inv = CuriosApi.getCuriosInventory(livingEntity).orElse(null);
        if (inv == null) {
            return ItemStack.EMPTY;
        }
        for (var entry : inv.getCurios().entrySet()) {
            ICurioStacksHandler stacksHandler = entry.getValue();
            if (stacksHandler == null) {
                continue;
            }
            var handler = stacksHandler.getStacks();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && predicate.test(stack)) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private static boolean incrementKillCount(ItemStack stack, String entityKey, int maxCount) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag killCounts = tag.getCompound(EvolutionNbtKeys.KILL_COUNTS);
        int current = killCounts.getInt(entityKey);
        if (current < maxCount) {
            killCounts.putInt(entityKey, current + 1);
            tag.put(EvolutionNbtKeys.KILL_COUNTS, killCounts);
            return true;
        }
        return false;
    }

    private static boolean incrementProgress(ItemStack stack, String progressKey, String capCounterKey, double cap, double value) {
        if (value == 0.0) {
            return false;
        }
        CompoundTag tag = stack.getOrCreateTag();
        if (progressKey == null || progressKey.isBlank() || capCounterKey == null || capCounterKey.isBlank()) {
            return false;
        }
        double counter = tag.getDouble(capCounterKey);
        if (counter >= cap) {
            return false;
        }
        double delta = value;
        if (delta > cap - counter) {
            delta = cap - counter;
        }
        if (delta == 0.0) {
            return false;
        }
        tag.putDouble(capCounterKey, counter + delta);
        tag.putDouble(progressKey, tag.getDouble(progressKey) + delta);
        return true;
    }

    private static String itemId(ItemStack stack) {
        if (stack.isEmpty()) {
            return "";
        }
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key != null ? key.toString() : "";
    }

    private static Item resolveItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return null;
        }
        try {
            return ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        } catch (Exception ignored) {
            return null;
        }
    }

 
}
