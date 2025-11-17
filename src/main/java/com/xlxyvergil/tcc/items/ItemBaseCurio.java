package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.handlers.CuriosItemEventHandler;
import net.minecraft.server.level.ServerPlayer;
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
     * 当物品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        // 如果是服务端玩家，通知事件处理器
        if (slotContext.entity() instanceof ServerPlayer serverPlayer) {
            CuriosItemEventHandler.onCurioEquip(serverPlayer, stack);
        }
    }
    
    /**
     * 当物品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        // 如果是服务端玩家，通知事件处理器
        if (slotContext.entity() instanceof ServerPlayer serverPlayer) {
            CuriosItemEventHandler.onCurioUnequip(serverPlayer, stack);
        }
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
}