package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class ChamberPrime extends TccCurioItem {
    private static final ResourceLocation DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "chamber_prime_9ab0de22_590c");
    
    
    public ChamberPrime(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(DAMAGE_ID, stack.getItem());
        boolean shouldApply = matchesRestriction(livingEntity) && GunTypeChecker.isHoldingGunWithFullMagazine(livingEntity);
        
        if (shouldApply) {
            double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.chamberPrimeSniperDamageBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageBoost, DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, DAMAGE_ID);
        }
        
        updateTaczCache(livingEntity);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, DAMAGE_ID);
        updateTaczCache(livingEntity);
    }

    private void updateTaczCache(LivingEntity livingEntity) {
        ItemStack mainHandItem = livingEntity.getMainHandItem();
        if (mainHandItem.getItem() instanceof IGun) {
            AttachmentPropertyManager.postChangeEvent(livingEntity, mainHandItem);
        }
    }


    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("sniper");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.chamberPrimeSniperDamageBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.chamber_prime.effect", String.format("%+.0f", damageBoost))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

    }
}