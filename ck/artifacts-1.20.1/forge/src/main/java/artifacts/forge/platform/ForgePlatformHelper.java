package artifacts.forge.platform;

import artifacts.client.item.renderer.ArtifactRenderer;
import artifacts.component.SwimData;
import artifacts.forge.capability.SwimDataCapability;
import artifacts.forge.integration.CosmeticArmorCompat;
import artifacts.item.wearable.WearableArtifactItem;
import artifacts.platform.PlatformHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ForgePlatformHelper implements PlatformHelper {

    @Override
    public boolean isEquippedBy(@Nullable LivingEntity entity, Predicate<ItemStack> predicate) {
        return entity != null && CuriosApi.getCuriosInventory(entity).resolve()
                .flatMap(inv -> inv.findFirstCurio(predicate))
                .isPresent();
    }

    @Override
    public Stream<ItemStack> findAllEquippedBy(LivingEntity entity, Item item) {
        return CuriosApi.getCuriosInventory(entity).resolve()
                .map(inv -> inv.findCurios(item))
                .orElse(List.of()).stream()
                .map(SlotResult::stack);
    }

    @Override
    public boolean tryEquipInFirstSlot(LivingEntity entity, ItemStack item) {
        Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(entity).resolve();
        if (optional.isPresent()) {
            ICuriosItemHandler handler = optional.get();
            for (Map.Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
                for (int i = 0; i < entry.getValue().getSlots(); i++) {
                    SlotContext slotContext = new SlotContext(entry.getKey(), entity, i, false, true);
                    //noinspection ConstantConditions
                    if (CuriosApi.isStackValid(slotContext, item) && entry.getValue().getStacks().getStackInSlot(i).isEmpty()) {
                        entry.getValue().getStacks().setStackInSlot(i, item);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Attribute getStepHeightAttribute() {
        return ForgeMod.STEP_HEIGHT_ADDITION.get();
    }

    @Override
    public boolean isCorrectTierForDrops(Tier tier, BlockState state) {
        return TierSortingRegistry.isCorrectTierForDrops(tier, state);
    }

    @Nullable
    @Override
    public SwimData getSwimData(LivingEntity player) {
        // noinspection ConstantConditions
        return player.getCapability(SwimDataCapability.CAPABILITY).orElse(null);
    }

    @Override
    public boolean isEyeInWater(Player player) {
        return player.isEyeInFluidType(ForgeMod.WATER_TYPE.get());
    }

    @Override
    public boolean isVisibleOnHand(LivingEntity entity, InteractionHand hand, WearableArtifactItem item) {
        return CuriosApi.getCuriosInventory(entity).resolve()
                .flatMap(handler -> Optional.ofNullable(handler.getCurios().get("hands")))
                .map(stacksHandler -> {
                    int startSlot = hand == InteractionHand.MAIN_HAND ? 0 : 1;
                    for (int slot = startSlot; slot < stacksHandler.getSlots(); slot += 2) {
                        ItemStack stack = stacksHandler.getCosmeticStacks().getStackInSlot(slot);
                        if (stack.isEmpty() && stacksHandler.getRenders().get(slot)) {
                            stack = stacksHandler.getStacks().getStackInSlot(slot);
                        }

                        if (stack.getItem() == item) {
                            return true;
                        }
                    }
                    return false;
                }).orElse(false);
    }

    @Override
    public boolean areBootsHidden(LivingEntity entity) {
        if (entity instanceof Player player && ModList.get().isLoaded("cosmeticarmorreworked")) {
            return CosmeticArmorCompat.areBootsHidden(player);
        }
        return false;
    }

    @Override
    public void registerArtifactRenderer(WearableArtifactItem item, Supplier<ArtifactRenderer> rendererSupplier) {
        CuriosRendererRegistry.register(item, () -> new ArtifactCurioRenderer(rendererSupplier.get()));
    }

    @Nullable
    @Override
    public ArtifactRenderer getArtifactRenderer(Item item) {
        Optional<ICurioRenderer> renderer = CuriosRendererRegistry.getRenderer(item);
        if (renderer.isPresent() && renderer.get() instanceof ArtifactCurioRenderer artifactTrinketRenderer) {
            return artifactTrinketRenderer.renderer();
        }
        return null;
    }

    private record ArtifactCurioRenderer(ArtifactRenderer renderer) implements ICurioRenderer {

        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            renderer.render(stack, slotContext.entity(), slotContext.index(), poseStack, multiBufferSource, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }
}
