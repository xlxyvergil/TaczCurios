package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.util.CurioGrantHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class AchievementRewards {
    private AchievementRewards() {}

    public static boolean execute(Player player, AchievementDefinitions.AchievementDef def) {
        AchievementDefinitions.Reward reward = def.reward();
        if (reward == null) return false;

        if (reward.isGrant()) {
            return executeGrant(player, reward);
        }
        // evolve 奖励不再自动原地替换：改为玩家在铁砧用「已绑定饰品 + 崩坏结晶」手动合成（见 AnvilEvolutionHandler）
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

}
