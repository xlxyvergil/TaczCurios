package artifacts.fabric.client;

import artifacts.Artifacts;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Supplier;

public class UmbrellaModelLoadingPlugin implements ModelLoadingPlugin {
    public static final ModelResourceLocation UMBRELLA_BASE_MODEL = new ModelResourceLocation(Artifacts.id("umbrella"), "inventory");
    public static final ResourceLocation UMBRELLA_BLOCKING_MODEL = Artifacts.id("item/umbrella_held_blocking");
    private static final ModelResourceLocation UMBRELLA_GUI_MODEL = new ModelResourceLocation(Artifacts.id("umbrella_gui"), "inventory");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.addModels(UMBRELLA_GUI_MODEL); // Manually load the GUI model

        pluginContext.modifyModelAfterBake().register((original, context) -> {
            if (context.id().equals(UMBRELLA_BASE_MODEL) || context.id().equals(UMBRELLA_BLOCKING_MODEL)) {
                BakedModel guiModel = context.baker().bake(UMBRELLA_GUI_MODEL, context.settings());
                if (original != null && guiModel != null) {
                    return new UmbrellaBakedModel(original, guiModel);
                }
            }

            return original;
        });
    }

    private static class UmbrellaBakedModel extends ForwardingBakedModel {
        private static final Set<ItemDisplayContext> ITEM_GUI_CONTEXTS = EnumSet.of(ItemDisplayContext.GUI, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED);

        private final BakedModel guiModel;
        private final ItemTransforms transforms;

        public UmbrellaBakedModel(BakedModel heldModel, BakedModel guiModel) {
            this.wrapped = heldModel;
            this.guiModel = guiModel;
            this.transforms = new ItemTransforms(
                    heldModel.getTransforms().thirdPersonLeftHand,
                    heldModel.getTransforms().thirdPersonRightHand,
                    heldModel.getTransforms().firstPersonLeftHand,
                    heldModel.getTransforms().firstPersonRightHand,
                    heldModel.getTransforms().head,
                    guiModel.getTransforms().gui,
                    guiModel.getTransforms().ground,
                    guiModel.getTransforms().fixed
            );
        }

        @Override
        public ItemTransforms getTransforms() {
            return transforms;
        }

        @Override
        public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
            if (ITEM_GUI_CONTEXTS.contains(context.itemTransformationMode())) {
                guiModel.emitItemQuads(stack, randomSupplier, context);
                return;
            }

            super.emitItemQuads(stack, randomSupplier, context);
        }

        @Override
        public boolean isVanillaAdapter() {
            return false;
        }
    }
}
