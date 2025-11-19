package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.items.ItemBaseCurio;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;

public class RiftSilver extends ItemBaseCurio {
    
    public RiftSilver(Properties properties) {
        super(properties
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!world.isClientSide && world instanceof ServerLevel serverLevel) {
            // 消耗掉使用的裂隙碎银
            stack.shrink(1);
            
            // 从战利品表中随机抽取一个饰品
            LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(
                new ResourceLocation("tcc", "tcc_curios_chest")
            );
            
            if (lootTable != null) {
                LootParams.Builder builder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player);
                
                LootParams lootParams = builder.create(LootContextParamSets.CHEST);
                java.util.List<ItemStack> loot = lootTable.getRandomItems(lootParams);
                
                // 给予玩家随机获得的饰品
                for (ItemStack lootStack : loot) {
                    if (!player.getInventory().add(lootStack)) {
                        // 如果背包满了，将物品掉落在玩家脚下
                        player.drop(lootStack, false);
                    }
                }
            }
            
            return InteractionResultHolder.success(stack);
        }
        
        return InteractionResultHolder.pass(stack);
    }
}