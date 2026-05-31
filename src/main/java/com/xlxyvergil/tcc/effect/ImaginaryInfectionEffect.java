package com.xlxyvergil.tcc.effect;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 虚数侵染 - 纯标记效果，不直接造成伤害。
 * 每级降低目标虚数抗性（可配置），伤害由虚数崩解独立处理。
 */
public class ImaginaryInfectionEffect extends MobEffect {

    private static final UUID RESISTANCE_REDUCTION_UUID = UUID.fromString("d7e8f9a0-b1c2-3d4e-5f67-89abcdef0123");

    public ImaginaryInfectionEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
        double reduction = TaczCuriosConfig.COMMON.imaginaryInfectionResistanceReduction.get();
        this.addAttributeModifier(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            RESISTANCE_REDUCTION_UUID.toString(),
            -reduction,
            AttributeModifier.Operation.ADDITION);
    }

    /**
     * 空curativeItems阻止Goety/Warlock/Codger等boss的Wartling剥离机制。
     */
    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 属性修饰由 addAttributeModifier 自动管理，无需手动处理
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
