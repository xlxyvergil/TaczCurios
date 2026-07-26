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
 * Listens for {@link PlayerEvent.ItemCraftedEvent} and increments a custom stat
 * ({@link TccStats#ITEMS_CRAFTED}) by 1 for each crafting operation.
 * <p>
 * This replaces the vanilla {@code minecraft:interact_with_crafting_table} stat,
 * which only counts opening the crafting table GUI rather than actual crafting.
 * One event firing = one craft operation, regardless of the output stack size.
 * <p>
 * Note: Refined Storage's shift-click batch crafting fires this event only once
 * for the entire batch, so batch crafts from RS may be undercounted.
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
