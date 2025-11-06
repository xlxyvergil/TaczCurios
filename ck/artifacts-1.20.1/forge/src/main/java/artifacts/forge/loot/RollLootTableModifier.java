package artifacts.forge.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class RollLootTableModifier extends LootModifier {

    public static final Supplier<Codec<RollLootTableModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(instance -> codecStart(instance)
                    .and(ResourceLocation.CODEC.fieldOf("lootTable").forGetter(m -> m.lootTable))
                    .and(Codec.BOOL.optionalFieldOf("replace", false).forGetter(m -> m.replace))
                    .apply(instance, RollLootTableModifier::new)
            )
    );

    private final ResourceLocation lootTable;
    private final boolean replace;

    public RollLootTableModifier(LootItemCondition[] conditions, ResourceLocation lootTable, boolean replace) {
        super(conditions);
        this.lootTable = lootTable;
        this.replace = replace;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (replace) {
            generatedLoot.clear();
        }
        // noinspection deprecation
        context.getResolver().getLootTable(lootTable).getRandomItemsRaw(context, generatedLoot::add);
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
