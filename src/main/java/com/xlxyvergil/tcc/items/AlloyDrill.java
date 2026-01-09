package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
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
public class AlloyDrill extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID ARMOR_IGNORE_UUID = UUID.fromString("06d45b6d-c8d2-4372-bdfd-b427651a2366");
    
    // 修饰符名称
    private static final String ARMOR_IGNORE_NAME = "tcc.alloy_drill.armor_ignore";
    
    public AlloyDrill(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给生物添加护甲忽略属性加成
        if (slotContext.entity() instanceof LivingEntity) {
            applyAlloyDrillEffects((LivingEntity) slotContext.entity());
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除生物的护甲忽略属性加成
        if (slotContext.entity() instanceof LivingEntity) {
            removeAlloyDrillEffects((LivingEntity) slotContext.entity());
        }
    }
    
    /**
     * 当物品在Curios插槽中时被右键点击
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
    
    /**
     * 应用钻头效果
     * 提升护甲忽略能力
     */
    private void applyAlloyDrillEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 获取配置中的护甲穿透加成值
        double armorIgnoreBoost = TaczCuriosConfig.COMMON.alloyDrillArmorPenetrationBoost.get();
        
        // 应用护甲忽略能力
        var armorIgnoreAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "armor_ignore")
            )
        );
        
        if (armorIgnoreAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            armorIgnoreAttribute.removeModifier(ARMOR_IGNORE_UUID);
            
            // 添加配置中的护甲忽略能力加成（乘法）
            var armorIgnoreModifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                ARMOR_IGNORE_UUID,
                ARMOR_IGNORE_NAME,
                armorIgnoreBoost,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_BASE
            );
            armorIgnoreAttribute.addPermanentModifier(armorIgnoreModifier);
        }
    }
    
    /**
     * 移除钻头效果
     */
    private void removeAlloyDrillEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 移除护甲忽略能力加成
        var armorIgnoreAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "armor_ignore")
            )
        );
        
        if (armorIgnoreAttribute != null) {
            armorIgnoreAttribute.removeModifier(ARMOR_IGNORE_UUID);
        }
    }
    
    /**
     * 当饰品在插槽中时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof LivingEntity) {
            applyAlloyDrillEffects((LivingEntity) slotContext.entity());
        }
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.alloy_drill.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double armorIgnoreBoost = TaczCuriosConfig.COMMON.alloyDrillArmorPenetrationBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.alloy_drill.effect", String.format("%.0f", armorIgnoreBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    /**
     * 获取装备槽位
     */
    public String getSlot() {
        return "tcc:tcc_slot";
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyAlloyDrillEffects(livingEntity);
    }
}