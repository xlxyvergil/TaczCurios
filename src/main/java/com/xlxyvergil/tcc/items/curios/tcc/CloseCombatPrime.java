package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

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
public class CloseCombatPrime extends TccCurioItem {
    private static final ResourceLocation SHOTGUN_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "close_combat_prime_fa19535c_c5b0");
    
    public CloseCombatPrime(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(SHOTGUN_DAMAGE_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.closeCombatPrimeShotgunDamageBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN, damageBoost, SHOTGUN_DAMAGE_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN, SHOTGUN_DAMAGE_ID);
    }
    

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.closeCombatPrimeShotgunDamageBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.close_combat_prime.effect", String.format("%+.0f", damageBoost))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("shotgun");
    }
    
}