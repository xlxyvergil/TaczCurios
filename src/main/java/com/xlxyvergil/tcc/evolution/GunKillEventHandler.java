package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.function.Predicate;

/**
 * gun_kill 触发器的成就/属性规则处理器。
 * 击杀判定统一由 GunKillDebugFallbackHandler.onLivingDeath 处理（基于 LivingDeathEvent + 死亡源 tag 校验），
 * 本类不再直接监听 EntityKillByGunEvent，handleGunKill 由 fallback 处理器在确认枪杀后调用。
 */
public final class GunKillEventHandler {
    public static final String TRIGGER_GUN_KILL = "gun_kill";

    private GunKillEventHandler() {}

    public static void handleGunKill(Player player, LivingEntity killed, ResourceLocation gunId) {
        ServerPlayer serverPlayer = player instanceof ServerPlayer sp ? sp : null;

        // 成就驱动的发放 / 进化
        if (serverPlayer != null) {

            for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(TRIGGER_GUN_KILL)) {
                if (def.reward() == null) continue;

                if (!def.isEnabled()) continue;

                if (!RuleAdvancementMapping.arePrerequisitesMet(serverPlayer, def)) continue;

                if (RuleAdvancementMapping.isAdvancementDone(serverPlayer, def.id())) continue;

                if (!AchievementConditionMatcher.matchesKillConditions(player, killed, gunId, def)) continue;

                // 每次击杀授予 1 步进度
                var kills = def.conditions() != null ? def.conditions().kills() : null;
                if (kills != null && kills.size() > 1) {
                    RuleAdvancementMapping.awardMultiTypeKill(
                            serverPlayer, def.id(), def, killed, 1);
                } else {
                    RuleAdvancementMapping.awardSteps(
                            serverPlayer, def.id(), def.targetCount(), 1);
                }
            }
        }

        // 属性规则（来自 evolution_rules.json）
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

        ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
        if (tracked.isEmpty()) return;

        if (!passesExtraRequirements(player, killed, gunId, rule.requirements)) return;

        boolean changed = false;
        for (EvolutionRegistry.KillGain k : rule.kills) {
            if (k == null || k.entity == null) continue;
            if (!EntityConditionHelper.matchesEntityKey(k.entity.key, killed)) continue;
            if (!EntityConditionHelper.matchesNbtFilters(killed, k.entity.nbt)) continue;
            changed |= incrementProgress(tracked, rule.progress.nbtKey,
                    rule.progress.capCounterKey, rule.progress.cap, k.value);
        }

        if (changed && tracked.getItem() instanceof com.xlxyvergil.tcc.items.BaseCurioItem curio) {
            curio.refreshEffects(player, tracked);
        }
    }

    private static boolean passesExtraRequirements(Player player, LivingEntity killed,
                                                    ResourceLocation gunId, EvolutionRegistry.Requirements req) {
        if (!req.requiredEffects.isEmpty()) {
            for (String effectId : req.requiredEffects) {
                net.minecraft.resources.ResourceLocation effectRl = net.minecraft.resources.ResourceLocation.tryParse(effectId);
                if (effectRl == null) return false;
                net.minecraft.world.effect.MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectRl);
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
