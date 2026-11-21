package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.MobEffectPoolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Eden extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("2d9f7e4b-6a1c-4b8e-9f2d-7c3e1a5b8d64");
    private static final UUID BASE_GUN_DAMAGE_UUID = UUID.fromString("3f6c2b9a-7d4e-4f1a-b8c5-2e9a0d7f6c31");

    private static double auraRange() {
        return TaczCuriosConfig.COMMON.edenAuraRange.get();
    }

    /** 技能冷却（秒）：由按键触发，冷却期内再次按键只提示剩余时间。 */
    public static int skillCooldownSeconds() {
        return TaczCuriosConfig.COMMON.edenIntervalSeconds.get();
    }

    private static int buffDuration() {
        return TaczCuriosConfig.COMMON.edenBuffDurationSeconds.get() * 20;
    }

    private static int buffAmplifier() {
        return TaczCuriosConfig.COMMON.edenBuffAmplifier.get();
    }

    public Eden(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Eden);
        CompoundTag tag = equipped.getTag();
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                total, IMAGINARY_RESISTANCE_UUID,
                "tcc.eden.imaginary_resistance", AttributeModifier.Operation.ADDITION);
        // 基乘算法：基础枪械伤害倍率降低（默认 -0.8 = 降低80%）
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE,
                TaczCuriosConfig.COMMON.edenGunDamageReduction.get(),
                BASE_GUN_DAMAGE_UUID, "tcc.eden.gun_damage", AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, BASE_GUN_DAMAGE_UUID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("pistol");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof Eden).isEmpty();
    }

    /**
     * 结算一次光环：以佩戴者为中心，为范围内的玩家/女仆随机施加一个正面 buff。
     *
     * 触发完全由玩家按键决定（见 EdenSeriesSkillHandler），这里不再自己判定冷却。
     */
    public static void applyAura(ServerLevel level, LivingEntity wearer) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(wearer,
                stack -> stack.getItem() instanceof Eden);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((Eden) equipped.getItem()).matchesRestriction(wearer)) {
            return;
        }
        MobEffect effect = MobEffectPoolHelper.randomBeneficial(wearer.getRandom());
        // 以佩戴者为中心扫描实体，对范围内的玩家和女仆施加 buff
        AABB box = wearer.getBoundingBox().inflate(auraRange());
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box,
                entity -> entity.isAlive() && MaidCompat.isPlayerOrMaid(entity));
        for (LivingEntity target : targets) {
            if (wearer.distanceToSqr(target) > auraRange() * auraRange()) {
                continue;
            }
            MobEffectPoolHelper.applyEffect(target, effect, buffDuration(), buffAmplifier(), wearer);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        appendImaginaryResistance(stack, tooltip);
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.edenGunDamageReduction.get() * 100,
                "%.0f%%", Component.translatable(AttributeHelper.BULLET_GUNDAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.golden.curio_effect.line1")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.golden.curio_effect.line2",
                (int) auraRange(), buffAmplifier() + 1)
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.golden.curio_effect.line3",
                (int) auraRange(), buffAmplifier() + 1)
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.golden.curio_effect.cooldown",
                skillCooldownSeconds())
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tcc.tooltip.suffocation_drowning_cramming_immunity")
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
