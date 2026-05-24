package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.items.ItemBaseCurio;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * 饰品基类 - 提供通用的饰品行为
 */
public abstract class BaseCurioItem extends ItemBaseCurio {
    
    public BaseCurioItem(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        LivingEntity entity = (LivingEntity) slotContext.entity();
        applyEffects(entity);
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        LivingEntity entity = (LivingEntity) slotContext.entity();
        removeEffects(entity);
    }
    
    /**
     * 检查是否可以装备到指定插槽
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 默认检查是否装备在TCC饰品槽位
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
     * 应用效果（子类实现）
     */
    protected abstract void applyEffects(LivingEntity entity);
    
    /**
     * 移除效果（子类实现）
     */
    protected abstract void removeEffects(LivingEntity entity);
    
    /**
     * 检查是否已装备指定类型的饰品
     * @param entity 实体
     * @param itemClass 饰品类型
     * @return 是否已装备
     */
    protected boolean hasEquipped(LivingEntity entity, Class<? extends BaseCurioItem> itemClass) {
        LazyOptional<ICuriosItemHandler> curiosInventory = 
            top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(entity);
        
        return curiosInventory
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem().getClass() == itemClass))
            .orElse(Optional.empty())
            .isPresent();
    }
    
    /**
     * 检查是否已装备满足条件的饰品
     * @param entity 实体
     * @param predicate 条件
     * @return 是否已装备
     */
    protected boolean hasEquipped(LivingEntity entity, Predicate<ItemStack> predicate) {
        LazyOptional<ICuriosItemHandler> curiosInventory = 
            top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(entity);
        
        return curiosInventory
            .map(inv -> inv.findFirstCurio(predicate))
            .orElse(Optional.empty())
            .isPresent();
    }
}
