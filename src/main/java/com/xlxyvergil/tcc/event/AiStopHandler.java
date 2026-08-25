package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.util.AiStopHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * AI 停止（定身）执行器 - 浮生神之键线。
 * <p>
 * 每 tick 检查实体 persistentData 中的 {@link AiStopHelper#AI_STOP_UNTIL_KEY}，
 * 未到截止时间则停止寻路并清空速度，达到「定身 5 秒」效果。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AiStopHandler {

    private AiStopHandler() {
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        CompoundTag data = entity.getPersistentData();
        long until = data.getLong(AiStopHelper.AI_STOP_UNTIL_KEY);
        if (until <= 0) {
            return;
        }
        if (entity.level().getGameTime() >= until) {
            data.remove(AiStopHelper.AI_STOP_UNTIL_KEY);
            return;
        }
        if (entity instanceof Mob mob) {
            mob.getNavigation().stop();
        }
        entity.setDeltaMovement(0, 0, 0);
    }
}
