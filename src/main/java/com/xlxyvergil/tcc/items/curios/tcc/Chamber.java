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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Chamber extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID DAMAGE_UUID = UUID.fromString("0d407ca4-24c0-4db7-bc3a-f7d92ab8f2ed");
    
    // 修饰符名称
    private static final String DAMAGE_NAME = "tcc.chamber.damage";
    
    public Chamber(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(DAMAGE_UUID, stack.getItem());
        boolean shouldApply = matchesRestriction(livingEntity) && GunTypeChecker.isHoldingGunWithFullMagazine(livingEntity);

        if (shouldApply) {
            double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.chamberSniperDamageBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageBoost, DAMAGE_UUID, DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, DAMAGE_UUID);
        }

        updateTaczCache(livingEntity);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, DAMAGE_UUID);
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.chamberSniperDamageBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.chamber.effect", String.format("%+.0f", damageBoost))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }
    
}