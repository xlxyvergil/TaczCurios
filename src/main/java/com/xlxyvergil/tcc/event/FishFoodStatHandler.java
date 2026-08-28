package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccStats;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

/**
 * 统计食用鱼类食物（空梦成就 tcc:fish_food_eaten）：生/熟鳕鱼、生/熟鲑鱼、河豚、热带鱼，吃完 +1。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FishFoodStatHandler {

    private static final Set<Item> FISH_FOODS = Set.of(
            Items.COD, Items.COOKED_COD,
            Items.SALMON, Items.COOKED_SALMON,
            Items.PUFFERFISH, Items.TROPICAL_FISH);

    private FishFoodStatHandler() {
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        ItemStack stack = event.getItem();
        if (!stack.isEmpty() && FISH_FOODS.contains(stack.getItem())) {
            player.awardStat(Stats.CUSTOM.get(TccStats.FISH_FOOD_EATEN));
        }
    }
}
