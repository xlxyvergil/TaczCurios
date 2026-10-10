package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.util.AttributeHelper;
import dev.shadowsoffire.apothic_attributes.client.ModifierSource;
import dev.shadowsoffire.apothic_attributes.client.ModifierSource.ItemModifierSource;
import dev.shadowsoffire.apothic_attributes.client.ModifierSourceType;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 客户端专用：向 Apothic Attributes 注册一个自定义 ModifierSourceType，
 * 让通过 {@link AttributeHelper} 动态施加的饰品修饰符，也能在属性面板中显示来源饰品图标。
 *
 * 由于饰品修饰符是动态加在实体属性实例上的，Apothic 默认的装备/饰品来源无法识别其修饰符 ID，
 * 因此这里按实体属性实例上的修饰符 ID → AttributeHelper 登记的来源饰品反查，
 * 再映射回实体当前佩戴的同款物品栈进行图标渲染。
 */
public final class ApothicCurioModifierSource {

    private ApothicCurioModifierSource() {
    }

    /** 帧内缓存条目：修饰符 → 来源饰品。 */
    private record CachedEntry(AttributeModifier modifier, ModifierSource<?> source) {
    }

    // 面板可能在一帧内多次调用 extract；这里按（实体 + 饰品修饰符版本）缓存抽取结果。
    // 只要有饰品增删修饰符，AttributeHelper 的版本号就会自增，从而自动失效重建；
    // 无变更时（面板持续打开、属性不变）不再每 tick 重新遍历全部属性与槽位。
    private static LivingEntity cachedEntity;
    private static int cachedModifierVersion = Integer.MIN_VALUE;
    private static List<CachedEntry> cachedEntries = List.of();

    static {
        ModifierSourceType.register(new ModifierSourceType<>() {

            @Override
            public void extract(LivingEntity entity, BiConsumer<AttributeModifier, ModifierSource<?>> map) {
                // 缓存键：实体 + 全局修饰符变更版本；任一变化即重建。
                int version = AttributeHelper.getModifierVersion();
                if (entity != cachedEntity || version != cachedModifierVersion) {
                    cachedEntity = entity;
                    cachedModifierVersion = version;
                    cachedEntries = computeEntries(entity);
                }
                for (CachedEntry entry : cachedEntries) {
                    map.accept(entry.modifier(), entry.source());
                }
            }

            @Override
            public int getPriority() {
                return 25;
            }
        });
    }

    /**
     * 抽取实体当前所有由饰品动态施加的修饰符及其来源饰品。
     */
    private static List<CachedEntry> computeEntries(LivingEntity entity) {
        List<CachedEntry> entries = new ArrayList<>();
        // 收集实体当前佩戴的所有饰品（按物品类型映射实际栈，保留 NBT）。
        Map<Item, ItemStack> wornStacks = collectWornStacks(entity);

        // 遍历实体拥有的全部属性实例，按修饰符 ID 反查来源饰品。
        for (Holder.Reference<Attribute> attribute : BuiltInRegistries.ATTRIBUTE.holders().toList()) {
            AttributeInstance instance = entity.getAttributes().getInstance(attribute);
            if (instance == null) {
                continue;
            }
            for (AttributeModifier modifier : instance.getModifiers()) {
                Item sourceItem = AttributeHelper.getSourceItem(modifier.id());
                if (sourceItem == null) {
                    continue;
                }
                ItemStack worn = wornStacks.get(sourceItem);
                if (worn == null || worn.isEmpty()) {
                    continue;
                }
                entries.add(new CachedEntry(modifier, new ItemModifierSource(worn)));
            }
        }
        return entries;
    }

    /**
     * 收集实体当前所有饰品槽位中的物品，按物品类型映射到实际 ItemStack（携带 NBT）。
     */
    private static Map<Item, ItemStack> collectWornStacks(LivingEntity entity) {
        Map<Item, ItemStack> result = new HashMap<>();
        CuriosApi.getCuriosInventory(entity).ifPresent(handler -> {
            for (ICurioStacksHandler stacksHandler : handler.getCurios().values()) {
                IDynamicStackHandler stacks = stacksHandler.getStacks();
                for (int i = 0; i < stacksHandler.getSlots(); i++) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if (!stack.isEmpty() && !result.containsKey(stack.getItem())) {
                        // 面板只读取图标与 NBT，无需复制栈；复制在面板逐帧渲染下会产生大量临时对象。
                        result.put(stack.getItem(), stack);
                    }
                }
            }
        });
        return result;
    }
}
