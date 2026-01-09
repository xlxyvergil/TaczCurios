package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;

/**
 * 客户端事件处理器
 * 用于监听饰品装备/卸载事件并刷新枪械属性显示
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEventHandler {
    
    @SubscribeEvent
    public static void onCurioEquip(CurioEquipEvent event) {
        refreshHeldGunTooltip();
        refreshRefitScreen();
    }
    
    @SubscribeEvent
    public static void onCurioUnequip(CurioUnequipEvent event) {
        refreshHeldGunTooltip();
        refreshRefitScreen();
    }
    
    /**
     * 刷新手持枪械的Tooltip显示
     */
    private static void refreshHeldGunTooltip() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            Player player = mc.player;
            ItemStack mainHandItem = player.getMainHandItem();
            ItemStack offHandItem = player.getOffhandItem();
            
            // 如果主手是枪械，强制刷新其属性缓存
            if (mainHandItem.getItem() instanceof IGun) {
                mainHandItem.setPopTime(1);
                // 更新属性缓存
                AttachmentPropertyManager.postChangeEvent(player, mainHandItem);
            }
            
            // 如果副手是枪械，强制刷新其属性缓存
            if (offHandItem.getItem() instanceof IGun) {
                offHandItem.setPopTime(1);
                // 更新属性缓存
                AttachmentPropertyManager.postChangeEvent(player, offHandItem);
            }
        }
    }
    
    /**
     * 刷新改装界面
     * 参照ServerMessageRefreshRefitScreen.updateScreen()的实现
     */
    private static void refreshRefitScreen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null && mc.screen instanceof GunRefitScreen screen) {
            // 重新初始化界面以刷新属性图
            screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            // 刷新配件数据，客户端的
            AttachmentPropertyManager.postChangeEvent(mc.player, mc.player.getMainHandItem());
        }
    }
    
    /**
     * 当屏幕打开时刷新枪械属性
     */
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        // 如果打开的是改装界面，强制刷新手持枪械属性
        if (event.getNewScreen() instanceof GunRefitScreen) {
            refreshHeldGunTooltip();
        }
    }
}