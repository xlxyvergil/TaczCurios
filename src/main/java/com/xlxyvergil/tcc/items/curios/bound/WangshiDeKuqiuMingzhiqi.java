package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.ImaginaryConversionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WangshiDeKuqiuMingzhiqi extends BoundCurioItem {
    public WangshiDeKuqiuMingzhiqi(Properties properties) {
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
                stack -> stack.getItem() instanceof WangshiDeKuqiuMingzhiqi).isEmpty();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity host = slotContext.entity();
        if (!(host instanceof LivingEntity) || host.level().isClientSide) return;
        if (host.isDeadOrDying()) return;
        if (!matchesRestriction(host)) return;
        // 每 1 秒刷新一次范围内虚数侵染
        if (host.tickCount % 20 != 0) return;
        applyInfectionAura(host);
    }

    /** 每 1 秒：对光环半径内非玩家实体施加持续指定秒数的指定等级虚数侵染 */
    private void applyInfectionAura(LivingEntity host) {
        double radius = TaczCuriosConfig.COMMON.wangshiDeKuqiuMingzhiqiAuraRadius.get();
        double radiusSq = radius * radius;
        int level = TaczCuriosConfig.COMMON.wangshiDeKuqiuMingzhiqiInfectionLevel.get();
        int duration = TaczCuriosConfig.COMMON.wangshiDeKuqiuMingzhiqiInfectionDurationSeconds.get();
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
                TaczCuriosConfig.COMMON.wangshiDeKuqiuMingzhiqiAuraRadius.get().intValue(),
                TaczCuriosConfig.COMMON.wangshiDeKuqiuMingzhiqiInfectionLevel.get())
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
