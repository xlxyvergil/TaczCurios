package com.xlxyvergil.tcc.attribute;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TccAttributes {
    
    public static final DeferredRegister<Attribute> ATTRIBUTES = 
        DeferredRegister.create(Registries.ATTRIBUTE, "tcc");
    
    /**
     * 虚数伤害抗性 - 范围 -100 到 +100，默认 0
     * 正值降低虚数伤害，负值增加虚数伤害
     */
    public static final RegistryObject<Attribute> IMAGINARY_DAMAGE_RESISTANCE = 
        ATTRIBUTES.register("imaginary_damage_resistance", 
            () -> new RangedAttribute("attribute.name.tcc.imaginary_damage_resistance", 0.0D, -100.0D, 100.0D).setSyncable(true));
    
    public static void register(IEventBus modEventBus) {
        ATTRIBUTES.register(modEventBus);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(type -> {
            event.add(type, IMAGINARY_DAMAGE_RESISTANCE.get());
        });
    }
}
