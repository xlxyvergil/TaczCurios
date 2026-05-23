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
     * 虚空伤害属性 - 攻击时附加的虚空伤害值
     */
    public static final RegistryObject<Attribute> VOID_DAMAGE = 
        ATTRIBUTES.register("void_damage", 
            () -> new RangedAttribute("tcc:void_damage", 0.0D, 0.0D, 1000.0D).setSyncable(true));
    
    /**
     * 虚空伤害抗性 - 按百分比抵消虚空伤害（0-100）
     */
    public static final RegistryObject<Attribute> VOID_DAMAGE_RESISTANCE = 
        ATTRIBUTES.register("void_damage_resistance", 
            () -> new RangedAttribute("tcc:void_damage_resistance", 0.0D, 0.0D, 100.0D).setSyncable(true));
    
    public static void register(IEventBus modEventBus) {
        ATTRIBUTES.register(modEventBus);
    }
}
