package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.recipe.CollapseCrystalCraftRecipeSerializer;
import com.xlxyvergil.tcc.recipe.FusionUpgradeRecipeSerializer;
import com.xlxyvergil.tcc.recipe.FusionVesselCombineRecipeSerializer;
import com.xlxyvergil.tcc.recipe.JiuChanZhiYuanCraftRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TccRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, TaczCurios.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, FusionUpgradeRecipeSerializer> FUSION_UPGRADE =
            RECIPE_SERIALIZERS.register("fusion_upgrade", FusionUpgradeRecipeSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, FusionVesselCombineRecipeSerializer> FUSION_VESSEL_COMBINE =
            RECIPE_SERIALIZERS.register("fusion_vessel_combine", FusionVesselCombineRecipeSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, CollapseCrystalCraftRecipeSerializer> COLLAPSE_CRYSTAL_CRAFT =
            RECIPE_SERIALIZERS.register("collapse_crystal_craft", CollapseCrystalCraftRecipeSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, JiuChanZhiYuanCraftRecipeSerializer> JIU_CHAN_ZHI_YUAN_CRAFT =
            RECIPE_SERIALIZERS.register("jiu_chan_zhi_yuan_craft", JiuChanZhiYuanCraftRecipeSerializer::new);
}
