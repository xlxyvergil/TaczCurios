package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
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
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class SacrificeSteel extends TccCurioItem {
    private static final ResourceLocation CRIT_CHANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sacrifice_steel_55038b53_5cc6");
    private static final ResourceLocation SET_BONUS_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sacrifice_steel_1aea61d9_92c5");


    public SacrificeSteel(Properties properties) {
        super(properties);
    }

    private static boolean hasSacrificeOppression(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
            .map(inv -> inv.findFirstCurio(TccItems.SACRIFICE_OPPRESSION).isPresent())
            .orElse(false);
    }

    /** 套装加成倍率随融合等级线性增长：0 级为 1.0（额外 +0%），满级为配置值（默认 1.25，即额外 +25%）。 */
    private static double getSetBonusValue(ItemStack stack) {
        double config = TaczCuriosConfig.COMMON.sacrificeSetBonus.get();
        FusionData fusion = FusionData.from(stack);
        int maxLevel = fusion.maxLevel();
        return maxLevel > 0 ? 1.0 + (config - 1.0) * ((double) fusion.level() / maxLevel) : config;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(CRIT_CHANCE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(SET_BONUS_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double critChanceBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.sacrificeSteelCritChance.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, critChanceBoost, CRIT_CHANCE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

            // 套装效果：同时装备牺牲压迫点时，额外 +25%
            if (hasSacrificeOppression(livingEntity)) {
                double setBonus = getSetBonusValue(stack);
                double bonusModifier = critChanceBoost * (setBonus - 1.0);
                AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, bonusModifier, SET_BONUS_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            } else {
                AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, SET_BONUS_ID);
            }
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, SET_BONUS_ID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double critChanceBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.sacrificeSteelCritChance.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.sacrifice_steel.effect",
                String.format("%+.0f", critChanceBoost))
            .withStyle(ChatFormatting.WHITE));

        double setBonusPct = (getSetBonusValue(stack) - 1.0) * 100;
        tooltip.add(Component.translatable("item.tcc.sacrifice_steel.set_bonus",
                String.format("%+.0f", setBonusPct))
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        
    }


}
