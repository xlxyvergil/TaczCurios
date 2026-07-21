package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;

/**
 * 吸收饰品事件处理器（受击触发式黄心）。
 * <p>
 * 采用<strong>受击触发式</strong>（参考 GoetyRevelation 的 Apostle 减伤模式）：
 * 饰品类在 {@code LivingHurtEvent} 中调用一次 {@link #tryTriggerAbsorption}，
 * 检查装备状态 + 血量阈值 + 冷却后，赋予 ABSORPTION（黄心）效果并进入冷却。
 * 无需在 {@code curioTick} 中每 tick 调用。
 * <p>
 * 冷却数据存储在玩家 {@link net.minecraft.world.entity.Entity#getPersistentData() PersistentData} 中，
 * 由 {@link #onLivingTick} 自动递减，卸下饰品后冷却依然保留直至归零。
 * <p>
 * <h3>使用方式</h3>
 * <pre>{@code
 * // 在 LivingHurtEvent 中调用：
 * @SubscribeEvent
 * public static void onLivingHurt(LivingHurtEvent event) {
 *     LivingEntity entity = event.getEntity();
 *     CurioAbsorptionEventHandler.tryTriggerAbsorption(
 *         entity, TccItems.YOUR_ITEM.get(),
 *         TaczCuriosConfig.COMMON.curioAbsorptionTriggerHp.get(),
 *         TaczCuriosConfig.COMMON.curioAbsorptionLevel.get(),
 *         TaczCuriosConfig.COMMON.curioAbsorptionDuration.get(),
 *         TaczCuriosConfig.COMMON.curioAbsorptionCooldown.get()
 *     );
 * }
 * }</pre>
 */
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class CurioAbsorptionEventHandler {

    // ==================== NBT Key 常量 ====================
    /** 吸收效果冷却 NBT Key */
    public static final String ABSORPTION_COOLDOWN_KEY = TaczCurios.MODID + ":absorption_cooldown";

    // ==================== 核心 API ====================

    /**
     * 尝试触发吸收效果（黄心）— 受击触发式。
     * <p>
     * 在 {@code LivingHurtEvent} 中调用。仅在以下条件全部满足时生效：
     * <ol>
     *   <li>实体装备了指定饰品</li>
     *   <li>当前血量比例 ≤ 触发阈值</li>
     *   <li>冷却已结束</li>
     * </ol>
     * 触发后会赋予 ABSORPTION 效果并重新进入冷却（冷却由 {@link #onLivingTick} 自动递减）。
     *
     * @param entity            实体（LivingEntity）
     * @param curioItem         要检查的饰品物品
     * @param triggerHpRatio    触发血量阈值（0~1，如 0.25 = 25%）
     * @param absorptionLevel   吸收效果等级（1 = ABSORPTION I，4 = ABSORPTION IV）
     * @param absorptionSeconds 吸收效果持续时间（秒）
     * @param cooldownSeconds   冷却时间（秒）
     * @return 是否成功触发
     */
    public static boolean tryTriggerAbsorption(
            LivingEntity entity,
            Item curioItem,
            double triggerHpRatio,
            int absorptionLevel,
            double absorptionSeconds,
            double cooldownSeconds
    ) {
        // 1. 检查饰品是否装备
        if (!isCurioEquipped(entity, curioItem)) {
            return false;
        }

        // 2. 检查冷却（冷却由 onLivingTick 统一倒计时）
        if (entity.getPersistentData().getInt(ABSORPTION_COOLDOWN_KEY) > 0) {
            return false;
        }

        // 3. 检查血量阈值
        float hpRatio = entity.getHealth() / entity.getMaxHealth();
        if (hpRatio > triggerHpRatio) {
            return false;
        }

        // 4. 触发吸收效果
        int durationTicks = (int) (absorptionSeconds * 20);
        int amplifier = absorptionLevel - 1; // MobEffectInstance amplifier: 0 = I 级
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, durationTicks, amplifier));

        // 5. 设置冷却
        int cooldownTicks = (int) (cooldownSeconds * 20);
        entity.getPersistentData().putInt(ABSORPTION_COOLDOWN_KEY, cooldownTicks);

        return true;
    }

    /**
     * 重置指定实体的吸收冷却（立即允许再次触发）。
     */
    public static void resetCooldown(LivingEntity entity) {
        entity.getPersistentData().putInt(ABSORPTION_COOLDOWN_KEY, 0);
    }

    /**
     * 获取当前冷却剩余 tick 数。
     */
    public static int getCooldown(LivingEntity entity) {
        return entity.getPersistentData().getInt(ABSORPTION_COOLDOWN_KEY);
    }

    // ==================== Tick 事件（冷却倒计时） ====================

    /**
     * 每 tick 对所有在线玩家执行冷却倒计时。
     * <p>
     * 注意：实际的触发逻辑不在这里，而是由具体的饰品类通过
     * {@link #tryTriggerAbsorption} 主动调用。
     * 这里只负责通用冷却倒计时。
     */
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!event.getEntity().isAlive()) return;
        LivingEntity entity = event.getEntity();

        // 冷却倒计时
        int cooldown = entity.getPersistentData().getInt(ABSORPTION_COOLDOWN_KEY);
        if (cooldown > 0) {
            entity.getPersistentData().putInt(ABSORPTION_COOLDOWN_KEY, cooldown - 1);
        }
    }

    // ==================== 内部工具方法 ====================

    private static boolean isCurioEquipped(LivingEntity entity, @Nullable Item item) {
        if (item == null) return false;
        return !CurioSearchHelper.findFirstEquippedStack(entity, stack -> stack.getItem() == item).isEmpty();
    }
}
