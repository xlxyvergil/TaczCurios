package com.xlxyvergil.tcc.evolution;

import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunKillEventHandler {
    public static final String TRIGGER_GUN_KILL = "gun_kill";

    private GunKillEventHandler() {}

    @SubscribeEvent
    public static void onGunKill(EntityKillByGunEvent event) {
        LivingEntity attacker = event.getAttacker();
        if (!(attacker instanceof Player player)) return;
        if (player.level().isClientSide) return;

        LivingEntity killed = event.getKilledEntity();
        handleGunKill(player, killed, event.getGunId());
    }

    public static void handleGunKill(Player player, LivingEntity killed, ResourceLocation gunId) {
        ServerPlayer serverPlayer = player instanceof ServerPlayer sp ? sp : null;

        // ===== Achievement-driven GRANT / EVOLVE =====
        if (serverPlayer != null) {

            for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(TRIGGER_GUN_KILL)) {
                // Only handle grant and evolve types
                if (def.reward() == null) continue;

                // Check prerequisites
                if (!RuleAdvancementMapping.arePrerequisitesMet(serverPlayer, def)) continue;

                // Already completed?
                if (RuleAdvancementMapping.isAdvancementDone(serverPlayer, def.id())) continue;

                // Check conditions
                if (!AchievementConditionMatcher.matchesKillConditions(player, killed, gunId, def)) continue;

                // Award criterion
                RuleAdvancementMapping.awardNextCriterion(
                        serverPlayer, def.id(), def.criteriaCount());
            }
        }

        // ===== ATTRIBUTE rules (unchanged, from evolution_rules.json) =====
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTriggerOrEmpty(TRIGGER_GUN_KILL)) {
            if (!rule.enabled) continue;
            if (rule.playerKilled) continue;
            if (rule.type != EvolutionRegistry.RuleType.ATTRIBUTE) continue;
            applyAttributeRule(player, killed, gunId, rule);
        }
    }

    private static void applyAttributeRule(Player player, LivingEntity killed,
                                            ResourceLocation gunId, EvolutionRegistry.Rule rule) {
        if (rule.item == null || rule.item.isBlank() || rule.progress == null || killed == null) return;
        String killedKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();

        ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
        if (tracked.isEmpty()) return;

        if (!passesExtraRequirements(player, killed, gunId, rule.requirements)) return;

        boolean changed = false;
        for (EvolutionRegistry.KillGain k : rule.kills) {
            if (k == null || k.entity == null) continue;
            if (!killedKey.equals(k.entity.key)) continue;
            if (!EntityConditionHelper.matchesNbtFilters(killed, k.entity.nbt)) continue;
            changed |= incrementProgress(tracked, rule.progress.nbtKey,
                    rule.progress.capCounterKey, rule.progress.cap, k.value);
        }

        if (changed && tracked.getItem() instanceof com.xlxyvergil.tcc.util.BaseCurioItem curio) {
            curio.refreshEffects(player);
        }
    }

    private static boolean passesExtraRequirements(Player player, LivingEntity killed,
                                                    ResourceLocation gunId, EvolutionRegistry.Requirements req) {
        if (!req.requiredEffects.isEmpty()) {
            for (String effectId : req.requiredEffects) {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectId));
                if (effect == null || !player.hasEffect(effect)) return false;
            }
        }
        if (!req.holdingGunTypes.isEmpty()) {
            if (!GunTypeChecker.matchesGunTypes(gunId, req.holdingGunTypes)) return false;
        }
        if (req.minDistance != null) {
            if (killed == null) return false;
            if (player.distanceToSqr(killed) < req.minDistance * req.minDistance) return false;
        }
        return true;
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

    static ItemStack findFirstEquippedStack(LivingEntity livingEntity, Predicate<ItemStack> predicate) {
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
}
