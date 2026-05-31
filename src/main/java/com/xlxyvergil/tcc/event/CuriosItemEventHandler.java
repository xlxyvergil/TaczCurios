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
 * Curios饰品事件处理器
 * 用于监听饰品装备/卸载事件并更新TACZ缓存
 * 参照TACZ处理配件安装和卸载的方式实现
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CuriosItemEventHandler {
    
    /**
     * 监听饰品装备事件
     * 参照ClientMessageRefitGun.handle()的实现方式
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
     * 参照ClientMessageUnloadAttachment.handle()的实现方式
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
     * 当GunSwitchEventHandler被调用时触发的方法
     * 由GunSwitchEventHandler调用
     */
    public static void onGunSwitchEvent(LivingEntity entity) {
        updateTacZCache(entity);
    }
    
    /**
     * 更新TACZ缓存
     * 当饰品状态发生变化时调用此方法触发缓存更新
     * @param entity 持有者实体（支持玩家、女仆等所有LivingEntity）
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