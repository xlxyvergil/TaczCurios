package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.entity.ZhenWoBarrierEntity;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 逐火之蛾「真我」- 最终阶段（tcc_3rd 槽，裂隙级）。
 * <p>
 * 属性：虚数抗性 +60；全属性提升 50%（乘法）。
 * <p>
 * 特殊效果「结界」：任意形式血量小于 5% 时触发——立即恢复 100% 血量，
 * 持续 30 秒内对 128 格内非玩家实体每 tick 施加缓慢 IX（60 秒），
 * 每 tick 造成佩戴者最大血量上限的伤害（applyImaginaryDamage），冷却 60 秒。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZhenWo extends BaseCurioItem {

    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("a1b2c3d4-0001-4000-8000-000000000001");
    private static final UUID ALL_ATTRIBUTES_UUID = UUID.fromString("a1b2c3d4-0002-4000-8000-000000000002");

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

    public static boolean hasEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof ZhenWo).isEmpty();
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
    }

    /**
     * 结界状态机（服务端每 tick）：
     * 结界激活（倒计时 > 0）→ 施加缓慢 + 每 tick 虚数伤害；
     * 结界结束 → 进入冷却；
     * 冷却结束且血量 < 5% → 再次触发结界。
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

        // 结界进行中
        if (barrierTicks > 0) {
            barrierTicks--;
            tag.putInt(BARRIER_KEY, barrierTicks);
            // 保证脚下地面特效存在（跟随玩家）
            ZhenWoBarrierEntity.ensureActive(player.level(), player, tag,
                TaczCuriosConfig.COMMON.zhenWoBarrierDurationSeconds.get() * 20);
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
            player.setHealth(player.getMaxHealth()); // 立即恢复 100% 血量
            tag.putInt(BARRIER_KEY, TaczCuriosConfig.COMMON.zhenWoBarrierDurationSeconds.get() * 20);
            // 生成脚下地面特效实体
            ZhenWoBarrierEntity.ensureActive(player.level(), player, tag,
                TaczCuriosConfig.COMMON.zhenWoBarrierDurationSeconds.get() * 20);
            applyBarrierEffects(player);
        }
    }

    /** 单 tick 结界效果：对结界范围（直径 zhenWoBarrierRadius）内非玩家实体施加缓慢 IX；每 tick 造成最大血量虚数伤害 */
    private static void applyBarrierEffects(Player player) {
        double radius = TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get() / 2.0D; // 配置值为直径，÷2 得半径
        double radiusSq = radius * radius;
        List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class,
            new AABB(player.blockPosition()).inflate(radius),
            e -> e != player && !(e instanceof Player) && e.isAlive()
                && e.distanceToSqr(player) <= radiusSq);

        if (targets.isEmpty()) return;

        // 每 tick：缓慢 IX（60 秒）
        int slownessDuration = TaczCuriosConfig.COMMON.zhenWoSlownessDurationSeconds.get() * 20;
        int slownessAmplifier = TaczCuriosConfig.COMMON.zhenWoSlownessAmplifier.get();
        MobEffectInstance slowness = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
            slownessDuration, slownessAmplifier, false, false, true);
        for (LivingEntity target : targets) {
            target.addEffect(slowness);
        }

        // 每 tick：造成佩戴者最大血量上限的伤害（applyImaginaryDamage）
        float damage = (float) (player.getMaxHealth() * TaczCuriosConfig.COMMON.zhenWoDamagePercent.get());
        DamageSource source = TccDamageSources.imaginaryDamage(player.level(), player);
        for (LivingEntity target : targets) {
            if (target.isDeadOrDying()) continue;
            TccAttributeEvents.applyImaginaryDamage(target, source, damage);
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

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.zhen_wo.effect",
                (int) (TaczCuriosConfig.COMMON.zhenWoTriggerHpRatio.get() * 100),
                TaczCuriosConfig.COMMON.zhenWoBarrierDurationSeconds.get(),
                TaczCuriosConfig.COMMON.zhenWoBarrierRadius.get(),
                TaczCuriosConfig.COMMON.zhenWoSlownessAmplifier.get() + 1,
                TaczCuriosConfig.COMMON.zhenWoCooldownSeconds.get())
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
