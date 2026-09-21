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
import java.util.UUID;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class HeavyCaliberTag extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final ResourceLocation[] DAMAGE_IDS = {
        ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "heavy_caliber_tag_0de3ed5d_3e9f"),
        ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "heavy_caliber_tag_86c52112_c971"),
        ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "heavy_caliber_tag_216b141e_a15e"),
        ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "heavy_caliber_tag_7df0af83_dea8"),
        ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "heavy_caliber_tag_006a5e24_caa3")
    };
    
    // 修饰符名
    
    private static final ResourceLocation INACCURACY_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "heavy_caliber_tag_4a8f7c31_3d2e");
    
    public HeavyCaliberTag(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(INACCURACY_ID, stack.getItem());
        for (ResourceLocation uuid : DAMAGE_IDS) {
            AttributeHelper.registerSourceItem(uuid, stack.getItem());
        }
        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.heavyCaliberTagDamageBoost.get());
        double inaccuracyBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.heavyCaliberTagInaccuracyBoost.get());

        // 枪械伤害加成只在主手持有对应武器类型时生效；切换武器时由 GunSwitchEventHandler 触发 refreshEffects 重新评估
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_RIFLE, damageBoost, DAMAGE_IDS[0], AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SNIPER, damageBoost, DAMAGE_IDS[1], AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SMG, damageBoost, DAMAGE_IDS[2], AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LMG, damageBoost, DAMAGE_IDS[3], AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, damageBoost, DAMAGE_IDS[4], AttributeModifier.Operation.ADD_VALUE);

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.INACCURACY, inaccuracyBoost, INACCURACY_ID, AttributeModifier.Operation.ADD_VALUE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_RIFLE, DAMAGE_IDS[0]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SNIPER, DAMAGE_IDS[1]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SMG, DAMAGE_IDS[2]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LMG, DAMAGE_IDS[3]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, DAMAGE_IDS[4]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.INACCURACY, INACCURACY_ID);
    }
    

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.heavyCaliberTagDamageBoost.get() ) * 100;
        double inaccuracyBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.heavyCaliberTagInaccuracyBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.heavy_caliber_tag.effect", 
                String.format("%+.0f", damageBoost), String.format("%+.0f", inaccuracyBoost))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }

}