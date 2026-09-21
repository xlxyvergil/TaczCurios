package com.xlxyvergil.tcc.attribute;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = TaczCurios.MODID)
public class TccAttributes {
    
    public static final DeferredRegister<Attribute> ATTRIBUTES = 
        DeferredRegister.create(Registries.ATTRIBUTE, "tcc");
    
    /**
     * 虚数伤害抗性，范围 -100~100，正值减伤、负值增伤。
     */
    public static final DeferredHolder<Attribute, Attribute> IMAGINARY_DAMAGE_RESISTANCE = 
        ATTRIBUTES.register("imaginary_damage_resistance", 
            () -> new RangedAttribute("attribute.name.tcc.imaginary_damage_resistance", 20.0D, -100.0D, 100.0D).setSyncable(true));
    
    public static void register(IEventBus modEventBus) {
        ATTRIBUTES.register(modEventBus);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(type -> {
            event.add(type, IMAGINARY_DAMAGE_RESISTANCE);
        });
    }
}
