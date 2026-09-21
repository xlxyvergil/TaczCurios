package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.ImaginaryConversionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.List;

@EventBusSubscriber(modid = TaczCurios.MODID)
public class LaZhiYan extends BoundCurioItem {
    private static float damageMultiplier() {
        return TaczCuriosConfig.COMMON.laZhiYanDamageMultiplier.get().floatValue();
    }

    public LaZhiYan(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("shotgun");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof LaZhiYan).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        // 仅响应真正的主动攻击，过滤 FastHurt 再入的强制子伤害，避免崩解/倍率被子伤害重复触发
        if (!TccAttributeEvents.isActiveAttackSource(event.getSource())) return;
        Entity attackerEntity = event.getSource().getEntity();
        if (!(attackerEntity instanceof LivingEntity player) || player.level().isClientSide) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(player,
                stack -> stack.getItem() instanceof LaZhiYan);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((LaZhiYan) equipped.getItem()).matchesRestriction(player)) {
            return;
        }
        if (player.getRandom().nextDouble() < ImaginaryResistanceHelper.getResistanceProbability(player)) {
            event.setAmount(event.getAmount() * damageMultiplier());
        }
        LivingEntity target = event.getEntity();
        if (!target.isDeadOrDying()) {
            TccAttributeEvents.applyCollapse(target, player);
        }
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(attacker,
                stack -> stack.getItem() instanceof LaZhiYan);
        if (equipped.isEmpty()) {
            return;
        }
        ImaginaryConversionHelper.convertToImaginary(event);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.dream.key_effect",
                String.format("%.1f", damageMultiplier()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tcc.tooltip.gun_to_imaginary")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        appendAlwaysImaginaryCollapse(tooltip);
        appendBoundPlayer(stack, tooltip);
    }
}
