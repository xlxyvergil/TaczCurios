package com.xlxyvergil.tcc.registries;

import com.mojang.serialization.Codec;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.loot.AddLootTableModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TaczCurios.MODID);
            
    public static final RegistryObject<Codec<AddLootTableModifier>> ADD_LOOT_TABLE_MODIFIER =
            LOOT_MODIFIERS.register("add_loot_table_modifier", AddLootTableModifier.CODEC);
}