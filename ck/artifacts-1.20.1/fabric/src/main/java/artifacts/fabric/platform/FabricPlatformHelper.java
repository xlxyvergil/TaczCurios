package artifacts.fabric.platform;

import artifacts.client.item.renderer.ArtifactRenderer;
import artifacts.component.SwimData;
import artifacts.fabric.client.CosmeticsHelper;
import artifacts.fabric.registry.ModComponents;
import artifacts.fabric.trinket.TrinketsHelper;
import artifacts.item.wearable.WearableArtifactItem;
import artifacts.platform.PlatformHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.stepheightentityattribute.StepHeightEntityAttributeMain;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FabricPlatformHelper implements PlatformHelper {

    @Override
    public boolean isEquippedBy(@Nullable LivingEntity entity, Predicate<ItemStack> predicate) {
        return TrinketsApi.getTrinketComponent(entity)
                .map(component -> component.isEquipped(predicate))
                .orElse(false);
    }

    @Override
    public Stream<ItemStack> findAllEquippedBy(LivingEntity entity, Item item) {
        return TrinketsHelper.findAllEquippedBy(entity).filter(stack -> stack.getItem() == item);
    }

    @Override
    public boolean tryEquipInFirstSlot(LivingEntity entity, ItemStack item) {
        if (TrinketsApi.getTrinketComponent(entity).isPresent()) {
            TrinketComponent component = TrinketsApi.getTrinketComponent(entity).get();
            for (Map<String, TrinketInventory> map : component.getInventory().values()) {
                for (TrinketInventory inventory : map.values()) {
                    if (TrinketSlot.canInsert(item, new SlotReference(inventory, 0), entity) && inventory.getItem(0).isEmpty()) {
                        inventory.setItem(0, item);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Attribute getStepHeightAttribute() {
        return StepHeightEntityAttributeMain.STEP_HEIGHT; // TODO 1.20.5
    }

    @Override
    public boolean isCorrectTierForDrops(Tier tier, BlockState state) {
        int i = tier.getLevel();
        if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return i >= 3;
        } else if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
            return i >= 2;
        } else if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
            return i >= 1;
        }
        return true;
    }

    @Nullable
    @Override
    public SwimData getSwimData(LivingEntity player) {
        return ModComponents.SWIM_DATA.getNullable(player);
    }

    @Override
    public boolean isEyeInWater(Player player) {
        return player.isEyeInFluid(FluidTags.WATER);
    }

    @Override
    public boolean isVisibleOnHand(LivingEntity entity, InteractionHand hand, WearableArtifactItem item) {
        return TrinketsApi.getTrinketComponent(entity).stream()
                .flatMap(component -> component.getAllEquipped().stream())
                .filter(tuple -> tuple.getA().inventory().getSlotType().getGroup().equals(
                        hand == InteractionHand.MAIN_HAND ? "hand" : "offhand"
                )).map(Tuple::getB)
                .filter(stack -> stack.is(item))
                .filter(stack -> !CosmeticsHelper.areCosmeticsToggledOffByPlayer(stack))
                .anyMatch(tuple -> true);
    }

    @Override
    public boolean areBootsHidden(LivingEntity entity) {
        return false;
    }

    @Override
    public void registerArtifactRenderer(WearableArtifactItem item, Supplier<ArtifactRenderer> rendererSupplier) {
        TrinketRendererRegistry.registerRenderer(item, new ArtifactTrinketRenderer(rendererSupplier.get()));
    }

    @Nullable
    @Override
    public ArtifactRenderer getArtifactRenderer(Item item) {
        Optional<TrinketRenderer> renderer = TrinketRendererRegistry.getRenderer(item);
        if (renderer.isPresent() && renderer.get() instanceof ArtifactTrinketRenderer artifactTrinketRenderer) {
            return artifactTrinketRenderer.renderer();
        }
        return null;
    }

    private record ArtifactTrinketRenderer(ArtifactRenderer renderer) implements TrinketRenderer {

        @Override
        public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> entityModel, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (CosmeticsHelper.areCosmeticsToggledOffByPlayer(stack)) {
                return;
            }
            int index = slotReference.index() + (slotReference.inventory().getSlotType().getGroup().equals("hand") ? 0 : 1);
            renderer.render(stack, entity, index, poseStack, multiBufferSource, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }
}
