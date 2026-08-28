package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.items.curios.bound.HeavenFireApocalypse;
import com.xlxyvergil.tcc.items.curios.bound.HeavenFireJudgment;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 天火系列饰品血量变化监听：仅监听血量变化事件，具体逻辑由饰品类自行处理。
 */
@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeavenFireHealthListener {
    
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null || target.level().isClientSide()) {
            return;
        }
        
        // 救世的常驻比例减伤已迁移至 Salvation#onPlayerTick（仅手枪，可配置，对 setHealth 亦生效）
        // 通知饰品类处理血量变化
        HeavenFireApocalypse.onHealthChanged(target);
        HeavenFireJudgment.onHealthChanged(target);
    }
    
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
