package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.GunShootEvent;
import com.xlxyvergil.tcc.TaczCurios;
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
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Eden extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("2d9f7e4b-6a1c-4b8e-9f2d-7c3e1a5b8d64");
    private static final UUID BASE_GUN_DAMAGE_UUID = UUID.fromString("3f6c2b9a-7d4e-4f1a-b8c5-2e9a0d7f6c31");

    /** 攻击触发冷却存储键：记录该佩戴者下次可触发的游戏刻。 */
    private static final String COOLDOWN_KEY = "tcc_eden_aura_next";

    private static double auraRange() {
        return TaczCuriosConfig.COMMON.edenAuraRange.get();
    }

    private static long interval() {
        return (long) TaczCuriosConfig.COMMON.edenIntervalSeconds.get() * 20L;
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
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(BASE_GUN_DAMAGE_UUID, stack.getItem());
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
    protected void removeEffects(LivingEntity livingEntity) {
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunShoot(GunShootEvent event) {
        // 开枪事件在客户端与服务端都会触发，只在服务端处理
        if (event.getLogicalSide() != LogicalSide.SERVER) {
            return;
        }
        LivingEntity shooter = event.getShooter();
        if (!(shooter.level() instanceof ServerLevel level)) {
            return;
        }
        // 冷却检查提前：冷却未结束时直接跳过，省去后续饰品查询与限制判定
        long gameTime = level.getGameTime();
        if (shooter.getPersistentData().getLong(COOLDOWN_KEY) > gameTime) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(shooter,
                stack -> stack.getItem() instanceof Eden);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((Eden) equipped.getItem()).matchesRestriction(shooter)) {
            return;
        }
        shooter.getPersistentData().putLong(COOLDOWN_KEY, gameTime + interval());
        applyAura(level, shooter);
    }

    private static void applyAura(ServerLevel level, LivingEntity wearer) {
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
        tooltip.add(Component.translatable("item.tcc.golden.curio_effect",
                (int) auraRange(), buffAmplifier() + 1)
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
