package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.capability.CurioAdaptationCapability;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 饰品适应效果：LivingHurtEvent 对活跃适应实例执行减免，死亡时清空适应数据。
 */
@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AdaptationEventHandler {

    /**
     * 优先级 LOW，与 L2Hostility AdaptingTrait 同级。
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurt(LivingHurtEvent event) {
        event.getEntity().getCapability(CurioAdaptationCapability.CAPABILITY).ifPresent(handler -> {
            if (!handler.hasAny()) return;

            // bypass 检查（与 L2Hostility AdaptingTrait 一致）
            if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)
             || event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS))
                return;

            String msgId = event.getSource().getMsgId();
            float[] ref = new float[]{event.getAmount()};
            handler.processAll(msgId, ref);
            event.setAmount(ref[0]);
        });
    }

    /**
     * 非玩家实体死亡时清空适应数据，避免残留。
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        event.getEntity().getCapability(CurioAdaptationCapability.CAPABILITY).ifPresent(CurioAdaptationCapability.Handler::clear);
    }

    /**
     * 玩家死亡复活时 Forge 已自动复制 Capability 数据，这里清空新实体数据，使适应记忆随死亡重置。
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getEntity().getCapability(CurioAdaptationCapability.CAPABILITY).ifPresent(CurioAdaptationCapability.Handler::clear);
        }
    }
}
