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
 * 饰品类在 LivingHurtEvent 中调用 tryTriggerAbsorption，检查装备 + 血量阈值 + 冷却后
 * 赋予 ABSORPTION（黄心）并进入冷却；冷却数据存于玩家 PersistentData，由 onLivingTick 自动递减。
 */
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class CurioAbsorptionEventHandler {

    public static final String ABSORPTION_COOLDOWN_KEY = TaczCurios.MODID + ":absorption_cooldown";

    /**
     * 尝试触发吸收效果（黄心），在 LivingHurtEvent 中调用。
     * 仅当实体装备了指定饰品、血量比例 ≤ 触发阈值、冷却已结束三者同时满足时生效；
     * 触发后赋予 ABSORPTION 并重新进入冷却（冷却由 onLivingTick 自动递减），返回是否成功触发。
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

    // Tick 事件（冷却倒计时）

    /**
     * 每 tick 对所有在线玩家执行冷却倒计时。
     * 实际触发逻辑不在本类，而是由具体饰品类调用 tryTriggerAbsorption；这里只负责通用冷却倒计时。
     */
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!event.getEntity().isAlive()) return;
        LivingEntity entity = event.getEntity();

        int cooldown = entity.getPersistentData().getInt(ABSORPTION_COOLDOWN_KEY);
        if (cooldown > 0) {
            entity.getPersistentData().putInt(ABSORPTION_COOLDOWN_KEY, cooldown - 1);
        }
    }

    // 内部工具方法

    private static boolean isCurioEquipped(LivingEntity entity, @Nullable Item item) {
        if (item == null) return false;
        return !CurioSearchHelper.findFirstEquippedStack(entity, stack -> stack.getItem() == item).isEmpty();
    }
}
