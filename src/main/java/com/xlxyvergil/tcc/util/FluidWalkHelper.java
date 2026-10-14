package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.api.items.IFluidWalk;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

/**
 * 流体行走的公共判定。
 * <p>
 * 闸门与手感规则集中在这里，「谁能走哪种流体」交给饰品的 IFluidWalk 规则。
 * 判定顺序按开销递增：先闸门与几何短路，非玩家/女仆或不在液面表层都不会扫描饰品槽。
 */
public final class FluidWalkHelper {

    private FluidWalkHelper() {
    }

    /**
     * 覆盖实体判定「能否站在该流体上」，由 mixin 调用。
     * <p>
     * 只在液面表层放行：表层以下交还原版泳动分支，否则会把游泳一并接管，泳速会被拖慢。
     */
    public static boolean canStandOnFluid(LivingEntity entity, FluidState state) {
        if (!MaidCompat.isPlayerOrMaid(entity)) return false;
        // 潜行时不吸附，供玩家主动沉入流体
        if (entity.isCrouching()) return false;
        if (!isAtSurface(entity)) return false;
        return isWalkable(entity, state);
    }

    /**
     * 佩戴者的每 tick 维护：把已浮到液面的实体托住，把仍在液面下的实体推回表面。
     * <p>
     * 仅在「能否站立」的判定上放行并不会提供支撑，故由这里补上。
     * 调用点必须在 travel 与重力结算之后（aiStep 末尾），顺序与数值共同决定稳定性：
     * 先结算重力、再衰减垂直速度并补一点上浮，速度才不会逐 tick 累积。
     * 需双端执行，否则客户端预测会与服务端不一致。
     */
    public static void tickFluidWalk(LivingEntity entity) {
        if (!MaidCompat.isPlayerOrMaid(entity)) return;
        // 潜行时不吸附也不推举，交还原版流体物理，供玩家主动沉入
        if (entity.isCrouching()) return;

        // 脚底贴在液面上时已不算「在水中」，故不能用 isInWater 做闸门，改判脚所在格是否为可走流体
        BlockPos pos = entity.blockPosition();
        FluidState feet = entity.level().getFluidState(pos);
        if (!isWalkable(entity, feet)) return;

        if (isAtSurface(entity)) {
            // 流体没有碰撞箱：只接管物理分支不会把实体托出液面，重力会把它压到表层判定的下限，脚因此浸在水中。
            // 故显式把脚底抬到液面之上。必须离开「在水中」判定：
            // 否则原版会判为游泳姿态，水面就只能走、无法疾跑。
            double surfaceY = pos.getY() + feet.getHeight(entity.level(), pos) + 0.001D;
            Vec3 motion = entity.getDeltaMovement();
            // 保留向上的速度分量，避免把跳跃一并抹掉；下沉分量清零，落地不再回弹。
            entity.setDeltaMovement(motion.x, Math.max(motion.y, 0.0D), motion.z);
            if (entity.getY() < surfaceY) {
                entity.setPos(entity.getX(), surfaceY, entity.getZ());
            }
            entity.setOnGround(true);
        } else {
            // 只衰减垂直分量，保留水平速度，避免拖慢泳速；
            // 上浮量取 0.01 而非常见配方里的 0.05，避免在水下被明显向上顶，同时仍能缓慢送回液面
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(motion.x, motion.y * 0.5D + 0.01D, motion.z);
        }
    }

    /** 实体是否处在流体表层：脚所在格是流体，且头顶那格不是同类流体。 */
    private static boolean isAtSurface(LivingEntity entity) {
        BlockPos pos = entity.blockPosition();
        FluidState feet = entity.level().getFluidState(pos);
        if (!CollisionContext.of(entity).isAbove(LiquidBlock.STABLE_SHAPE, pos, true)) return false;
        return !entity.level().getFluidState(pos.above()).getType().isSame(feet.getType());
    }

    /** 该实体是否佩戴了可走该流体的饰品。 */
    private static boolean isWalkable(LivingEntity entity, FluidState state) {
        if (state.isEmpty() || (!state.is(FluidTags.WATER) && !state.is(FluidTags.LAVA))) return false;
        for (ItemStack stack : CurioSearchHelper.equippedStacks(entity)) {
            if (stack.getItem() instanceof IFluidWalk rule && rule.canStandOnFluid(entity, state)) {
                return true;
            }
        }
        return false;
    }
}
