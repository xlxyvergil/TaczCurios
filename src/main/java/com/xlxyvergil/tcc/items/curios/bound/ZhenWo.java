package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZhenWo extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("2df6423f-42bc-4629-8624-ebb8cf4ff4c8");
    private static final UUID ALL_ATTRIBUTES_UUID = UUID.fromString("8f851e69-0217-4e14-af7f-ee655b4a1cc7");
    private static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.fromString("9b7d1c2e-3f4a-5b6c-7d8e-9f0a1b2c3d4e");
    private static final String BARRIER_KEY = "tcc_zhen_wo_barrier";
    private static final String COOLDOWN_KEY = "tcc_zhen_wo_cooldown";

    /** 真我结界激活中的佩戴者 UUID 集合（仅服务端维护），供两参 getEntities 过滤使用。 */
    private static final Set<UUID> ACTIVE_BARRIER_WEARERS = new HashSet<>();

    public ZhenWo(Properties properties) {
        super(properties);
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                s -> s.getItem() instanceof ZhenWo).isEmpty();
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return null;
    }

    /** 收束：真我仅玩家可佩戴，女仆等非玩家实体不可装备。 */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player)) return false;
        return super.canEquip(slotContext, stack);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(ALL_ATTRIBUTES_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(KNOCKBACK_RESISTANCE_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                TaczCuriosConfig.COMMON.zhenWoImaginaryResistance.get(), IMAGINARY_RESISTANCE_UUID,
                "tcc.zhen_wo.imaginary_resistance", AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_UUID,
                "tcc.zhen_wo.all_attributes", TaczCuriosConfig.COMMON.zhenWoAllAttributesPercent.get(),
                AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, Attributes.KNOCKBACK_RESISTANCE,
                1.0, KNOCKBACK_RESISTANCE_UUID, "tcc.zhen_wo.knockback_resistance",
                AttributeModifier.Operation.ADDITION);
            if (livingEntity instanceof Player) {
                DamageResistanceHelper.setDamageReduction(livingEntity,
                    (float) (1 - TaczCuriosConfig.COMMON.zhenWoDamageTakenFactor.get()));
            } else {
                // 真我减伤仅对玩家生效，非玩家清除可能的残留
                DamageResistanceHelper.clearDamageReduction(livingEntity);
            }
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.applyAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_UUID,
            "tcc.zhen_wo.all_attributes", 0, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.removeModifier(livingEntity, Attributes.KNOCKBACK_RESISTANCE,
            KNOCKBACK_RESISTANCE_UUID);
        DamageResistanceHelper.clearDamageCap(livingEntity);
        DamageResistanceHelper.clearDamageReduction(livingEntity);
        livingEntity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
        // 只扣掉真我维持的那一份黄心，保留外部来源（只减不压）
        float absorptionTarget = TaczCuriosConfig.COMMON.zhenWoAbsorptionAmount.get();
        livingEntity.setAbsorptionAmount(Math.max(0.0F,
            livingEntity.getAbsorptionAmount() - absorptionTarget));
        ACTIVE_BARRIER_WEARERS.remove(livingEntity.getUUID());
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide) return;

        if (entity.isDeadOrDying()) return;

        // 真我相关的免死与减伤仅对玩家生效：非玩家不再享有减伤、结界与免死触发，并清理历史残留。
        if (!(entity instanceof Player)) {
            ACTIVE_BARRIER_WEARERS.remove(entity.getUUID());
            DamageResistanceHelper.clearDamageReduction(entity);
            CompoundTag tag = stack.getTag();
            if (tag != null && (tag.getInt(BARRIER_KEY) != 0 || tag.getInt(COOLDOWN_KEY) != 0)) {
                tag.putInt(BARRIER_KEY, 0);
                tag.putInt(COOLDOWN_KEY, 0);
            }
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        int barrierTicks = tag.getInt(BARRIER_KEY);
        int cooldownTicks = tag.getInt(COOLDOWN_KEY);

        // 同步结界激活中标记，供两参 getEntities 过滤
        if (barrierTicks > 0) {
            ACTIVE_BARRIER_WEARERS.add(entity.getUUID());
        } else {
            ACTIVE_BARRIER_WEARERS.remove(entity.getUUID());
        }

        DamageResistanceHelper.setDamageReduction(entity,
            (float) (1 - TaczCuriosConfig.COMMON.zhenWoDamageTakenFactor.get()));

        // 参考救世：周期性续期抗性提升，时长为无限，卸下时移除
        if (entity.tickCount % 10 == 0) {
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, MobEffectInstance.INFINITE_DURATION,
                TaczCuriosConfig.COMMON.zhenWoResistanceLevel.get(), false, false, true));
        }

        // 每间隔把黄心抬到保底值：只抬不压，若外部来源更高则保留，避免覆盖他人贡献
        int absorptionInterval = TaczCuriosConfig.COMMON.zhenWoAbsorptionInterval.get() * 20;
        if (entity.tickCount % absorptionInterval == 0) {
            float target = TaczCuriosConfig.COMMON.zhenWoAbsorptionAmount.get();
            entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount(), target));
        }

        if (barrierTicks > 0) {
            barrierTicks--;
            tag.putInt(BARRIER_KEY, barrierTicks);
            if (entity.tickCount % 5 == 0) {
                refreshBarrierBuff(entity, barrierTicks);
            }
            if (barrierTicks <= 0) {
                tag.putInt(COOLDOWN_KEY, TaczCuriosConfig.COMMON.zhenWoCooldownSeconds.get() * 20);
                return;
            }
            applyBarrierEffects(entity);
            return;
        }

        if (cooldownTicks > 0) {
            tag.putInt(COOLDOWN_KEY, cooldownTicks - 1);
            return;
        }

        double triggerRatio = TaczCuriosConfig.COMMON.zhenWoTriggerHpRatio.get();
        if (entity.getHealth() / entity.getMaxHealth() < triggerRatio) {
            activateBarrier(entity, stack);
        }
    }

    private static void refreshBarrierBuff(LivingEntity player, int remainingTicks) {
        player.addEffect(new MobEffectInstance(TccMobEffects.ZHEN_WO_BARRIER.get(),
            remainingTicks, 0, false, false, true));
    }

    private static void applyBarrierEffects(LivingEntity player) {
        // 治疗、减速、粉色光柱本就以 20 tick 为周期，因此整体按周期执行，
        // 避免每 tick 做一次昂贵的半径范围查询（getEntitiesOfClass）。
        if (player.tickCount % 20 != 0) return;

        double radius = TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get();
        double radiusSq = radius * radius;
        AABB sphereBox = new AABB(player.blockPosition()).inflate(radius);

        healAllies(player, radiusSq, sphereBox);

        List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class, sphereBox,
            e -> e != player && !(e instanceof Player) && !isMaid(e) && e.isAlive()
                && e.distanceToSqr(player) <= radiusSq
                && isBarrierTarget(e, player));

        if (targets.isEmpty()) return;

        int slownessDuration = TaczCuriosConfig.COMMON.zhenWoSlownessDurationSeconds.get() * 20;
        int slownessAmplifier = TaczCuriosConfig.COMMON.zhenWoSlownessAmplifier.get();
        MobEffectInstance slowness = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
            slownessDuration, slownessAmplifier, false, false, true);

        for (LivingEntity target : targets) {
            target.addEffect(slowness);
            if (target.isDeadOrDying()) continue;
            startPinkBeam(player, target);
        }
    }

    private static boolean isBarrierTarget(LivingEntity target, LivingEntity player) {
        if (target instanceof Enemy) return true;
        if (target instanceof Mob mob && mob.getTarget() == player) return true;
        return player.getLastHurtByMob() == target;
    }

    private static final int PINK_BEAM_TICKS = 20;
    private static final double PINK_BEAM_HEIGHT = 32.0;
    private static final Vector3f PINK_BEAM_COLOR = new Vector3f(1.0F, 0.55F, 0.9F);
    private static final Set<PinkBeam> ACTIVE_BEAMS = new HashSet<>();

    private static final class PinkBeam {
        final LivingEntity owner;
        final LivingEntity target;
        int age;

        PinkBeam(LivingEntity owner, LivingEntity target) {
            this.owner = owner;
            this.target = target;
        }
    }

    private static void startPinkBeam(LivingEntity owner, LivingEntity target) {
        ACTIVE_BEAMS.add(new PinkBeam(owner, target));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (ACTIVE_BEAMS.isEmpty()) return;

        Iterator<PinkBeam> it = ACTIVE_BEAMS.iterator();
        while (it.hasNext()) {
            PinkBeam beam = it.next();
            LivingEntity target = beam.target;
            if (target == null || !target.isAlive()) {
                it.remove();
                continue;
            }

            beam.age++;
            if (target.level() instanceof ServerLevel serverLevel) {
                spawnPinkPillar(serverLevel, target.getX(), target.getY(), target.getZ(), beam.age);
            }

            if (beam.age >= PINK_BEAM_TICKS) {
                it.remove();
                if (beam.owner != null && beam.owner.isAlive() && !target.isDeadOrDying()) {
                    float imaginaryResistance = (float) beam.owner.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                    float damage = (imaginaryResistance / 100.0f) * beam.owner.getMaxHealth();
                    DamageSource source = TccDamageSources.imaginaryDamage(beam.owner.level(), beam.owner);
                    TccAttributeEvents.applyImaginaryDamage(target, source, damage);
                }
            }
        }
    }

    private static void spawnPinkPillar(ServerLevel level, double x, double y, double z, int age) {
        double headY = y + (age / (double) PINK_BEAM_TICKS) * PINK_BEAM_HEIGHT;
        DustParticleOptions pink = new DustParticleOptions(PINK_BEAM_COLOR, 1.2F);
        level.sendParticles(pink, x, headY, z, 8, 0.18, 0.25, 0.18, 0.0);
    }

    /** 该实体当前是否为真我结界激活中的佩戴者（不校验距离）。服务端查集合，客户端用同步的结界 buff 判定。 */
    public static boolean isBarrierActiveWearer(LivingEntity entity) {
        if (entity == null) return false;
        if (entity.level().isClientSide) {
            return entity.hasEffect(TccMobEffects.ZHEN_WO_BARRIER.get());
        }
        // 服务端：无人处于结界激活状态时直接返回，省去 getUUID() + 集合查找。
        if (ACTIVE_BARRIER_WEARERS.isEmpty()) return false;
        return ACTIVE_BARRIER_WEARERS.contains(entity.getUUID());
    }

    public static boolean isInsideActiveBarrier(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide) return false;
        Level level = entity.level();
        double radius = TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get();
        double radiusSq = radius * radius;
        Vec3 pos = entity.position();

        for (Player player : level.players()) {
            if (isActiveBarrierWearer(player, pos, radiusSq)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isActiveBarrierWearer(LivingEntity wearer, Vec3 pos, double radiusSq) {
        if (wearer == null || !wearer.isAlive()) return false;
        ItemStack stack = CurioSearchHelper.findFirstEquippedStack(wearer,
            s -> s.getItem() instanceof ZhenWo);
        if (stack.isEmpty()) return false;
        if (stack.getOrCreateTag().getInt(BARRIER_KEY) <= 0) return false;
        return wearer.distanceToSqr(pos) <= radiusSq;
    }

    private static void healAllies(LivingEntity player, double radiusSq, AABB sphereBox) {
        List<Player> friendlyPlayers = player.level().getEntitiesOfClass(Player.class, sphereBox,
            p -> p.isAlive() && p.distanceToSqr(player) <= radiusSq);
        for (Player p : friendlyPlayers) {
            p.setHealth(p.getMaxHealth());
            p.getFoodData().setFoodLevel(20);
            p.getFoodData().setSaturation(20.0F);
        }

        for (LivingEntity maid : MaidCompat.getMaidsNear(player.level(), sphereBox,
                m -> m.isAlive() && m.distanceToSqr(player) <= radiusSq)) {
            maid.setHealth(maid.getMaxHealth());
        }
    }

    private static boolean isMaid(LivingEntity e) {
        return MaidCompat.isMaid(e);
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) return;
        MobEffectInstance effect = event.getEffectInstance();
        if (effect == null) return;
        if (effect.getEffect().getCategory() != MobEffectCategory.HARMFUL) return;
        if (!CurioSearchHelper.findFirstEquippedStack(entity, s -> s.getItem() instanceof ZhenWo).isEmpty()) {
            event.setResult(Event.Result.DENY);
        }
    }

    /** 开启结界：设结界计时、清冷却、回满血并刷新结界 buff 与效果。 */
    private static void activateBarrier(LivingEntity player, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int duration = TaczCuriosConfig.COMMON.zhenWoBarrierDurationSeconds.get() * 20;
        tag.putInt(BARRIER_KEY, duration);
        tag.putInt(COOLDOWN_KEY, 0);
        ACTIVE_BARRIER_WEARERS.add(player.getUUID());

        player.setHealth(player.getMaxHealth());

        refreshBarrierBuff(player, duration);
        applyBarrierEffects(player);
    }

    /**
     * 非结界期正门 hurt 致死兜底：仅玩家佩戴真我且不在冷却时，取消死亡并开启结界（结界已激活则回满血刷新结界）。
     * 第三方绕过 hurt 直接写血的致死不在此列——非结界期 set 层不做免死兜底。
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity player = event.getEntity();
        if (player.level().isClientSide) return;
        // 真我免死仅对玩家生效
        if (!(player instanceof Player)) return;
        ItemStack stack = CurioSearchHelper.findFirstEquippedStack(player,
            s -> s.getItem() instanceof ZhenWo);
        if (stack.isEmpty()) return;

        boolean barrierActive = stack.getOrCreateTag().getInt(BARRIER_KEY) > 0;

        // 免死与结界共用同一冷却：冷却期间免死不生效，正常死亡。
        if (!barrierActive && stack.getOrCreateTag().getInt(COOLDOWN_KEY) > 0) return;

        event.setCanceled(true);

        player.setDeltaMovement(Vec3.ZERO);
        player.hurtTime = 0;
        player.deathTime = 0;
        player.invulnerableTime = 100;

        if (barrierActive) {
            player.setHealth(player.getMaxHealth());
            refreshBarrierBuff(player, stack.getOrCreateTag().getInt(BARRIER_KEY));
        } else {
            activateBarrier(player, stack);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double resistance = TaczCuriosConfig.COMMON.zhenWoImaginaryResistance.get();
        tooltip.add(formatModifierTooltip(resistance, "%.0f",
                Component.translatable(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get().getDescriptionId()))
            .withStyle(ChatFormatting.GOLD));

        double allAttrs = TaczCuriosConfig.COMMON.zhenWoAllAttributesPercent.get() * 100;
        tooltip.add(Component.translatable("item.tcc.zhen_wo.all_attributes",
                String.format("%.0f", allAttrs))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(formatModifierTooltip(1.0, "%.0f",
                Component.translatable(Attributes.KNOCKBACK_RESISTANCE.getDescriptionId()))
            .withStyle(ChatFormatting.GOLD));

        double damageTakenFactor = TaczCuriosConfig.COMMON.zhenWoDamageTakenFactor.get() * 100;
        tooltip.add(Component.translatable("tcc.tooltip.damage_reduction",
                String.format("%.0f", damageTakenFactor))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tcc.tooltip.debuff_immunity")
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(formatEffectTooltip(MobEffects.DAMAGE_RESISTANCE,
                TaczCuriosConfig.COMMON.zhenWoResistanceLevel.get())
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("item.tcc.zhen_wo.effect.absorption",
                TaczCuriosConfig.COMMON.zhenWoAbsorptionInterval.get(),
                TaczCuriosConfig.COMMON.zhenWoAbsorptionAmount.get())
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.zhen_wo.effect.trigger",
                (int) (TaczCuriosConfig.COMMON.zhenWoTriggerHpRatio.get() * 100))
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.zhen_wo.effect.duration",
                TaczCuriosConfig.COMMON.zhenWoBarrierDurationSeconds.get(),
                TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get().intValue())
            .withStyle(ChatFormatting.RED));
        double imaginaryDamage = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null && !CurioSearchHelper.findFirstEquippedStack(wearer,
                    s -> s.getItem() instanceof ZhenWo).isEmpty()) {
                double imaginaryResistance = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                double maxHealth = wearer.getAttributeValue(Attributes.MAX_HEALTH);
                imaginaryDamage = (imaginaryResistance / 100.0) * maxHealth;
            }
        }
        tooltip.add(Component.translatable("item.tcc.zhen_wo.effect.damage",
                TaczCuriosConfig.COMMON.zhenWoSlownessAmplifier.get() + 1,
                String.format("%.2f", imaginaryDamage))
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.zhen_wo.effect.cooldown",
                TaczCuriosConfig.COMMON.zhenWoCooldownSeconds.get())
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
