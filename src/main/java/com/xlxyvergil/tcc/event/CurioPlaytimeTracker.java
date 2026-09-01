package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.capability.TccPlayerDataCapability;
import com.xlxyvergil.tcc.evolution.AchievementConditionMatcher;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.RuleAdvancementMapping;
import com.xlxyvergil.tcc.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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


@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CurioPlaytimeTracker {

    
    private static final int SYNC_INTERVAL = 100;
    
    private static final int ACHIEVEMENT_CHECK_INTERVAL = 20;

    private CurioPlaytimeTracker() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        long t = player.level().getGameTime();
        boolean wearing = false;

        if (hasCurioEquipped(player, "tcc:griseo")) {
            TccPlayerDataCapability.incrementPlayTimeGriseo(player);
            wearing = true;
        }
        if (hasCurioEquipped(player, "tcc:huishi_zhijuan")) {
            TccPlayerDataCapability.incrementPlayTimeHuishiZhijuan(player);
            wearing = true;
        }
        if (hasCurioEquipped(player, "tcc:fanxing")) {
            TccPlayerDataCapability.incrementPlayTimeFanxing(player);
            wearing = true;
        }
        if (hasCurioEquipped(player, "tcc:qishi_zhijian")) {
            TccPlayerDataCapability.incrementPlayTimeQishiZhijian(player);
            wearing = true;
        }

        if (wearing && t % SYNC_INTERVAL == 0) {
            NetworkHandler.syncPlayTime(player);
        }
        if (t % ACHIEVEMENT_CHECK_INTERVAL == 0) {
            awardPlaytimeAchievements(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            resetQishiPlaytime(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && event.getEntity() instanceof ServerPlayer player) {
            resetQishiPlaytime(player);
        }
    }

    private static void resetQishiPlaytime(ServerPlayer player) {
        TccPlayerDataCapability.setPlayTimeQishiZhijian(player, 0);
        NetworkHandler.syncPlayTime(player);
    }

    private static void awardPlaytimeAchievements(ServerPlayer player) {
        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(AchievementDefinitions.TRIGGER_PLAY_TIME)) {
            if (!def.isEnabled()) continue;
            if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) continue;
            if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) continue;
            if (!AchievementConditionMatcher.matchesStatBiomeConditions(player, def)) continue;

            AchievementDefinitions.AchievementConditions conds = def.conditions();
            if (conds == null || conds.stat() == null) continue;
            if (TccPlayerDataCapability.getCustomStat(player, conds.stat()) >= def.targetCount()) {
                RuleAdvancementMapping.awardAll(player, def.id(), def.targetCount());
            }
        }
    }

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
