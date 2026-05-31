package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.event.SummerBeachDropEvent;
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
 * 梵天百兽 - 提供基础40点虚数抗性，击杀配置列表中的实体获得额外抗性
 */
public class BrahmaBeasts extends BaseCurioItem {
    
    private static final UUID IMAGINARY_RESISTANCE_MODIFIER_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");
    
    private static final String KILL_COUNTS_TAG = "KillCounts";
    private static final String CARRIED_RESISTANCE_TAG = "CarriedResistance";
    // 该饰品可从击杀中获得的最大虚数抗性（上限20点）
    public static final int MAX_KILL_RESISTANCE = 20;
    
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
        int maxKillResistance = TaczCuriosConfig.COMMON.brahmaBeastsMaxKillResistance.get();
        int resistanceFromKills = Math.min(getResistanceFromKills(livingEntity), maxKillResistance);
        int carriedResistance = getCarriedResistance(livingEntity);
        double totalResistance = carriedResistance + resistanceFromKills;
        
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
            totalResistance, IMAGINARY_RESISTANCE_MODIFIER_UUID, "tcc_brahma_beasts_resistance", AttributeModifier.Operation.ADDITION);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
            IMAGINARY_RESISTANCE_MODIFIER_UUID);
    }
    
    public static int getTotalKills(LivingEntity entity) {
        if (!(entity instanceof Player player)) return 0;
        
        return CuriosApi.getCuriosInventory(player)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof BrahmaBeasts) {
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
    
    public static void incrementEntityKillCount(Player player, String entityKey, int maxCount) {
        CuriosApi.getCuriosInventory(player)
            .ifPresent(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof BrahmaBeasts bb) {
                            CompoundTag tag = stack.getOrCreateTag();
                            CompoundTag killCounts = tag.getCompound(KILL_COUNTS_TAG);
                            int currentCount = killCounts.getInt(entityKey);
                            if (currentCount < maxCount) {
                                killCounts.putInt(entityKey, currentCount + 1);
                                tag.put(KILL_COUNTS_TAG, killCounts);
                                // 立即刷新虚数抗性属性
                                bb.removeEffects(player);
                                bb.applyEffects(player);
                            }
                            break;
                        }
                    }
                }
            });
    }
    
    public static boolean areAllRequirementsMet(LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;
        
        List<? extends List<String>> requirements = TaczCuriosConfig.COMMON.brahmaBeastsEvolutionRequirements.get();
        if (requirements.isEmpty()) return false;
        
        return CuriosApi.getCuriosInventory(player)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof BrahmaBeasts) {
                            CompoundTag tag = stack.getTag();
                            if (tag == null) return false;
                            CompoundTag killCounts = tag.getCompound(KILL_COUNTS_TAG);
                            for (List<String> req : requirements) {
                                String reqEntity = req.get(0);
                                String nbtFilter = req.size() > 2 ? req.get(2) : null;
                                String matchKey = SummerBeachDropEvent.getMatchKey(reqEntity, nbtFilter);
                                int required = Integer.parseInt(req.get(1));
                                if (killCounts.getInt(matchKey) < required) {
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
        
        CompoundTag tag = stack.getTag();
        
        // 显示当前抗性构成
        int maxKillResistance = TaczCuriosConfig.COMMON.brahmaBeastsMaxKillResistance.get();
        if (tag != null) {
            int carried = tag.getInt(CARRIED_RESISTANCE_TAG);
            int fromKills = 0;
            List<? extends List<String>> resistList = TaczCuriosConfig.COMMON.brahmaBeastsResistanceEntities.get();
            CompoundTag killCounts = tag.getCompound(KILL_COUNTS_TAG);
            for (List<String> entry : resistList) {
                String entityKey = entry.get(0);
                String nbtFilter = entry.size() > 2 ? entry.get(2) : null;
                String matchKey = SummerBeachDropEvent.getMatchKey(entityKey, nbtFilter);
                int kills = killCounts.getInt(matchKey);
                int perKill = Integer.parseInt(entry.get(1));
                fromKills += kills * perKill;
            }
            fromKills = Math.min(fromKills, maxKillResistance);
            int total = carried + fromKills;
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.brahma_beasts.effect", String.valueOf(total))
                .withStyle(ChatFormatting.AQUA));
        } else {
            tooltip.add(Component.translatable("item.tcc.brahma_beasts.effect", "0~" + TaczCuriosConfig.COMMON.brahmaBeastsMaxKillResistance.get())
                .withStyle(ChatFormatting.AQUA));
        }
        
        // EL 第四诅咒削弱（仅加载神秘遗物时显示）
        if (net.minecraftforge.fml.ModList.get().isLoaded("enigmaticlegacy")) {
            double curseReduction = TaczCuriosConfig.COMMON.brahmaBeastsELCurseReduction.get();
            tooltip.add(Component.translatable("item.tcc.brahma_beasts.el_curse_reduction", String.format("%.0f", curseReduction * 100))
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        
        List<? extends List<String>> requirements = TaczCuriosConfig.COMMON.brahmaBeastsEvolutionRequirements.get();
        if (!requirements.isEmpty()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.brahma_beasts.kill_progress_title")
                .withStyle(ChatFormatting.GREEN));
            CompoundTag killCounts = tag != null ? tag.getCompound(KILL_COUNTS_TAG) : null;
            for (List<String> req : requirements) {
                String reqEntity = req.get(0);
                String nbtFilter = req.size() > 2 ? req.get(2) : null;
                String matchKey = SummerBeachDropEvent.getMatchKey(reqEntity, nbtFilter);
                int required = Integer.parseInt(req.get(1));
                int current = killCounts != null ? killCounts.getInt(matchKey) : 0;
                String entityDisplay = getEntityDisplayName(matchKey);
                tooltip.add(Component.translatable("item.tcc.brahma_beasts.evolution_progress", entityDisplay, current, required)
                    .withStyle(current >= required ? ChatFormatting.GREEN : ChatFormatting.GRAY));
            }
        }
        
        // 显示抵抗实体列表
        List<? extends List<String>> resistList = TaczCuriosConfig.COMMON.brahmaBeastsResistanceEntities.get();
        if (!resistList.isEmpty()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.brahma_beasts.resist_source_title", String.valueOf(maxKillResistance))
                .withStyle(ChatFormatting.AQUA));
            CompoundTag killCounts = tag != null ? tag.getCompound(KILL_COUNTS_TAG) : null;
            for (List<String> entry : resistList) {
                String entityKey = entry.get(0);
                String nbtFilter = entry.size() > 2 ? entry.get(2) : null;
                String matchKey = SummerBeachDropEvent.getMatchKey(entityKey, nbtFilter);
                int resistancePerKill = Integer.parseInt(entry.get(1));
                int kills = killCounts != null ? killCounts.getInt(matchKey) : 0;
                String entityDisplay = getEntityDisplayName(matchKey);
                tooltip.add(Component.translatable("item.tcc.brahma_beasts.resist_detail", entityDisplay, kills * resistancePerKill)
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
    
    private static String getEntityDisplayName(String namespace) {
        // 处理复合键如 "minecraft:wither[apoth.boss=true]"
        String baseId = SummerBeachDropEvent.getBaseEntityId(namespace);
        String nbtSuffix = "";
        if (!baseId.equals(namespace)) {
            nbtSuffix = " " + namespace.substring(baseId.length());
        }
        try {
            ResourceLocation rl = new ResourceLocation(baseId);
            var entityType = BuiltInRegistries.ENTITY_TYPE.get(rl);
            return entityType.getDescription().getString() + nbtSuffix;
        } catch (Exception ignored) {
            return namespace;
        }
    }
    
    /**
     * 按抵抗实体列表加权计算击杀带来的虚数抗性
     */
    public static int getResistanceFromKills(LivingEntity entity) {
        if (!(entity instanceof Player player)) return 0;
        List<? extends List<String>> resistanceEntities = TaczCuriosConfig.COMMON.brahmaBeastsResistanceEntities.get();
        
        return CuriosApi.getCuriosInventory(player)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof BrahmaBeasts) {
                            CompoundTag tag = stack.getTag();
                            if (tag != null) {
                                CompoundTag killCounts = tag.getCompound(KILL_COUNTS_TAG);
                                int total = 0;
                                for (List<String> entry : resistanceEntities) {
                                    String entityId = entry.get(0);
                                    String nbtFilter = entry.size() > 2 ? entry.get(2) : null;
                                    String matchKey = SummerBeachDropEvent.getMatchKey(entityId, nbtFilter);
                                    int kills = killCounts.getInt(matchKey);
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
                        if (stack.getItem() instanceof BrahmaBeasts) {
                            CompoundTag tag = stack.getTag();
                            return tag != null ? tag.getInt(CARRIED_RESISTANCE_TAG) : 0;
                        }
                    }
                }
                return 0;
            })
            .orElse(0);
    }
    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
    
    public static boolean hasBrahmaBeastsEquipped(LivingEntity livingEntity) {
        return CuriosApi.getCuriosInventory(livingEntity)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof BrahmaBeasts) {
                            return true;
                        }
                    }
                }
                return false;
            })
            .orElse(false);
    }
}
