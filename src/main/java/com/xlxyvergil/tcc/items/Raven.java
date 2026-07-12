package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Raven extends BaseCurioItem {

    private static final UUID ARMOR_UUID = UUID.fromString("3d18c48e-0b11-4cb9-ae4e-55f9e1bf78d6");
    private static final UUID MOVE_SPEED_UUID = UUID.fromString("c2c51883-9c72-46bf-9f46-2d4a622b0e08");
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("22b2a8f2-1f8d-4b32-b243-5021d626b1fa");

    public Raven(Properties properties) {
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
        ItemStack equipped = findEquippedStack(livingEntity);
        CompoundTag tag = equipped.getTag();
        double total = ImaginaryResistanceHelper.calculateTotalResistance(TaczCuriosConfig.COMMON.xioraBaseResistance.get(), tag);

        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ARMOR, TaczCuriosConfig.COMMON.ravenArmorMultiplier.get(), ARMOR_UUID,
            "tcc.raven.armor", AttributeModifier.Operation.MULTIPLY_TOTAL);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, TaczCuriosConfig.COMMON.ravenSpeedMultiplier.get(), MOVE_SPEED_UUID,
            "tcc.raven.movement_speed", AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), total, IMAGINARY_RESISTANCE_UUID,
            "tcc.raven.imaginary_resistance", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ARMOR, ARMOR_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, MOVE_SPEED_UUID);
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
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
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide) return;

        // 每N秒重新施加隐身（持续刷新）
        if (entity.tickCount % TaczCuriosConfig.COMMON.ravenInvisRefreshInterval.get() == 0) {
            int duration = TaczCuriosConfig.COMMON.ravenInvisDuration.get();
            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false, true));

            // 如果加载了铁魔法，同时施加真实隐身（完全阻止怪物追踪）
            if (ModList.get().isLoaded("irons_spellbooks")) {
                MobEffect trueInvis = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("irons_spellbooks", "true_invisibility"));
                if (trueInvis != null) {
                    entity.addEffect(new MobEffectInstance(trueInvis, duration, 0, false, false, true));
                }
            }
        }

        // 攻击后N秒破除隐身
        int lastHurtTs = entity.getLastHurtMobTimestamp();
        int breakDelay = TaczCuriosConfig.COMMON.ravenInvisBreakDelay.get();
        if (lastHurtTs > 0 && entity.tickCount - lastHurtTs == breakDelay) {
            entity.removeEffect(MobEffects.INVISIBILITY);
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
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof Raven);
    }

    

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        CompoundTag tag = stack.getTag();
        double extra = ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        double cap = ImaginaryResistanceHelper.getMaxExtraResistanceFromProgressRules("tcc:raven");
        double total = TaczCuriosConfig.COMMON.xioraBaseResistance.get() + extra;

        tooltip.add(Component.translatable("item.tcc.raven.effect",
                String.format("%+.0f", TaczCuriosConfig.COMMON.ravenArmorMultiplier.get() * 100),
                String.format("%+.0f", TaczCuriosConfig.COMMON.ravenSpeedMultiplier.get() * 100),
                String.format("%.1f", TaczCuriosConfig.COMMON.ravenInvisRefreshInterval.get() / 20.0),
                String.format("%.1f", TaczCuriosConfig.COMMON.ravenInvisDuration.get() / 20.0))
            .withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.translatable("item.tcc.raven.resistance", String.format("%.0f", total))
            .withStyle(ChatFormatting.AQUA));

        EvolutionRegistry.Rule evolveRule = getEvolutionRuleOrNull();
        if (evolveRule != null && !evolveRule.requirements.kills.isEmpty()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.raven.kill_progress_title")
                .withStyle(ChatFormatting.GREEN));
            CompoundTag killCounts = tag != null ? tag.getCompound(com.xlxyvergil.tcc.util.EvolutionNbtKeys.KILL_COUNTS) : null;
            for (EvolutionRegistry.KillRequirement req : evolveRule.requirements.kills) {
                String matchKey = EntityConditionHelper.getMatchKey(req.entity.key, req.entity.nbt);
                int current = killCounts != null ? killCounts.getInt(matchKey) : 0;
                String entityDisplay = getEntityDisplayName(req.entity);
                tooltip.add(Component.translatable("item.tcc.raven.evolution_progress", entityDisplay, current, req.count)
                    .withStyle(current >= req.count ? ChatFormatting.GREEN : ChatFormatting.GRAY));
            }
        }

        Map<String, Double> sources = new LinkedHashMap<>();
        Map<String, EvolutionRegistry.EntityRef> sourceEntities = new LinkedHashMap<>();
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.ATTRIBUTE, "tcc:raven")) {
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
            for (EvolutionRegistry.KillGain k : rule.kills) {
                String key = EntityConditionHelper.getMatchKey(k.entity.key, k.entity.nbt);
                sources.merge(key, k.value, Double::sum);
                sourceEntities.putIfAbsent(key, k.entity);
            }
        }

        if (cap > 0 && !sources.isEmpty()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.raven.resist_source_title", String.format("%.0f", cap))
                .withStyle(ChatFormatting.AQUA));
            for (var entry : sources.entrySet()) {
                EvolutionRegistry.EntityRef entity = sourceEntities.get(entry.getKey());
                String display = entity != null ? getEntityDisplayName(entity) : entry.getKey();
                tooltip.add(Component.translatable("item.tcc.raven.resist_detail", display, (int) Math.round(entry.getValue()))
                    .withStyle(ChatFormatting.GRAY));
            }
        }

        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.raven.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }

        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.raven.how_to_obtain")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static EvolutionRegistry.Rule getEvolutionRuleOrNull() {
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.EVOLVE, "tcc:raven")) {
            if ("tcc:island_boom_raven".equals(rule.to)) {
                return rule;
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
}
