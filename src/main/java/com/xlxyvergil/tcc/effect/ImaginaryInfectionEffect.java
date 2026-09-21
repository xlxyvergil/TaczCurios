package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import com.xlxyvergil.tcc.TaczCurios;
/**
 * 虚数侵染标记：每级降低目标虚数抗性，伤害由虚数崩解独立处理。
 */
public class ImaginaryInfectionEffect extends MobEffect {

    private static final ResourceLocation RESISTANCE_REDUCTION_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "imaginary_infection_effect_d7e8f9a0_0123");

    public ImaginaryInfectionEffect() {
        super(MobEffectCategory.NEUTRAL, 0x8B0000);
        double reduction = TaczCuriosConfig.COMMON.imaginaryInfectionResistanceReduction.get();
        this.addAttributeModifier(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
            RESISTANCE_REDUCTION_ID,
            -reduction,
            AttributeModifier.Operation.ADD_VALUE);
    }

    /**
     * 不提供任何 cure，阻止 Goety/Warlock/Codger 等 boss 的 Wartling 剥离机制。
     */
    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        // 留空即表示不可被任何 cure 解除
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 属性修饰由 addAttributeModifier 自动管理，无需手动处理
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
