package artifacts.forge.loot;

import artifacts.item.wearable.hands.PickaxeHeaterItem;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class SmeltOresWithPickaxeHeaterModifier extends LootModifier {

    public static final Supplier<Codec<SmeltOresWithPickaxeHeaterModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(instance -> codecStart(instance)
                    .apply(instance, SmeltOresWithPickaxeHeaterModifier::new)
            )
    );

    protected SmeltOresWithPickaxeHeaterModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> items, LootContext context) {
        return PickaxeHeaterItem.getModifiedBlockDrops(items, context, Tags.Blocks.ORES, Tags.Items.RAW_MATERIALS);
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
