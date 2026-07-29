package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccStats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

/**
 * 追踪玩家佩戴特定饰品时的存活时长（tick），写入自定义统计。
 * 每个饰品对应一个独立的自定义统计，每 tick 检查佩戴状态并递增。
 * <ul>
 *   <li>griseo / huishi_zhijuan / fanxing — 死亡不重置，累计总时长</li>
 *   <li>qishi_zhijian — 死亡重置归 0，等同于"佩戴时的自上次死亡以来"</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CurioPlaytimeTracker {

    private CurioPlaytimeTracker() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        // 每 tick 检查并递增
        if (hasCurioEquipped(player, "tcc:griseo")) {
            incrementStat(player, TccStats.PLAY_TIME_GRISEO);
        }
        if (hasCurioEquipped(player, "tcc:huishi_zhijuan")) {
            incrementStat(player, TccStats.PLAY_TIME_HUISHI_ZHIJUAN);
        }
        if (hasCurioEquipped(player, "tcc:fanxing")) {
            incrementStat(player, TccStats.PLAY_TIME_FANXING);
        }
        if (hasCurioEquipped(player, "tcc:qishi_zhijian")) {
            incrementStat(player, TccStats.PLAY_TIME_QISHI_ZHIJIAN);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // qishi_zhijian：死亡重置归 0
            player.getStats().setValue(player, Stats.CUSTOM.get(TccStats.PLAY_TIME_QISHI_ZHIJIAN), 0);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && event.getEntity() instanceof ServerPlayer player) {
            // 死亡复活后，确保 qishi_zhijian 统计保持归零（防止被克隆覆盖）
            player.getStats().setValue(player, Stats.CUSTOM.get(TccStats.PLAY_TIME_QISHI_ZHIJIAN), 0);
        }
    }

    /**
     * 递增指定自定义统计的值（+1 tick）。
     */
    private static void incrementStat(ServerPlayer player, ResourceLocation statKey) {
        Stat<ResourceLocation> stat = Stats.CUSTOM.get(statKey);
        int current = player.getStats().getValue(stat);
        player.getStats().setValue(player, stat, current + 1);
    }

    /**
     * 检查玩家是否佩戴了指定 itemId 的饰品。
     */
    private static boolean hasCurioEquipped(Player player, String itemId) {
        ICuriosItemHandler inv = CuriosApi.getCuriosInventory(player).orElse(null);
        if (inv == null) return false;

        for (var entry : inv.getCurios().entrySet()) {
            ICurioStacksHandler handler = entry.getValue();
            if (handler == null) continue;

            var stacks = handler.getStacks();
            for (int i = 0; i < stacks.getSlots(); i++) {
                ItemStack stack = stacks.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (id != null && id.toString().equals(itemId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
