package com.xlxyvergil.tcc.core;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TccDamageSources {
    
    
    public static final ResourceKey<DamageType> IMAGINARY_DAMAGE = 
        ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tcc", "imaginary_damage"));
    
    public static final TagKey<DamageType> IMAGINARY_DAMAGE_TAG = 
        TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tcc", "imaginary_damage"));
    
    private static Holder<DamageType> imaginaryDamageHolder;
    
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        imaginaryDamageHolder = event.getServer().registryAccess()
            .registryOrThrow(Registries.DAMAGE_TYPE)
            .getHolderOrThrow(IMAGINARY_DAMAGE);
    }
    
    public static DamageSource imaginaryDamage(Entity attacker) {
        // 使用三参数构造函数，使 source.getEntity() 能正确返回攻击者
        return new DamageSource(imaginaryDamageHolder, null, attacker);
    }
}
