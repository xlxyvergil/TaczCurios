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
public class GildedSteelSlash extends TccCurioItem {
    private static final ResourceLocation BASE_CRIT_CHANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "gilded_steel_slash_4fa674c5_63c6");

    public GildedSteelSlash(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(BASE_CRIT_CHANCE_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double baseCritChance = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.gildedSteelSlashCritChanceBase.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, baseCritChance, BASE_CRIT_CHANCE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, BASE_CRIT_CHANCE_ID);
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


        double baseCritChance = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.gildedSteelSlashCritChanceBase.get()) * 100;
        int fusionLevel = FusionData.from(stack).level();
        double buffCritDmg = TaczCuriosConfig.COMMON.gildedSteelSlashCritDamagePerLevel.get() * 100 * (fusionLevel + 1);
        int duration = TaczCuriosConfig.COMMON.gildedSteelSlashDuration.get();
        int maxStacks = TaczCuriosConfig.COMMON.gildedSteelSlashMaxStacks.get() / TaczCuriosConfig.COMMON.fusionMaxLevelEpic.get();
        tooltip.add(Component.translatable("item.tcc.gilded_steel_slash.effect_base",
                String.format("%+.0f", baseCritChance))
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.tcc.gilded_steel_slash.effect_kill",
                String.format("%+.0f", buffCritDmg), maxStacks, duration)
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(""));
        
    }

}
