package com.xlxyvergil.tcc.util;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;

/**
 * TACZ 伤害事件工具类
 */
public class TacDamageHelper {
    
    /**
     * 检查是否为有效的 TACZ 枪械伤害事件。
     */
    public static boolean isValidGunDamage(EntityHurtByGunEvent.Post event) {
        // 只在服务端执行
        if (event.getLogicalSide() != LogicalSide.SERVER) {
            return false;
        }
        
        // 攻击者不能为空
        return event.getAttacker() != null;
    }
    
    /**
     * 获取枪械伤害的攻击者，如果无效则返回 null。
     */
    public static LivingEntity getAttacker(EntityHurtByGunEvent.Post event) {
        if (!isValidGunDamage(event)) {
            return null;
        }
        return event.getAttacker();
    }
    
    /**
     * 检查是否为爆头伤害。
     */
    public static boolean isHeadShot(EntityHurtByGunEvent.Post event) {
        return event.isHeadShot();
    }
    
    /**
     * 检查是否为爆头击杀。
     */
    public static boolean isHeadShotKill(EntityKillByGunEvent event) {
        return event.isHeadShot();
    }
    
    /**
     * 检查玩家是否正在瞄准。
     */
    public static boolean isAiming(IGunOperator operator) {
        return operator.getSynIsAiming();
    }
}
