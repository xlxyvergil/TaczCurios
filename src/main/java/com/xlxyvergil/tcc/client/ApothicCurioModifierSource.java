package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.util.AttributeHelper;
import dev.shadowsoffire.attributeslib.client.ModifierSource;
import dev.shadowsoffire.attributeslib.client.ModifierSource.ItemModifierSource;
import dev.shadowsoffire.attributeslib.client.ModifierSourceType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * 客户端专用：向 Apothic Attributes 注册一个自定义 ModifierSourceType，
 * 让通过 {@link AttributeHelper} 动态施加的饰品修饰符，也能在属性面板中显示来源饰品图标。
 *
 * <p>由于饰品修饰符是动态加在实体属性实例上的，Apothic 默认的装备/饰品来源无法识别其 UUID，
 * 因此这里按「实体属性实例上的修饰符 UUID → AttributeHelper 登记的来源饰品」反查，
 * 再映射回实体当前佩戴的同款物品栈进行图标渲染。</p>
 */
public final class ApothicCurioModifierSource {

    private ApothicCurioModifierSource() {
    }

    static {
        ModifierSourceType.register(new ModifierSourceType<>() {

            @Override
            public void extract(LivingEntity entity, BiConsumer<AttributeModifier, ModifierSource<?>> map) {
                // 收集实体当前佩戴的所有饰品（按物品类型映射实际栈，保留 NBT）。
                Map<Item, ItemStack> wornStacks = collectWornStacks(entity);

                // 遍历实体拥有的全部属性实例，按修饰符 UUID 反查来源饰品。
                for (Attribute attribute : ForgeRegistries.ATTRIBUTES.getValues()) {
                    AttributeInstance instance = entity.getAttributes().getInstance(attribute);
                    if (instance == null) {
                        continue;
                    }
                    for (AttributeModifier.Operation op : AttributeModifier.Operation.values()) {
                        for (AttributeModifier modifier : instance.getModifiers(op)) {
                            Item sourceItem = AttributeHelper.getSourceItem(modifier.getId());
                            if (sourceItem == null) {
                                continue;
                            }
                            ItemStack worn = wornStacks.get(sourceItem);
                            if (worn == null || worn.isEmpty()) {
                                continue;
                            }
                            map.accept(modifier, new ItemModifierSource(worn));
                        }
                    }
                }
            }

            @Override
            public int getPriority() {
                return 25;
            }
        });
    }

    /**
     * 收集实体当前所有饰品槽位中的物品，按物品类型映射到实际 ItemStack（携带 NBT）。
     */
    private static Map<Item, ItemStack> collectWornStacks(LivingEntity entity) {
        Map<Item, ItemStack> result = new HashMap<>();
        CuriosApi.getCuriosHelper().getCuriosHandler(entity).ifPresent(handler -> {
            for (ICurioStacksHandler stacksHandler : handler.getCurios().values()) {
                IDynamicStackHandler stacks = stacksHandler.getStacks();
                for (int i = 0; i < stacksHandler.getSlots(); i++) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if (!stack.isEmpty() && !result.containsKey(stack.getItem())) {
                        result.put(stack.getItem(), stack.copy());
                    }
                }
            }
        });
        return result;
    }
}
