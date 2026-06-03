package com.xlxyvergil.tcc.evolution;

import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.EvolutionNbtKeys;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Collections;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunKillEventHandler {
    public static final String TRIGGER_GUN_KILL = "gun_kill";

    private GunKillEventHandler() {
    }

    @SubscribeEvent
    public static void onGunKill(EntityKillByGunEvent event) {
        LivingEntity attacker = event.getAttacker();
        if (!(attacker instanceof Player player)) {
            return;
        }
        if (player.level().isClientSide) {
            return;
        }

        LivingEntity killed = event.getKilledEntity();
        handleGunKill(player, killed, event.getGunId());
    }

    public static void handleGunKill(Player player, LivingEntity killed, ResourceLocation gunId) {
        String killedKey = killed != null ? BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString() : "";

        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTriggerOrEmpty(TRIGGER_GUN_KILL)) {
            if (!rule.enabled) {
                continue;
            }
            if (rule.playerKilled) {
                continue;
            }

            if (rule.type == EvolutionRegistry.RuleType.GRANT) {
                if (!matchesGrantRequirements(player, killed, killedKey, gunId, rule)) {
                    continue;
                }
                LivingDeathEventHandler.executeGrant(player, rule);
                continue;
            }

            if (rule.type == EvolutionRegistry.RuleType.ATTRIBUTE) {
                applyAttributeRule(player, killed, killedKey, gunId, rule);
                continue;
            }

            if (rule.type == EvolutionRegistry.RuleType.EVOLVE) {
                if (tryEvolve(player, killed, killedKey, gunId, rule)) {
                    for (EvolutionRegistry.LinkedEvolve linked : rule.requirementsRef) {
                        tryLinkedEvolve(player, linked);
                    }
                }
            }
        }
    }

    private static boolean matchesGrantRequirements(Player player, LivingEntity killed, String killedKey, ResourceLocation gunId, EvolutionRegistry.Rule rule) {
        if (rule.grant == null) {
            return false;
        }

        if (rule.item != null && !rule.item.isBlank()) {
            if (!LivingDeathEventHandler.hasEquipped(player, rule.item)) {
                return false;
            }
        }

        for (String requiredCurio : rule.requirements.equippedCurios) {
            if (!LivingDeathEventHandler.hasEquipped(player, requiredCurio)) {
                return false;
            }
        }

        if (!passesExtraRequirements(player, killed, gunId, rule.requirements)) {
            return false;
        }

        if (!matchesKilled(rule, killed, killedKey)) {
            return false;
        }

        for (EvolutionRegistry.AttributeRequirement req : rule.requirements.attributes) {
            Attribute attr = AttributeHelper.resolveAttribute(req.attribute);
            if (attr == null) {
                return false;
            }
            double value = player.getAttributeValue(attr);
            if (!compare(value, req.comparator, req.value)) {
                return false;
            }
        }

        String onceTag = rule.grant.oncePerPlayer ? rule.ruleId : null;
        if (onceTag != null && !onceTag.isBlank() && player.getPersistentData().getBoolean(onceTag)) {
            return false;
        }

        return true;
    }

    private static void applyAttributeRule(Player player, LivingEntity killed, String killedKey, ResourceLocation gunId, EvolutionRegistry.Rule rule) {
        if (rule.item == null || rule.item.isBlank()) {
            return;
        }
        if (rule.progress == null) {
            return;
        }
        if (killed == null) {
            return;
        }

        ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
        if (tracked.isEmpty()) {
            return;
        }

        if (!passesExtraRequirements(player, killed, gunId, rule.requirements)) {
            return;
        }

        boolean changed = false;
        for (EvolutionRegistry.KillGain k : rule.kills) {
            if (k == null || k.entity == null) {
                continue;
            }
            if (!killedKey.equals(k.entity.key)) {
                continue;
            }
            if (!EntityConditionHelper.matchesNbtFilters(killed, k.entity.nbt)) {
                continue;
            }
            changed |= incrementProgress(tracked, rule.progress.nbtKey, rule.progress.capCounterKey, rule.progress.cap, k.value);
        }

        if (changed && tracked.getItem() instanceof com.xlxyvergil.tcc.util.BaseCurioItem curio) {
            curio.refreshEffects(player);
        }
    }

    private static boolean tryEvolve(Player player, LivingEntity killed, String killedKey, ResourceLocation gunId, EvolutionRegistry.Rule rule) {
        if (rule.item == null || rule.item.isBlank()) {
            return false;
        }
        if (rule.to == null || rule.to.isBlank()) {
            return false;
        }

        ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
        if (tracked.isEmpty()) {
            return false;
        }

        CompoundTag tag = tracked.getOrCreateTag();
        boolean changed = false;
        for (EvolutionRegistry.KillRequirement req : rule.requirements.kills) {
            if (req == null || req.entity == null) {
                continue;
            }
            if (!matchesKilledReq(req, killed, killedKey)) {
                continue;
            }
            if (!passesExtraRequirements(player, killed, gunId, rule.requirements)) {
                continue;
            }
            String matchKey = EntityConditionHelper.getMatchKey(req.entity.key, req.entity.nbt);
            changed |= incrementKillCount(tag, matchKey, req.count);
        }

        if (changed && tracked.getItem() instanceof com.xlxyvergil.tcc.util.BaseCurioItem curio) {
            curio.refreshEffects(player);
        }

        if (!matchesEvolveRequirements(player, tracked, killed, gunId, rule)) {
            return false;
        }

        Item toItem = resolveItem(rule.to);
        if (toItem == null) {
            return false;
        }

        return EvolutionExecutor.evolve(player, stack -> rule.item.equals(itemId(stack)),
            () -> new ItemStack(toItem),
            EvolutionExecutor.NbtMode.COPY_ALL, rule.excludeNbtKeys, (oldStack, newStack) -> {
                LivingDeathEventHandler.resetCapCountersForItem(rule.to, newStack);
                if (rule.bindToPlayer) {
                    LivingDeathEventHandler.bindToPlayer(newStack, player);
                }
            }, true);
    }

    private static boolean matchesEvolveRequirements(Player player, ItemStack trackedStack, LivingEntity killed, ResourceLocation gunId, EvolutionRegistry.Rule rule) {
        CompoundTag tag = trackedStack.getOrCreateTag();

        for (String requiredCurio : rule.requirements.equippedCurios) {
            if (!hasEquipped(player, requiredCurio)) {
                return false;
            }
        }

        if (!passesExtraRequirements(player, killed, gunId, rule.requirements)) {
            return false;
        }

        CompoundTag killCounts = tag.getCompound(EvolutionNbtKeys.KILL_COUNTS);
        for (EvolutionRegistry.KillRequirement req : rule.requirements.kills) {
            String matchKey = EntityConditionHelper.getMatchKey(req.entity.key, req.entity.nbt);
            if (killCounts.getInt(matchKey) < req.count) {
                return false;
            }
        }

        for (EvolutionRegistry.AttributeRequirement req : rule.requirements.attributes) {
            Attribute attr = AttributeHelper.resolveAttribute(req.attribute);
            if (attr == null) {
                return false;
            }
            double value = player.getAttributeValue(attr);
            if (!compare(value, req.comparator, req.value)) {
                return false;
            }
        }

        return true;
    }

    private static void tryLinkedEvolve(Player player, EvolutionRegistry.LinkedEvolve linked) {
        Item toItem = resolveItem(linked.to);
        if (toItem == null) {
            return;
        }
        EvolutionExecutor.evolve(player, stack -> linked.item.equals(itemId(stack)),
            () -> new ItemStack(toItem),
            EvolutionExecutor.NbtMode.COPY_ALL, Collections.emptyList(),
            (oldStack, newStack) -> LivingDeathEventHandler.resetCapCountersForItem(linked.to, newStack), true);
    }

    private static boolean passesExtraRequirements(Player player, LivingEntity killed, ResourceLocation gunId, EvolutionRegistry.Requirements req) {
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
            if (!GunTypeChecker.matchesGunTypes(gunId, req.holdingGunTypes)) {
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

    private static boolean matchesKilled(EvolutionRegistry.Rule rule, LivingEntity killed, String killedKey) {
        if (rule.requirements.kills.isEmpty()) {
            return true;
        }
        if (killed == null) {
            return false;
        }
        for (EvolutionRegistry.KillRequirement req : rule.requirements.kills) {
            if (req == null || req.entity == null) {
                continue;
            }
            if (!"*".equals(req.entity.key) && !killedKey.equals(req.entity.key)) {
                continue;
            }
            if (EntityConditionHelper.matchesNbtFilters(killed, req.entity.nbt)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesKilledReq(EvolutionRegistry.KillRequirement req, LivingEntity killed, String killedKey) {
        if (killed == null) {
            return false;
        }
        if (!"*".equals(req.entity.key) && !killedKey.equals(req.entity.key)) {
            return false;
        }
        return EntityConditionHelper.matchesNbtFilters(killed, req.entity.nbt);
    }

    private static boolean incrementKillCount(CompoundTag itemTag, String entityKey, int maxCount) {
        CompoundTag killCounts = itemTag.getCompound(EvolutionNbtKeys.KILL_COUNTS);
        int current = killCounts.getInt(entityKey);
        if (current < maxCount) {
            killCounts.putInt(entityKey, current + 1);
            itemTag.put(EvolutionNbtKeys.KILL_COUNTS, killCounts);
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

    private static boolean compare(double current, String comparator, double expected) {
        return switch (comparator) {
            case "gt" -> current > expected;
            case "gte" -> current >= expected;
            case "lt" -> current < expected;
            case "lte" -> current <= expected;
            case "eq" -> Double.compare(current, expected) == 0;
            case "ne" -> Double.compare(current, expected) != 0;
            default -> current >= expected;
        };
    }

    private static boolean hasEquipped(Player player, String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return false;
        }
        return !findFirstEquippedStack(player, stack -> itemId.equals(itemId(stack))).isEmpty();
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
}
