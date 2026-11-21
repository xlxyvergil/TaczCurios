package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.items.curios.bound.CuiyaoZhiGe;
import com.xlxyvergil.tcc.items.curios.bound.Eden;
import com.xlxyvergil.tcc.items.curios.bound.Huangjin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * 「伊甸系列」技能的三件饰品判定，客户端与服务端共用同一份。
 *
 * eden / cuiyao_zhi_ge / huangjin 三件饰品授予同一个技能：按技能键发动一次
 * 光环，为周围玩家与女仆随机施加一个正面 buff。旧实现是开枪命中时自动触发，
 * 现在改为完全由玩家按键控制，因此判定从事件里搬到了这里。
 *
 * 判定只保留这一处：客户端要用它做按键门禁（没戴就完全不发包），服务端要用它做能力校验。
 */
public final class EdenSeriesSkillCurios {

    private EdenSeriesSkillCurios() {}

    /** 该物品是否属于授予伊甸系列技能的三件饰品之一。 */
    public static boolean matches(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.getItem() instanceof Eden
                || stack.getItem() instanceof CuiyaoZhiGe
                || stack.getItem() instanceof Huangjin;
    }

    /**
     * 实体当前是否佩戴了上述任意一件。
     *
     * 两端都可用：Curios 的饰品栏在客户端有一份同步副本，因此客户端门禁与服务端校验
     * 走的是同一个判断，不会出现判定的分裂。
     */
    public static boolean isWorn(@Nullable LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        return !CurioSearchHelper.findFirstEquippedStack(entity, EdenSeriesSkillCurios::matches).isEmpty();
    }
}
