package com.xlxyvergil.tcc.loot;

import com.mojang.serialization.Codec;
import com.xlxyvergil.tcc.TaczCurios;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TaczCurios.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> TCC_LOOT_MODIFIER =
            LOOT_MODIFIER_SERIALIZERS.register("tcc_loot_modifier", () -> TccLootModifier.CODEC);
}