package com.xlxyvergil.tcc.handlers;

import com.xlxyvergil.tcc.event.HeavenFireBleedingSettlementEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HeavenFireSettlementHandler {
    
    @SubscribeEvent
    public void onHeavenFireBleedingSettlement(HeavenFireBleedingSettlementEvent event) {
        LivingEntity entity = event.getEntity();
        
        if (!(entity instanceof Player)) {
            return;
        }
        
        if (event.isDead()) {
            return;
        }
    }
}
