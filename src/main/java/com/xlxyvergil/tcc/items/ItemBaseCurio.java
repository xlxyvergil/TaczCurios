package com.xlxyvergil.tcc.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * 简化的Curio物品基类，参考Enigmatic-Legacy的实现
 * 直接实现ICurioItem接口，避免复杂的继承层次
 */
public class ItemBaseCurio extends Item implements ICurioItem {
    
    public ItemBaseCurio(Properties properties) {
        super(properties);
    }
    
    /**
     * 检查是否可以装备到指定插槽
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return true;
    }
    
    /**
     * 检查是否可以卸下
     */
    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return true;
    }
    
    /**
     * 当物品在Curios插槽中时被右键点击
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
    
    /**
     * 应用饰品效果
     * 当玩家切换武器时被GunSwitchEventHandler调用
     * 子类需要重写此方法来实现具体的效果逻辑
     */
    public void applyGunSwitchEffect(Player player) {
        // 默认实现为空，子类可以重写
    }
}