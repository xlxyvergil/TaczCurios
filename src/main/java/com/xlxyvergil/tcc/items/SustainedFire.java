package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.network.chat.Component;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 持续火力 - 提升装填速度
 * 效果：提升装填速度（加算）
 */
public class SustainedFire extends ItemBaseCurio {

    // 属性修饰符UUID - 用于唯一标识修饰
    private static final UUID RELOAD_UUID = UUID.fromString("45c0a867-83d3-4c7b-a316-20ef80ad857e");

    // 修饰符名
    private static final String RELOAD_NAME = "tcc.sustained_fire.reload";

    public SustainedFire(Properties properties) {
        super(properties);
    }

    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);

        // 给生物添加属性修改
        applySustainedFireEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);

        // 移除生物的属性修改
        removeSustainedFireEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 检查是否可以装备到指定插槽
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        return slotContext.identifier().equals("tcc_slot");
    }

    /**
     * 当物品在Curios插槽中时被右键点
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    /**
     * 应用持续火力效果
     * 提升装填速度（加算）
     */
    public void applySustainedFireEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();

        // 获取装填速度属
        var reloadAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "reload_time")
            )
        );

        if (reloadAttribute != null) {
            // 移除已存在的修饰
            reloadAttribute.removeModifier(RELOAD_UUID);

            // 检查生物是否持有手枪，只有持有手枪时才应用加成
            if (GunTypeChecker.isHoldingPistol(livingEntity)) {
                // 获取配置中的装填速度加成
                double reloadBoost = TaczCuriosConfig.COMMON.sustainedFireReloadSpeedBoost.get();
                // 添加配置中的装填速度加成（加算）
                var reloadModifier = new AttributeModifier(
                    RELOAD_UUID,
                    RELOAD_NAME,
                    reloadBoost,
                    AttributeModifier.Operation.ADDITION
                );
                reloadAttribute.addPermanentModifier(reloadModifier);
            }
        }
    }

    /**
     * 移除持续火力效果
     */
    public void removeSustainedFireEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();

        // 获取装填速度属
        var reloadAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "reload_time")
            )
        );

        if (reloadAttribute != null) {
            reloadAttribute.removeModifier(RELOAD_UUID);
        }
    }

    /**
     * 当生物持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        applySustainedFireEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.sustained_fire.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));

        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        double reloadBoost = TaczCuriosConfig.COMMON.sustainedFireReloadSpeedBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.sustained_fire.effect", String.format("%.0f", reloadBoost))
            .withStyle(net.minecraft.ChatFormatting.GREEN));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.common"));
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applySustainedFireEffects(livingEntity);
    }
}
