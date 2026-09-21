package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
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
public class SevenThunders extends BoundCurioItem {
    private static final ResourceLocation HEADSHOT_MULTIPLIER_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "seven_thunders_6e2a8b94_0b2a");
    private static final ResourceLocation CRIT_CHANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "seven_thunders_e0c9302a_2b65");
    private static final ResourceLocation CRIT_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "seven_thunders_a5a91c44_f6c0");

    public SevenThunders(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(HEADSHOT_MULTIPLIER_ID, stack.getItem());
        AttributeHelper.registerSourceItem(CRIT_CHANCE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(CRIT_DAMAGE_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER,
                TaczCuriosConfig.COMMON.sevenThundersHeadshotMultiplier.get(), HEADSHOT_MULTIPLIER_ID, AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                TaczCuriosConfig.COMMON.sevenThundersCritChance.get(), CRIT_CHANCE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                TaczCuriosConfig.COMMON.sevenThundersCritDamage.get(), CRIT_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER, HEADSHOT_MULTIPLIER_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_ID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("sniper");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.sevenThundersHeadshotMultiplier.get() * 100, "%.0f%%", Component.translatable(AttributeHelper.HEADSHOT_MULTIPLIER.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.sevenThundersCritChance.get() * 100, "%.0f%%", Component.translatable(AttributeHelper.CRIT_CHANCE.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.sevenThundersCritDamage.get() * 100, "%.0f%%", Component.translatable(AttributeHelper.CRIT_DAMAGE.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

        appendBoundPlayer(stack, tooltip);
    }
}
