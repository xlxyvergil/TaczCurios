package artifacts.platform;

import artifacts.client.item.renderer.ArtifactRenderer;
import artifacts.component.SwimData;
import artifacts.item.wearable.WearableArtifactItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface PlatformHelper {

    default boolean isEquippedBy(@Nullable LivingEntity entity, Item item) {
        return isEquippedBy(entity, stack -> stack.is(item));
    }

    boolean isEquippedBy(@Nullable LivingEntity entity, Predicate<ItemStack> predicate);

    Stream<ItemStack> findAllEquippedBy(LivingEntity entity, Item item);

    boolean tryEquipInFirstSlot(LivingEntity entity, ItemStack item);

    Attribute getStepHeightAttribute();

    boolean isCorrectTierForDrops(Tier tier, BlockState state);

    @Nullable
    SwimData getSwimData(LivingEntity player);

    boolean isEyeInWater(Player player);

    boolean isVisibleOnHand(LivingEntity entity, InteractionHand hand, WearableArtifactItem item);

    boolean areBootsHidden(LivingEntity entity);

    void registerArtifactRenderer(WearableArtifactItem item, Supplier<ArtifactRenderer> rendererSupplier);

    @Nullable
    ArtifactRenderer getArtifactRenderer(Item item);
}
