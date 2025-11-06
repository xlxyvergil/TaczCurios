package artifacts.loot;

import artifacts.Artifacts;
import artifacts.registry.ModLootFunctions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ReplaceWithLootTableFunction extends LootItemConditionalFunction {

    private final ResourceLocation lootTable;

    public ReplaceWithLootTableFunction(LootItemCondition[] conditions, ResourceLocation lootTable) {
        super(conditions);
        this.lootTable = lootTable;
    }

    @Override
    public LootItemFunctionType getType() {
        return ModLootFunctions.REPLACE_WITH_LOOT_TABLE.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext lootContext) {
        if (stack.isEmpty()) {
            return stack;
        }
        LootTable table = lootContext.getLevel().getServer().getLootData().getLootTable(lootTable);
        ObjectArrayList<ItemStack> loot = new ObjectArrayList<>();
        table.getRandomItemsRaw(lootContext, loot::add);
        if (loot.size() > 1) {
            Artifacts.LOGGER.warn("Loot table {} in roll_loot_table function generated more than 1 item", lootTable.toString());
        } else if (loot.size() == 0) {
            Artifacts.LOGGER.warn("Failed to generate any loot from loot table {}", lootTable.toString());
            return ItemStack.EMPTY;
        }
        return loot.get(0);
    }

    public static LootItemConditionalFunction.Builder<?> replaceWithLootTable(ResourceLocation lootTable) {
        return simpleBuilder((conditions) -> new ReplaceWithLootTableFunction(conditions, lootTable));
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<ReplaceWithLootTableFunction> {

        @Override
        public void serialize(JsonObject object, ReplaceWithLootTableFunction instance, JsonSerializationContext context) {
            super.serialize(object, instance, context);
            object.addProperty("loot_table", instance.lootTable.toString());
        }

        @Override
        public ReplaceWithLootTableFunction deserialize(JsonObject object, JsonDeserializationContext context, LootItemCondition[] conditions) {
            return new ReplaceWithLootTableFunction(conditions, new ResourceLocation(GsonHelper.getAsString(object, "loot_table")));
        }
    }
}
