package com.xlxyvergil.tcc.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 持久化地给目标施加「移动速度 ×0」的属性修饰符（MULTIPLY_TOTAL 值 = -1），使目标最终移动速度归零。
 * <p>
 * 使用永久修饰符（写入实体属性 NBT），不会随时间自动解除；羽渡尘/凡尘难渡/不识时务共用同一修饰符 ID，
 * 重复命中不会叠加。
 */
public final class SpeedZeroHelper {

    private static final ResourceLocation SPEED_ZERO_ID = ResourceLocation.fromNamespaceAndPath("tcc", "speed_zero");

    private SpeedZeroHelper() {
    }

    /** 使目标移动速度最终归零（持久化，客户端不生效）。 */
    public static void apply(LivingEntity target) {
        if (target == null || target.level().isClientSide) {
            return;
        }
        AttributeHelper.applyModifier(target, AttributeHelper.MOVEMENT_SPEED, -1.0,
                SPEED_ZERO_ID, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
