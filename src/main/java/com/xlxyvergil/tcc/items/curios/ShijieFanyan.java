package com.xlxyvergil.tcc.items.curios;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
public class ShijieFanyan extends BaseCurioItem {

    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("d94e7f8a-2c5d-6a84-d0f7-5e9c2f8b1a4d");
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("e05f8a9b-3d6e-7b95-e1f8-6a0d3f9c2b5e");
    private static final UUID LUCK_UUID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789003");

    private static final String INFECTION_KEY = "tcc_shijie_fanyan_infection";

    public ShijieFanyan(Properties properties) {
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
        if (GunTypeChecker.isHoldingAnyGun(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.LUCK,
                TaczCuriosConfig.COMMON.shijieFanyanLuck.get(), LUCK_UUID,
                "tcc.shijie_fanyan.luck", AttributeModifier.Operation.ADDITION);

            int luck = (int) livingEntity.getAttributeValue(AttributeHelper.LUCK);
            double critChance = Math.round(luck * TaczCuriosConfig.COMMON.shijieFanyanCritChancePerLuck.get() * 100.0) / 100.0;
            double critDamage = Math.round(luck * TaczCuriosConfig.COMMON.shijieFanyanCritDamagePerLuck.get() * 100.0) / 100.0;

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                critChance, CRIT_CHANCE_UUID,
                "tcc.shijie_fanyan.crit_chance", AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                critDamage, CRIT_DAMAGE_UUID,
                "tcc.shijie_fanyan.crit_damage", AttributeModifier.Operation.ADDITION);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.LUCK, LUCK_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
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
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity());
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    public static boolean hasEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof ShijieFanyan).isEmpty();
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingAnyGun(attacker)) return;

        // 所有枪械伤害转为虚数伤害
        event.setDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
        event.setDamageSource(GunDamageSourcePart.ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));

        // 标记触发侵染
        if (event.getBullet() != null) {
            event.getBullet().getPersistentData().putBoolean(INFECTION_KEY, true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingAnyGun(attacker)) return;

        if (event.getBullet() == null) return;
        if (!event.getBullet().getPersistentData().getBoolean(INFECTION_KEY)) return;

        Entity hurtEntity = event.getHurtEntity();
        if (!(hurtEntity instanceof LivingEntity targetLiving)) return;
        if (targetLiving.isDeadOrDying()) return;

        // 攻击必定触发侵染
        var infection = TccMobEffects.IMAGINARY_INFECTION.get();
        int duration = TaczCuriosConfig.COMMON.imaginaryInfectionDuration.get();
        var infectionInstance = new MobEffectInstance(
            infection,
            duration * 20,
            0,
            false, false, true
        );
        targetLiving.addEffect(infectionInstance, attacker);
        forceAddEffect(targetLiving, infectionInstance);

        // 虚数崩解触发概率：基础5% + 每10点幸运值+1%
        int luck = (int) attacker.getAttributeValue(AttributeHelper.LUCK);
        double collapseChance = Math.round((TaczCuriosConfig.COMMON.shijieFanyanCollapseBaseChance.get()
            + (luck / 10.0) * TaczCuriosConfig.COMMON.shijieFanyanCollapsePerLuck.get()) * 100.0) / 100.0;
        var collapse = TccMobEffects.IMAGINARY_COLLAPSE.get();
        if (!targetLiving.hasEffect(collapse) && attacker.getRandom().nextDouble() < collapseChance) {
            int collapseDuration = TaczCuriosConfig.COMMON.imaginaryInfectionDuration.get();
            var collapseInstance = new MobEffectInstance(
                collapse,
                collapseDuration * 20,
                0,
                false, false, true
            );
            targetLiving.addEffect(collapseInstance, attacker);
            forceAddEffect(targetLiving, collapseInstance);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        String gunTypes = GunTypeChecker.formatGunTypes(List.of("pistol", "rifle", "shotgun", "sniper", "smg", "mg", "rpg"));
        tooltip.add(Component.translatable("tcc.tooltip.restricted_gun_types", gunTypes));

        int luck = TaczCuriosConfig.COMMON.shijieFanyanLuck.get();
        double critChance = 0;
        double critDamage = 0;
        double collapseChance = 0;
        if (level != null && level.isClientSide()) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                int actualLuck = (int) player.getAttributeValue(AttributeHelper.LUCK);
                critChance = actualLuck * TaczCuriosConfig.COMMON.shijieFanyanCritChancePerLuck.get() * 100;
                critDamage = actualLuck * TaczCuriosConfig.COMMON.shijieFanyanCritDamagePerLuck.get() * 100;
                collapseChance = (TaczCuriosConfig.COMMON.shijieFanyanCollapseBaseChance.get()
                    + (actualLuck / 10.0) * TaczCuriosConfig.COMMON.shijieFanyanCollapsePerLuck.get()) * 100;
            }
        }
        String sfCritChanceStr = String.format("%.0f", critChance);
        String sfCritDamageStr = String.format("%.0f", critDamage);
        String sfCollapseStr = String.format("%.1f", collapseChance);
        tooltip.add(formatModifierTooltip(luck, "%.0f", Component.translatable(AttributeHelper.LUCK.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(formatModifierTooltip(critChance, "%.0f%%", Component.translatable(AttributeHelper.CRIT_CHANCE.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(formatModifierTooltip(critDamage, "%.0f%%", Component.translatable(AttributeHelper.CRIT_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.shijie_fanyan.special",
                luck, sfCritChanceStr, sfCritDamageStr, sfCollapseStr)
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

    private static void forceAddEffect(LivingEntity e, MobEffectInstance ins) {
        MobEffect effect = ins.getEffect();
        MobEffectInstance old = e.getActiveEffectsMap().get(effect);
        if (old == null) {
            e.getActiveEffectsMap().put(effect, ins);
            effect.addAttributeModifiers(e, e.getAttributes(), ins.getAmplifier());
            e.onEffectAdded(ins, null);
        } else {
            int prevAmp = old.getAmplifier();
            old.update(ins);
            if (old.getAmplifier() != prevAmp) {
                effect.addAttributeModifiers(e, e.getAttributes(), old.getAmplifier());
            }
        }
    }
}
