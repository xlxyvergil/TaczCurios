package com.xlxyvergil.tcc.items.materials;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class RiftSilver extends Item {
    public RiftSilver(Properties properties) {
        super(properties
            .stacksTo(64));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.rift_silver.usage"));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        player.awardStat(Stats.ITEM_USED.get(this));
        
        if (!world.isClientSide && world instanceof ServerLevel serverLevel) {
            LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(
                new ResourceLocation("tcc", "rift_silver_curios")
            );
            
            if (lootTable != null) {
                LootParams.Builder builder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player);
                
                LootParams lootParams = builder.create(LootContextParamSets.CHEST);
                java.util.List<ItemStack> loot = lootTable.getRandomItems(lootParams);
                
                ItemStack selectedStack = null;
                for (ItemStack lootStack : loot) {
                    if (lootStack.getItem().getDescriptionId().contains("tcc") && !lootStack.isEmpty()) {
                        selectedStack = lootStack.copy();
                        break;
                    }
                }
                
                // 只有在找到合适物品时才消耗裂隙碎银
                if (selectedStack != null && !selectedStack.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(selectedStack);
                    
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    
                    return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
                }
            } 
        }
        
        return InteractionResultHolder.pass(stack);
    }
}