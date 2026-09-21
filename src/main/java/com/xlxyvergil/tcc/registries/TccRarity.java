package com.xlxyvergil.tcc.registries;

import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class TccRarity {

    public static final EnumProxy<Rarity> RIFT_PROXY = new EnumProxy<>(Rarity.class,
            -1,
            "tcc:rift",
            (UnaryOperator<Style>) style -> style.withColor(ChatFormatting.LIGHT_PURPLE)
    );
}
