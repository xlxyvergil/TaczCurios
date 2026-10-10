package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = TaczCurios.MODID)
public class HeiyuanBaihua extends BoundCurioItem {
    public HeiyuanBaihua(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return null;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof HeiyuanBaihua).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        // 虚数伤害由本饰品自身发起，直接跳过，避免重复施加侵染（防重入由 applyImaginaryDamage 兜底）。
        if (event.getSource().is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) return;
        if (!TccAttributeEvents.isActiveAttackSource(event.getSource())) return;
        if (event.getEntity().level().isClientSide) return;

        LivingEntity target = event.getEntity();
        if (target.isDeadOrDying()) return;

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker)) return;
        // 收束：仅玩家与女仆可触发黑渊白花的效果，其他实体直接跳过以削减开销
        if (!(attacker instanceof Player) && !MaidCompat.isMaid(attacker)) return;
        if (target == attacker) return;
        if (!isEquipped(attacker)) return;

        double imaginaryResistance = attacker.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE);
        float damage = (float) (attacker.getMaxHealth() * (imaginaryResistance / 100.0)
            * TaczCuriosConfig.COMMON.heiyuanBaihuaImaginaryDamageScale.get());
        if (damage <= 0) return;

        TccAttributeEvents.applyImaginaryDamage(target,
            TccDamageSources.imaginaryDamageMelee(target.level(), attacker), damage);

        // 近战路径只施加虚数侵染；崩解改为仅枪械命中时由 onGunHurtPost 施加
        TccAttributeEvents.applyInfection(target, attacker,
            TaczCuriosConfig.COMMON.specialImaginaryInfectionMaxLevel.get());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        // 枪械命中：TACZ 子弹普通命中的 LivingIncomingDamageEvent 伤害源无法被 isActiveAttackSource 识别，
        // 因此黑渊白花需额外监听枪击事件才能对普通枪击施加虚数伤害/侵染/崩解。
        if (event.getLogicalSide().isClient()) return;
        if (!(event.getHurtEntity() instanceof LivingEntity target)) return;
        if (target.isDeadOrDying()) return;

        LivingEntity attacker = event.getAttacker();
        if (attacker == null) return;
        // 收束：仅玩家与女仆可触发黑渊白花的效果，其他实体直接跳过以削减开销
        if (!(attacker instanceof Player) && !MaidCompat.isMaid(attacker)) return;
        if (!isEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (target == attacker) return;

        double imaginaryResistance = attacker.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE);
        float damage = (float) (attacker.getMaxHealth() * (imaginaryResistance / 100.0)
            * TaczCuriosConfig.COMMON.heiyuanBaihuaImaginaryDamageScale.get());
        if (damage > 0) {
            TccAttributeEvents.applyImaginaryDamage(target,
                TccDamageSources.imaginaryDamage(target.level(), attacker), damage);
        }

        // 先施加侵染，再施加剧增崩解，确保崩解结算时目标带侵染
        TccAttributeEvents.applyInfection(target, attacker,
            TaczCuriosConfig.COMMON.specialImaginaryInfectionMaxLevel.get());

        TccAttributeEvents.applyCollapse(target, attacker);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));
        double resistancePercent = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null && isEquipped(wearer)) {
                resistancePercent = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE);
            }
        }
        double damageScale = TaczCuriosConfig.COMMON.heiyuanBaihuaImaginaryDamageScale.get();
        tooltip.add(Component.translatable("item.tcc.heiyuan_baihua.effect",
                (int) (resistancePercent * damageScale))
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendGunImaginaryCollapse(tooltip);
        appendBoundPlayer(stack, tooltip);
    }
}
