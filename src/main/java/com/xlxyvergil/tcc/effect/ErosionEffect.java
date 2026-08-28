package com.xlxyvergil.tcc.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 侵蚀标记：虚数崩解结算负面数量增益时据此判定。
 */
public class ErosionEffect extends MobEffect {

    public ErosionEffect() {
        super(MobEffectCategory.NEUTRAL, 0x6A0DAD);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 纯标记效果，无 tick 逻辑
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
