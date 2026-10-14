package com.xlxyvergil.tcc.api.items;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * 减伤规则：由饰品自身声明，受击时由事件层在佩戴者的饰品槽中现场求值。
 * <p>
 * 判定对象是「受伤实体」本身，因此对任意 LivingEntity（玩家、女仆等）通用，且不依赖任何全局登记表：
 * 状态直接从「是否佩戴 + 当前条件」推导，卸下即自然失效，无需登记与清理，也不会跨存档残留。
 */
public interface IDamageMitigation {

    /**
     * 常驻比例减伤：返回本次受击保留的伤害比例 [0,1]（0 表示完全免伤）；返回 null 表示本次不生效。
     * 多个饰品同时命中时按比例相乘结算。
     */
    default Float retainFactor(LivingEntity wearer, DamageSource source, float amount) {
        return null;
    }

    /**
     * 受击冷却：返回「受击后进入的冷却时长（tick）」，冷却期内该实体受到的伤害归零；
     * 返回 null 或 &lt;= 0 表示不启用。多个饰品同时命中时取最大值。
     */
    default Integer hurtCooldownTicks(LivingEntity wearer) {
        return null;
    }
}
