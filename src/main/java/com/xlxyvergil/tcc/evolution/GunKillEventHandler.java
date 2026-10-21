package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.KillEventDedupe;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
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

import javax.annotation.Nullable;

/**
 * gun_kill 触发器的成就/属性规则处理器。
 * 直接监听 TACZ 的 EntityKillByGunEvent（枪械击杀事件）：致死命中时 TACZ 必定发出该事件，
 * 且事件自带 attacker / killedEntity / gunId / headshot，无需依赖 damage source 或 tag 判定。
 * <p>
 * 例外：原版末影龙在非停驻阶段被打到 0 血时会被 {@code setHealth(1.0F)} 回血并切到 DYING 阶段，
 * TACZ 结算后判断 {@code isDeadOrDying()} 为 false，因而不会发出击杀事件。
 * 这类目标由 {@link LivingDeathEventHandler} 按致死伤害来源补记，统一走 {@link #handleGunKill}。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunKillEventHandler {
    public static final String TRIGGER_GUN_KILL = "gun_kill";

    /** 多弹头去重：每个触发器独立实例，避免与其它触发器的记录互相干扰。 */
    private static final KillEventDedupe GUN_KILL_DEDUPE = new KillEventDedupe();

    private GunKillEventHandler() {}

    /**
     * 枪械击杀主路径：监听 TACZ 官方击杀事件。
     * 致死命中时 TACZ 只发 EntityKillByGunEvent（与 EntityHurtByGunEvent.Post 互斥），且事件自带 gunId，
     * 因此不需要也无法用 damage source tag 二次确认。
     */
    @SubscribeEvent
    public static void onGunKill(EntityKillByGunEvent event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        LivingEntity attacker = event.getAttacker();
        LivingEntity killed = event.getKilledEntity();
        if (attacker == null || killed == null) {
            return;
        }
        Player player = MaidCompat.resolveOwnerPlayer(attacker);
        if (!(player instanceof ServerPlayer sp)) {
            return;
        }
        handleGunKill(sp, killed, event.getGunId());
    }

    /**
     * 从致死伤害来源反推枪械 id，供死亡兜底使用。
     * <p>
     * 原版末影龙在非停驻阶段被打到 0 血时，{@code EnderDragon#hurt(DragonPart, …)} 会立刻
     * {@code setHealth(1.0F)} 并切到 DYING 阶段，TACZ 在伤害结算后判断
     * {@code isDeadOrDying()} 得到 false，于是只发 {@code EntityHurtByGunEvent.Post}、
     * 不发 {@code EntityKillByGunEvent} —— gun_kill 类成就永远等不到事件。
     * 这类目标只能从死亡事件补记，枪械 id 由子弹实体反查。
     */
    @Nullable
    public static ResourceLocation resolveGunIdFromDamage(@Nullable DamageSource source) {
        if (source == null) return null;
        // 子弹伤害本身，或三阶神之键把子弹伤害转成的虚数伤害（直接实体仍然是子弹）
        boolean gunDamage = TccDamageSources.isTaczBulletSource(source)
                || source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG);
        if (!gunDamage) return null;
        return source.getDirectEntity() instanceof EntityKineticBullet bullet ? bullet.getGunId() : null;
    }

    public static void handleGunKill(Player player, LivingEntity killed, ResourceLocation gunId) {
        ServerPlayer serverPlayer = player instanceof ServerPlayer sp ? sp : null;

        // 同一被杀实体在短窗口内只计一次：致死命中可能同时走 EntityKillByGunEvent
        // 与 LivingDeathEvent 两条路径（末影龙先 die() 再被回血续命就是这种情况）。
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
