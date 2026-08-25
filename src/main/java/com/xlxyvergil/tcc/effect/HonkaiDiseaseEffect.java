package com.xlxyvergil.tcc.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 崩坏病 - 中性（NEUTRAL）标记效果。
 * <p>
 * 纯标记，不通过属性修饰实现易伤；易伤增伤（amplifier 0/1/2 → 20%/40%/60%）
 * 由 {@code NewSeriesMechanicEvents#onLivingHurt} 统一处理。
 */
public class HonkaiDiseaseEffect extends MobEffect {

    public HonkaiDiseaseEffect() {
        super(MobEffectCategory.NEUTRAL, 0x9C27B0);
    }

    /**
     * 空 curativeItems 阻止其他模组（Goety/Warlock/Codger 等）的 Wartling 剥离机制。
     */
    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 增伤逻辑在伤害事件中处理
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
