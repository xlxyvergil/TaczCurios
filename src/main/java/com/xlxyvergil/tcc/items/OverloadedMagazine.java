package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotContext;


import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 过载弹匣 - 提升弹匣容量，降低装填速度
 * 效果：提升弹匣容量（加算），降低装填速度（加算）
 */
public class OverloadedMagazine extends ItemBaseCurio {

    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID MAGAZINE_CAPACITY_UUID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID RELOAD_UUID = UUID.fromString("22222222-2222-2222-2223-222222222223");

    // 修饰符名称
    private static final String MAGAZINE_CAPACITY_NAME = "tcc.overloaded_magazine.magazine_capacity";
    private static final String RELOAD_NAME = "tcc.overloaded_magazine.reload";

    public OverloadedMagazine(Properties properties) {
        super(properties);
    }

    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);

        // 给玩家添加属性修改
        if (slotContext.entity() instanceof Player player) {
            applyOverloadedMagazineEffects(player);
        }
    }

    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);

        // 移除玩家的属性修改
        if (slotContext.entity() instanceof Player player) {
            removeOverloadedMagazineEffects(player);
        }
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
     * 当物品在Curios插槽中时被右键点击
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    /**
     * 应用过载弹匣效果
     * 提升弹匣容量（加算）和降低装填速度（加算）
     */
    public void applyOverloadedMagazineEffects(Player player) {
        var attributes = player.getAttributes();

        // 获取弹匣容量属性
        var capacityAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "magazine_capacity")
            )
        );

        // 移除已存在的修饰符
        if (capacityAttribute != null) {
            capacityAttribute.removeModifier(MAGAZINE_CAPACITY_UUID);
        }

        // 获取装填速度属性
        var reloadAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "reload_time")
            )
        );

        if (reloadAttribute != null) {
            reloadAttribute.removeModifier(RELOAD_UUID);
        }

        // 检查玩家是否持有霰弹枪，只有持有霰弹枪时才应用加成
        if (GunTypeChecker.isHoldingShotgun(player)) {
            // 获取配置中的弹匣容量加成值和装填速度减益值
            double magazineCapacityBoost = TaczCuriosConfig.COMMON.overloadedMagazineCapacityBoost.get();
            double reloadDebuff = -TaczCuriosConfig.COMMON.overloadedMagazineReloadSpeedReduction.get();

            // 应用弹匣容量加成
            if (capacityAttribute != null) {
                // 添加配置中的弹匣容量加成（加算）
                var magazineCapacityModifier = new AttributeModifier(
                    MAGAZINE_CAPACITY_UUID,
                    MAGAZINE_CAPACITY_NAME,
                    magazineCapacityBoost,
                    AttributeModifier.Operation.ADDITION
                );
                capacityAttribute.addPermanentModifier(magazineCapacityModifier);
            }

            // 应用装填速度减益
            if (reloadAttribute != null) {
                // 添加配置中的装填速度减益（加算）
                var reloadModifier = new AttributeModifier(
                    RELOAD_UUID,
                    RELOAD_NAME,
                    reloadDebuff,
                    AttributeModifier.Operation.ADDITION
                );
                reloadAttribute.addPermanentModifier(reloadModifier);
            }
        }
    }

    /**
     * 移除过载弹匣效果
     */
    public void removeOverloadedMagazineEffects(Player player) {
        var attributes = player.getAttributes();

        // 获取弹匣容量属性
        var capacityAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "magazine_capacity")
            )
        );

        if (capacityAttribute != null) {
            capacityAttribute.removeModifier(MAGAZINE_CAPACITY_UUID);
        }

        // 获取装填速度属性
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
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof Player player) {
            applyOverloadedMagazineEffects(player);
        }
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.overloaded_magazine.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));

        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        double magazineCapacityBoost = TaczCuriosConfig.COMMON.overloadedMagazineCapacityBoost.get() * 100;
        double reloadDebuff = TaczCuriosConfig.COMMON.overloadedMagazineReloadSpeedReduction.get() * 100;
        tooltip.add(Component.translatable("item.tcc.overloaded_magazine.effect", 
                String.format("%.0f", magazineCapacityBoost), String.format("%.0f", reloadDebuff))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(Player player) {
        applyOverloadedMagazineEffects(player);
    }
}