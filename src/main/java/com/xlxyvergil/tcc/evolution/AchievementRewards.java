package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.CurioGrantHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Executes rewards (grant item or evolve item) when an achievement is completed.
 */
public final class AchievementRewards {
    private AchievementRewards() {}

    /** Execute the reward defined in an achievement definition. */
    public static boolean execute(Player player, AchievementDefinitions.AchievementDef def) {
        AchievementDefinitions.Reward reward = def.reward();
        if (reward == null) return false;

        if (reward.isGrant()) {
            return executeGrant(player, reward);
        } else if (reward.isEvolve()) {
            return executeEvolve(player, reward);
        }
        return false;
    }

    private static boolean executeGrant(Player player, AchievementDefinitions.Reward reward) {
        if (reward.item() == null) return false;
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(reward.item()));
        if (item == null) return false;

        ItemStack stack = new ItemStack(item);
        if (reward.shouldBind()) {
            LivingDeathEventHandler.bindToPlayer(stack, player);
        }
        return CurioGrantHelper.give(player, stack, reward.getOverflow());
    }

    private static boolean executeEvolve(Player player, AchievementDefinitions.Reward reward) {
        if (reward.item() == null || reward.to() == null) return false;
        Item toItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(reward.to()));
        if (toItem == null) return false;

        boolean ok = EvolutionExecutor.evolve(player,
                stack -> reward.item().equals(itemId(stack)),
                () -> new ItemStack(toItem),
                EvolutionExecutor.NbtMode.COPY_ALL, java.util.Collections.emptyList(),
                (oldStack, newStack) -> {
                    LivingDeathEventHandler.resetCapCountersForItem(reward.to(), newStack);
                    if (reward.shouldBind()) {
                        LivingDeathEventHandler.bindToPlayer(newStack, player);
                    }
                }, true);

        // Execute linked evolves
        if (ok && reward.linkedEvolves() != null) {
            for (AchievementDefinitions.LinkedEvolveRef linked : reward.linkedEvolves()) {
                Item linkedTo = ForgeRegistries.ITEMS.getValue(new ResourceLocation(linked.to()));
                if (linkedTo == null) continue;
                EvolutionExecutor.evolve(player,
                        stack -> linked.item().equals(itemId(stack)),
                        () -> new ItemStack(linkedTo),
                        EvolutionExecutor.NbtMode.COPY_ALL, java.util.Collections.emptyList(),
                        (oldStack, newStack) -> LivingDeathEventHandler.resetCapCountersForItem(linked.to(), newStack),
                        true);
            }
        }

        return ok;
    }

    private static String itemId(ItemStack stack) {
        if (stack.isEmpty()) return "";
        var key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key != null ? key.toString() : "";
    }
}
