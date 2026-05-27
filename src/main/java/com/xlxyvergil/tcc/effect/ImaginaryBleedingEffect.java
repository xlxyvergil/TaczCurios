package com.xlxyvergil.tcc.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.function.Consumer;

public class ImaginaryBleedingEffect extends MobEffect {

    private static final ResourceLocation ICON = new ResourceLocation("tcc", "textures/mob_effect/imaginary_bleeding.png");

    public ImaginaryBleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                RenderSystem.setShaderTexture(0, ICON);
                RenderSystem.enableBlend();
                guiGraphics.blit(ICON, x, y, 0, 0, 18, 18);
                return true;
            }

            @Override
            public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics guiGraphics, int x, int y, float z, float alpha) {
                RenderSystem.setShaderTexture(0, ICON);
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                guiGraphics.blit(ICON, x, y, 0, 0, 18, 18);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                return true;
            }

            @Override
            public boolean isVisibleInInventory(MobEffectInstance instance) {
                return true;
            }

            @Override
            public boolean isVisibleInGui(MobEffectInstance instance) {
                return true;
            }
        });
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        double damagePerLevel = -TaczCuriosConfig.COMMON.imaginaryBleedingDamagePerLevel.get();
        float maxHealth = entity.getMaxHealth();
        float damage = (float) (maxHealth * damagePerLevel * (amplifier + 1));
        DamageSource imaginarySource = TccDamageSources.imaginaryDamage(entity);
        entity.hurt(imaginarySource, damage);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }
}