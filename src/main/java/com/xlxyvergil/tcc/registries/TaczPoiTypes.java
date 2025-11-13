package com.xlxyvergil.tcc.registries;

import com.google.common.collect.ImmutableSet;
import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.core.registries.Registries;

import java.util.Set;

public class TaczPoiTypes {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, TaczCurios.MODID);
    
    // 注册Teshin工作台POI类型
    public static final RegistryObject<PoiType> TESHIN_WORKBENCH_POI = POI_TYPES.register("teshin_workbench", 
        () -> new PoiType(ImmutableSet.copyOf(TaczBlocks.TESHIN_WORKBENCH.get().getStateDefinition().getPossibleStates()), 1, 1));
        
    public static void init() {}
}