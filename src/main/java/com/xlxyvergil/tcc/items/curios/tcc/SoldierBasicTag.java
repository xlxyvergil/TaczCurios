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
public class SoldierBasicTag extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识这个修饰
    private static final ResourceLocation GUN_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "soldier_basic_tag_725a607c_caf4");
    
    // 修饰符名
    
    public SoldierBasicTag(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(GUN_DAMAGE_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.soldierBasicTagDamageBoost.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageBoost, GUN_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GUN_DAMAGE_ID);
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

        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.soldierBasicTagDamageBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.soldier_basic_tag.effect", String.format("%+.0f", damageBoost))
            .withStyle(ChatFormatting.BLUE));

        tooltip.add(Component.literal(""));

    }
    

}
