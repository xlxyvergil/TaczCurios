package com.xlxyvergil.tcc.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.xlxyvergil.tcc.items.materials.FusionVesselItem;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetFusionCountFunction extends LootItemConditionalFunction {

    private final int minCount;
    private final int maxCount;

    private SetFusionCountFunction(LootItemCondition[] conditions, int minCount, int maxCount) {
        super(conditions);
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        int count;
        if (minCount >= maxCount) {
            count = minCount;
        } else {
            count = minCount + context.getRandom().nextInt(maxCount - minCount + 1);
        }
        FusionVesselItem.setFusionCount(stack, count);
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootTableEventHandler.SET_FUSION_COUNT;
    }

    // -- 构造器 --

    public static Builder builder(int minCount, int maxCount) {
        return new Builder(minCount, maxCount);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<SetFusionCountFunction.Builder> {
        private final int minCount;
        private final int maxCount;

        public Builder(int minCount, int maxCount) {
            this.minCount = minCount;
            this.maxCount = maxCount;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetFusionCountFunction(getConditions(), minCount, maxCount);
        }
    }

    // -- 序列化 --

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetFusionCountFunction> {
        @Override
        public void serialize(JsonObject json, SetFusionCountFunction value, JsonSerializationContext context) {
            super.serialize(json, value, context);
            json.addProperty("min", value.minCount);
            json.addProperty("max", value.maxCount);
        }

        @Override
        public SetFusionCountFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
            int min = GsonHelper.getAsInt(json, "min");
            int max = GsonHelper.getAsInt(json, "max");
            return new SetFusionCountFunction(conditions, min, max);
        }
    }
}
