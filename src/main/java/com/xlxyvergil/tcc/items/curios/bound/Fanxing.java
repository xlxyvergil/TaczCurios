package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
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

public class Fanxing extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("e1f2a3b4-c5d6-7890-abcd-ef1234567801");
    private static final UUID LUCK_UUID = UUID.fromString("d4e5f6a7-b8c9-0123-defa-123456789004");

    public Fanxing(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(LUCK_UUID, stack.getItem());
        // 虚数抗性不受武器类型限制，装备即生效。
        ItemStack equipped = findEquippedStack(livingEntity);
        CompoundTag tag = equipped.getTag();
        double resistance = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            resistance, IMAGINARY_RESISTANCE_UUID,
            "tcc.fanxing.imaginary_resistance", AttributeModifier.Operation.ADDITION);

        if (matchesRestriction(livingEntity)) {
            double totalResistance = livingEntity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            int luckFromResistance = (int) ((int) Math.round(totalResistance * TaczCuriosConfig.COMMON.fanxingLuckPerResistance.get() * 10000.0) / 10000.0);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.LUCK,
                luckFromResistance, LUCK_UUID,
                "tcc.fanxing.luck", AttributeModifier.Operation.ADDITION);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.LUCK, LUCK_UUID);
        DamageResistanceHelper.clearHurtCooldown(livingEntity);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity == null) return;
        applyEffects(entity, stack);
        if (entity.level().isClientSide) return;
        // 受伤冷却仅在持枪时登记：持枪则注册受击自动冷却，非持枪则清除登记（已进入的冷却在剩余时间内继续生效）。
        if (!GunTypeChecker.isHoldingAnyGun(entity)) {
            DamageResistanceHelper.clearHurtCooldown(entity);
            return;
        }
        DamageResistanceHelper.setHurtCooldown(entity, computeCooldownTicks(entity));
    }

    /** 依据当前幸运值计算受伤冷却时长（tick），并受配置上限约束。 */
    private static int computeCooldownTicks(LivingEntity entity) {
        int luck = (int) entity.getAttributeValue(AttributeHelper.LUCK);
        int cooldownTicks = TaczCuriosConfig.COMMON.fanxingBaseCooldown.get()
            + (luck / 2) * TaczCuriosConfig.COMMON.fanxingLuckPerTick.get();
        int maxCooldown = TaczCuriosConfig.COMMON.fanxingMaxCooldown.get();
        return Math.min(cooldownTicks, maxCooldown);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof Fanxing).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity,
            stack -> stack.getItem() instanceof Fanxing);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("smg");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        appendImaginaryResistance(stack, tooltip);

        tooltip.add(Component.literal(""));

        double resistance = 0;
        int computedLuck = 0;
        int computedCooldown = TaczCuriosConfig.COMMON.fanxingBaseCooldown.get();
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null) {
                resistance = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                int luck = (int) wearer.getAttributeValue(AttributeHelper.LUCK);
                computedLuck = (int)(resistance * TaczCuriosConfig.COMMON.fanxingLuckPerResistance.get());
                computedCooldown = TaczCuriosConfig.COMMON.fanxingBaseCooldown.get()
                    + (luck / 2) * TaczCuriosConfig.COMMON.fanxingLuckPerTick.get();
                int max = TaczCuriosConfig.COMMON.fanxingMaxCooldown.get();
                if (computedCooldown > max) computedCooldown = max;
            }
        }


        tooltip.add(formatModifierTooltip(computedLuck, "%.0f", Component.translatable(AttributeHelper.LUCK.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_luck")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("item.tcc.fanxing.special_cooldown",
                computedCooldown)
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
