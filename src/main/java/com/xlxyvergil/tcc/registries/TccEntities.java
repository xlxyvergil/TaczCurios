package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.entity.ZhenWoBarrierEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TccEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TaczCurios.MODID);

    /** 逐火之蛾「真我」结界地面特效实体（纯视觉，无碰撞） */
    public static final RegistryObject<EntityType<ZhenWoBarrierEntity>> ZHEN_WO_BARRIER =
        ENTITY_TYPES.register("zhen_wo_barrier", () ->
            EntityType.Builder.<ZhenWoBarrierEntity>of(ZhenWoBarrierEntity::new, MobCategory.MISC)
                .sized(0.01F, 0.01F)
                .noSummon()
                .noSave()
                .build(TaczCurios.MODID + ":zhen_wo_barrier"));
}
