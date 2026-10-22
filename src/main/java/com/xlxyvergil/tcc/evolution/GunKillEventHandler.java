package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.KillEventDedupe;
import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.function.Predicate;
import com.xlxyvergil.tcc.util.ItemNbtHelper;

/**
 * gun_kill 触发器的成就/属性规则处理器。
 * <p>
 * 判定入口是 LivingDeathEvent，而不是 TACZ 的 {@code EntityKillByGunEvent}：
 * 原版末影龙在非停驻阶段被打到 0 血时，{@code EnderDragon#hurt(DragonPart, …)} 会在同一次调用里
 * {@code setHealth(1.0F)} 并切到 DYING 阶段，TACZ 结算完再判断 {@code isDeadOrDying()} 得到 false，
 * 因此永远不会发出击杀事件 —— 依赖该事件会让「击杀末影龙」类成就永远无法在飞行阶段完成。
 * <p>
 * 死亡事件不受此影响，且致死伤害的直接实体就是 TACZ 的子弹实体
 * （{@code tacz:bullet}），枪械 id 可直接由 EntityKineticBullet.getGunId() 反查，
 * 因此不需要依赖伤害类型 tag，也不受数据包改写 tag 的影响。
 */
@EventBusSubscriber(modid = TaczCurios.MODID)
public final class GunKillEventHandler {
    public static final String TRIGGER_GUN_KILL = "gun_kill";

    /** 同一被杀实体的重复死亡记录去重（复活类模组/异常重入时兜底）。 */
    private static final KillEventDedupe GUN_KILL_DEDUPE = new KillEventDedupe();

    private GunKillEventHandler() {}

    /**
     * 枪械击杀判定：致死伤害的直接实体是 TACZ 子弹即视为枪械击杀。
     * 不查伤害类型 tag，因此对「子弹伤害被转成虚数伤害」的三阶神之键同样成立。
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed.level().isClientSide) return;

        DamageSource source = event.getSource();
        if (!(source.getDirectEntity() instanceof EntityKineticBullet bullet)) return;

        ResourceLocation gunId = bullet.getGunId();
        if (gunId == null) return;

        Player player = MaidCompat.resolveOwnerPlayer(source.getEntity());
        if (!(player instanceof ServerPlayer sp)) return;

        handleGunKill(sp, killed, gunId);
    }

    public static void handleGunKill(Player player, LivingEntity killed, ResourceLocation gunId) {
        ServerPlayer serverPlayer = player instanceof ServerPlayer sp ? sp : null;

        // 同一被杀实体在短窗口内只计一次（含复活/重入导致死亡事件重复的极端情况）。
        if (GUN_KILL_DEDUPE.isDuplicate(killed.getUUID(), player.level().getGameTime())) {
            return;
        }

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
                net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect =
                        BuiltInRegistries.MOB_EFFECT.getHolder(effectRl).orElse(null);
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
        CompoundTag tag = ItemNbtHelper.getTag(stack);
        if (progressKey == null || progressKey.isBlank() || capCounterKey == null || capCounterKey.isBlank())
            return false;
        double counter = tag.getDouble(capCounterKey);
        if (counter >= cap) return false;
        double delta = Math.min(value, cap - counter);
        if (delta == 0.0) return false;
        tag.putDouble(capCounterKey, counter + delta);
        tag.putDouble(progressKey, tag.getDouble(progressKey) + delta);
        ItemNbtHelper.setTag(stack, tag);
        return true;
    }

    private static String itemId(ItemStack stack) {
        if (stack.isEmpty()) return "";
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
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
