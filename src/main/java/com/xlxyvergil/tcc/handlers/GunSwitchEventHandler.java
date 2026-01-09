package com.xlxyvergil.tcc.handlers;

import com.tacz.guns.api.event.common.GunDrawEvent;
import com.xlxyvergil.tcc.items.ItemBaseCurio;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import com.xlxyvergil.tcc.TaczCurios;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GunSwitchEventHandler {
    
    @SubscribeEvent
    public static void onGunDraw(GunDrawEvent event) {
        // 玩家切换武器后检查所有装备的饰品并应用效果
        if (event.getEntity() instanceof Player player) {
            // 获取玩家所有的Curios饰品并遍历所有装备的饰品
            CuriosApi.getCuriosInventory(player).ifPresent(curiosInventory -> {
                curiosInventory.getCurios().forEach((slotIdentifier, stacksHandler) -> {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (!stack.isEmpty() && stack.getItem() instanceof ItemBaseCurio curioItem) {
                            // 直接调用基类的效果应用方法
                            curioItem.applyGunSwitchEffect(player);
                        }
                    }
                });
            });
            
            // 枪械切换后更新TACZ属性缓存
            if (player instanceof ServerPlayer serverPlayer) {
                updateTaczCache(serverPlayer);
            }
        }
    }
    
    /**
     * 更新TACZ缓存
     * 当枪械切换时调用此方法触发属性重新计算
     * @param player 玩家实体
     */
    private static void updateTaczCache(ServerPlayer player) {
        // 触发TACZ的附件属性变更事件，强制重新计算属性
        com.tacz.guns.resource.modifier.AttachmentPropertyManager.postChangeEvent(player, player.getMainHandItem());
    }
}