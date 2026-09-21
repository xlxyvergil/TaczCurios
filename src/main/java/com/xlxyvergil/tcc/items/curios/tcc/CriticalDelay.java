package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;
import com.xlxyvergil.tcc.util.GunTypeChecker;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class CriticalDelay extends TccCurioItem {
    private static final ResourceLocation CRIT_CHANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "critical_delay_3984a595_b819");
    private static final ResourceLocation FIRE_RATE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "critical_delay_e061a849_2bc7");


    public CriticalDelay(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(CRIT_CHANCE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(FIRE_RATE_ID, stack.getItem());
        // 仅在主手持有符合武器类型（任意枪械）时才生效；切换武器时由 GunSwitchEventHandler 触发 refreshEffects 重新评估
        if (!matchesRestriction(livingEntity)) return;

        double critChanceBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.criticalDelayCritChanceBoost.get());
        double fireRateReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.criticalDelayFireRateReduction.get());

        AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, critChanceBoost, CRIT_CHANCE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, fireRateReduction, FIRE_RATE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ROUNDS_PER_MINUTE, FIRE_RATE_ID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return GunTypeChecker.ALL_GUN_TYPES_LIST;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double critChanceBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.criticalDelayCritChanceBoost.get() ) * 100;
        double fireRateReduction = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.criticalDelayFireRateReduction.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.critical_delay.effect",
                String.format("%+.0f", critChanceBoost), String.format("%+.0f", fireRateReduction))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));
        
    }

}
