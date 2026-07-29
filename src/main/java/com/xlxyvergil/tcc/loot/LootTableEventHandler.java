package com.xlxyvergil.tcc.loot;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

/**
 * 战利品表事件处理器 — 在下界要塞、堡垒遗迹和末地城战利品箱中
 * 添加融合容器（FusionVessel），内含随机数量的内融核心（CoreFusion）。
 */
public class LootTableEventHandler {

    private static final Set<ResourceLocation> NETHER_TABLES = Set.of(
            new ResourceLocation("minecraft:chests/nether_bridge"),
            new ResourceLocation("minecraft:chests/bastion_treasure"),
            new ResourceLocation("minecraft:chests/bastion_other"),
            new ResourceLocation("minecraft:chests/bastion_bridge"),
            new ResourceLocation("minecraft:chests/bastion_hoglin_stable")
    );

    private static final ResourceLocation END_CITY_TABLE =
            new ResourceLocation("minecraft:chests/end_city_treasure");

    /** 自定义战利品函数类型 — 类加载时自动注册到 BuiltInRegistries */
    public static final LootItemFunctionType SET_FUSION_COUNT = register(
            "set_fusion_count", new SetFusionCountFunction.Serializer());

    private static LootItemFunctionType register(String id, Serializer<? extends LootItemFunction> serializer) {
        return Registry.register(
                BuiltInRegistries.LOOT_FUNCTION_TYPE,
                new ResourceLocation(TaczCurios.MODID, id),
                new LootItemFunctionType(serializer));
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation id = event.getName();
        if (id == null) return;

        float chance;
        int min, max;

        if (NETHER_TABLES.contains(id)) {
            chance = TaczCuriosConfig.COMMON.fusionVesselNetherChance.get().floatValue();
            min = TaczCuriosConfig.COMMON.fusionVesselNetherMin.get();
            max = TaczCuriosConfig.COMMON.fusionVesselNetherMax.get();
        } else if (END_CITY_TABLE.equals(id)) {
            chance = TaczCuriosConfig.COMMON.fusionVesselEndChance.get().floatValue();
            min = TaczCuriosConfig.COMMON.fusionVesselEndMin.get();
            max = TaczCuriosConfig.COMMON.fusionVesselEndMax.get();
        } else {
            return;
        }

        if (chance <= 0 || min > max) return;

        // 将几率转换为负数滚动范围：UniformGenerator.between(minRoll, 1)
        // 只有 roll >= 1 时才生成物品，概率 = 1 / (1 - minRoll + 1) = 1 / (2 - minRoll)
        // 已知 chance = 1/N → minRoll = 2 - N
        int n = (int) Math.round(1.0 / chance);
        int minRoll = 2 - n;

        LootPool pool = LootPool.lootPool()
                .name("tcc_fusion_vessel")
                .setRolls(UniformGenerator.between(minRoll, 1))
                .add(LootItem.lootTableItem(TccItems.FUSION_VESSEL)
                        .apply(SetFusionCountFunction.builder(min, max)))
                .build();

        event.getTable().addPool(pool);
    }
}
