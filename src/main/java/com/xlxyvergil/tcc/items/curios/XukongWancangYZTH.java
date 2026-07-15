package com.xlxyvergil.tcc.items.curios;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.AmmoRegenHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.xlxyvergil.tcc.attribute.TccAttributes;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class XukongWancangYZTH extends BaseCurioItem {

    private static final String INFECTION_KEY = "tcc_xukongwancang_infection";

    public XukongWancangYZTH(Properties properties) {
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
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
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
            stack -> stack.getItem() instanceof XukongWancangYZTH).isEmpty();
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !isEquipped(attacker)) return;
        if (!GunTypeChecker.isHoldingHeavyWeapon(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;

        event.setDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
        event.setDamageSource(GunDamageSourcePart.ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));

        if (event.getBullet() != null) {
            event.getBullet().getPersistentData().putBoolean(INFECTION_KEY, true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !isEquipped(attacker)) return;
        if (!GunTypeChecker.isHoldingHeavyWeapon(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;

        Entity hurtEntity = event.getHurtEntity();
        if (!(hurtEntity instanceof LivingEntity targetLiving)) return;
        if (targetLiving.isDeadOrDying()) return;

        // 额外虚数伤害：20 + attack_damage * (虚数抗性 / 100)
        double attackDamage = attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double imaginaryResistance = attacker.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        float imaginaryBonus = (float) (TaczCuriosConfig.COMMON.xukongWancangYZTHImaginaryDamage.get().floatValue()
            + (float) Math.round(attackDamage * (imaginaryResistance / 100.0) * 100.0) / 100.0);
        TccAttributeEvents.applyImaginaryDamage(
            targetLiving,
            TccDamageSources.imaginaryDamage(targetLiving.level(), attacker),
            imaginaryBonus
        );

        if (event.getBullet() == null) return;
        if (!event.getBullet().getPersistentData().getBoolean(INFECTION_KEY)) return;

        // 攻击必定触发侵染
        var infection = TccMobEffects.IMAGINARY_INFECTION.get();
        int duration = TaczCuriosConfig.COMMON.xukongWancangYZTHInfectionDuration.get();
        var infectionInstance = new MobEffectInstance(
            infection, duration * 20, 0,
            false, false, true
        );
        targetLiving.addEffect(infectionInstance, attacker);
        forceAddEffect(targetLiving, infectionInstance);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        // 每秒恢复一次弹药
        if (player.tickCount % 20 != 0) return;
        if (!GunTypeChecker.isHoldingHeavyWeapon(player)) return;

        ItemStack held = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(held);
        if (iGun == null) return;

        AmmoRegenHelper.regenAmmo(player, held, iGun,
            (double) TaczCuriosConfig.COMMON.xukongWancangYZTHAmmoRegenPercent.get());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double ammoRegen = TaczCuriosConfig.COMMON.xukongWancangYZTHAmmoRegenPercent.get() * 100;

        String gunTypes = GunTypeChecker.formatGunTypes(List.of("rpg", "mg"));
        tooltip.add(Component.translatable("tcc.tooltip.restricted_gun_types", gunTypes));

        double computedImaginaryDamage = 0;
        if (level != null && level.isClientSide()) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                double attackDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                double resistance = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                computedImaginaryDamage = TaczCuriosConfig.COMMON.xukongWancangYZTHImaginaryDamage.get()
                    + attackDamage * (resistance / 100.0);
            }
        }
        tooltip.add(Component.translatable("item.tcc.xukong_wancang_yzth.effect.damage",
                String.format("%.0f", computedImaginaryDamage))
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.xukong_wancang_yzth.effect.ammo",
                String.format("%.0f", ammoRegen))
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.gun_to_imaginary")
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.always_infection")
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

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
