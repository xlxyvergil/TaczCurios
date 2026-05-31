package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.core.TccDamageSources;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 天火系列饰品血量变化监听器
 * 仅负责监听血量变化事件，具体逻辑由饰品类自行处理
 */
@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeavenFireHealthListener {
    
    /**
     * 监听受伤事件 - 处理天火饰品的伤害降低
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null || target.level().isClientSide()) {
            return;
        }
        
        // 检查受害者是否装备了救世，如果是则降低受到的伤害（可配置）
        if (com.xlxyvergil.tcc.items.Salvation.hasSalvationEquipped(target)) {
            double damageReduction = com.xlxyvergil.tcc.config.TaczCuriosConfig.COMMON.salvationDamageReduction.get();
            float originalDamage = event.getAmount();
            float reducedDamage = originalDamage * (float)(1.0 - damageReduction);
            event.setAmount(reducedDamage);
        }
        
        // 检查攻击者是否装备了天火圣裁或天火劫灭
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            // 检查是否为虚数伤害（天火饰品转换的伤害类型）
            if (event.getSource().is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) {
                double damageConversionRatio = 0;
                boolean hasHeavenFireItem = false;
                
                // 检查天火圣裁
                if (com.xlxyvergil.tcc.items.HeavenFireJudgment.hasHeavenFireJudgmentEquipped(attacker)) {
                    damageConversionRatio = TaczCuriosConfig.COMMON.heavenFireJudgmentDamageConversionRatio.get();
                    hasHeavenFireItem = true;
                }
                // 检查天火劫灭
                else if (com.xlxyvergil.tcc.items.HeavenFireApocalypse.hasHeavenFireApocalypseEquipped(attacker)) {
                    damageConversionRatio = TaczCuriosConfig.COMMON.heavenFireApocalypseDamageConversionRatio.get();
                    hasHeavenFireItem = true;
                }
                // 检查天火劫灭·无烬终焉
                else if (com.xlxyvergil.tcc.items.HeavenFireApocalypseEndless.hasHeavenFireApocalypseEndlessEquipped(attacker)) {
                    damageConversionRatio = TaczCuriosConfig.COMMON.endlessDamageConversionRatio.get();
                    hasHeavenFireItem = true;
                }
                
                // 应用伤害降低
                if (hasHeavenFireItem) {
                    // 根据攻击者的虚数抗性提升保留系数
                    double resistance = attacker.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                    double resistanceBonusPerPoint = TaczCuriosConfig.COMMON.imaginaryDamageResistanceBonusPerPoint.get();
                    damageConversionRatio += resistance * resistanceBonusPerPoint;
                    
                    float originalDamage = event.getAmount();
                    float reducedDamage = originalDamage * (float)damageConversionRatio;
                    event.setAmount(reducedDamage);
                }
            }
        }
        
        // 通知饰品类处理血量变化
        com.xlxyvergil.tcc.items.HeavenFireApocalypse.onHealthChanged(target);
        com.xlxyvergil.tcc.items.HeavenFireJudgment.onHealthChanged(target);
    }
    
    /**
     * 监听治疗事件
     */
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide()) {
            return;
        }
        
        // 通知饰品类处理血量变化
        com.xlxyvergil.tcc.items.HeavenFireApocalypse.onHealthChanged(entity);
        com.xlxyvergil.tcc.items.HeavenFireJudgment.onHealthChanged(entity);
    }
}
