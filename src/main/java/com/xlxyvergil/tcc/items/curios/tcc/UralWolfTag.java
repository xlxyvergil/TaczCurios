package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import com.xlxyvergil.tcc.util.GunTypeChecker;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class UralWolfTag extends TccCurioItem {
    private static final ResourceLocation HEADSHOT_MULTIPLIER_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "ural_wolf_tag_96a4146f_c5f6");
    
    public UralWolfTag(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(HEADSHOT_MULTIPLIER_MODIFIER_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double multiplierBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.uralWolfTagHeadshotMultiplierBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER, multiplierBoost, HEADSHOT_MULTIPLIER_MODIFIER_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER, HEADSHOT_MULTIPLIER_MODIFIER_ID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return GunTypeChecker.ALL_GUN_TYPES_LIST;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

        tooltip.add(Component.literal(""));
        
        double multiplierBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.uralWolfTagHeadshotMultiplierBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.ural_wolf_tag.effect", String.format("%+.0f", multiplierBoost))
            .withStyle(ChatFormatting.AQUA));
        
        tooltip.add(Component.literal(""));
        
    }
    

}