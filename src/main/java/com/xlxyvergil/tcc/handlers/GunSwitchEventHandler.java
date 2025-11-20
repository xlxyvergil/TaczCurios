package com.xlxyvergil.tcc.handlers;

import com.tacz.guns.api.event.common.GunDrawEvent;
import com.xlxyvergil.tcc.items.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import com.xlxyvergil.tcc.TaczCurios;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GunSwitchEventHandler {
    
    @SubscribeEvent
    public static void onGunDraw(GunDrawEvent event) {
        // 玩家切换武器后检查是否装备了膛室或膛室Prime饰品
        if (event.getEntity() instanceof Player player) {
            // 检查玩家是否装备了膛室饰品
            Optional<SlotResult> chamberResult = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof Chamber));
            
            if (chamberResult.isPresent()) {
                ItemStack chamberStack = chamberResult.get().stack();
                if (chamberStack.getItem() instanceof Chamber chamber) {
                    // 根据枪械类型判断是否应用效果
                    chamber.applyChamberEffect(player);
                }
            }
            
            // 检查玩家是否装备了膛室Prime饰品
            Optional<SlotResult> chamberPrimeResult = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof ChamberPrime));
            
            if (chamberPrimeResult.isPresent()) {
                ItemStack chamberPrimeStack = chamberPrimeResult.get().stack();
                if (chamberPrimeStack.getItem() instanceof ChamberPrime chamberPrime) {
                    // 根据枪械类型判断是否应用效果
                    chamberPrime.applyChamberPrimeEffect(player);
                }
            }
            
            // 检查玩家是否装备了恶性扩散饰品
            Optional<SlotResult> malignantSpreadResult = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof MalignantSpread));
            
            if (malignantSpreadResult.isPresent()) {
                ItemStack malignantSpreadStack = malignantSpreadResult.get().stack();
                if (malignantSpreadStack.getItem() instanceof MalignantSpread malignantSpread) {
                    // 根据枪械类型判断是否应用效果
                    malignantSpread.applyMalignantSpreadEffects(player);
                }
            }
            
            // 检查玩家是否装备了重装火力饰品
            Optional<SlotResult> heavyFirepowerResult = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof HeavyFirepower));
            
            if (heavyFirepowerResult.isPresent()) {
                ItemStack heavyFirepowerStack = heavyFirepowerResult.get().stack();
                if (heavyFirepowerStack.getItem() instanceof HeavyFirepower heavyFirepower) {
                    // 根据枪械类型判断是否应用效果
                    heavyFirepower.applyHeavyFirepowerEffects(player);
                }
            }
            
            // 检查玩家是否装备了重口径饰品
            Optional<SlotResult> heavyCaliberTagResult = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof HeavyCaliberTag));
            
            if (heavyCaliberTagResult.isPresent()) {
                ItemStack heavyCaliberTagStack = heavyCaliberTagResult.get().stack();
                if (heavyCaliberTagStack.getItem() instanceof HeavyCaliberTag heavyCaliberTag) {
                    // 根据枪械类型判断是否应用效果
                    heavyCaliberTag.applyHeavyCaliberEffects(player);
                }
            }
            
            // 检查玩家是否装备了膛线饰品
            Optional<SlotResult> riflingResult = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof Rifling));
            
            if (riflingResult.isPresent()) {
                ItemStack riflingStack = riflingResult.get().stack();
                if (riflingStack.getItem() instanceof Rifling rifling) {
                    // 根据枪械类型判断是否应用效果
                    rifling.applyRiflingEffects(player);
                }
            }
            
            // 检查玩家是否装备了并合膛线饰品
            Optional<SlotResult> mergedRiflingResult = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof MergedRifling));
            
            if (mergedRiflingResult.isPresent()) {
                ItemStack mergedRiflingStack = mergedRiflingResult.get().stack();
                if (mergedRiflingStack.getItem() instanceof MergedRifling mergedRifling) {
                    // 根据枪械类型判断是否应用效果
                    mergedRifling.applyRiflingEffects(player);
                }
            }
            
            // 检查玩家是否装备了极恶精准饰品
            Optional<SlotResult> evilAccuracyResult = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof EvilAccuracy));
            
            if (evilAccuracyResult.isPresent()) {
                ItemStack evilAccuracyStack = evilAccuracyResult.get().stack();
                if (evilAccuracyStack.getItem() instanceof EvilAccuracy evilAccuracy) {
                    // 根据枪械类型判断是否应用效果
                    evilAccuracy.applyEffects(player);
                }
            }
            
            // 检查玩家是否装备了天火劫灭饰品
            Optional<SlotResult> heavenFireApocalypseResult = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof HeavenFireApocalypse));
            
            if (heavenFireApocalypseResult.isPresent()) {
                ItemStack heavenFireApocalypseStack = heavenFireApocalypseResult.get().stack();
                if (heavenFireApocalypseStack.getItem() instanceof HeavenFireApocalypse heavenFireApocalypse) {
                    // 根据枪械类型判断是否应用效果
                    heavenFireApocalypse.applyEffects(player);
                }
            }
            
            // 触发TACZ缓存更新，确保面板显示正确
            updateTacZCacheIfNeeded(player);
            
            // 通知CuriosItemEventHandler更新缓存
            if (player instanceof ServerPlayer serverPlayer) {
                CuriosItemEventHandler.onGunSwitchEvent(serverPlayer);
            }
        }
    }
    
    /**
     * 更新TACZ缓存（如果需要）
     * @param player 玩家
     */
    private static void updateTacZCacheIfNeeded(Player player) {
        // 获取玩家主手物品
        ItemStack mainHandItem = player.getMainHandItem();
        
        // 检查是否是枪械
        if (!(mainHandItem.getItem() instanceof com.tacz.guns.api.item.IGun)) {
            return;
        }
        
        // 只在服务端更新TACZ配件属性缓存
        if (!player.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            // 调用TACZ的缓存更新机制，触发属性重新计算
            com.tacz.guns.resource.modifier.AttachmentPropertyManager.postChangeEvent(serverPlayer, mainHandItem);
        }
    }
}