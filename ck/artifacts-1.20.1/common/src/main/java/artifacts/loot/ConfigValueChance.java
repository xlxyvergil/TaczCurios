package artifacts.loot;

import artifacts.Artifacts;
import artifacts.registry.ModLootConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Locale;
import java.util.function.Supplier;

public class ConfigValueChance implements LootItemCondition {

    private final ChanceConfig chanceConfig;

    private ConfigValueChance(ChanceConfig chanceConfig) {
        this.chanceConfig = chanceConfig;
    }

    @Override
    public LootItemConditionType getType() {
        return ModLootConditions.CONFIG_VALUE_CHANCE.get();
    }

    @Override
    public boolean test(LootContext context) {
        return context.getRandom().nextDouble() < chanceConfig.value.get();
    }

    public static LootItemCondition.Builder archaeologyChance() {
        return () -> new ConfigValueChance(ChanceConfig.ARCHAEOLOGY);
    }

    public static LootItemCondition.Builder entityEquipmentChance() {
        return () -> new ConfigValueChance(ChanceConfig.ENTITY_EQUIPMENT);
    }

    public static LootItemCondition.Builder everlastingBeefChance() {
        return () -> new ConfigValueChance(ChanceConfig.EVERLASTING_BEEF);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ConfigValueChance> {

        @Override
        public void serialize(JsonObject object, ConfigValueChance condition, JsonSerializationContext context) {
            object.addProperty("config", condition.chanceConfig.name);
        }

        @Override
        public ConfigValueChance deserialize(JsonObject object, JsonDeserializationContext context) {
            ChanceConfig config = ChanceConfig.byName(GsonHelper.getAsString(object, "config"));
            return new ConfigValueChance(config);
        }
    }

    private enum ChanceConfig {
        ARCHAEOLOGY("archaeology", () -> Artifacts.CONFIG.common.archaeologyChance),
        ENTITY_EQUIPMENT("entity_equipment", () -> Artifacts.CONFIG.common.entityEquipmentChance),
        EVERLASTING_BEEF("everlasting_beef", () -> Artifacts.CONFIG.common.everlastingBeefChance);

        final String name;
        final Supplier<Double> value;

        ChanceConfig(String name, Supplier<Double> value) {
            this.name = name;
            this.value = value;
        }

        static ChanceConfig byName(String name) {
            return valueOf(name.toUpperCase(Locale.ROOT));
        }
    }
}
