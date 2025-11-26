package com.xlxyvergil.tcc.items;

import java.util.List;

import javax.annotation.Nullable;

import com.xlxyvergil.tcc.TaczCurios;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class RiftSilver extends Item {
    
    public RiftSilver(Properties properties) {
        super(properties
            .stacksTo(64)
            .rarity(Rarity.EPIC));
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        // 添加使用效果说明
        tooltip.add(Component.translatable("item.tcc.rift_silver.usage"));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // 增加使用统计
        player.awardStat(Stats.ITEM_USED.get(this));
        
        if (!world.isClientSide && world instanceof ServerLevel serverLevel) {
            // 先检查战利品表是否可用，确保可以获取物品
            LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(
                new ResourceLocation("tcc", "rift_silver_curios")
            );
            
            if (lootTable != null) {
                LootParams.Builder builder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player);
                
                LootParams lootParams = builder.create(LootContextParamSets.CHEST);
                java.util.List<ItemStack> loot = lootTable.getRandomItems(lootParams);
                
                // 查找一个合适的TCC物品
                ItemStack selectedStack = null;
                for (ItemStack lootStack : loot) {
                    TaczCurios.LOGGER.info("RiftSilver obtained item: {} with description ID: {} and count: {}", 
                        lootStack.getItem().toString(), lootStack.getItem().getDescriptionId(), lootStack.getCount());
                    
                    // 检查是否为TCC模组物品且物品有效
                    if (lootStack.getItem().getDescriptionId().contains("tcc") && !lootStack.isEmpty()) {
                        selectedStack = lootStack.copy();
                        TaczCurios.LOGGER.info("RiftSilver selected item: {} with description ID: {} and count: {}", 
                            selectedStack.getItem().toString(), selectedStack.getItem().getDescriptionId(), selectedStack.getCount());
                        break;
                    } else {
                        TaczCurios.LOGGER.info("RiftSilver item does not match criteria or is empty: {}", 
                            lootStack.getItem().getDescriptionId());
                    }
                }
                
                // 只有在找到合适物品时才消耗裂隙碎银
                if (selectedStack != null && !selectedStack.isEmpty()) {
                    TaczCurios.LOGGER.info("RiftSilver final selected item: {} with description ID: {} and count: {}", 
                        selectedStack.getItem().toString(), selectedStack.getItem().getDescriptionId(), selectedStack.getCount());
                    
                    // 直接将物品添加到玩家背包
                    player.getInventory().placeItemBackInInventory(selectedStack);
                    
                    // 消耗掉使用的裂隙碎银
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    
                    TaczCurios.LOGGER.info("RiftSilver successfully added item to inventory: {}", selectedStack.getItem().getDescriptionId());
                    return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
                } else {
                    // 没有找到合适的物品
                    TaczCurios.LOGGER.warn("RiftSilver did not find any suitable items from loot table");
                }
            } else {
                // 战利品表为空
                TaczCurios.LOGGER.error("RiftSilver loot table is null");
            }
        }
        
        // 无法获取物品或在客户端，返回pass
        return InteractionResultHolder.pass(stack);
    }
}