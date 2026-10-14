package com.xlxyvergil.tcc.api.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;

/**
 * 流体行走规则：由饰品自身声明，实体每次流体碰撞判定时现场求值。
 * <p>
 * 与减伤规则同理，状态直接从「是否佩戴 + 当前条件」推导，卸下即失效，无需登记与清理。
 */
public interface IFluidWalk {

    /** 佩戴者能否站在该流体表面。 */
    default boolean canStandOnFluid(LivingEntity wearer, FluidState state) {
        return false;
    }
}
