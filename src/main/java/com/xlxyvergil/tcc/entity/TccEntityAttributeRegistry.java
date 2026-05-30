package com.xlxyvergil.tcc.entity;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.core.TccAttributes;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * TCC实体属性注册类
 * 将自定义属性绑定到所有实体类型
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TccEntityAttributeRegistry {
    
    /**
     * 将所有TCC自定义属性绑定到实体上
     */
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        // 按照TACZ的方式：直接对所有实体类型添加属性
        event.getTypes().forEach(type -> {
            // 虚数伤害属性
            event.add(type, TccAttributes.IMAGINARY_DAMAGE.get());
            event.add(type, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        });
    }
}
