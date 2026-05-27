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
 * 梵天百兽 - 提供40点虚数抗性，增强天火饰品效果
 */
public class BrahmaBeasts extends BaseCurioItem {
    
    // 虚数抗性修饰符的UUID（确保唯一性）
    private static final UUID IMAGINARY_RESISTANCE_MODIFIER_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");
    
    // NBT 标签键
    private static final String KILL_COUNT_TAG = "EnderDragonKillCount";
    private static final int MAX_BONUS = 20;  // 抗性封顶
    private static final int MAX_KILLS = 30;  // 计数上限（进化用）
    
    public BrahmaBeasts(Properties properties) {
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
        // 基础40点虚数抗性 + 击杀末影龙获得的额外抗性（封顶20）
        int killCount = getEnderDragonKillCount(livingEntity);
        double bonusResistance = Math.min(killCount, MAX_BONUS);
        double totalResistance = 40.0 + bonusResistance;
        
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
            totalResistance, IMAGINARY_RESISTANCE_MODIFIER_UUID, "tcc_brahma_beasts_resistance", AttributeModifier.Operation.ADDITION);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), 
            IMAGINARY_RESISTANCE_MODIFIER_UUID);
    }
    
    /**
     * 获取该玩家装备的梵天百兽的末影龙击杀数
     */
    public static int getEnderDragonKillCount(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return 0;
        }
        
        return CuriosApi.getCuriosInventory(player)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof BrahmaBeasts) {
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
     * 增加梵天百兽的末影龙击杀数
     */
    public static void incrementEnderDragonKillCount(Player player) {
        CuriosApi.getCuriosInventory(player)
            .ifPresent(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_3rd");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof BrahmaBeasts) {
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
        // 检查是否绑定（继承自夏日沙滩）
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
        tooltip.add(Component.translatable("item.tcc.brahma_beasts.effect", "+40")
            .withStyle(ChatFormatting.AQUA));
        
        // 添加天火饰品增强效果
        double multiplier = com.xlxyvergil.tcc.config.TaczCuriosConfig.COMMON.brahmaBeastsHeavenFireMultiplier.get();
        tooltip.add(Component.translatable("item.tcc.brahma_beasts.heaven_fire_boost", String.format("%.0f", multiplier * 100))
            .withStyle(ChatFormatting.GOLD));
        
        // 获取 NBT 标签
        CompoundTag tag = stack.getTag();
        
        // 获取进化目标实体显示名
        String entityNamespace = TaczCuriosConfig.COMMON.brahmaBeastsEvolutionEntity.get();
        String entityName = entityNamespace;
        try {
            ResourceLocation rl = new ResourceLocation(entityNamespace);
            var entityType = BuiltInRegistries.ENTITY_TYPE.get(rl);
            entityName = entityType.getDescription().getString();
        } catch (Exception ignored) {}
        
        // 添加击杀进度
        int killCount = tag != null ? tag.getInt(KILL_COUNT_TAG) : 0;
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.brahma_beasts.kill_progress", killCount, MAX_KILLS, entityName)
            .withStyle(ChatFormatting.GREEN));
        
        // 检查是否绑定
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("item.tcc.brahma_beasts.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot.3rd"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.legendary"));
        
        // 添加获取方式
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.brahma_beasts.how_to_obtain", entityName)
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
    
    /**
     * 检查实体是否装备了梵天百兽
     */
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
