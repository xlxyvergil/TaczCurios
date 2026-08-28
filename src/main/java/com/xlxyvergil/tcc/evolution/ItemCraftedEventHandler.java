package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccStats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 监听合成事件，每次合成使自定义统计 ITEMS_CRAFTED +1。
 * 替代原版 minecraft:interact_with_crafting_table（只统计打开合成台界面而非实际合成）；Refined Storage 的 shift 批量合成只触发一次，可能被低估。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ItemCraftedEventHandler {

    private ItemCraftedEventHandler() {}

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() == null) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ResourceLocation statKey = TccStats.ITEMS_CRAFTED;
        int current = player.getStats().getValue(Stats.CUSTOM.get(statKey));
        player.getStats().setValue(player, Stats.CUSTOM.get(statKey), current + 1);
    }
}
