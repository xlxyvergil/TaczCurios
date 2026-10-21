package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class Imer extends BoundCurioItem {
    private static final ResourceLocation ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "imer_e1880895_9a20");

    public Imer(Properties properties) {
        super(properties);
    }

    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        if (GunTypeChecker.isHoldingMeleeWeapon(livingEntity)) {
            double attackBonus = TaczCuriosConfig.COMMON.imerAttackDamageBonus.get();
            AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_DAMAGE,
                attackBonus, ATTACK_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            removeModifiers(livingEntity);
        }
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_ID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                s -> s.getItem() instanceof Imer).isEmpty();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        double bonusPercent = TaczCuriosConfig.COMMON.imerAttackDamageBonus.get() * 100;
        tooltip.add(formatModifierTooltip(bonusPercent, "%.0f%%", Component.translatable(AttributeHelper.ATTACK_DAMAGE.value().getDescriptionId()))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_max_health")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
