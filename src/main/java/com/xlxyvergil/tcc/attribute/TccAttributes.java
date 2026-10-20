package com.xlxyvergil.tcc.attribute;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.taa.compat.attributeslib.AttributesLibCompat;
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
     * 虚数伤害抗性，范围 -100~100，正值减伤、负值增伤。
     */
    public static final RegistryObject<Attribute> IMAGINARY_DAMAGE_RESISTANCE = 
        ATTRIBUTES.register("imaginary_damage_resistance", 
            () -> new RangedAttribute("attribute.name.tcc.imaginary_damage_resistance", 20.0D, -100.0D, 100.0D).setSyncable(true));

    /**
     * 生命偷取：造成物理伤害时按比例转化为自身治疗。1.0 = 100%。
     * 结算见 {@link com.xlxyvergil.tcc.event.LifeStealOverhealHandler}。
     */
    public static final RegistryObject<Attribute> LIFE_STEAL =
        ATTRIBUTES.register("life_steal",
            () -> AttributesLibCompat.create("attribute.name.tcc.life_steal", 0.0D, 0.0D, 10.0D).setSyncable(true));

    /**
     * 过量治疗：造成物理伤害时按比例转化为伤害吸收（金心），上限为攻击者最大生命的一半。1.0 = 100%。
     * 结算见 {@link com.xlxyvergil.tcc.event.LifeStealOverhealHandler}；虚数伤害（含枪械子弹转换来的）
     * 由 TccAttributeEvents#imaginaryDamageOnAttack 在抗性结算后转入同一入口。
     */
    public static final RegistryObject<Attribute> OVERHEAL =
        ATTRIBUTES.register("overheal",
            () -> AttributesLibCompat.create("attribute.name.tcc.overheal", 0.0D, 0.0D, 10.0D).setSyncable(true));
    
    public static void register(IEventBus modEventBus) {
        ATTRIBUTES.register(modEventBus);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(type -> {
            event.add(type, IMAGINARY_DAMAGE_RESISTANCE.get());
            event.add(type, LIFE_STEAL.get());
            event.add(type, OVERHEAL.get());
        });
    }
}
