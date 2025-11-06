package artifacts.fabric.registry;

import artifacts.Artifacts;
import artifacts.loot.ConfigValueChance;
import artifacts.loot.ReplaceWithLootTableFunction;
import artifacts.registry.ModLootTables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;

public class ModLootTablesFabric {

    public static void onLootTableLoad(ResourceLocation id, LootTable.Builder supplier) {
        if (ModLootTables.INJECTED_LOOT_TABLES.contains(id)) {
            supplier.withPool(LootPool.lootPool().add(LootTableReference.lootTableReference(getInjectName(id))));
        }
        if (ModLootTables.ARCHAEOLOGY_LOOT_TABLES.contains(id)) {
            supplier.modifyPools(pool -> pool.apply(ReplaceWithLootTableFunction
                    .replaceWithLootTable(getInjectName(id))
                    .when(ConfigValueChance.archaeologyChance()))
            );
        }
    }

    private static ResourceLocation getInjectName(ResourceLocation name) {
        return Artifacts.id("inject/" + name.getPath());
    }
}
