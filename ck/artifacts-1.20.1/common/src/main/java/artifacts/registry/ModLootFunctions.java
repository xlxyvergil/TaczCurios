package artifacts.registry;

import artifacts.Artifacts;
import artifacts.loot.ReplaceWithLootTableFunction;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import java.util.function.Supplier;

public class ModLootFunctions {

    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = DeferredRegister.create(Artifacts.MOD_ID, Registries.LOOT_FUNCTION_TYPE);

    public static final RegistrySupplier<LootItemFunctionType> REPLACE_WITH_LOOT_TABLE = register("replace_with_loot_table", ReplaceWithLootTableFunction.Serializer::new);

    private static RegistrySupplier<LootItemFunctionType> register(String name, Supplier<Serializer<? extends LootItemFunction>> serializer) {
        return RegistrySupplier.of(LOOT_FUNCTIONS.register(name, () -> new LootItemFunctionType(serializer.get())));
    }
}
