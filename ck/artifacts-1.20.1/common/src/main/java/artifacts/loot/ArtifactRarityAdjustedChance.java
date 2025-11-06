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

public record ArtifactRarityAdjustedChance(float defaultProbability) implements LootItemCondition {

    @Override
    public LootItemConditionType getType() {
        return ModLootConditions.ARTIFACT_RARITY_ADJUSTED_CHANCE.get();
    }

    @Override
    public boolean test(LootContext context) {
        if (Artifacts.CONFIG.common.getArtifactRarity() > 9999) {
            return false;
        }
        float r = (float) Artifacts.CONFIG.common.getArtifactRarity();
        float p = defaultProbability;
        float adjustedProbability = p / (p + r - r * p);
        return context.getRandom().nextFloat() < adjustedProbability;
    }

    public static LootItemCondition.Builder adjustedChance(float probability) {
        return () -> new ArtifactRarityAdjustedChance(probability);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ArtifactRarityAdjustedChance> {

        @Override
        public void serialize(JsonObject object, ArtifactRarityAdjustedChance condition, JsonSerializationContext context) {
            object.addProperty("default_probability", condition.defaultProbability);
        }

        @Override
        public ArtifactRarityAdjustedChance deserialize(JsonObject object, JsonDeserializationContext context) {
            return new ArtifactRarityAdjustedChance(GsonHelper.getAsFloat(object, "default_probability"));
        }
    }
}
