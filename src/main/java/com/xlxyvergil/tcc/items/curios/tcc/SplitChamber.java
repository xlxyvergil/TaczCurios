package com.xlxyvergil.tcc.items.curios.tcc;


import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class SplitChamber extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID AMMO_UUID = UUID.fromString("7ee8eee4-ae89-490c-83d1-1392a6a71aa7");
    
    // 修饰符名
    private static final String AMMO_NAME = "tcc.split_chamber.bullet_count";
    
    public SplitChamber(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(AMMO_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double ammoBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.splitChamberBulletCountBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_COUNT, ammoBoost, AMMO_UUID, AMMO_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_COUNT, AMMO_UUID);
    }
    
    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double ammoBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.splitChamberBulletCountBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.split_chamber.effect", String.format("%+.0f", ammoBoost))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }
    

}
