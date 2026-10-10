package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.items.curios.bound.ZhenWo;
import net.minecraft.world.entity.LivingEntity;

/**
 * 真我结界 / 免死判定的中转入口。
 * <p>
 * mixin 类不能直接引用 {@link ZhenWo}：它继承 {@code Item}，Mixin 在目标类（如 Player）加载过程中
 * 解析其调用目标的类元数据时会递归加载继承链，触发 ClassMetadataNotFoundException 导致启动崩溃。
 * 本类不继承任何原版类，故可被 mixin 安全引用，实际逻辑转发给 ZhenWo 在运行时解析。
 */
public final class ZhenWoGuard {

    private ZhenWoGuard() {
    }

    public static boolean isBarrierActiveWearer(LivingEntity entity) {
        return ZhenWo.isBarrierActiveWearer(entity);
    }

    public static boolean isInsideActiveBarrier(LivingEntity entity) {
        return ZhenWo.isInsideActiveBarrier(entity);
    }
}
