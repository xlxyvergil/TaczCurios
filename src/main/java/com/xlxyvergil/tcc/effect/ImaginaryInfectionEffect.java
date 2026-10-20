package com.xlxyvergil.tcc.effect;

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
 * 虚数侵染标记：每级降低目标虚数抗性，伤害由虚数崩解独立处理。
 */
public class ImaginaryInfectionEffect extends MobEffect {
    /** 每级数值。注册期读不到配置，所以写死在常量里；tooltip 复用同一个常量。 */
    public static final double VALUE_PER_LEVEL = 5.0D;

    private static final UUID RESISTANCE_REDUCTION_UUID = UUID.fromString("d7e8f9a0-b1c2-3d4e-5f67-89abcdef0123");

    public ImaginaryInfectionEffect() {
        super(MobEffectCategory.NEUTRAL, 0x8B0000);
        this.addAttributeModifier(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            RESISTANCE_REDUCTION_UUID.toString(),
            -VALUE_PER_LEVEL,
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
