package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.event.common.GunDrawEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.util.CurioEffectRefresher;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GunSwitchEventHandler {
    
    @SubscribeEvent
    public static void onGunDraw(GunDrawEvent event) {
        // 玩家切换武器后统一重建所有饰品效果，并按当前手持枪械类型确保属性仅在对应枪械下生效
        if (event.getEntity() instanceof Player player) {
            CurioEffectRefresher.refreshAll(player);
        }
    }
}
