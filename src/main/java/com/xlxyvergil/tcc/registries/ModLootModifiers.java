package com.xlxyvergil.tcc.registries;

import com.mojang.serialization.Codec;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.loot.AddItemModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TaczCurios.MODID);

    public static final RegistryObject<Codec<AddItemModifier>> ADD_ITEM_MODIFIER =
            LOOT_MODIFIERS.register("add_item_modifier", AddItemModifier.CODEC);
}