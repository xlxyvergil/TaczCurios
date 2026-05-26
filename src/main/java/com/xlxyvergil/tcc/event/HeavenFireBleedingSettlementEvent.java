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
    
    /**
     * 获取受影响的实体
     */
    public LivingEntity getEntity() {
        return entity;
    }
    
    /**
     * 判断实体是否死亡
     */
    public boolean isDead() {
        return isDead;
    }
}
