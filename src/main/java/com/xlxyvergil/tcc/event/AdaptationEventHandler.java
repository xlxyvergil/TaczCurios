package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.capability.CurioAdaptationCapability;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 饰品适应效果：LivingIncomingDamageEvent 对活跃适应实例执行减免，死亡时清空适应数据。
 */
@EventBusSubscriber(modid = "tcc")
public class AdaptationEventHandler {

    /**
     * 优先级 LOW，与 L2Hostility AdaptingTrait 同级。
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        var handler = CurioAdaptationCapability.of(event.getEntity());
        if (!handler.hasAny()) return;

        // bypass 检查（与 L2Hostility AdaptingTrait 一致）
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)
         || event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS))
            return;

        String msgId = event.getSource().getMsgId();
        float[] ref = new float[]{event.getAmount()};
        handler.processAll(msgId, ref);
        event.setAmount(ref[0]);
    }

    /**
     * 非玩家实体死亡时清空适应数据，避免残留。
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        CurioAdaptationCapability.of(event.getEntity()).clear();
    }

    /**
     * 玩家的适应数据未设置 copyOnDeath，重生后即为空，这里无需额外处理。
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            CurioAdaptationCapability.of(event.getEntity()).clear();
        }
    }
}
