package com.xlxyvergil.tcc.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 天火流血结算事件处理器
 */
public class HeavenFireSettlementHandler {
    
    @SubscribeEvent
    public void onHeavenFireBleedingSettlement(HeavenFireBleedingSettlementEvent event) {
        LivingEntity entity = event.getEntity();
        
        // 只处理玩家
        if (!(entity instanceof Player)) {
            return;
        }
        
        // 如果玩家死亡,不给予奖励
        if (event.isDead()) {
            return;
        }
    }
}
