package com.xlxyvergil.tcc.items.curios;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 逐火之蛾「真我」- 最终阶段（tcc_3rd 槽，裂隙级）。
 * <p>
 * 属性：虚数抗性 +60；全属性提升 50%（乘法）；佩戴期间获得创造飞行能力。
 * <p>
 * 特殊效果「结界」：任意形式血量小于 20% 时触发——立即恢复 100% 血量，
 * 持续 30 秒内，对结界（球形）内玩家和车万女仆每秒恢复 100% 血量，
 * 对结界内敌对怪物、或对佩戴者产生仇恨/造成过伤害的实体每秒施加缓慢 IX（60 秒）并造成佩戴者最大血量上限的伤害，冷却 60 秒。
 * 佩戴期间常驻免疫所有有害效果（debuff），与结界是否激活无关。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZhenWo extends BaseCurioItem {

    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("2df6423f-42bc-4629-8624-ebb8cf4ff4c8");
    private static final UUID ALL_ATTRIBUTES_UUID = UUID.fromString("8f851e69-0217-4e14-af7f-ee655b4a1cc7");

    /** 结界倒计时（剩余 tick），存于饰品 NBT，随物品持久 */
    private static final String BARRIER_KEY = "tcc_zhen_wo_barrier";
    /** 冷却倒计时（剩余 tick） */
    private static final String COOLDOWN_KEY = "tcc_zhen_wo_cooldown";

    public ZhenWo(Properties properties) {
        super(properties);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        if (slotContext.entity() instanceof Player player) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.getBoolean("IsBound")) {
                tag.putBoolean("IsBound", true);
                tag.putString("BoundPlayer", player.getStringUUID());
                tag.putString("BoundPlayerName", player.getGameProfile().getName());
            }
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerUUID = tag.getString("BoundPlayer");
            if (slotContext.entity() instanceof Player player) {
                return player.getStringUUID().equals(boundPlayerUUID);
            }
            return false;
        }
        return super.canEquip(slotContext, stack);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return null; // 无武器限制，空手也能触发
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                TaczCuriosConfig.COMMON.zhenWoImaginaryResistance.get(), IMAGINARY_RESISTANCE_UUID,
                "tcc.zhen_wo.imaginary_resistance", AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_UUID,
                "tcc.zhen_wo.all_attributes", TaczCuriosConfig.COMMON.zhenWoAllAttributesPercent.get(),
                AttributeModifier.Operation.MULTIPLY_BASE);
            // 常驻比例减伤：佩戴期间始终保留此比例伤害，对标准 hurt 与直接 setHealth 扣血均生效
            DamageResistanceHelper.setDamageReduction(livingEntity,
                (float) (1 - TaczCuriosConfig.COMMON.zhenWoDamageTakenFactor.get()));
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
        DamageResistanceHelper.clearDamageCap(livingEntity);
        DamageResistanceHelper.clearDamageReduction(livingEntity);
    }

    /**
     * 结界状态机（服务端每 tick）：
     * 结界激活（倒计时 > 0）→ 施加缓慢 + 每 tick 虚数伤害；
     * 结界结束 → 进入冷却；
     * 冷却结束且血量 < 5% → 再次触发结界。
     * 结界激活期间维持标记 buff（ZhenWoBarrierEffect），客户端据此渲染脚下地面特效。
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide) return;
        if (!(entity instanceof Player player)) return;

        if (player.isDeadOrDying()) return;

        CompoundTag tag = stack.getOrCreateTag();
        int barrierTicks = tag.getInt(BARRIER_KEY);
        int cooldownTicks = tag.getInt(COOLDOWN_KEY);

        // 真我结界持续期间减伤 100%（保留比例为 0）；其余时间维持配置的减伤比例
        float retain = barrierTicks > 0 ? 0.0F
            : (float) (1 - TaczCuriosConfig.COMMON.zhenWoDamageTakenFactor.get());
        DamageResistanceHelper.setDamageReduction(player, retain);

        // 结界进行中
        if (barrierTicks > 0) {
            barrierTicks--;
            tag.putInt(BARRIER_KEY, barrierTicks);
            // 每 5 tick 以剩余结界时长为 buff 时长续期，客户端据此渲染脚下特效并淡出
            if (player.tickCount % 5 == 0) {
                refreshBarrierBuff(player, barrierTicks);
            }
            if (barrierTicks <= 0) {
                tag.putInt(COOLDOWN_KEY, TaczCuriosConfig.COMMON.zhenWoCooldownSeconds.get() * 20);
                return;
            }
            applyBarrierEffects(player);
            return;
        }

        // 冷却中
        if (cooldownTicks > 0) {
            tag.putInt(COOLDOWN_KEY, cooldownTicks - 1);
            return;
        }

        // 任意形式血量小于 5% 时触发
        double triggerRatio = TaczCuriosConfig.COMMON.zhenWoTriggerHpRatio.get();
        if (player.getHealth() / player.getMaxHealth() < triggerRatio) {
            activateBarrier(player, stack);
        }
    }

    /** 施加/续期结界标记 buff（时长 = 剩余结界 tick，客户端据此渲染脚下地面特效） */
    private static void refreshBarrierBuff(Player player, int remainingTicks) {
        player.addEffect(new MobEffectInstance(TccMobEffects.ZHEN_WO_BARRIER.get(),
            remainingTicks, 0, false, false, true));
    }

    /**
     * 单 tick 结界效果（球形范围，半径 zhenWoBarrierRadius）：
     * <ul>
     *   <li>每秒：对结界内玩家与车万女仆恢复 100% 血量（debuff 免疫由佩戴常驻 {@link #onEffectApplicable} 提供，结界不再清 debuff）。</li>
     *   <li>每秒：对结界内敌对怪物、或对佩戴者产生仇恨/造成过伤害的实体（见 {@link #isBarrierTarget}，
     *   不含玩家与车万女仆）施加缓慢 IX，并造成最大血量虚数伤害。</li>
     * </ul>
     */
    private static void applyBarrierEffects(Player player) {
        double radius = TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get(); // 配置值即为生效半径
        double radiusSq = radius * radius;
        AABB sphereBox = new AABB(player.blockPosition()).inflate(radius);

        // 每秒：结界内玩家与车万女仆恢复 100% 血量
        if (player.tickCount % 20 == 0) {
            healAllies(player, radiusSq, sphereBox);
        }

        // 对结界内敌对怪物、或对佩戴者产生仇恨/造成过伤害的实体：施加缓慢 + 造成虚数伤害
        List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class, sphereBox,
            e -> e != player && !(e instanceof Player) && !isMaid(e) && e.isAlive()
                && e.distanceToSqr(player) <= radiusSq
                && isBarrierTarget(e, player));

        if (targets.isEmpty()) return;

        // 每秒：对结界内实体施加缓慢 IX，并登记粉色光柱（先升起 1 秒光柱、再结算虚数伤害）
        if (player.tickCount % 20 == 0) {
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
    }

    /**
     * 判断实体是否应被结界锁定（均为原版判定规则）：
     * <ul>
     *   <li>敌对怪物（{@link Enemy}）；</li>
     *   <li>对佩戴者产生仇恨的实体（{@link Mob#getTarget()} == 佩戴者，含被激怒的中立生物）；</li>
     *   <li>对佩戴者造成过伤害的实体（{@link LivingEntity#getLastHurtByMob()} == 佩戴者）。</li>
     * </ul>
     */
    private static boolean isBarrierTarget(LivingEntity target, Player player) {
        if (target instanceof Enemy) return true;
        if (target instanceof Mob mob && mob.getTarget() == player) return true;
        return player.getLastHurtByMob() == target;
    }

    // ===== 粉色光柱特效 =====
    /** 光柱持续时长（tick）：20 = 1 秒 */
    private static final int PINK_BEAM_TICKS = 20;
    /** 光柱高度（格） */
    private static final double PINK_BEAM_HEIGHT = 32.0;
    /** 粉色粒子颜色（RGB，0~1） */
    private static final Vector3f PINK_BEAM_COLOR = new Vector3f(1.0F, 0.55F, 0.9F);
    /** 进行中的粉色光柱任务，由 {@link #onServerTick} 每 tick 推进并在 1 秒后结算伤害 */
    private static final Set<PinkBeam> ACTIVE_BEAMS = new HashSet<>();

    /** 粉色光柱任务：记录佩戴者、伤害目标与已推进的 tick 数。 */
    private static final class PinkBeam {
        final Player owner;
        final LivingEntity target;
        int age;

        PinkBeam(Player owner, LivingEntity target) {
            this.owner = owner;
            this.target = target;
        }
    }

    /** 登记一道粉色光柱：1 秒（{@link #PINK_BEAM_TICKS} tick）后对目标结算佩戴者最大血量虚数伤害 */
    private static void startPinkBeam(Player owner, LivingEntity target) {
        ACTIVE_BEAMS.add(new PinkBeam(owner, target));
    }

    /** 每 tick 推进粉色光柱：跟随目标坐标从下向上生长粒子，满 1 秒后结算虚数伤害（先光柱、后伤害） */
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
                // 光柱结束，结算伤害：佩戴者仍在场且目标仍存活
                if (beam.owner != null && beam.owner.isAlive() && !target.isDeadOrDying()) {
                    // 结界伤害 = 佩戴者虚数伤害抗性 × 最大生命值
                    float imaginaryResistance = (float) beam.owner.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                    float damage = imaginaryResistance * beam.owner.getMaxHealth();
                    DamageSource source = TccDamageSources.imaginaryDamage(beam.owner.level(), beam.owner);
                    TccAttributeEvents.applyImaginaryDamage(target, source, damage);
                }
            }
        }
    }

    /** 在指定位置生成一段粉色光柱粒子：age 越大、粒子簇越高（模拟从下向上生长） */
    private static void spawnPinkPillar(ServerLevel level, double x, double y, double z, int age) {
        double headY = y + (age / (double) PINK_BEAM_TICKS) * PINK_BEAM_HEIGHT;
        DustParticleOptions pink = new DustParticleOptions(PINK_BEAM_COLOR, 1.2F);
        level.sendParticles(pink, x, headY, z, 8, 0.18, 0.25, 0.18, 0.0);
    }

    /**
     * 判断实体是否处于「任一佩戴真我且结界正在激活（BARRIER_KEY > 0）」的玩家结界球形范围内。
     * <p>
     * 球形判定与 {@link #applyBarrierEffects} 保持一致：以佩戴者位置为球心、zhenWoBarrierRadius
     * 为半径，用 distanceToSqr 判定。仅服务端有意义（客户端直接返回 false）。
     * 供实体死亡时抑制经验球生成等「结界内实体」判定复用。
     */
    public static boolean isInsideActiveBarrier(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide) return false;
        Level level = entity.level();
        double radius = TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get();
        double radiusSq = radius * radius;
        Vec3 pos = entity.position();

        for (Player player : level.players()) {
            if (player == null || !player.isAlive()) continue;
            ItemStack stack = CurioSearchHelper.findFirstEquippedStack(player,
                s -> s.getItem() instanceof ZhenWo);
            if (stack.isEmpty()) continue;
            if (stack.getOrCreateTag().getInt(BARRIER_KEY) <= 0) continue;
            if (player.distanceToSqr(pos) <= radiusSq) {
                return true;
            }
        }
        return false;
    }

    /** 每秒：对结界内玩家与车万女仆恢复 100% 血量，并为玩家回满饱食度（debuff 免疫由佩戴常驻 {@link #onEffectApplicable} 提供） */
    private static void healAllies(Player player, double radiusSq, AABB sphereBox) {
        List<Player> friendlyPlayers = player.level().getEntitiesOfClass(Player.class, sphereBox,
            p -> p.isAlive() && p.distanceToSqr(player) <= radiusSq);
        for (Player p : friendlyPlayers) {
            p.setHealth(p.getMaxHealth());
            p.getFoodData().setFoodLevel(20);
            p.getFoodData().setSaturation(20.0F);
        }

        if (ModList.get().isLoaded("touhou_little_maid")) {
            List<EntityMaid> maids = player.level().getEntitiesOfClass(EntityMaid.class, sphereBox,
                m -> m.isAlive() && m.distanceToSqr(player) <= radiusSq);
            for (EntityMaid maid : maids) {
                maid.setHealth(maid.getMaxHealth());
            }
        }
    }

    /** 判断实体是否为车万女仆（touhou_little_maid） */
    private static boolean isMaid(LivingEntity e) {
        return ModList.get().isLoaded("touhou_little_maid") && e instanceof EntityMaid;
    }

    /**
     * 常驻 debuff 免疫（主动拦截）：
     * 只要佩戴者装备了真我，其即将受到的有害效果（HARMFUL）在上身前即被拒绝，与结界是否激活无关。
     * <p>
     * 仅拦截 HARMFUL 效果；佩戴者的标记 buff（{@code ZhenWoBarrierEffect}，NEUTRAL）不会被误拦。
     */
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

    /**
     * 统一触发结界：立即恢复 100% 血量、激活结界（施加标记 buff 并对范围内实体生效）。
     * 供低血量自然触发（{@link #curioTick}）与死亡复活（{@link #onLivingDeath}）共用。
     * 不再施加生命恢复 IX：结界持续期间每秒对结界内玩家/女仆恢复 100% 血量（debuff 免疫为佩戴常驻）。
     */
    private static void activateBarrier(Player player, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int duration = TaczCuriosConfig.COMMON.zhenWoBarrierDurationSeconds.get() * 20;
        tag.putInt(BARRIER_KEY, duration);
        tag.putInt(COOLDOWN_KEY, 0);

        player.setHealth(player.getMaxHealth()); // 立即恢复 100% 血量

        refreshBarrierBuff(player, duration);
        applyBarrierEffects(player);
    }

    /**
     * 死亡复活保底：玩家死亡时（含血量直接归零、未触发低血结界的情况），
     * 在死亡点取消死亡并立即原地复活。
     * <ul>
     *   <li>结界持续期间：立即回满血复活，但不重置结界剩余时长——直到结界持续结束才停止复活保护。</li>
     *   <li>非结界期间：触发死亡保底，立即激活结界（重置结界时长与冷却）。</li>
     * </ul>
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        ItemStack stack = CurioSearchHelper.findFirstEquippedStack(player,
            s -> s.getItem() instanceof ZhenWo);
        if (stack.isEmpty()) return;

        boolean barrierActive = stack.getOrCreateTag().getInt(BARRIER_KEY) > 0;

        // 取消死亡，玩家留在死亡点（原地复活）
        event.setCanceled(true);

        // 清除受伤/死亡动画状态，防止客户端残留；施加短无敌帧，避免复活瞬间被连续伤害秒杀
        player.setDeltaMovement(Vec3.ZERO);
        player.hurtTime = 0;
        player.deathTime = 0;
        player.invulnerableTime = 100;

        if (barrierActive) {
            // 结界持续期间：立即原地复活，但不重置结界剩余时长（直到结界持续结束）
            player.setHealth(player.getMaxHealth());
            refreshBarrierBuff(player, stack.getOrCreateTag().getInt(BARRIER_KEY));
        } else {
            // 非结界期间死亡：触发死亡保底，立即激活结界
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

        double damageTakenFactor = TaczCuriosConfig.COMMON.zhenWoDamageTakenFactor.get() * 100;
        tooltip.add(Component.translatable("tcc.tooltip.damage_reduction",
                String.format("%.0f", damageTakenFactor))
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
            Player player = Minecraft.getInstance().player;
            if (player != null && !CurioSearchHelper.findFirstEquippedStack(player,
                    s -> s.getItem() instanceof ZhenWo).isEmpty()) {
                double imaginaryResistance = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                double maxHealth = player.getAttributeValue(Attributes.MAX_HEALTH);
                imaginaryDamage = imaginaryResistance * maxHealth;
            }
        }
        tooltip.add(Component.translatable("item.tcc.zhen_wo.effect.damage",
                TaczCuriosConfig.COMMON.zhenWoSlownessAmplifier.get() + 1,
                String.format("%.2f", imaginaryDamage))
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.zhen_wo.effect.cooldown",
                TaczCuriosConfig.COMMON.zhenWoCooldownSeconds.get())
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
