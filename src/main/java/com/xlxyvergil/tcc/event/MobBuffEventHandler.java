package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 怪物虚数抗性Buff事件处理器。
 * 在非玩家实体生成时为其添加虚数抗性Buff。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobBuffEventHandler {

    /** 9999小时对应的tick数 */
    private static final long DURATION_TICKS = 9999L * 3600L * 20L;

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof LivingEntity living)) return;

        // 非玩家实体生成时自动获取该Buff
        if (living instanceof Player) return;

        living.addEffect(new MobEffectInstance(
            TccMobEffects.MOB_IMAGINARY_RESISTANCE_BUFF.get(),
            (int) DURATION_TICKS,
            0,
            false,
            false,
            false
        ));
    }
}
