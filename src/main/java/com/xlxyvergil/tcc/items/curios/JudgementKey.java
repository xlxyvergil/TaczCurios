package com.xlxyvergil.tcc.items.curios;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JudgementKey extends BaseCurioItem {

    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("f13a5b08-523d-4b62-b9f4-8a284f9c3bdf");
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("2a1e47bd-1b05-44cf-9a2c-ea6c0612b47c");

    private static final String PROC_KEY = "tcc_judgement_key_set_proc";
    private static final String PROC_DAMAGE_KEY = "tcc_judgement_key_set_damage";
    private static final String PROC_DAMAGE_AFTER_HEADSHOT_KEY = "tcc_judgement_key_set_damage_after_headshot";

    public JudgementKey(Properties properties) {
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
        if (GunTypeChecker.isHoldingSniper(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                TaczCuriosConfig.COMMON.judgementKeyCritChance.get(), CRIT_CHANCE_UUID,
                "tcc.judgement_key.crit_chance", AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                TaczCuriosConfig.COMMON.judgementKeyCritDamage.get(), CRIT_DAMAGE_UUID,
                "tcc.judgement_key.crit_damage", AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
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

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }

    public static boolean hasEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof JudgementKey).isEmpty();
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingSniper(attacker)) return;

        // 始终转换为虚数伤害（触发虚数侵染 + 虚数抗性计算）
        event.setDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
        event.setDamageSource(GunDamageSourcePart.ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));

        if (!event.isHeadShot()) return;

        // 始终记录伤害数据，Post 事件独立掷 setHealth 和崩解几率
        if (event.getBullet() != null) {
            event.getBullet().getPersistentData().putBoolean(PROC_KEY, true);
            float damage = event.getBaseAmount();
            event.getBullet().getPersistentData().putFloat(PROC_DAMAGE_KEY, damage);
            float damageAfterHeadshot = damage * event.getHeadshotMultiplier();
            event.getBullet().getPersistentData().putFloat(PROC_DAMAGE_AFTER_HEADSHOT_KEY, damageAfterHeadshot);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingSniper(attacker)) return;

        Entity bullet = event.getBullet();
        if (bullet == null) return;

        var data = bullet.getPersistentData();
        if (!data.getBoolean(PROC_KEY)) return;

        float damageAfterHeadshot = data.getFloat(PROC_DAMAGE_AFTER_HEADSHOT_KEY);

        Entity hurtEntity = event.getHurtEntity();
        if (!(hurtEntity instanceof LivingEntity targetLiving)) return;
        if (targetLiving.isDeadOrDying()) return;

        // 1. 独立掷 setHealth 直接真伤
        double setHealthProc = TaczCuriosConfig.COMMON.judgementProcChance.get();
        if (attacker.getRandom().nextDouble() < setHealthProc && damageAfterHeadshot > 0) {
            double directPercent = TaczCuriosConfig.COMMON.judgementDirectDamagePercent.get();
            float directDamage = (float) (damageAfterHeadshot * directPercent);
            TccAttributeEvents.applyImaginaryDamage(targetLiving, TccDamageSources.imaginaryDamage(targetLiving.level(), attacker), directDamage);
        }

        // 2. 独立掷虚数崩解
        double collapseProc = TaczCuriosConfig.COMMON.judgementCollapseProcChance.get();
        var collapse = TccMobEffects.IMAGINARY_COLLAPSE.get();
        if (!targetLiving.hasEffect(collapse) && attacker.getRandom().nextDouble() < collapseProc) {
            int duration = TaczCuriosConfig.COMMON.imaginaryInfectionDuration.get();
            var collapseInstance = new MobEffectInstance(
                collapse,
                duration * 20,
                0,
                false, false, true
            );
            targetLiving.addEffect(collapseInstance, attacker);
            forceAddEffect(targetLiving, collapseInstance);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        String gunTypes = GunTypeChecker.formatGunTypes(List.of("sniper"));
        tooltip.add(Component.translatable("tcc.tooltip.restricted_gun_types", gunTypes));

        tooltip.add(Component.translatable("item.tcc.judgement_key.effect",
                String.format("%+.0f", TaczCuriosConfig.COMMON.judgementKeyCritChance.get() * 100),
                String.format("%+.0f", TaczCuriosConfig.COMMON.judgementKeyCritDamage.get() * 100))
            .withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("tcc.tooltip.rarity.rift"));

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.judgement_key.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }

        EvolutionRegistry.Rule evolveRule = getLinkedEvolveRuleOrNull();
        if (evolveRule != null) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.judgement_key.evolution_title")
                .withStyle(ChatFormatting.GREEN));
            if (!evolveRule.requirements.kills.isEmpty()) {
                for (EvolutionRegistry.KillRequirement req : evolveRule.requirements.kills) {
                    tooltip.add(Component.translatable("item.tcc.judgement_key.evolution_kill",
                            getEntityDisplayName(req.entity), req.count)
                        .withStyle(ChatFormatting.GRAY));
                }
            }
        }

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.judgement_key.how_to_obtain")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static EvolutionRegistry.Rule getLinkedEvolveRuleOrNull() {
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getAllRules()) {
            if (rule.type != EvolutionRegistry.RuleType.EVOLVE) continue;
            if (rule.requirementsRef == null || rule.requirementsRef.isEmpty()) continue;
            for (EvolutionRegistry.LinkedEvolve linked : rule.requirementsRef) {
                if ("tcc:seven_thunders_thunder_seen".equals(linked.item) && "tcc:judgement_key".equals(linked.to)) {
                    return rule;
                }
            }
        }
        return null;
    }

    private static String getEntityDisplayName(EvolutionRegistry.EntityRef entity) {
        try {
            ResourceLocation rl = new ResourceLocation(entity.key);
            var entityType = BuiltInRegistries.ENTITY_TYPE.get(rl);
            String suffix = entity.name == null || entity.name.isBlank() ? "" : " " + entity.name;
            return entityType.getDescription().getString() + suffix;
        } catch (Exception ignored) {
            return entity.key;
        }
    }

    /**
     * 与崩解完全一致的 forceAddEffect 模式：
     * 直接操作 activeEffectsMap，绕过 MobEffectEvent.Added，确保效果不被外部监听器干扰。
     */
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
