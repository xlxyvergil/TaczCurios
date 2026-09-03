package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.ImaginaryInfectionHelper;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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

    public static boolean hasEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof HeiyuanBaihua).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!TccAttributeEvents.isActiveAttackSource(event.getSource())) return;
        if (event.getEntity().level().isClientSide) return;

        LivingEntity target = event.getEntity();
        if (target.isDeadOrDying()) return;

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker)) return;
        if (target == attacker) return;
        if (!hasEquipped(attacker)) return;

        double imaginaryResistance = attacker.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        float damage = (float) (attacker.getMaxHealth() * (imaginaryResistance / 100.0)
            * TaczCuriosConfig.COMMON.heiyuanBaihuaImaginaryDamageScale.get());
        if (damage <= 0) return;

        TccAttributeEvents.applyImaginaryDamage(target,
            TccDamageSources.imaginaryDamage(target.level(), attacker), damage);

        // 先施加侵染，再施加剧增崩解，确保崩解结算时目标带侵染
        TccAttributeEvents.applyInfection(target, attacker,
            TaczCuriosConfig.COMMON.specialImaginaryInfectionMaxLevel.get());

        TccAttributeEvents.applyCollapse(target, attacker);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));
        double resistancePercent = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null && hasEquipped(wearer)) {
                resistancePercent = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            }
        }
        double damageScale = TaczCuriosConfig.COMMON.heiyuanBaihuaImaginaryDamageScale.get();
        tooltip.add(Component.translatable("item.tcc.heiyuan_baihua.effect",
                (int) (resistancePercent * damageScale))
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendAlwaysImaginaryCollapse(tooltip);
        appendBoundPlayer(stack, tooltip);
    }
}
