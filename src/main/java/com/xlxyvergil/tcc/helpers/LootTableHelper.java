package com.xlxyvergil.tcc.helpers;

import java.lang.reflect.Field;
import java.util.List;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

// 基于神秘遗物的LootTableHelper实现
public class LootTableHelper {
    public static Field isFrozenTable = null;
    public static Field isFrozenPool = null;
    public static Field lootPoolsTable = null;

    static {
        try {
            isFrozenTable = LootTable.class.getDeclaredField("isFrozen");
            isFrozenPool = LootPool.class.getDeclaredField("isFrozen");

            try {
                lootPoolsTable = LootTable.class.getDeclaredField("pools");
            } catch (NoSuchFieldException ex) {
                lootPoolsTable = LootTable.class.getDeclaredField("f_79109_");
            }

            isFrozenTable.setAccessible(true);
            isFrozenPool.setAccessible(true);
            lootPoolsTable.setAccessible(true);
        } catch (Throwable ex) {
            LogUtils.getLogger().error("FAILED TO REFLECT LOOTTABLE FIELDS", ex);
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static void unfreezePlease(LootTable table) {
        try {
            isFrozenTable.set(table, false);
            List<LootPool> poolList = (List<LootPool>)lootPoolsTable.get(table);

            for (LootPool pool : poolList) {
                unfreezePlease(pool);
            }
        } catch (Throwable ex) {
            LogUtils.getLogger().error("FAILED TO UNFREEZE LOOT TABLE", ex);
            throw new RuntimeException(ex);
        }
    }

    public static void unfreezePlease(LootPool pool) {
        try {
            isFrozenPool.set(pool, false);
        } catch (Throwable ex) {
            LogUtils.getLogger().error("FAILED TO UNFREEZE LOOT POOL", ex);
            throw new RuntimeException(ex);
        }
    }
    
    public static void addLootEntryToChest(LootTable table, ItemStack itemStack, int minCount, int maxCount, int weight) {
        try {
            // 解冻战利品表以便修改
            unfreezePlease(table);
            
            // 创建战利品项
            LootPoolEntryContainer.Builder<?> lootItem = LootItem.lootTableItem(itemStack.getItem())
                    .setWeight(weight)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(minCount, maxCount)));
            
            // 创建新的战利品池并添加到表中
            LootPool newPool = LootPool.lootPool()
                    .add(lootItem)
                    .setRolls(ConstantValue.exactly(1))
                    .build();
            
            // 使用反射获取池列表并添加新池
            List<LootPool> poolList = (List<LootPool>) lootPoolsTable.get(table);
            poolList.add(newPool);
            
        } catch (Throwable ex) {
            LogUtils.getLogger().error("FAILED TO ADD LOOT ENTRY TO CHEST", ex);
            throw new RuntimeException(ex);
        }
    }
}