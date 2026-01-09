package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 剑风 - 提升1.1近战距离
 * 效果：提.1近战距离（加算）
 */
public class SwordWind extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰
    private static final UUID MELEE_DISTANCE_UUID = UUID.fromString("3f7ed736-62d3-4835-bc94-2834d4b91832");
    
    // 修饰符名
    private static final String MELEE_DISTANCE_NAME = "tcc.sword_wind.melee_distance";
    
    // 效果参数
    // private static final double MELEE_DISTANCE_BOOST = 1.1; // 1.1加成 - 现在从配置文件读
    
    public SwordWind(Properties properties) {
        super(properties);
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * SwordWind与SwordWindPrime互斥，不能同时装
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查是否已经装备了SwordWindPrime
        return !top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem() instanceof SwordWindPrime))
            .orElse(java.util.Optional.empty()).isPresent();
    }
    
    /**
     * 当饰品被装备时调
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        applySwordWindEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        removeSwordWindEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 应用剑风效果
     * 给实体添.1的近战距离加成（加算     */
    private void applySwordWindEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 近战距离属
        var meleeDistanceAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "melee_distance")
            )
        );
        
        if (meleeDistanceAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移
            meleeDistanceAttribute.removeModifier(MELEE_DISTANCE_UUID);
            
            // 从配置文件获取近战距离加成
            double meleeDistanceBoost = TaczCuriosConfig.COMMON.swordWindMeleeRangeBoost.get();
            
            // 添加配置的近战距离加成（加算
            var meleeDistanceModifier = new AttributeModifier(
                MELEE_DISTANCE_UUID,
                MELEE_DISTANCE_NAME,
                meleeDistanceBoost,
                AttributeModifier.Operation.ADDITION
            );
            meleeDistanceAttribute.addPermanentModifier(meleeDistanceModifier);
        }
    }
    
    /**
     * 移除剑风效果
     */
    private void removeSwordWindEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 近战距离属
        var meleeDistanceAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "melee_distance")
            )
        );
        
        if (meleeDistanceAttribute != null) {
            meleeDistanceAttribute.removeModifier(MELEE_DISTANCE_UUID);
        }
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.sword_wind.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double meleeDistanceBoost = TaczCuriosConfig.COMMON.swordWindMeleeRangeBoost.get();
        tooltip.add(Component.translatable("item.tcc.sword_wind.effect", String.format("%.1f", meleeDistanceBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.common"));
    }
}

