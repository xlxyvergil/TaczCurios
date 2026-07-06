package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.EvolutionNbtKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 梵天百兽 - 提供基础40点虚数抗性，击杀配置列表中的实体获得额外抗性
 */
public class BrahmaBeasts extends BaseCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_MODIFIER_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");
    
    public BrahmaBeasts(Properties properties) {
        super(properties.stacksTo(1).fireResistant());
    }
    
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    
    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 0;
    }
    
    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return false;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        ItemStack equipped = findEquippedStack(livingEntity);
        CompoundTag tag = equipped.getTag();
        double total = ImaginaryResistanceHelper.calculateTotalResistance(getBaseResistance(), tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            total, IMAGINARY_RESISTANCE_MODIFIER_UUID, "tcc_brahma_beasts_resistance", AttributeModifier.Operation.ADDITION);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_MODIFIER_UUID);
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        
        CompoundTag tag = stack.getTag();
        
        double baseValue = getBaseResistance();
        double maxProgress = ImaginaryResistanceHelper.getMaxExtraResistanceFromProgressRules("tcc:brahma_beasts");
        double total = baseValue + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.brahma_beasts.effect", String.format("%.0f", total))
            .withStyle(ChatFormatting.AQUA));
        
        // EL 第四诅咒削弱（仅加载神秘遗物时显示）
        if (net.minecraftforge.fml.ModList.get().isLoaded("enigmaticlegacy")) {
            double curseReduction = TaczCuriosConfig.COMMON.brahmaBeastsELCurseReduction.get();
            tooltip.add(Component.translatable("item.tcc.brahma_beasts.el_curse_reduction", String.format("%.0f", curseReduction * 100))
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        
        List<EvolutionRegistry.KillRequirement> requirements = getEvolutionKillRequirements();
        if (!requirements.isEmpty()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.brahma_beasts.kill_progress_title")
                .withStyle(ChatFormatting.GREEN));
            CompoundTag killCounts = tag != null ? tag.getCompound(EvolutionNbtKeys.KILL_COUNTS) : null;
            for (EvolutionRegistry.KillRequirement req : requirements) {
                String matchKey = EntityConditionHelper.getMatchKey(req.entity.key, req.entity.nbt);
                int current = killCounts != null ? killCounts.getInt(matchKey) : 0;
                String entityDisplay = getEntityDisplayName(req.entity);
                tooltip.add(Component.translatable("item.tcc.brahma_beasts.evolution_progress", entityDisplay, current, req.count)
                    .withStyle(current >= req.count ? ChatFormatting.GREEN : ChatFormatting.GRAY));
            }
        }
        
        Map<String, Double> sources = new LinkedHashMap<>();
        Map<String, EvolutionRegistry.EntityRef> sourceEntities = new LinkedHashMap<>();
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.ATTRIBUTE, "tcc:brahma_beasts")) {
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

        if (maxProgress > 0 && !sources.isEmpty()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.brahma_beasts.resist_source_title", String.format("%.0f", maxProgress))
                .withStyle(ChatFormatting.AQUA));
            for (var entry : sources.entrySet()) {
                EvolutionRegistry.EntityRef entity = sourceEntities.get(entry.getKey());
                String display = entity != null ? getEntityDisplayName(entity) : entry.getKey();
                tooltip.add(Component.translatable("item.tcc.brahma_beasts.resist_detail", display, (int) Math.round(entry.getValue()))
                    .withStyle(ChatFormatting.GRAY));
            }
        }
        
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.brahma_beasts.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
        
        tooltip.add(Component.literal(""));
 
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
        
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.brahma_beasts.how_to_obtain")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static List<EvolutionRegistry.KillRequirement> getEvolutionKillRequirements() {
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.EVOLVE, "tcc:brahma_beasts")) {
            if ("tcc:salvation".equals(rule.to)) {
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

    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
    
    public static boolean hasBrahmaBeastsEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof BrahmaBeasts).isEmpty();
    }

    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof BrahmaBeasts);
    }

    private static int getBaseResistance() {
        return TaczCuriosConfig.COMMON.summerBeachBaseResistance.get();
    }

    }
