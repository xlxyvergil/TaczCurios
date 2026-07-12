package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;


/**
 * 合金钻头 - 提升穿透能力
 * 效果：穿透能力加成
 */
public class AlloyDrill extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID ARMOR_IGNORE_UUID = UUID.fromString("06d45b6d-c8d2-4372-bdfd-b427651a2366");
    
    // 修饰符名称
    private static final String ARMOR_IGNORE_NAME = "tcc.alloy_drill.armor_ignore";
    
    public AlloyDrill(Properties properties) {
        super(properties);
    }
    

    
    /**
     * 应用钻头效果
     * 提升护甲忽略能力
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double armorIgnoreBoost = TaczCuriosConfig.COMMON.alloyDrillArmorPenetrationBoost.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.ARMOR_IGNORE, armorIgnoreBoost, ARMOR_IGNORE_UUID, ARMOR_IGNORE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    
    /**
     * 移除钻头效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.ARMOR_IGNORE, ARMOR_IGNORE_UUID);
    }
    
    /**
     * 当饰品在插槽中时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 属性修饰符是持久的，不需要每tick刷新
        // 效果在 onEquip/onUnequip/applyGunSwitchEffect 中管理
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double armorIgnoreBoost = TaczCuriosConfig.COMMON.alloyDrillArmorPenetrationBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.alloy_drill.effect", String.format("%+.0f", armorIgnoreBoost))
            .withStyle(ChatFormatting.GOLD));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}