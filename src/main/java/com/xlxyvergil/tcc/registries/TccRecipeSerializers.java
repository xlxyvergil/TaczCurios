package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.recipe.CollapseCrystalCraftRecipeSerializer;
import com.xlxyvergil.tcc.recipe.FusionUpgradeRecipeSerializer;
import com.xlxyvergil.tcc.recipe.FusionVesselCombineRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TccRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TaczCurios.MODID);

    public static final RegistryObject<FusionUpgradeRecipeSerializer> FUSION_UPGRADE =
            RECIPE_SERIALIZERS.register("fusion_upgrade", FusionUpgradeRecipeSerializer::new);

    public static final RegistryObject<FusionVesselCombineRecipeSerializer> FUSION_VESSEL_COMBINE =
            RECIPE_SERIALIZERS.register("fusion_vessel_combine", FusionVesselCombineRecipeSerializer::new);

    public static final RegistryObject<CollapseCrystalCraftRecipeSerializer> COLLAPSE_CRYSTAL_CRAFT =
            RECIPE_SERIALIZERS.register("collapse_crystal_craft", CollapseCrystalCraftRecipeSerializer::new);
}
