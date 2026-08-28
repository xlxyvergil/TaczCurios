package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import java.util.UUID;



public class LimitSpeed extends TccCurioItem {
    private static final UUID AMMO_SPEED_UUID = UUID.fromString("ad27e195-8647-4497-8792-9720043e1e95");
    
    private static final String AMMO_SPEED_NAME = "tcc.limit_speed.ammo_speed";
    
    public LimitSpeed(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double ammoSpeedBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.limitSpeedBulletSpeedBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.AMMO_SPEED, ammoSpeedBoost, AMMO_SPEED_UUID, AMMO_SPEED_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.AMMO_SPEED, AMMO_SPEED_UUID);
    }


    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return GunTypeChecker.ALL_GUN_TYPES_LIST;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double ammoSpeedBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.limitSpeedBulletSpeedBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.limit_speed.effect", String.format("%+.0f", ammoSpeedBoost))
            .withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.literal(""));

    }
}
