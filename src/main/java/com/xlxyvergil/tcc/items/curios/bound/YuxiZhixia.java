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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class YuxiZhixia extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("b8c9d0e1-f2a3-4567-bcde-f01234567890");

    public YuxiZhixia(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_UUID, stack.getItem());
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof YuxiZhixia);
        CompoundTag tag = equipped.getTag();
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            total, IMAGINARY_RESISTANCE_UUID,
            "tcc.yuxi_zhixia.imaginary_resistance", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        // 只扣掉雨曦之匣维持的那一份黄心，保留外部来源（只减不压）
        float absorptionTarget = TaczCuriosConfig.COMMON.yuxiZhixiaAbsorptionAmount.get();
        livingEntity.setAbsorptionAmount(Math.max(0.0F,
            livingEntity.getAbsorptionAmount() - absorptionTarget));
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof YuxiZhixia).isEmpty();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide()) return;
        if (!GunTypeChecker.isHoldingHeavyWeapon(entity)) return;

        int interval = TaczCuriosConfig.COMMON.yuxiZhixiaAbsorptionInterval.get() * 20;
        if (entity.tickCount % interval != 0) return;

        // 只抬不压，保留外部更高来源
        float target = TaczCuriosConfig.COMMON.yuxiZhixiaAbsorptionAmount.get();
        entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount(), target));
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("rpg", "mg");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        appendImaginaryResistance(stack, tooltip);

        int absorptionInterval = TaczCuriosConfig.COMMON.yuxiZhixiaAbsorptionInterval.get();
        int absorptionAmount = TaczCuriosConfig.COMMON.yuxiZhixiaAbsorptionAmount.get();

        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("item.tcc.yuxi_zhixia.effect",
                absorptionInterval,
                absorptionAmount)
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
