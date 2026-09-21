package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class YongjieZhijian extends BoundCurioItem {
    private static final ResourceLocation CRIT_CHANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "yongjie_zhijian_a81d5c7e_8d2b");
    private static final ResourceLocation CRIT_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "yongjie_zhijian_c92e6f8d_0e3c");
    private static final ResourceLocation LUCK_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "yongjie_zhijian_8acb73d0_7608");

    public YongjieZhijian(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(CRIT_CHANCE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(CRIT_DAMAGE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(LUCK_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.LUCK,
                TaczCuriosConfig.COMMON.yongjieZhijianLuck.get(), LUCK_ID, AttributeModifier.Operation.ADD_VALUE);

            int luck = (int) livingEntity.getAttributeValue(AttributeHelper.LUCK);
            double critChance = Math.round(luck * TaczCuriosConfig.COMMON.yongjieZhijianCritChancePerLuck.get() * 10000.0) / 10000.0;
            double critDamage = Math.round(luck * TaczCuriosConfig.COMMON.yongjieZhijianCritDamagePerLuck.get() * 10000.0) / 10000.0;

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                critChance, CRIT_CHANCE_ID, AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                critDamage, CRIT_DAMAGE_ID, AttributeModifier.Operation.ADD_VALUE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.LUCK, LUCK_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_ID);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity(), stack);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("smg");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        int luck = TaczCuriosConfig.COMMON.yongjieZhijianLuck.get();
        double critChance = luck * TaczCuriosConfig.COMMON.yongjieZhijianCritChancePerLuck.get();
        double critDamage = luck * TaczCuriosConfig.COMMON.yongjieZhijianCritDamagePerLuck.get();
        tooltip.add(formatModifierTooltip(luck, "%.0f", Component.translatable(AttributeHelper.LUCK.value().getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(critChance, "%.2f", Component.translatable(AttributeHelper.CRIT_CHANCE.value().getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(critDamage, "%.2f", Component.translatable(AttributeHelper.CRIT_DAMAGE.value().getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_luck")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
