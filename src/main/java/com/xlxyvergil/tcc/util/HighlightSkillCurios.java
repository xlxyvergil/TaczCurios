package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.items.curios.bound.Kongmeng;
import com.xlxyvergil.tcc.items.curios.bound.LuejiZhiShou;
import com.xlxyvergil.tcc.items.curios.bound.PadoPhilipis;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * 「探索高亮」技能的三件饰品判定，客户端与服务端共用同一份。
 *
 * pado_philipis / lueji_zhi_shou / kongmeng 三件饰品授予同一个技能：按技能键（默认 V）
 * 让服务端扫描周围并向本人下发高亮坐标。
 *
 * 判定只保留这一处：客户端要用它做按键门禁（没戴就完全不做事），服务端要用它做能力校验，
 * 两边若各写一份，早晚会走样成"客户端以为戴着、服务端认为没戴"。
 */
public final class HighlightSkillCurios {

    private HighlightSkillCurios() {}

    /** 该物品是否属于授予高亮技能的三件饰品之一。 */
    public static boolean matches(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.getItem() instanceof PadoPhilipis
                || stack.getItem() instanceof LuejiZhiShou
                || stack.getItem() instanceof Kongmeng;
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
        return !CurioSearchHelper.findFirstEquippedStack(entity, HighlightSkillCurios::matches).isEmpty();
    }
}
