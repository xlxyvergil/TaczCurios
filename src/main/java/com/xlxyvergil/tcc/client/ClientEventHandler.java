package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.event.SlotModifiersUpdatedEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID)
public class ClientEventHandler {
    
    @SubscribeEvent
    public static void onCurioChanged(SlotModifiersUpdatedEvent event) {
        refreshHeldGunTooltip();
        refreshRefitScreen();
    }
    
    private static void refreshHeldGunTooltip() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            Player player = mc.player;
            ItemStack mainHandItem = player.getMainHandItem();
            ItemStack offHandItem = player.getOffhandItem();
            
            // 如果主手是枪械，强制刷新其属性缓存
            if (mainHandItem.getItem() instanceof IGun) {
                mainHandItem.setPopTime(1);
                AttachmentPropertyManager.postChangeEvent(player, mainHandItem);
            }
            
            // 如果副手是枪械，强制刷新其属性缓存
            if (offHandItem.getItem() instanceof IGun) {
                offHandItem.setPopTime(1);
                AttachmentPropertyManager.postChangeEvent(player, offHandItem);
            }
        }
    }
    
    private static void refreshRefitScreen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null && mc.screen instanceof GunRefitScreen screen) {
            // 重新初始化界面以刷新属性图
            screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            AttachmentPropertyManager.postChangeEvent(mc.player, mc.player.getMainHandItem());
        }
    }
    
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        // 如果打开的是改装界面，强制刷新手持枪械属性
        if (event.getNewScreen() instanceof GunRefitScreen) {
            refreshHeldGunTooltip();
        }
    }
}