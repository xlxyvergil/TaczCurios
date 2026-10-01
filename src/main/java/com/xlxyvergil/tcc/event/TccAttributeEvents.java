package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.capability.ImaginaryHealthLedgerCapability;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.util.ForcedKillHelper;
import com.xlxyvergil.tcc.util.ImaginaryInfectionHelper;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;

import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@EventBusSubscriber(modid = "tcc")
public class TccAttributeEvents {

    public static final String INFECTION_ATTACKER_KEY = "tcc_infection_attacker";

    /**
     * 重入防护：当一个目标正在被本次附加虚数伤害（applyImaginaryDamage 的 hurt 路径）结算时，
     * 该目标会临时进入此集合，用于切断：嵌套 LivingIncomingDamageEvent 带来的第二次抗性/侵染结算，
     * 以及饰品 onLivingHurt 监听器再次触发 applyImaginaryDamage 导致的无限递归。
     */
    private static final Set<LivingEntity> IMAGINARY_HURT_GUARD = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * 侵蚀进度 HUD 的观察者注册表：玩家 UUID → 其最近造成伤害的实体（含时间戳）。
     * 由攻击时写入，实体账本变化时按其定向推送，因此多个玩家围殴同一只怪都能各自看到该怪进度，
     * 转火别的怪后自动切换目标。超过 {@link #HUD_TIMEOUT_MS} 未再攻击则视作脱离战斗并清理。
     */
    private static final Map<UUID, WatchedTarget> HUD_TARGETS = new ConcurrentHashMap<>();

    /** 观察者失效时长：与客户端 HUD 的显示超时一致。 */
    private static final long HUD_TIMEOUT_MS = 5000L;

    private record WatchedTarget(Entity entity, long atMs) {
        boolean isExpired(long now) {
            return now - atMs >= HUD_TIMEOUT_MS;
        }
    }

    /** tacz:bullets —— TACZ 枪械子弹伤害 tag */
    private static final TagKey<DamageType> TACZ_BULLETS_TAG =
        TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("tacz", "bullets"));

