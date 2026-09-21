package com.xlxyvergil.tcc.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xlxyvergil.tcc.items.materials.FusionVesselItem;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetFusionCountFunction extends LootItemConditionalFunction {

    public static final MapCodec<SetFusionCountFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance)
                    .and(instance.group(
                            Codec.INT.fieldOf("min").forGetter(f -> f.minCount),
                            Codec.INT.fieldOf("max").forGetter(f -> f.maxCount)))
                    .apply(instance, SetFusionCountFunction::new));

    private final int minCount;
    private final int maxCount;

    private SetFusionCountFunction(List<LootItemCondition> conditions, int minCount, int maxCount) {
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
    public LootItemFunctionType<SetFusionCountFunction> getType() {
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
}
