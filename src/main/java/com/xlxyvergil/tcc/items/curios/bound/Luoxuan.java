package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
public class Luoxuan extends BoundCurioItem {
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "luoxuan_f2a3b4c5_8902");

    public Luoxuan(Properties properties) {
        super(properties);
    }

    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Luoxuan);
        CompoundTag tag = ItemNbtHelper.getTag(equipped);
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
            total, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, IMAGINARY_RESISTANCE_ID);
        // 伤害吸收黄心不再主动扣减：无法区分来源，扣减会误伤金苹果等外部效果；改为自然消耗/消失
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof Luoxuan).isEmpty();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide()) return;
        if (!GunTypeChecker.isHoldingHeavyWeapon(entity)) return;

        int interval = TaczCuriosConfig.COMMON.luoxuanAbsorptionInterval.get() * 20;
        if (entity.tickCount % interval != 0) return;

        float target = TaczCuriosConfig.COMMON.luoxuanAbsorptionAmount.get();
        entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount(), target));
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("rpg", "mg");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        appendImaginaryResistance(stack, tooltip);

        double absorptionIntervalSecs = TaczCuriosConfig.COMMON.luoxuanAbsorptionInterval.get();
        int absorptionAmount = TaczCuriosConfig.COMMON.luoxuanAbsorptionAmount.get();

        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("item.tcc.luoxuan.special_absorption",
                "",
                absorptionIntervalSecs,
                absorptionAmount)
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
