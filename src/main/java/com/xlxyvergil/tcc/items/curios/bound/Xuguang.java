package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
public class Xuguang extends BoundCurioItem {
    private static final ResourceLocation ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "xuguang_161e973d_68d7");
    private static final ResourceLocation ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "xuguang_ca6fabe6_01b5");
    private static final ResourceLocation CRIT_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "xuguang_71aa144b_f2e0");
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "xuguang_5c1e9b7f_4b28");

    private static double attackSpeedPct() {
        return TaczCuriosConfig.COMMON.xuguangAttackSpeedPercent.get();
    }

    private static double attackDamagePct() {
        return TaczCuriosConfig.COMMON.xuguangAttackDamagePercent.get();
    }

    private static double critDamage() {
        return TaczCuriosConfig.COMMON.xuguangCritDamagePercent.get();
    }

    public Xuguang(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof Xuguang).isEmpty();
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ATTACK_SPEED_ID, stack.getItem());
        AttributeHelper.registerSourceItem(ATTACK_DAMAGE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(CRIT_DAMAGE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_ID, stack.getItem());
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Xuguang);
        CompoundTag tag = ItemNbtHelper.getTag(equipped);
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
                total, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_SPEED,
                    attackSpeedPct(), ATTACK_SPEED_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_DAMAGE,
                    attackDamagePct(), ATTACK_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            if (critDamage() > 0) {
                AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                        critDamage(), CRIT_DAMAGE_ID, AttributeModifier.Operation.ADD_VALUE);
            }
        } else {
            // 武器不匹配时只清理受限制的攻击/暴击加成，虚数抗性为无条件施加，需保留。
            AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_SPEED, ATTACK_SPEED_ID);
            AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_ID);
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_ID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE, IMAGINARY_RESISTANCE_ID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_SPEED, ATTACK_SPEED_ID);
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_ID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity(), stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);
        appendImaginaryResistance(stack, tooltip);
        tooltip.add(formatModifierTooltip(attackSpeedPct() * 100, "%.0f%%", Component.translatable(Attributes.ATTACK_SPEED.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(formatModifierTooltip(attackDamagePct() * 100, "%.0f%%", Component.translatable(AttributeHelper.ATTACK_DAMAGE.value().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        if (critDamage() > 0) {
            tooltip.add(formatModifierTooltip(critDamage() * 100, "%.0f%%", Component.translatable(AttributeHelper.CRIT_DAMAGE.value().getDescriptionId()))
                    .withStyle(ChatFormatting.GOLD));
        }
        appendBoundPlayer(stack, tooltip);
    }
}
