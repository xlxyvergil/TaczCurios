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
public class QianjieYicheng extends BoundCurioItem {
    private static final ResourceLocation LUCK_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "qianjie_yicheng_d94c75ed_776e");

    public QianjieYicheng(Properties properties) {
        super(properties);
    }

    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            int luck = TaczCuriosConfig.COMMON.qianjieYichengLuck.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.LUCK,
                luck, LUCK_ID, AttributeModifier.Operation.ADD_VALUE);
        } else {
            removeModifiers(livingEntity);
        }
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.LUCK, LUCK_ID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("smg");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                s -> s.getItem() instanceof QianjieYicheng).isEmpty();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        int luck = TaczCuriosConfig.COMMON.qianjieYichengLuck.get();

        tooltip.add(formatModifierTooltip(luck, "%.0f", Component.translatable(AttributeHelper.LUCK.value().getDescriptionId()))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
