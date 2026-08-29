package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.evolution.AchievementConditionMatcher;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.RuleAdvancementMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

/**
 * 鱼类食用次数统计。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FishFoodStatHandler {

    private FishFoodStatHandler() {
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack stack = event.getItem();
        if (stack.isEmpty()) {
            return;
        }
        if (!isFishFood(stack)) {
            return;
        }

        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(AchievementDefinitions.TRIGGER_FISH_FOOD_EATEN)) {
            if (!def.isEnabled()) continue;
            if (RuleAdvancementMapping.isAdvancementDone(player, def.id())) continue;
            if (!RuleAdvancementMapping.arePrerequisitesMet(player, def)) continue;
            if (!AchievementConditionMatcher.matchesStatBiomeConditions(player, def)) continue;

            RuleAdvancementMapping.awardSteps(player, def.id(), def.targetCount(), 1);
        }
    }

    private static boolean isFishFood(ItemStack stack) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key == null) {
            return false;
        }
        String itemId = key.toString();
        Set<String> fishFoods = new HashSet<>(TaczCuriosConfig.COMMON.fishFoodItems.get());
        return fishFoods.contains(itemId);
    }
}
