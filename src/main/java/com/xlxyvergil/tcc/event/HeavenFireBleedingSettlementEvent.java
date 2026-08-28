package com.xlxyvergil.tcc.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * 天火流血结算事件 - 在天火流血效果结束时触发
 */
public class HeavenFireBleedingSettlementEvent extends Event {
    
    private final LivingEntity entity;
    private final boolean isDead;
    
    public HeavenFireBleedingSettlementEvent(LivingEntity entity, boolean isDead) {
        this.entity = entity;
        this.isDead = isDead;
    }
    
    public LivingEntity getEntity() {
        return entity;
    }
    
    public boolean isDead() {
        return isDead;
    }
}