    /**
     * 判断伤害来源是否属于实体的主动攻击：
     *  玩家/生物普通攻击（PLAYER_ATTACK / MOB_ATTACK）、
     *  枪械子弹（tacz:bullets）、
     *  以及虚数枪伤（tcc:imaginary_damage，由 ImaginaryConversionHelper 在 Pre 阶段把子弹源替换而来）。
     * 用于过滤其它模组在 LivingIncomingDamageEvent 中通过 FastHurt 再入产生的强制子伤害
     *  （IN_FIRE / WIND_FLOW / FROST_FLAME / MOB_CUTTING / GENERIC_KILL 等），切断再入栈溢出。
     */
    public static boolean isActiveAttackSource(DamageSource source) {
        return source.is(DamageTypes.PLAYER_ATTACK)
            || source.is(DamageTypes.MOB_ATTACK)
            || source.is(TACZ_BULLETS_TAG)
            || source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG);
    }

    /**
     * 虚数/崩解伤害的落点：扣减实体身上的虚数死亡进度账本（attachment）。
     * 账本一旦进入即为实体血量的权威镜像（起点取首次命中时的当前血量），因此不受 setHealth 限伤/锁血影响。
     * 每次扣减后立即把账本值强行写回血量字段并同步客户端，归零后由 triggerLedgerDeath 统一收尾。
     */
    private static boolean applyLedgerDamage(LivingEntity target, DamageSource source, float finalDamage) {
        var ledger = ImaginaryHealthLedgerCapability.get(target);
        if (ledger == null) return false;

        if (!ledger.isInitialized()) {
            ledger.init(target.getMaxHealth(), target.getHealth());
        }
        ledger.setLastSource(source);
        recordHudTarget(target, source);
        float remain = ledger.reduce(finalDamage);
        if (remain > 0.0F) {
            // 把账本值强行写回血量并同步客户端（绕过限伤/锁血，UI 反馈即时）
            ForcedKillHelper.forceHealth(target, remain);
            pushHud(target);
            return true;
        }
        pushHud(target);
        triggerLedgerDeath(target, ledger);
        return true;
    }

    /**
     * 账本记账：账本已初始化的实体，其本次实际受到的伤害（护甲/抗性/吸收减免后、即将从血量扣除的量）
     * 同步记入账本，使账本始终是该实体血量的权威镜像。
     * 走 hurt 的虚数伤害路线（IMAGINARY_HURT_GUARD 期间）不记账。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLedgerDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) return;
        if (IMAGINARY_HURT_GUARD.contains(target)) return;

        float amount = event.getNewDamage();
        if (amount <= 0.0F) return;

        var ledger = ImaginaryHealthLedgerCapability.get(target);
        if (ledger == null || !ledger.isInitialized()) return;

        ledger.setLastSource(event.getSource());
        recordHudTarget(target, event.getSource());
        ledger.reduce(amount);
        pushHud(target);
    }

    /**
     * 账本写回：账本是权威血量镜像，一旦与真实血量出现偏差（第三方限伤/锁血把血量卡住），
     * 每 tick 强行写回并同步客户端；账本归零则统一收尾。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLedgerTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity.level().isClientSide) return;
        if (entity.isDeadOrDying()) return;

        var ledger = ImaginaryHealthLedgerCapability.get(entity);
        if (ledger == null || !ledger.isInitialized()) return;

        float authoritative = ledger.getLedger();
        if (authoritative <= 0.0F) {
            triggerLedgerDeath(entity, ledger);
            return;
        }
        if (Math.abs(entity.getHealth() - authoritative) > 1.0E-4F) {
            ForcedKillHelper.forceHealth(entity, authoritative);
        }
    }

    /**
     * 账本归零的统一收尾：先把血量字段写成 0（绕过限伤/锁血），再走标准 die()；
     * 若 die() 被拦截未置位 dead，则补掉落并 remove(KILLED)。
     * tcc:tcc_forced_kill 命中则跳过 die()，直接补掉落 + 移除。
     */
    private static void triggerLedgerDeath(LivingEntity target, ImaginaryHealthLedgerCapability.Handler ledger) {
        DamageSource recorded = ledger != null ? ledger.getLastSource() : null;
        if (ledger != null) {
            ledger.reset();
        }
        ForcedKillHelper.forceZeroHealth(target);

        DamageSource source = recorded != null ? recorded : target.damageSources().genericKill();

        if (ForcedKillHelper.requiresForcedKill(target)) {
            ForcedKillHelper.dropAllDeathLoot(target, source);
            target.remove(Entity.RemovalReason.KILLED);
            return;
        }

        target.die(source);
        if (!target.dead) {
            ForcedKillHelper.dropAllDeathLoot(target, source);
            target.remove(Entity.RemovalReason.KILLED);
        }
    }

    /**
     * 解析本次伤害归属的玩家，作为侵蚀进度 HUD 的观察者：攻击者是玩家则取其本人，
     * 是女仆则取其主人；其它来源返回 null（此时不改变其原有观察目标）。
     */
    private static ServerPlayer resolveViewer(DamageSource source) {
        if (source == null) return null;
        Entity attacker = source.getEntity();
        if (attacker == null) return null;
        Player owner = MaidCompat.resolveOwnerPlayer(attacker);
        if (owner instanceof ServerPlayer sp) return sp;
        if (attacker instanceof ServerPlayer sp) return sp;
        return null;
    }

    /** 记录该玩家「当前正在打的目标」为本次伤害的实体，供后续 HUD 定向推送。 */
    private static void recordHudTarget(LivingEntity target, DamageSource source) {
        ServerPlayer viewer = resolveViewer(source);
        if (viewer == null) return;
        HUD_TARGETS.put(viewer.getUUID(), new WatchedTarget(target, System.currentTimeMillis()));
    }

    /**
     * 把 target 的最新侵蚀进度推送给「当前目标正好是它」的所有在线玩家，并顺带清理超时观察者。
     * 多个玩家围殴同一只怪时都会收到；某人转火后不再收到该怪更新（其目标已变）。
     */
    private static void pushHud(LivingEntity target) {
        if (!(target.level() instanceof ServerLevel serverLevel)) return;
        var ledger = ImaginaryHealthLedgerCapability.get(target);
        if (ledger == null) return;

        long now = System.currentTimeMillis();
        String name = MaidCompat.getDisplayName(target).getString();
        float progress = ledger.getProgress();
        var playerList = serverLevel.getServer().getPlayerList();

        var it = HUD_TARGETS.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            WatchedTarget watched = entry.getValue();
            if (watched.isExpired(now)) {
                it.remove();
                continue;
            }
            if (watched.entity() != target) continue;
            ServerPlayer viewer = playerList.getPlayer(entry.getKey());
            if (viewer != null) {
                NetworkHandler.sendErosionProgress(viewer, name, progress);
            }
        }
    }

    /**
     * 虚数伤害结算入口（非崩解）：由饰品命中时调用。
     * imaginaryDamageUseSetHealth 为 false（默认）走常规 hurt；为 true 走账本。
     */
    public static boolean applyImaginaryDamage(LivingEntity target, DamageSource source, float intendedDamage) {
        if (intendedDamage <= 0) return false;
        if (IMAGINARY_HURT_GUARD.contains(target)) return false;

        target.invulnerableTime = 0;

        float finalDamage = resolveFinalImaginaryDamage(target, source, intendedDamage);
        if (finalDamage <= 0) return false;

        if (source.getEntity() instanceof LivingEntity attacker) {
            target.setLastHurtByMob(attacker);
        }

        if (TaczCuriosConfig.COMMON.imaginaryDamageUseSetHealth.get()) {
            return applyLedgerDamage(target, source, finalDamage);
        }

        IMAGINARY_HURT_GUARD.add(target);
        try {
            return target.hurt(source, finalDamage);
        } finally {
            IMAGINARY_HURT_GUARD.remove(target);
        }
    }

    /** 崩解伤害入口：直接扣账本。由 ImaginaryCollapseEffect 调用。 */
    public static boolean applyCollapseDamage(LivingEntity target, DamageSource source, float intendedDamage) {
        if (intendedDamage <= 0) return false;

        target.invulnerableTime = 0;

        float finalDamage = resolveFinalImaginaryDamage(target, source, intendedDamage);
        if (finalDamage <= 0) return false;

        if (source.getEntity() instanceof LivingEntity attacker) {
            target.setLastHurtByMob(attacker);
        }

        return applyLedgerDamage(target, source, finalDamage);
    }

    private static float resolveFinalImaginaryDamage(LivingEntity target, DamageSource source, float baseDamage) {
        if (baseDamage <= 0) return 0;

        double resistance = target.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE);
        resistance = Math.max(-100.0, Math.min(100.0, resistance));

        float damageAfterResistance = (float) (baseDamage * (1.0 - resistance / 100.0));

        double ampPerLevel = TaczCuriosConfig.COMMON.imaginaryInfectionAmpPerLevel.get();
        int infectionLevel = 0;
        MobEffectInstance infectionInstance = target.getEffect(TccMobEffects.IMAGINARY_INFECTION);
        if (infectionInstance != null) {
            infectionLevel = infectionInstance.getAmplifier() + 1;
        }

        return (float) ((float) Math.round((damageAfterResistance * (1.0 + infectionLevel * ampPerLevel)) * 10000.0) / 10000.0);
    }

    @SubscribeEvent
    public static void applyImaginaryInfection(EntityHurtByGunEvent.Post event) {
        if (event.getLogicalSide().isClient()) return;
        var target = event.getHurtEntity();
        if (!(target instanceof LivingEntity living) || living.isDeadOrDying()) return;

        DamageSource source = event.getDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING);
        if (!source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) return;
        if (source.getEntity() == target) return;

        var srcEntity = source.getEntity();
        if (!(srcEntity instanceof LivingEntity attacker)) return;

        applyInfection(living, attacker, ImaginaryInfectionHelper.resolveMaxLevel(attacker));
    }

    public static void applyInfection(LivingEntity living, LivingEntity attacker, int maxLevel) {
        if (maxLevel <= 0) return;
        int duration = TaczCuriosConfig.COMMON.imaginaryInfectionDuration.get();

        MobEffectInstance existingEffect = living.getEffect(TccMobEffects.IMAGINARY_INFECTION);
        int newAmplifier = 0;
        if (existingEffect != null) {
            newAmplifier = Math.min(existingEffect.getAmplifier() + 1, maxLevel - 1);
        }
        var newInstance = new MobEffectInstance(
            TccMobEffects.IMAGINARY_INFECTION,
            duration * 20,
            newAmplifier,
            false, false, true
        );

        living.forceAddEffect(newInstance, attacker);
        living.addEffect(newInstance, attacker);

        // 记录侵染来源攻击者，使虚数崩解击杀能正确归属
        // （若攻击者是女仆，转换为女仆主人，以让崩解击杀计入主人名下）
        LivingEntity credited = MaidCompat.resolveOwnerPlayer(attacker);
        living.getPersistentData().putString(
                INFECTION_ATTACKER_KEY, (credited != null ? credited : attacker).getStringUUID());
    }

    /**
     * 统一施加剧增崩解的实际入口：写入来源攻击者 NBT 并施加崩解效果。
     * 供各饰品在命中时（含概率判定通过后）调用。
     * 返回是否真正新施加了崩解（目标此前无崩解且存活）；目标已有崩解或已死亡则返回 false。
     */
    public static boolean applyCollapse(LivingEntity target, LivingEntity attacker) {
        if (target == null || attacker == null) return false;
        if (target.isDeadOrDying()) return false;
        var collapse = TccMobEffects.IMAGINARY_COLLAPSE;
        if (target.hasEffect(collapse)) return false;

        int duration = TaczCuriosConfig.COMMON.imaginaryInfectionDuration.get();
        var collapseInstance = new MobEffectInstance(
            collapse,
            duration * 20,
            0,
            false, false, true
        );
        target.forceAddEffect(collapseInstance, attacker);
        target.addEffect(collapseInstance, attacker);

        // 记录侵染来源攻击者，使崩解击杀能正确归属（女仆转换为女仆主人）
        LivingEntity credited = MaidCompat.resolveOwnerPlayer(attacker);
        target.getPersistentData().putString(
                INFECTION_ATTACKER_KEY, (credited != null ? credited : attacker).getStringUUID());
        return true;
    }

    @SubscribeEvent
    public static void onGunOverheal(EntityHurtByGunEvent.Post event) {
        if (event.getLogicalSide().isClient()) return;

        LivingEntity attacker = event.getAttacker();
        if (attacker == null) return;

        DamageSource source = event.getDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING);
        if (!source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) return;

        if (source.getDirectEntity() instanceof LivingEntity) return;

        Holder<Attribute> overhealAttr = BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.fromNamespaceAndPath("apothic_attributes", "overheal")).orElse(null);
        if (overhealAttr == null) return;
        float overheal = (float) attacker.getAttributeValue(overhealAttr);
        if (overheal <= 0) return;

        float damage = event.getBaseAmount();
        if (event.isHeadShot()) damage *= event.getHeadshotMultiplier();

        if (!(event.getHurtEntity() instanceof LivingEntity target)) return;
        float effectiveDamage = Math.min(damage, target.getMaxHealth());

        float maxOverheal = attacker.getMaxHealth() * 0.5F;
        if (attacker.getAbsorptionAmount() < maxOverheal) {
            attacker.setAbsorptionAmount(
                Math.min(maxOverheal, attacker.getAbsorptionAmount() + effectiveDamage * overheal)
            );
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void imaginaryDamageOnAttack(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide || target.isDeadOrDying()) return;
        if (IMAGINARY_HURT_GUARD.contains(target)) return;

        DamageSource source = event.getSource();

        if (source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) {
            event.setAmount(resolveFinalImaginaryDamage(target, source, event.getAmount()));
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(TccMobEffects.IMAGINARY_INFECTION)
                && !entity.hasEffect(TccMobEffects.IMAGINARY_COLLAPSE)) {
            return;
        }

        // 仅再生 buff 生效时允许回血，并把回血量同步恢复进账本（侵蚀进度随之回退）。
        if (entity.hasEffect(MobEffects.REGENERATION)) {
            var ledger = ImaginaryHealthLedgerCapability.get(entity);
            if (ledger != null && ledger.isInitialized()) {
                ledger.recover(event.getAmount());
                pushHud(entity);
                return;
            }
        }

        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getResult() == MobEffectEvent.Applicable.Result.APPLY) return;
        var key = BuiltInRegistries.MOB_EFFECT.getKey(event.getEffectInstance().getEffect().value());
        if (key != null && key.getNamespace().equals("tcc")) {
            event.setResult(MobEffectEvent.Applicable.Result.APPLY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        LivingEntity entity = event.getEntity();
        if (entity.isDeadOrDying()) return;

        Holder<MobEffect> effect = event.getEffect();

        var key = BuiltInRegistries.MOB_EFFECT.getKey(effect.value());
        if (key != null && key.getNamespace().equals("tcc")) {
            MobEffectInstance instance = entity.getEffect(effect);
            if (instance != null && instance.getDuration() > 0) {
                event.setCanceled(true);
            }
        }
    }

}
