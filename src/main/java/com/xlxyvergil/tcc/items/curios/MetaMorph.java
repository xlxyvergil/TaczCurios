package com.xlxyvergil.tcc.items.curios;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MetaMorph extends BaseCurioItem {

    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-1234567893");
    private static final UUID LIFE_STEAL_UUID = UUID.fromString("d4e5f6a7-b8c9-0123-def4-567890abcdef");

    public MetaMorph(Properties properties) {
        super(properties);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        if (slotContext.entity() instanceof Player player) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.getBoolean("IsBound")) {
                tag.putBoolean("IsBound", true);
                tag.putString("BoundPlayer", player.getStringUUID());
                tag.putString("BoundPlayerName", player.getGameProfile().getName());
            }
        }
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingMeleeWeapon(livingEntity)) {
            double maxHealth = livingEntity.getAttributeValue(Attributes.MAX_HEALTH);
            double totalResistance = livingEntity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            double attackBonus = (maxHealth * TaczCuriosConfig.COMMON.metaMorphHealthToAttackPercent.get()
                + totalResistance * TaczCuriosConfig.COMMON.metaMorphResistanceToAttackPercent.get()) / 100.0;
            AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_DAMAGE,
                attackBonus, ATTACK_DAMAGE_UUID,
                "tcc.meta_morph.attack_damage", AttributeModifier.Operation.MULTIPLY_BASE);

            double lifeSteal = Math.round(totalResistance * TaczCuriosConfig.COMMON.metaMorphLifeStealPerResistance.get() * 100.0) / 100.0;
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.LIFE_STEAL,
                lifeSteal, LIFE_STEAL_UUID,
                "tcc.meta_morph.life_steal", AttributeModifier.Operation.ADDITION);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.LIFE_STEAL, LIFE_STEAL_UUID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerUUID = tag.getString("BoundPlayer");
            if (slotContext.entity() instanceof Player player) {
                return player.getStringUUID().equals(boundPlayerUUID);
            }
            return false;
        }
        return super.canEquip(slotContext, stack);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof MetaMorph).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        DamageSource source = event.getSource();
        if (source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) return;

        Entity attackerEntity = source.getEntity();
        if (!(attackerEntity instanceof LivingEntity attacker)) return;
        if (!isEquipped(attacker)) return;
        if (!GunTypeChecker.isHoldingMeleeWeapon(attacker)) return;

        LivingEntity target = event.getEntity();
        if (target.isDeadOrDying()) return;

        double attackDamage = attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float imaginaryBonus = (float) (Math.round(attackDamage * 100.0) / 100.0);
        TccAttributeEvents.applyImaginaryDamage(
            target,
            TccDamageSources.imaginaryDamage(target.level(), attacker),
            imaginaryBonus
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

tooltip.add(Component.translatable("tcc.tooltip.restricted_melee"));

        double attackFromHealth = 0;
        double lifeStealFromResistance = 0;
        double imaginaryDamage = 0;
        if (level != null && level.isClientSide()) {
            Player player = Minecraft.getInstance().player;
            if (player != null && isEquipped(player)) {
                double maxHealth = player.getAttributeValue(Attributes.MAX_HEALTH);
                double resistance = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                attackFromHealth = (maxHealth * TaczCuriosConfig.COMMON.metaMorphHealthToAttackPercent.get()
                    + resistance * TaczCuriosConfig.COMMON.metaMorphResistanceToAttackPercent.get()) / 100.0;
                lifeStealFromResistance = resistance * TaczCuriosConfig.COMMON.metaMorphLifeStealPerResistance.get();
                imaginaryDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            }
        }
        tooltip.add(formatModifierTooltip(attackFromHealth * 100, "%.1f%%", Component.translatable(AttributeHelper.ATTACK_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.affected_by_max_health")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(formatModifierTooltip(lifeStealFromResistance, "%.2f", Component.translatable(AttributeHelper.LIFE_STEAL.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.tcc.meta_morph.special_damage",
                String.format("%.2f", imaginaryDamage))
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rift"));

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
    }
}
