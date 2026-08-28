package com.xlxyvergil.tcc.util;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;

public class TacDamageHelper {
    
    public static boolean isValidGunDamage(EntityHurtByGunEvent.Post event) {
        // 只在服务端执行
        if (event.getLogicalSide() != LogicalSide.SERVER) {
            return false;
        }
        
        // 攻击者不能为空
        return event.getAttacker() != null;
    }
    
    /** 获取枪械伤害攻击者，无效时返回 null。 */
    public static LivingEntity getAttacker(EntityHurtByGunEvent.Post event) {
        if (!isValidGunDamage(event)) {
            return null;
        }
        return event.getAttacker();
    }
    
    public static boolean isHeadShot(EntityHurtByGunEvent.Post event) {
        return event.isHeadShot();
    }
    
    public static boolean isHeadShotKill(EntityKillByGunEvent event) {
        return event.isHeadShot();
    }
    
    public static boolean isAiming(IGunOperator operator) {
        return operator.getSynIsAiming();
    }
}
