package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.curios.HeavenFireApocalypse;
import com.xlxyvergil.tcc.items.curios.HeavenFireJudgment;
import com.xlxyvergil.tcc.items.curios.Salvation;
import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 天火系列饰品血量变化监听器
 * 仅负责监听血量变化事件，具体逻辑由饰品类自行处理
 */
@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeavenFireHealthListener {
    
    /**
     * 监听受伤事件 - 处理救世饰品的伤害降低
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null || target.level().isClientSide()) {
            return;
        }
        
        // 救世：采用苏的限伤机制，读取降低比例计算保留伤害上限（可配置，默认 0.5 = 降低50%）
        if (Salvation.hasSalvationEquipped(target) && GunTypeChecker.isHoldingPistol(target)) {
            float cap = event.getAmount() * (float) (1 - TaczCuriosConfig.COMMON.salvationDamageReduction.get());
            DamageResistanceHelper.setDamageCap(target, cap);
        }
        
        // 通知饰品类处理血量变化
        HeavenFireApocalypse.onHealthChanged(target);
        HeavenFireJudgment.onHealthChanged(target);
    }
    
    /**
     * 监听治疗事件
     */
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide()) {
            return;
        }
        
        // 通知饰品类处理血量变化
        HeavenFireApocalypse.onHealthChanged(entity);
        HeavenFireJudgment.onHealthChanged(entity);
    }
}
