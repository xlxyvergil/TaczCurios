package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.api.item.IGun;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

@EventBusSubscriber(modid = TaczCurios.MODID)
public class CuriosItemEventHandler {
    
    @SubscribeEvent
    public static void onCurioChanged(CurioChangeEvent event) {
        LivingEntity entity = event.getEntity();
        // Curios 内部已调用 onEquip/onUnequip（由 BaseCurioItem 处理属性+TACZ缓存）
        // 此处仅作为兜底确保缓存更新（支持玩家、女仆等所有 LivingEntity）
        updateTacZCache(entity);
    }
    
    public static void onCurioEquip(LivingEntity entity, ItemStack stack) {
        updateTacZCache(entity);
    }
    
    public static void onCurioUnequip(LivingEntity entity, ItemStack stack) {
        updateTacZCache(entity);
    }
    
    public static void onGunSwitchEvent(LivingEntity entity) {
        updateTacZCache(entity);
    }
    
    /**
     * 饰品状态变化时触发属性重新计算，支持玩家、女仆等所有 LivingEntity。
     */
    private static void updateTacZCache(LivingEntity entity) {
        ItemStack mainHandItem = entity.getMainHandItem();
        ItemStack offHandItem = entity.getOffhandItem();
        
        if (mainHandItem.getItem() instanceof IGun) {
            AttachmentPropertyManager.postChangeEvent(entity, mainHandItem);
            return;
        }
        
        if (offHandItem.getItem() instanceof IGun) {
            AttachmentPropertyManager.postChangeEvent(entity, offHandItem);
            return;
        }
        
        // 即使没有持枪也触发一次，确保属性正确应用
        AttachmentPropertyManager.postChangeEvent(entity, ItemStack.EMPTY);
    }
}