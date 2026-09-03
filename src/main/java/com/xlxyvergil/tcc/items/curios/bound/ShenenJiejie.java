package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.ImaginaryConversionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShenenJiejie extends BoundCurioItem {
    public ShenenJiejie(Properties properties) {
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
                stack -> stack.getItem() instanceof ShenenJiejie).isEmpty();
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(attacker,
                stack -> stack.getItem() instanceof ShenenJiejie);
        if (equipped.isEmpty()) {
            return;
        }
        ImaginaryConversionHelper.convertToImaginary(event);
        Entity hurt = event.getHurtEntity();
        if (hurt instanceof LivingEntity target && !target.isDeadOrDying()) {
            TccAttributeEvents.applyCollapse(target, attacker);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity host = slotContext.entity();
        if (!(host instanceof LivingEntity) || host.level().isClientSide) return;
        if (host.isDeadOrDying()) return;
        if (!matchesRestriction(host)) return;
        if (host.tickCount % 20 != 0) return;
        applyInfectionAura(host);
    }

    private void applyInfectionAura(LivingEntity host) {
        double radius = TaczCuriosConfig.COMMON.shenenJiejieAuraRadius.get();
        double radiusSq = radius * radius;
        int level = TaczCuriosConfig.COMMON.shenenJiejieInfectionLevel.get();
        int duration = TaczCuriosConfig.COMMON.shenenJiejieInfectionDurationSeconds.get();
        List<LivingEntity> targets = host.level().getEntitiesOfClass(LivingEntity.class,
                new AABB(host.blockPosition()).inflate(radius),
                e -> e != host && !(e instanceof Player) && !MaidCompat.isMaid(e) && e.isAlive()
                        && e.distanceToSqr(host) <= radiusSq);
        if (targets.isEmpty()) return;
        for (LivingEntity target : targets) {
            ImaginaryConversionHelper.applyInfection(target, host, level, duration);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.discipline.key_effect",
                TaczCuriosConfig.COMMON.shenenJiejieAuraRadius.get().intValue(),
                TaczCuriosConfig.COMMON.shenenJiejieInfectionLevel.get())
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tcc.tooltip.gun_to_imaginary")
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
