package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.util.AiStopHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 每 tick 检查 persistentData 中的截止时间，未到则对 Mob 停用寻路/追击/攻击并清空速度；
 * 定身结束按记录的原始 NoAI 状态恢复，避免误开原本关闭的 AI。
 * <p>
 * 为避免对全世界生物逐 tick 读取 persistentData（首次访问会为每个实体创建 CompoundTag），
 * 这里用 {@link AiStopHelper#hasAnyActive()} 做快速路径：没有任何定身进行中时直接跳过全部实体。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AiStopHandler {

    /**
     * 世界加载后的「强制全量扫描」截止游戏刻：用于回收重启前遗留、未登记进 ACTIVE_STOPS 的定身。
     */
    private static long rescanUntilGameTime;

    private AiStopHandler() {
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide()) {
            return;
        }
        // 清空登记并保留一段强制扫描窗口，让遗留 NBT 被重新登记。
        AiStopHelper.clearAllActive();
        rescanUntilGameTime = level.getGameTime() + 200;
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        // 快速路径：没有任何定身进行中时，无需为全部生物创建/读取 persistentData。
        if (!AiStopHelper.hasAnyActive() && entity.level().getGameTime() >= rescanUntilGameTime) {
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
            AiStopHelper.clearActive(entity);
            return;
        }
        boolean stopped = entity.level().getGameTime() < until;
        if (!(entity instanceof Mob mob)) {
            // 非 Mob 实体现无 AI 可停，仅清空速度使其定在原地
            if (stopped) {
                entity.setDeltaMovement(0, 0, 0);
                AiStopHelper.markActive(entity);
            } else {
                data.remove(AiStopHelper.AI_STOP_UNTIL_KEY);
                AiStopHelper.clearActive(entity);
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
            AiStopHelper.markActive(entity);
        } else {
            // 定身结束：恢复原始 AI 状态并清理标记
            if (data.contains(AiStopHelper.AI_STOP_PREV_NOAI_KEY)) {
                mob.setNoAi(data.getBoolean(AiStopHelper.AI_STOP_PREV_NOAI_KEY));
                data.remove(AiStopHelper.AI_STOP_PREV_NOAI_KEY);
            }
            data.remove(AiStopHelper.AI_STOP_UNTIL_KEY);
            AiStopHelper.clearActive(entity);
        }
    }

    /** 实体死亡后无需再维持定身登记。 */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        AiStopHelper.clearActive(event.getEntity());
    }

    /** 实体离开世界（跨维度、卸载）后清除登记，避免集合残留导致快速路径失效。 */
    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity living) {
            AiStopHelper.clearActive(living);
        }
    }
}
