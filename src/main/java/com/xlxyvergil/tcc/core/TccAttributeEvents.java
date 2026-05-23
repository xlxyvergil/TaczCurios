package com.xlxyvergil.tcc.core;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.xlxyvergil.tcc.mixin.LivingEntityAccessor;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TccAttributeEvents {
    
    /**
     * 监听所有攻击事件，附加虚空伤害
     * 适用于：近战、TACZ枪械、其他模组的远程/魔法攻击
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void voidDamageOnAttack(LivingAttackEvent e) {
        if (e.getEntity().level().isClientSide || e.getEntity().isDeadOrDying()) return;
        
        // 获取攻击者（可能是玩家、实体、或null）
        if (e.getSource().getEntity() instanceof LivingEntity attacker) {
            // 读取攻击者的虚空伤害属性值
            double voidDmg = attacker.getAttributeValue(TccAttributes.VOID_DAMAGE.get());
            
            if (voidDmg > 0.001) {
                LivingEntity target = e.getEntity();
                
                // 读取目标的虚空伤害抗性（百分比）
                double resistance = target.getAttributeValue(TccAttributes.VOID_DAMAGE_RESISTANCE.get());
                
                // 计算实际伤害：应用抗性减免
                float damage = (float) (voidDmg * (1.0 - resistance / 100.0));
                
                // 如果抗性 100%，完全免疫
                if (damage <= 0) {
                    return;
                }
                
                // 虚空伤害：直接扣除生命值，不碰吸收值
                float newHealth = target.getHealth() - damage;
                
                // 检查是否会致死，如果是则尝试触发不死图腾
                if (newHealth <= 0) {
                    // 调用原版不死图腾检查机制
                    boolean totemActivated = ((LivingEntityAccessor) target).callCheckTotemDeathProtection(e.getSource());
                    
                    if (!totemActivated) {
                        // 没有不死图腾，直接死亡
                        target.setHealth(0);
                        target.die(e.getSource());
                        return;
                    }
                    // 不死图腾已激活，checkTotemDeathProtection 会自动设置生命值为 1
                    return;
                }
                
                // 不会致死，直接设置新生命值
                target.setHealth(newHealth);
                
                // 触发受伤效果
                target.hurtMarked = true;
            }
        }
    }
}
