package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.api.item.IGun;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;

/**
 * Curios 饰品事件处理器：监听饰品装备/卸载事件并更新 TACZ 缓存。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CuriosItemEventHandler {
    
    /**
     * 监听饰品装备事件
     */
    @SubscribeEvent
    public static void onCurioEquipped(CurioEquipEvent event) {
        LivingEntity entity = event.getEntity();
        // Curios 内部已调用 onEquip（由 BaseCurioItem 处理属性+TACZ缓存）
        // 此处仅作为兜底确保缓存更新（支持玩家、女仆等所有 LivingEntity）
        updateTacZCache(entity);
    }
    
    /**
     * 监听饰品卸载事件
     */
    @SubscribeEvent
    public static void onCurioUnequipped(CurioUnequipEvent event) {
        LivingEntity entity = event.getEntity();
        // Curios 内部已调用 onUnequip（由 BaseCurioItem 处理属性+TACZ缓存）
        // 此处仅作为兜底确保缓存更新（支持玩家、女仆等所有 LivingEntity）
        updateTacZCache(entity);
    }
    
    /**
     * 由饰品直接调用的装备事件处理方法
     */
    public static void onCurioEquip(LivingEntity entity, ItemStack stack) {
        updateTacZCache(entity);
    }
    
    /**
     * 由饰品直接调用的卸载事件处理方法
     */
    public static void onCurioUnequip(LivingEntity entity, ItemStack stack) {
        updateTacZCache(entity);
    }
    
    /**
     * 由 GunSwitchEventHandler 调用：枪械切换时更新 TACZ 缓存。
     */
    public static void onGunSwitchEvent(LivingEntity entity) {
        updateTacZCache(entity);
    }
    
    /**
     * 更新 TACZ 缓存：饰品状态变化时触发属性重新计算（支持玩家、女仆等所有 LivingEntity）。
     */
    private static void updateTacZCache(LivingEntity entity) {
        ItemStack mainHandItem = entity.getMainHandItem();
        ItemStack offHandItem = entity.getOffhandItem();
        
        // 检查主手是否是枪械
        if (mainHandItem.getItem() instanceof IGun) {
            AttachmentPropertyManager.postChangeEvent(entity, mainHandItem);
            return;
        }
        
        // 检查副手是否是枪械
        if (offHandItem.getItem() instanceof IGun) {
            AttachmentPropertyManager.postChangeEvent(entity, offHandItem);
            return;
        }
        
        // 即使没有持枪也触发一次，确保属性正确应用
        AttachmentPropertyManager.postChangeEvent(entity, ItemStack.EMPTY);
    }
}