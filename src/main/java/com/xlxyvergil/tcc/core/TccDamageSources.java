package com.xlxyvergil.tcc.core;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TccDamageSources {
    
    
    public static final ResourceKey<DamageType> VOID_DAMAGE = 
        ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tcc", "void_damage"));
    
    private static Holder<DamageType> voidDamageHolder;
    
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        voidDamageHolder = event.getServer().registryAccess()
            .registryOrThrow(Registries.DAMAGE_TYPE)
            .getHolderOrThrow(VOID_DAMAGE);
    }
    
    public static DamageSource voidDamage(Entity attacker) {
        return new DamageSource(voidDamageHolder);
    }
}
