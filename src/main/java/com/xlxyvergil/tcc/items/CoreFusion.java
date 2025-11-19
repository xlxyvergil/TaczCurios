package com.xlxyvergil.tcc.items;

import net.minecraft.world.item.Rarity;

public class CoreFusion extends ItemBaseCurio {
    
    public CoreFusion(Properties properties) {
        super(properties
            .stacksTo(64)
            .rarity(Rarity.COMMON));
    }
}