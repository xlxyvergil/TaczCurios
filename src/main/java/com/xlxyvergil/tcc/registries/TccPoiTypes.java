package com.xlxyvergil.tcc.registries;

import com.google.common.collect.ImmutableSet;
import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.registries.ForgeRegistries;

public class TccPoiTypes {
    public static final ResourceLocation ID = new ResourceLocation(TaczCurios.MODID, "teshin_workbench");
    public static final ResourceKey<PoiType> POI_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ID);

    public static PoiType TESHIN_WORKBENCH_POI;

    // 注册Teshin工作台POI类型
    public static void init() {
        TESHIN_WORKBENCH_POI = new PoiType(
            ImmutableSet.copyOf(TccBlocks.TESHIN_WORKBENCH.getStateDefinition().getPossibleStates()), 1, 1);
        ForgeRegistries.POI_TYPES.register(ID, TESHIN_WORKBENCH_POI);
    }
}
