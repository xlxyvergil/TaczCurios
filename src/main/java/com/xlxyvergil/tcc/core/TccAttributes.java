package com.xlxyvergil.tcc.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class TccAttributes {
    
    public static final DeferredRegister<Attribute> ATTRIBUTES = 
        DeferredRegister.create(Registries.ATTRIBUTE, "tcc");
    
    /**
     * 虚数伤害属性 - 攻击时附加的虚数伤害值
     */
    public static final RegistryObject<Attribute> IMAGINARY_DAMAGE = 
        ATTRIBUTES.register("imaginary_damage", 
            () -> new RangedAttribute("tcc:imaginary_damage", 0.0D, 0.0D, 1000.0D).setSyncable(true));
    
    /**
     * 虚数伤害抗性 - 按百分比抵消虚数伤害（1-100）
     */
    public static final RegistryObject<Attribute> IMAGINARY_DAMAGE_RESISTANCE = 
        ATTRIBUTES.register("imaginary_damage_resistance", 
            () -> new RangedAttribute("tcc:imaginary_damage_resistance", 1.0D, 1.0D, 100.0D).setSyncable(true));
    
    public static void register(IEventBus modEventBus) {
        ATTRIBUTES.register(modEventBus);
    }
}
