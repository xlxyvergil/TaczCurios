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
 * 未到截止时间则对 {@link Mob} 调用 {@code setNoAi(true)} 暂停其 goalSelector
 * （停止寻路/追击/攻击/施法等 AI），并在每 tick 清空速度，达到「真·停止 AI」效果。
 * 定身结束时依据之前记录的原始 NoAI 状态恢复，避免误开普通生物原本关闭的 AI。
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
            // 防御性清理：若残留恢复标记（例如异常情况下 key 丢失），恢复 AI 并移除标记
            if (data.contains(AiStopHelper.AI_STOP_PREV_NOAI_KEY)) {
                if (entity instanceof Mob mob) {
                    mob.setNoAi(data.getBoolean(AiStopHelper.AI_STOP_PREV_NOAI_KEY));
                }
                data.remove(AiStopHelper.AI_STOP_PREV_NOAI_KEY);
            }
            return;
        }
        boolean stopped = entity.level().getGameTime() < until;
        if (!(entity instanceof Mob mob)) {
            // 非 Mob 实体现无 AI 可停，仅清空速度使其定在原地
            if (stopped) {
                entity.setDeltaMovement(0, 0, 0);
            } else {
                data.remove(AiStopHelper.AI_STOP_UNTIL_KEY);
            }
            return;
        }
        if (stopped) {
            // 首次冻结时记录原始 NoAI 状态，便于定身结束后恢复
            if (!data.contains(AiStopHelper.AI_STOP_PREV_NOAI_KEY)) {
                data.putBoolean(AiStopHelper.AI_STOP_PREV_NOAI_KEY, mob.isNoAi());
            }
            mob.setNoAi(true);
            mob.getNavigation().stop();
            mob.setDeltaMovement(0, 0, 0);
        } else {
            // 定身结束：恢复原始 AI 状态并清理标记
            if (data.contains(AiStopHelper.AI_STOP_PREV_NOAI_KEY)) {
                mob.setNoAi(data.getBoolean(AiStopHelper.AI_STOP_PREV_NOAI_KEY));
                data.remove(AiStopHelper.AI_STOP_PREV_NOAI_KEY);
            }
            data.remove(AiStopHelper.AI_STOP_UNTIL_KEY);
        }
    }
}
