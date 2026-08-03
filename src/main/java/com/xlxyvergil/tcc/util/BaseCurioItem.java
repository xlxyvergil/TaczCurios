package com.xlxyvergil.tcc.util;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.items.ItemBaseCurio;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 饰品基类 - 提供通用的饰品行为
 */
public abstract class BaseCurioItem extends ItemBaseCurio {
    
    // 互斥映射表：物品注册名 -> 互斥的物品注册名集合
    private static final Map<String, Set<String>> CONFLICT_MAP = new HashMap<>();

    // 融合等级和稀有度现在由 applyEffects 的 stack 参数直接读取，不再使用 ThreadLocal

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
        applyEffects(entity, stack);
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

    public final void refreshEffects(LivingEntity entity, ItemStack stack) {
        removeEffects(entity);
        applyEffects(entity, stack);
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
    }

    /**
     * 应用效果（子类实现）
     */
    protected abstract void applyEffects(LivingEntity entity, ItemStack stack);
    
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

    /**
     * 检查当前实体是否满足该饰品的武器类型限制。
     * <p>
     * 返回 {@code true} 表示当前持有的武器符合限制，效果应生效；<br>
     * 返回 {@code false} 表示不符合限制，效果不应生效。
     *
     * @param entity 要检查的生物实体
     * @return 是否符合武器类型限制
     */
    public boolean matchesRestriction(LivingEntity entity) {
        List<String> restriction = getWeaponTypeRestriction();
        if (restriction == null || restriction.isEmpty()) {
            return true; // 无限制
        }
        if (restriction.size() == 1 && "melee".equals(restriction.get(0))) {
            return GunTypeChecker.isHoldingMeleeWeapon(entity);
        }
        // 全枪械
        if (restriction.equals(GunTypeChecker.ALL_GUN_TYPES_LIST)) {
            return GunTypeChecker.isHoldingAnyGun(entity);
        }
        // 其他枪械类型组合
        return GunTypeChecker.isHoldingConfiguredGunTypes(entity, restriction);
    }

    /**
     * 根据值正负生成属性修饰符 tooltip Component，复用 Apothic Attributes 的翻译键
     */
    protected static MutableComponent formatModifierTooltip(double value, String valueFormat, Component attrName) {
        String formatted = String.format(valueFormat, value >= 0 ? value : -value);
        String key = value >= 0 ? "attributeslib.modifier.plus" : "attributeslib.modifier.take";
        return Component.translatable(key, formatted, attrName);
    }

    /**
     * 返回该饰品的武器类型限制。
     * <p>
     * 返回 {@code null} 表示无限制，不在工具提示中显示限制信息。<br>
     * 返回单元素列表 {@code ["melee"]} 表示近战限制，显示 "限定：近战武器"。<br>
     * 返回枪械类型列表（如 {@code ["pistol"]}、{@code ["rifle", "sniper"]}）
     * 显示 "限定枪械：xxx".
     *
     * @return 武器类型限制列表，{@code null} 表示无限制
     */
    @Nullable
    public List<String> getWeaponTypeRestriction() {
        return null;
    }
}
