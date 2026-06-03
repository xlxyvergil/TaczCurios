package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Xiora extends BaseCurioItem {

    private static final UUID ARMOR_UUID = UUID.fromString("1b0eb9dc-4f2c-4b2f-9e80-cb6c3b0a9b3c");
    private static final UUID MOVE_SPEED_UUID = UUID.fromString("d8a8f4c6-1a12-4c3a-9ee2-7b190f0a0cf7");
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("8f21d12f-2c90-4c8f-a3f0-6bf0f64b4fdf");

    public Xiora(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        ItemStack equipped = findEquippedStack(livingEntity);
        CompoundTag tag = equipped.getTag();
        double total = 10.0 + (tag != null ? getExtraResistanceFromProgress(tag) : 0.0);

        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ARMOR, -0.2, ARMOR_UUID,
            "tcc.xiora.armor", AttributeModifier.Operation.MULTIPLY_TOTAL);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, 0.5, MOVE_SPEED_UUID,
            "tcc.xiora.movement_speed", AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), total, IMAGINARY_RESISTANCE_UUID,
            "tcc.xiora.imaginary_resistance", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ARMOR, ARMOR_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MOVEMENT_SPEED, MOVE_SPEED_UUID);
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }

    public static boolean hasEquipped(LivingEntity livingEntity) {
        return !findEquippedStack(livingEntity).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof Xiora);
    }

    private static double getExtraResistanceFromProgress(CompoundTag tag) {
        String nbtKey = null;
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.ATTRIBUTE, "tcc:xiora")) {
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

    private static double getMaxExtraResistanceFromProgressRules() {
        double cap = 0.0;
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.ATTRIBUTE, "tcc:xiora")) {
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
            cap = Math.max(cap, progress.cap);
        }
        return cap;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        CompoundTag tag = stack.getTag();
        double extra = tag != null ? getExtraResistanceFromProgress(tag) : 0.0;
        double cap = getMaxExtraResistanceFromProgressRules();
        double total = 10.0 + extra;

        tooltip.add(Component.translatable("item.tcc.xiora.effect",
                String.format("%+.0f", -20.0),
                String.format("%+.0f", 50.0))
            .withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.translatable("item.tcc.xiora.resistance", String.format("%.0f", total))
            .withStyle(ChatFormatting.AQUA));

        List<EvolutionRegistry.KillRequirement> requirements = getEvolutionKillRequirements();
        if (!requirements.isEmpty()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.xiora.kill_progress_title")
                .withStyle(ChatFormatting.GREEN));
            CompoundTag killCounts = tag != null ? tag.getCompound(com.xlxyvergil.tcc.util.EvolutionNbtKeys.KILL_COUNTS) : null;
            for (EvolutionRegistry.KillRequirement req : requirements) {
                String matchKey = EntityConditionHelper.getMatchKey(req.entity.key, req.entity.nbt);
                int current = killCounts != null ? killCounts.getInt(matchKey) : 0;
                String entityDisplay = getEntityDisplayName(req.entity);
                tooltip.add(Component.translatable("item.tcc.xiora.evolution_progress", entityDisplay, current, req.count)
                    .withStyle(current >= req.count ? ChatFormatting.GREEN : ChatFormatting.GRAY));
            }
        }

        Map<String, Double> sources = new LinkedHashMap<>();
        Map<String, EvolutionRegistry.EntityRef> sourceEntities = new LinkedHashMap<>();
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.ATTRIBUTE, "tcc:xiora")) {
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
            tooltip.add(Component.translatable("item.tcc.xiora.resist_source_title", String.format("%.0f", cap))
                .withStyle(ChatFormatting.AQUA));
            for (var entry : sources.entrySet()) {
                EvolutionRegistry.EntityRef entity = sourceEntities.get(entry.getKey());
                String display = entity != null ? getEntityDisplayName(entity) : entry.getKey();
                tooltip.add(Component.translatable("item.tcc.xiora.resist_detail", display, (int) Math.round(entry.getValue()))
                    .withStyle(ChatFormatting.GRAY));
            }
        }

        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.xiora.how_to_obtain")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static List<EvolutionRegistry.KillRequirement> getEvolutionKillRequirements() {
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.EVOLVE, "tcc:xiora")) {
            if ("tcc:raven".equals(rule.to)) {
                return rule.requirements.kills;
            }
        }
        return List.of();
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
