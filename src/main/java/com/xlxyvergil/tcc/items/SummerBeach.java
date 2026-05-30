package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccAttributes;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
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
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 夏日沙滩 - 提供基础20点虚数抗性，击杀配置列表中的实体获得额外抗性
 */
public class SummerBeach extends BaseCurioItem {
    
    // 虚数抗性修饰符的UUID（确保唯一性）
    private static final UUID IMAGINARY_RESISTANCE_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    
    // NBT 标签键
    private static final String KILL_COUNTS_TAG = "KillCounts";
    private static final String CARRIED_RESISTANCE_TAG = "CarriedResistance";
    public static final int MAX_KILL_RESISTANCE = 20;
    
    public SummerBeach(Properties properties) {
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
        int baseResistance = TaczCuriosConfig.COMMON.summerBeachBaseResistance.get();
        int maxKillResistance = TaczCuriosConfig.COMMON.summerBeachMaxKillResistance.get();
        int resistanceFromKills = Math.min(getResistanceFromKills(livingEntity), maxKillResistance);
        int carriedResistance = getCarriedResistance(livingEntity);
        double totalResistance = Math.min(baseResistance + carriedResistance + resistanceFromKills, baseResistance + maxKillResistance);
        
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
            totalResistance, IMAGINARY_RESISTANCE_MODIFIER_UUID, "tcc_summer_beach_resistance", AttributeModifier.Operation.ADDITION);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
            IMAGINARY_RESISTANCE_MODIFIER_UUID);
    }
    
    /**
     * 按抵抗实体列表加权计算击杀带来的虚数抗性
     */
    public static int getResistanceFromKills(LivingEntity entity) {
        if (!(entity instanceof Player player)) return 0;
        List<? extends List<String>> resistanceEntities = TaczCuriosConfig.COMMON.summerBeachResistanceEntities.get();
        
        return CuriosApi.getCuriosInventory(player)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof SummerBeach) {
                            CompoundTag tag = stack.getTag();
                            if (tag != null) {
                                CompoundTag killCounts = tag.getCompound(KILL_COUNTS_TAG);
                                int total = 0;
                                for (List<String> entry : resistanceEntities) {
                                    int kills = killCounts.getInt(entry.get(0));
                                    int resistancePerKill = Integer.parseInt(entry.get(1));
                                    total += kills * resistancePerKill;
                                }
                                return total;
                            }
                            return 0;
                        }
                    }
                }
                return 0;
            })
            .orElse(0);
    }
    
    /**
     * 获取从上一级进化继承的抗性值
     */
    public static int getCarriedResistance(LivingEntity entity) {
        if (!(entity instanceof Player player)) return 0;
        
        return CuriosApi.getCuriosInventory(player)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof SummerBeach) {
                            CompoundTag tag = stack.getTag();
                            return tag != null ? tag.getInt(CARRIED_RESISTANCE_TAG) : 0;
                        }
                    }
                }
                return 0;
            })
            .orElse(0);
    }
    
    /**
     * 获取该玩家装备的夏日沙滩的所有实体总击杀数
     */
    public static int getTotalKills(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return 0;
        }
        
        return CuriosApi.getCuriosInventory(player)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof SummerBeach) {
                            CompoundTag tag = stack.getTag();
                            if (tag != null) {
                                CompoundTag killCounts = tag.getCompound(KILL_COUNTS_TAG);
                                int total = 0;
                                for (String key : killCounts.getAllKeys()) {
                                    total += killCounts.getInt(key);
                                }
                                return total;
                            }
                            return 0;
                        }
                    }
                }
                return 0;
            })
            .orElse(0);
    }
    
    /**
     * 增加夏日沙滩的某个实体击杀数
     */
    public static void incrementEntityKillCount(Player player, String entityKey, int maxCount) {
        CuriosApi.getCuriosInventory(player)
            .ifPresent(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof SummerBeach sb) {
                            CompoundTag tag = stack.getOrCreateTag();
                            CompoundTag killCounts = tag.getCompound(KILL_COUNTS_TAG);
                            int currentCount = killCounts.getInt(entityKey);
                            if (currentCount < maxCount) {
                                killCounts.putInt(entityKey, currentCount + 1);
                                tag.put(KILL_COUNTS_TAG, killCounts);
                                // 立即刷新虚数抗性属性
                                sb.removeEffects(player);
                                sb.applyEffects(player);
                            }
                            break;
                        }
                    }
                }
            });
    }
    
    /**
     * 检查是否满足所有进化需求
     */
    public static boolean areAllRequirementsMet(LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;
        
        List<? extends List<String>> requirements = TaczCuriosConfig.COMMON.summerBeachEvolutionRequirements.get();
        if (requirements.isEmpty()) return false;
        
        return CuriosApi.getCuriosInventory(player)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof SummerBeach) {
                            CompoundTag tag = stack.getTag();
                            if (tag == null) return false;
                            CompoundTag killCounts = tag.getCompound(KILL_COUNTS_TAG);
                            for (List<String> req : requirements) {
                                String reqEntity = req.get(0);
                                int required = Integer.parseInt(req.get(1));
                                if (killCounts.getInt(reqEntity) < required) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
                return false;
            })
            .orElse(false);
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
        
        // NBT
        CompoundTag tag = stack.getTag();
        
        // 装备效果
        // 显示当前抗性构成
        int baseResistance = TaczCuriosConfig.COMMON.summerBeachBaseResistance.get();
        if (tag != null) {
            int carried = tag.getInt(CARRIED_RESISTANCE_TAG);
            int fromKills = 0;
            int maxKillResistance = TaczCuriosConfig.COMMON.summerBeachMaxKillResistance.get();
            List<? extends List<String>> resistList = TaczCuriosConfig.COMMON.summerBeachResistanceEntities.get();
            CompoundTag killCounts = tag.getCompound(KILL_COUNTS_TAG);
            for (List<String> entry : resistList) {
                int kills = killCounts.getInt(entry.get(0));
                int perKill = Integer.parseInt(entry.get(1));
                fromKills += kills * perKill;
            }
            fromKills = Math.min(fromKills, maxKillResistance);
            int total = baseResistance + carried + fromKills;
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.summer_beach.effect", String.valueOf(total))
                .withStyle(ChatFormatting.AQUA));
        } else {
            tooltip.add(Component.translatable("item.tcc.summer_beach.effect", baseResistance + "~" + (baseResistance + TaczCuriosConfig.COMMON.summerBeachMaxKillResistance.get()))
                .withStyle(ChatFormatting.AQUA));
        }
        
        // 天火增强
        double multiplier = TaczCuriosConfig.COMMON.summerBeachHeavenFireMultiplier.get();
        tooltip.add(Component.translatable("item.tcc.summer_beach.heaven_fire_boost", String.format("%.0f", multiplier * 100))
            .withStyle(ChatFormatting.GOLD));

        // EL 第四诅咒削弱（仅加载神秘遗物时显示）
        if (net.minecraftforge.fml.ModList.get().isLoaded("enigmaticlegacy")) {
            double curseReduction = TaczCuriosConfig.COMMON.summerBeachELCurseReduction.get();
            tooltip.add(Component.translatable("item.tcc.summer_beach.el_curse_reduction", String.format("%.0f", curseReduction * 100))
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        
        // 显示每个实体的击杀进度
        List<? extends List<String>> requirements = TaczCuriosConfig.COMMON.summerBeachEvolutionRequirements.get();
        if (!requirements.isEmpty()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.summer_beach.kill_progress_title")
                .withStyle(ChatFormatting.GREEN));
            CompoundTag killCounts = tag != null ? tag.getCompound(KILL_COUNTS_TAG) : null;
            for (List<String> req : requirements) {
                String reqEntity = req.get(0);
                int required = Integer.parseInt(req.get(1));
                int current = killCounts != null ? killCounts.getInt(reqEntity) : 0;
                String entityDisplay = getEntityDisplayName(reqEntity);
                tooltip.add(Component.translatable("item.tcc.summer_beach.evolution_progress", entityDisplay, current, required)
                    .withStyle(current >= required ? ChatFormatting.GREEN : ChatFormatting.GRAY));
            }
        }
        
        // 显示抵抗实体列表（击杀增长抗性）
        List<? extends List<String>> resistList = TaczCuriosConfig.COMMON.summerBeachResistanceEntities.get();
        if (!resistList.isEmpty()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.summer_beach.resist_source_title")
                .withStyle(ChatFormatting.AQUA));
            CompoundTag killCounts = tag != null ? tag.getCompound(KILL_COUNTS_TAG) : null;
            for (List<String> entry : resistList) {
                String entityKey = entry.get(0);
                int resistancePerKill = Integer.parseInt(entry.get(1));
                int kills = killCounts != null ? killCounts.getInt(entityKey) : 0;
                String entityDisplay = getEntityDisplayName(entityKey);
                tooltip.add(Component.translatable("item.tcc.summer_beach.resist_detail", entityDisplay, kills * resistancePerKill)
                    .withStyle(ChatFormatting.GRAY));
            }
        }
        
        // 绑定信息
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.summer_beach.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
        
        // 槽位和稀有度
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot.3rd"));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
        
        // 获取方式
        String obtainEntityName = getEntityDisplayName(TaczCuriosConfig.COMMON.summerBeachObtainEntity.get());
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.summer_beach.how_to_obtain", obtainEntityName)
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
    
    private static String getEntityDisplayName(String namespace) {
        try {
            ResourceLocation rl = new ResourceLocation(namespace);
            var entityType = BuiltInRegistries.ENTITY_TYPE.get(rl);
            return entityType.getDescription().getString();
        } catch (Exception ignored) {
            return namespace;
        }
    }
    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
    
    public static boolean hasSummerBeachEquipped(LivingEntity livingEntity) {
        return CuriosApi.getCuriosInventory(livingEntity)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof SummerBeach) {
                            return true;
                        }
                    }
                }
                return false;
            })
            .orElse(false);
    }
}
