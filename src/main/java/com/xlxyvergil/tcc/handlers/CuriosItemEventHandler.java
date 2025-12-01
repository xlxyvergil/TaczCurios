package com.xlxyvergil.tcc.handlers;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.ItemBaseCurio;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.api.item.IGun;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;
import top.theillusivec4.curios.api.SlotContext;

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
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof ServerPlayer player)) {
            return;
        }
        
        ItemStack stack = event.getStack();
        Item item = stack.getItem();

        // 处理饰品装备时的属性添加
        if (item instanceof ItemBaseCurio curioItem) {
            // 调用饰品的onEquip方法（如果需要）
            curioItem.onEquip(event.getSlotContext(), ItemStack.EMPTY, stack);
        }

        // 更新枪械属性缓存（无论是否持有枪械都更新）
        updateTacZCache(player);
    }
    
    /**
     * 监听饰品卸载事件
     * 参照ClientMessageUnloadAttachment.handle()的实现方式
     */
    @SubscribeEvent
    public static void onCurioUnequipped(CurioUnequipEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof ServerPlayer player)) {
            return;
        }
        
        ItemStack stack = event.getStack();
        Item item = stack.getItem();

        // 处理饰品卸下时的属性移除
        if (item instanceof ItemBaseCurio curioItem) {
            // 调用饰品的onUnequip方法（如果需要）
            curioItem.onUnequip(event.getSlotContext(), ItemStack.EMPTY, stack);
        }

        // 更新枪械属性缓存（无论是否持有枪械都更新）
        updateTacZCache(player);
    }
    
    /**
     * 由饰品直接调用的装备事件处理方法
     */
    public static void onCurioEquip(ServerPlayer player, ItemStack stack) {
        // 立即更新缓存
        updateTacZCache(player);
    }
    
    /**
     * 由饰品直接调用的卸载事件处理方法
     */
    public static void onCurioUnequip(ServerPlayer player, ItemStack stack) {
        // 立即更新缓存
        updateTacZCache(player);
    }
    
    /**
     * 当GunSwitchEventHandler被调用时触发的方法
     * 由GunSwitchEventHandler调用
     */
    public static void onGunSwitchEvent(ServerPlayer player) {
        // 立即更新缓存
        updateTacZCache(player);
    }
    
    /**
     * 更新TACZ缓存
     * 当饰品状态发生变化时调用此方法触发缓存更新
     * @param player 玩家实体
     */
    private static void updateTacZCache(ServerPlayer player) {
        // 获取玩家主手物品
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offHandItem = player.getOffhandItem();
        
        // 检查主手是否是枪械
        if (mainHandItem.getItem() instanceof IGun) {
            // 调用TACZ的缓存更新机制，触发属性重新计算
            AttachmentPropertyManager.postChangeEvent(player, mainHandItem);
            return;
        }
        
        // 检查副手是否是枪械
        if (offHandItem.getItem() instanceof IGun) {
            // 调用TACZ的缓存更新机制，触发属性重新计算
            AttachmentPropertyManager.postChangeEvent(player, offHandItem);
            return;
        }
        
        // 如果玩家没有持枪，也触发一次更新以确保属性正确应用
        // 创建一个空的ItemStack用于触发更新
        AttachmentPropertyManager.postChangeEvent(player, ItemStack.EMPTY);
    }
}