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
 * 夏日沙滩 - 提供20点虚数抗性
 */
public class SummerBeach extends BaseCurioItem {
    
    // 虚数抗性修饰符的UUID（确保唯一性）
    private static final UUID IMAGINARY_RESISTANCE_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    
    // NBT 标签键
    private static final String KILL_COUNT_TAG = "WitherKillCount";
    private static final int MAX_BONUS = 20;  // 抗性加成封顶
    private static final int MAX_KILLS = 30;  // 进化所需击杀数
    
    public SummerBeach(Properties properties) {
        super(properties.stacksTo(1).fireResistant());
    }
    
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;  // 不可附魔
    }
    
    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 0;
    }
    
    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return false;  // 无合成残留
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 基础20点虚数抗性 + 击杀凋零获得的额外抗性
        int killCount = getWitherKillCount(livingEntity);
        double bonusResistance = Math.min(killCount, MAX_BONUS);
        double totalResistance = 20.0 + bonusResistance;
        
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
            totalResistance, IMAGINARY_RESISTANCE_MODIFIER_UUID, "tcc_summer_beach_resistance", AttributeModifier.Operation.ADDITION);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
            IMAGINARY_RESISTANCE_MODIFIER_UUID);
    }
    
    /**
     * 获取该玩家装备的夏日沙滩的凋零击杀数
     */
    public static int getWitherKillCount(LivingEntity entity) {
        if (!(entity instanceof net.minecraft.world.entity.player.Player player)) {
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
                                return tag.getInt(KILL_COUNT_TAG);
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
     * 增加夏日沙滩的凋零击杀数
     */
    public static void incrementWitherKillCount(net.minecraft.world.entity.player.Player player) {
        CuriosApi.getCuriosInventory(player)
            .ifPresent(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof SummerBeach) {
                            CompoundTag tag = stack.getOrCreateTag();
                            int currentCount = tag.getInt(KILL_COUNT_TAG);
                            if (currentCount < MAX_KILLS) {
                                tag.putInt(KILL_COUNT_TAG, currentCount + 1);
                            }
                            break;
                        }
                    }
                }
            });
    }
    
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否绑定
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            // 只有绑定的玩家才能装备
            String boundPlayerUUID = tag.getString("BoundPlayer");
            if (slotContext.entity() instanceof Player player) {
                return player.getStringUUID().equals(boundPlayerUUID);
            }
            return false;
        }
        return super.canEquip(slotContext, stack);
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.summer_beach.effect", "+20")
            .withStyle(ChatFormatting.AQUA));
        
        // 添加天火饰品增强效果
        double multiplier = com.xlxyvergil.tcc.config.TaczCuriosConfig.COMMON.summerBeachHeavenFireMultiplier.get();
        tooltip.add(Component.translatable("item.tcc.summer_beach.heaven_fire_boost", String.format("%.0f", multiplier * 100))
            .withStyle(ChatFormatting.GOLD));
        
        // 获取 NBT 标签
        CompoundTag tag = stack.getTag();
        
        // 获取进化目标实体显示名
        String entityNamespace = TaczCuriosConfig.COMMON.summerBeachEvolutionEntity.get();
        String entityName = entityNamespace;
        try {
            ResourceLocation rl = new ResourceLocation(entityNamespace);
            var entityType = BuiltInRegistries.ENTITY_TYPE.get(rl);
            entityName = entityType.getDescription().getString();
        } catch (Exception ignored) {}
        
        // 添加击杀进度
        int killCount = tag != null ? tag.getInt(KILL_COUNT_TAG) : 0;
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.summer_beach.kill_progress", killCount, MAX_KILLS, entityName)
            .withStyle(ChatFormatting.GREEN));
        
        // 检查是否绑定
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.summer_beach.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot.3rd"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
        
        // 添加获取方式
        String obtainEntityNamespace = TaczCuriosConfig.COMMON.summerBeachObtainEntity.get();
        String obtainEntityName = obtainEntityNamespace;
        try {
            ResourceLocation rl = new ResourceLocation(obtainEntityNamespace);
            var entityType = BuiltInRegistries.ENTITY_TYPE.get(rl);
            obtainEntityName = entityType.getDescription().getString();
        } catch (Exception ignored) {}
        String brahmaBeastsEntityNamespace = TaczCuriosConfig.COMMON.brahmaBeastsEvolutionEntity.get();
        String brahmaBeastsEntityName = brahmaBeastsEntityNamespace;
        try {
            ResourceLocation rl = new ResourceLocation(brahmaBeastsEntityNamespace);
            var entityType = BuiltInRegistries.ENTITY_TYPE.get(rl);
            brahmaBeastsEntityName = entityType.getDescription().getString();
        } catch (Exception ignored) {}
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.summer_beach.how_to_obtain", obtainEntityName, brahmaBeastsEntityName)
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
    
    /**
     * 检查实体是否装备了夏日沙滩
     */
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
