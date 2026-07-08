package com.xlxyvergil.tcc.util;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.items.ItemBaseCurio;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.*;

/**
 * 饰品基类 - 提供通用的饰品行为
 */
public abstract class BaseCurioItem extends ItemBaseCurio {
    
    // 互斥映射表：物品注册名 -> 互斥的物品注册名集合
    private static final Map<String, Set<String>> CONFLICT_MAP = new HashMap<>();
    
    static {
        loadConflictsFromConfig();
    }
    
    /**
     * 从配置文件加载互斥关系
     */
    private static void loadConflictsFromConfig() {
        List<? extends String> conflictGroups = TaczCuriosConfig.COMMON.curioConflicts.get();
        
        for (String group : conflictGroups) {
            // 解析逗号分隔的物品注册名
            String[] items = group.split(",");
            Set<String> groupSet = new HashSet<>();
            for (String item : items) {
                groupSet.add(item.trim());
            }
            
            // 为组内每个物品添加互斥关系(包含自身)
            for (String itemName : groupSet) {
                Set<String> conflicts = CONFLICT_MAP.computeIfAbsent(itemName, k -> new HashSet<>());
                conflicts.addAll(groupSet);
                conflicts.add(itemName); // 确保包含自身
            }
        }
    }
    
    public BaseCurioItem(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        LivingEntity entity = slotContext.entity();
        ensureCapCounters(stack);
        applyEffects(entity);
        // 更新TACZ枪械属性缓存，让属性变化立即生效（支持玩家、女仆等所有LivingEntity）
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        LivingEntity entity = slotContext.entity();
        removeEffects(entity);
        // 更新TACZ枪械属性缓存，让属性变化立即生效（支持玩家、女仆等所有LivingEntity）
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
    }
    
    /**
     * 检查是否可以装备到指定插槽
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (!super.canEquip(slotContext, stack)) {
            return false;
        }
        
        String slotId = slotContext.identifier();
        LivingEntity entity = (LivingEntity) slotContext.entity();
        String currentRegName = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
        Set<String> conflictNames = CONFLICT_MAP.getOrDefault(currentRegName, new HashSet<>());
        
        if (!conflictNames.isEmpty()) {
            LazyOptional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(entity);
            Boolean hasConflict = curiosInventory.map(inv -> {
                var handlerOpt = inv.getStacksHandler(slotId);
                if (handlerOpt.isPresent()) {
                    var handler = handlerOpt.orElse(null);
                    for (int i = 0; i < handler.getSlots(); i++) {
                        if (i == slotContext.index()) continue;
                        ItemStack slotStack = handler.getStacks().getStackInSlot(i);
                        if (!slotStack.isEmpty()) {
                            String slotRegName = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(slotStack.getItem()).toString();
                            if (conflictNames.contains(slotRegName)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }).orElse(false);
            
            if (hasConflict) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 当物品在Curios插槽中时被右键点击
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    public final void refreshEffects(LivingEntity entity) {
        removeEffects(entity);
        applyEffects(entity);
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
    }
    
    /**
     * 应用效果（子类实现）
     */
    protected abstract void applyEffects(LivingEntity entity);
    
    /**
     * 移除效果（子类实现）
     */
    protected abstract void removeEffects(LivingEntity entity);

    private static void ensureCapCounters(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        ResourceLocation key = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key == null) {
            return;
        }
        String itemId = key.toString();
        CompoundTag tag = stack.getOrCreateTag();
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.ATTRIBUTE, itemId)) {
            EvolutionRegistry.Progress progress = rule.progress;
            if (progress == null || progress.capCounterKey == null || progress.capCounterKey.isBlank()) {
                continue;
            }
            if (!tag.contains(progress.capCounterKey)) {
                tag.putDouble(progress.capCounterKey, 0.0);
            }
        }
    }
}
