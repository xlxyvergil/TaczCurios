package com.xlxyvergil.tcc.loot;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Set;

/** 在下界要塞、堡垒遗迹与末地城战利品箱中随机添加融合容器（FusionVessel）。 */
public class LootTableEventHandler {

    private static final Set<ResourceLocation> NETHER_TABLES = Set.of(
            ResourceLocation.parse("minecraft:chests/nether_bridge"),
            ResourceLocation.parse("minecraft:chests/bastion_treasure"),
            ResourceLocation.parse("minecraft:chests/bastion_other"),
            ResourceLocation.parse("minecraft:chests/bastion_bridge"),
            ResourceLocation.parse("minecraft:chests/bastion_hoglin_stable")
    );

    private static final ResourceLocation END_CITY_TABLE =
            ResourceLocation.parse("minecraft:chests/end_city_treasure");

    /**
     * 自定义战利品函数类型。
     *
     * <p>原先在类初始化时直接 {@code Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, ...)}，
     * 但 1.21.1 的注册表在 RegisterEvent 阶段结束后即被冻结，而本类由
     * {@code FMLCommonSetupEvent} 才触发类加载，必然抛
     * "Registry is already frozen"。改为 DeferredRegister，交由 RegisterEvent 注册。
     */
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTION_TYPES =
            DeferredRegister.create(BuiltInRegistries.LOOT_FUNCTION_TYPE, TaczCurios.MODID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetFusionCountFunction>> SET_FUSION_COUNT =
            LOOT_FUNCTION_TYPES.register("set_fusion_count", () -> new LootItemFunctionType<>(SetFusionCountFunction.CODEC));

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
