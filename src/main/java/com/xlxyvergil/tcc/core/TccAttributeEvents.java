package com.xlxyvergil.tcc.core;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TccAttributeEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void imaginaryDamageOnAttack(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide || target.isDeadOrDying()) return;

        DamageSource source = event.getSource();

        if (source.getMsgId() != null && source.getMsgId().equals("imaginary_damage")) {
            double resistance = target.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            
            // 限制抗性值范围：最低1，最高100
            resistance = Math.max(1.0, Math.min(100.0, resistance));

            float originalDamage = event.getAmount();
            float damageAfterResistance = (float) (originalDamage * (1.0 - resistance / 100.0));
            
            // 虚数伤害根据目标流血层数增伤（5%每层）
            var bleedingEffect = ALObjects.MobEffects.BLEEDING.get();
            int bleedingLevel = 0;
            if (bleedingEffect != null) {
                var effectInstance = target.getEffect(bleedingEffect);
                if (effectInstance != null) {
                    bleedingLevel = effectInstance.getAmplifier() + 1;
                }
            }
            float finalDamage = damageAfterResistance * (1.0f + bleedingLevel * 0.05f);

            event.setAmount(finalDamage);
            
            // 虚数伤害施加虚数流血效果（根据抗性判断）
            int maxLevel = TaczCuriosConfig.COMMON.imaginaryBleedingMaxLevel.get();
            int duration = TaczCuriosConfig.COMMON.imaginaryBleedingDuration.get();
            
            // 根据抗性计算最大可施加等级：每20%抗性降低1级
            // 0%抗性=5级, 20%=4级, 40%=3级, 60%=2级, 80%=1级, 100%=0级(不施加)
            int resistanceInt = (int) Math.round(resistance);
            int allowedMaxLevel = Math.max(0, maxLevel - (resistanceInt / 20));
            
            if (allowedMaxLevel > 0) {
                // 计算施加概率：100%抗性=0%, 0%抗性=100%
                double applyChance = 1.0 - (resistance / 100.0);
                
                // 随机判定是否施加
                Random random = new Random();
                if (random.nextDouble() < applyChance) {
                    // 检查是否已有虚数流血效果，如果有则叠加等级（不超过抗性允许的等级）
                    var imaginaryBleeding = TccMobEffects.IMAGINARY_BLEEDING.get();
                    MobEffectInstance existingEffect = target.getEffect(imaginaryBleeding);
                    int newAmplifier = 0;
                    
                    if (existingEffect != null) {
                        newAmplifier = Math.min(existingEffect.getAmplifier() + 1, allowedMaxLevel - 1);
                    }
                    
                    target.addEffect(new MobEffectInstance(
                        imaginaryBleeding,
                        duration * 20,  // 转换为tick
                        newAmplifier,
                        false,  // 不是药水
                        false,  // 不显示粒子
                        true    // 显示图标
                    ));
                }
            }
        }
    }
}
