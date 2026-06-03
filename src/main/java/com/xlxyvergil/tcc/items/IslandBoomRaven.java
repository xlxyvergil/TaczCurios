package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class IslandBoomRaven extends BaseCurioItem {

    private static final UUID ARMOR_UUID = UUID.fromString("2dddf4c2-5d16-4f88-9e08-e5f9131c7b4e");
    private static final UUID MOVE_SPEED_UUID = UUID.fromString("1ed0c2f3-7bcd-4a1e-bc6f-13d1fcb6c7ad");
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("90e98bd7-80b6-4e7f-8b1f-b6a0d74c3f78");

    public IslandBoomRaven(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        ItemStack equipped = findEquippedStack(livingEntity);
        CompoundTag tag = equipped.getTag();
        double total = 10.0 + (tag != null ? getExtraResistanceFromProgress(tag) : 0.0);

        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ARMOR, -0.4, ARMOR_UUID,
            "tcc.island_boom_raven.armor", AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, 1.0, MOVE_SPEED_UUID,
            "tcc.island_boom_raven.movement_speed", AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), total, IMAGINARY_RESISTANCE_UUID,
            "tcc.island_boom_raven.imaginary_resistance", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ARMOR, ARMOR_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, MOVE_SPEED_UUID);
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide) return;

        if (entity.tickCount % 200 == 0) {
            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 600, 0, false, false, true));
        }

        MobEffectInstance regen = entity.getEffect(MobEffects.REGENERATION);
        if (regen == null || regen.getAmplifier() < 1 || regen.getDuration() < 40) {
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 1, false, false, true));
        }
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }

    public static boolean hasEquipped(LivingEntity livingEntity) {
        return !findEquippedStack(livingEntity).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof IslandBoomRaven);
    }

    private static double getExtraResistanceFromProgress(CompoundTag tag) {
        String nbtKey = null;
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.ATTRIBUTE, "tcc:island_boom_raven")) {
            EvolutionRegistry.Progress progress = rule.progress;
            if (progress == null) {
                continue;
            }
            if (!"tcc:imaginary_damage_resistance".equals(progress.attribute)) {
                continue;
            }
            if (progress.operation != AttributeModifier.Operation.ADDITION) {
                continue;
            }
            nbtKey = progress.nbtKey;
        }
        if (nbtKey == null) {
            return 0.0;
        }
        return tag.getDouble(nbtKey);
    }
}
