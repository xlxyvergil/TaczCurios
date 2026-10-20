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

    /**
     * 生命偷取：造成物理伤害时按比例转化为自身治疗。1.0 = 100%。
     * 结算见 {@link com.xlxyvergil.tcc.event.LifeStealOverhealHandler}。
     */
    public static final DeferredHolder<Attribute, Attribute> LIFE_STEAL =
        ATTRIBUTES.register("life_steal",
            () -> new RangedAttribute("attribute.name.tcc.life_steal", 0.0D, 0.0D, 10.0D).setSyncable(true));

    /**
     * 过量治疗：造成物理伤害时按比例转化为伤害吸收（金心），上限为攻击者最大生命的一半。1.0 = 100%。
     * 结算见 {@link com.xlxyvergil.tcc.event.LifeStealOverhealHandler}；虚数伤害（含枪械子弹转换来的）
     * 由 TccAttributeEvents#imaginaryDamageOnAttack 在抗性结算后转入同一入口。
     */
    public static final DeferredHolder<Attribute, Attribute> OVERHEAL =
        ATTRIBUTES.register("overheal",
            () -> new RangedAttribute("attribute.name.tcc.overheal", 0.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static void register(IEventBus modEventBus) {
        ATTRIBUTES.register(modEventBus);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(type -> {
            event.add(type, IMAGINARY_DAMAGE_RESISTANCE);
            event.add(type, LIFE_STEAL);
            event.add(type, OVERHEAL);
        });
    }
}
