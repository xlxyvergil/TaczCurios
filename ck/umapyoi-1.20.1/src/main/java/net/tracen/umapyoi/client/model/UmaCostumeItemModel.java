package net.tracen.umapyoi.client.model;

import org.jetbrains.annotations.Nullable;

import cn.mcmod_mmf.mmlib.client.model.DynamicItemBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.tracen.umapyoi.item.UmaCostumeItem;

public class UmaCostumeItemModel extends DynamicItemBakedModel {

	public UmaCostumeItemModel(BakedModel original, ModelBakery loader) {
		super(original, loader);
	}

	@Override
	public BakedModel resolveModel(BakedModel original, ItemStack stack, @Nullable ClientLevel world,
			@Nullable LivingEntity entity, int seed) {
		if (!stack.isEmpty()) {
			if (stack.getItem() instanceof UmaCostumeItem) {
				ModelResourceLocation modelPath = new ModelResourceLocation(
						getModelLocation(UmaCostumeItem.getCostumeID(stack)), "inventory");
				
				BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelPath);
				if(model == Minecraft.getInstance().getModelManager().getMissingModel())
					return this.getOriginalModel();
				return model;
			}
		}
		return this.getOriginalModel();
	}

	private ResourceLocation getModelLocation(ResourceLocation id) {
		return new ResourceLocation(id.getNamespace(), "costume/" + id.getPath());
	}
}
