package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.ImaginaryInfectionHelper;
import net.minecraft.ChatFormatting;
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
import top.theillusivec4.curios.api.SlotContext;

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
        return null; // 无武器限制，空手也能触发
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 无常驻属性
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        // 无常驻属性
    }

    public static boolean hasEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof HeiyuanBaihua).isEmpty();
    }

    /**
     * 佩戴者每次造成伤害时（覆盖近战/枪械/爆炸等），附加等同于当前血量 100% 的虚数伤害。
     * applyImaginaryDamage 走 setHealth 直伤，不触发本事件，因此不会递归。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;

        LivingEntity target = event.getEntity();
        if (target.isDeadOrDying()) return;

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker)) return;
        if (target == attacker) return; // 排除自伤
        if (!hasEquipped(attacker)) return;

        float damage = (float) (attacker.getHealth() * TaczCuriosConfig.COMMON.heiyuanBaihuaDamagePercent.get());
        if (damage <= 0) return;

        TccAttributeEvents.applyImaginaryDamage(target,
            TccDamageSources.imaginaryDamage(target.level(), attacker), damage);

        // 附加虚数侵染效果，最高等级与「天火劫灭·无烬终焉」一致（不触发虚数崩解）
        TccAttributeEvents.applyInfection(target, attacker,
            new ImaginaryInfectionHelper.InfectionInfo(
                TaczCuriosConfig.COMMON.endlessImaginaryInfectionMaxLevel.get(), false));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.heiyuan_baihua.effect",
                (int) (TaczCuriosConfig.COMMON.heiyuanBaihuaDamagePercent.get() * 100))
            .withStyle(ChatFormatting.RED));

        // 附加虚数侵染，最高等级与「天火劫灭·无烬终焉」一致
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse.inflection_max",
                String.format("%d", TaczCuriosConfig.COMMON.endlessImaginaryInfectionMaxLevel.get()))
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
