package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.recipe.FusionUpgradeRecipeSerializer;
import com.xlxyvergil.tcc.recipe.FusionUpgradeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TccRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TaczCurios.MODID);

    public static final RegistryObject<FusionUpgradeRecipeSerializer> FUSION_UPGRADE =
            RECIPE_SERIALIZERS.register("fusion_upgrade", FusionUpgradeRecipeSerializer::new);
}
