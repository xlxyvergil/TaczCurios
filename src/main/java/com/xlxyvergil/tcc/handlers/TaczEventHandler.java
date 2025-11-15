package com.xlxyvergil.tcc.handlers;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.HeavenFireApocalypse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class TaczEventHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TaczEventHandler.class);
    
    // 定义需要添加我们战利品的战利品表
    private static final List<ResourceLocation> DUNGEON_LOOT_TABLES = Arrays.asList(
            BuiltInLootTables.SIMPLE_DUNGEON,
            BuiltInLootTables.ABANDONED_MINESHAFT,
            BuiltInLootTables.NETHER_BRIDGE,
            BuiltInLootTables.STRONGHOLD_CORRIDOR,
            BuiltInLootTables.STRONGHOLD_CROSSING,
            BuiltInLootTables.STRONGHOLD_LIBRARY,
            BuiltInLootTables.DESERT_PYRAMID,
            BuiltInLootTables.JUNGLE_TEMPLE,
            BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER,
            BuiltInLootTables.BURIED_TREASURE,
            BuiltInLootTables.BASTION_TREASURE,
            BuiltInLootTables.BASTION_OTHER,
            BuiltInLootTables.BASTION_BRIDGE,
            BuiltInLootTables.END_CITY_TREASURE,
            BuiltInLootTables.WOODLAND_MANSION,
            BuiltInLootTables.UNDERWATER_RUIN_BIG,
            BuiltInLootTables.UNDERWATER_RUIN_SMALL,
            BuiltInLootTables.SHIPWRECK_TREASURE,
            BuiltInLootTables.SHIPWRECK_SUPPLY,
            BuiltInLootTables.PILLAGER_OUTPOST,
            BuiltInLootTables.VILLAGE_WEAPONSMITH,
            BuiltInLootTables.VILLAGE_TOOLSMITH,
            BuiltInLootTables.VILLAGE_ARMORER
    );
    
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        // 检查当前加载的战利品表是否是我们想要添加物品的列表之一
        if (DUNGEON_LOOT_TABLES.contains(event.getName())) {
            LOGGER.info("Adding TaczCurios loot to loot table: {}", event.getName());
            
            // 创建一个新的战利品池，引用我们的战利品表
            LootPool pool = LootPool.lootPool()
                    .name("tcc_curios") // 添加池名称
                    .add(LootTableReference.lootTableReference(new ResourceLocation(TaczCurios.MODID, "tcc_curios_chest"))
                            .setWeight(1))
                    .setRolls(ConstantValue.exactly(1)) // 固定roll 1次
                    .build();
            
            // 将池添加到战利品表中
            event.getTable().addPool(pool);
            
            LOGGER.info("Successfully added TaczCurios loot pool to loot table: {}", event.getName());
        }
    }
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            // 检查并更新天火劫灭对周围玩家的buff
            HeavenFireApocalypse.tickNearbyPlayerBuffs(player);
        }
    }
}