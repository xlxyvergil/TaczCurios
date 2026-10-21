package com.xlxyvergil.tcc.event;

import com.google.common.collect.Multimap;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.BaseCurioItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.Map;

 /**
 * 把饰品的属性声明接进 Curios 官方通道。
 *
 * <p>Curios 在计算某件饰品当前该施加的修饰符时会触发 {@link CurioAttributeModifierEvent}：
 * 装备/卸下时在服务端触发（用于施加与移除），工具提示和 Apothic Attributes 的属性面板在客户端触发
 * （用于显示属性行与来源图标）。这里把饰品声明的修饰符补进事件，于是：
 * <ul>
 *   <li>Curios 自己负责装备时的施加与卸下时的移除；</li>
 *   <li>物品工具提示使用 Curios 官方的属性段，不需要各饰品手写；</li>
 *   <li>Apothic Attributes 的 {@code CuriosCompat} 能把修饰符映射回佩戴的饰品栈，显示来源图标。</li>
 * </ul>
 *
 * <p>声明本身由 {@link BaseCurioItem#declaredEffects} 在收集模式下求得，条件判断（手持武器类型等）
 * 仍在各饰品的 {@code declareModifiers} 里求值，客户端与服务端的口径一致。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CurioAttributeDeclarationHandler {

    private CurioAttributeDeclarationHandler() {
    }

    @SubscribeEvent
    public static void onCurioAttributeModifiers(CurioAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof BaseCurioItem item)) {
            return;
        }
        LivingEntity entity = event.getSlotContext().entity();
        if (entity == null) {
            return;
        }
        Multimap<Attribute, AttributeModifier> declared = item.declaredEffects(entity, stack);
        for (Map.Entry<Attribute, AttributeModifier> entry : declared.entries()) {
            event.addModifier(entry.getKey(), entry.getValue());
        }
    }
}
