package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 剑风Prime - 提升3近战距离
 * 效果：提升3近战距离（加算）
 */
public class SwordWindPrime extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰符
    private static final UUID MELEE_DISTANCE_UUID = UUID.fromString("3a32b0f7-9ef5-4c63-bb08-610762942881");
    
    // 修饰符名称
    private static final String MELEE_DISTANCE_NAME = "tcc.sword_wind_prime.melee_distance";
    
 
    
    public SwordWindPrime(Properties properties) {
        super(properties);
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * SwordWindPrime与SwordWind互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查是否已经装备了SwordWind
        return !top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem() instanceof SwordWind))
            .orElse(java.util.Optional.empty()).isPresent();
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            applySwordWindPrimeEffects(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            removeSwordWindPrimeEffects(player);
        }
    }
    
    /**
     * 应用剑风Prime效果
     * 给玩家添加3的近战距离加成（加算）
     */
    private void applySwordWindPrimeEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 近战距离属性
        var meleeDistanceAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "melee_distance")
            )
        );
        
        if (meleeDistanceAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            meleeDistanceAttribute.removeModifier(MELEE_DISTANCE_UUID);
            
            // 从配置文件获取近战距离加成值
            double meleeDistanceBoost = TaczCuriosConfig.COMMON.swordWindPrimeMeleeRangeBoost.get();
            
            // 添加配置的近战距离加成（加算）
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
     * 移除剑风Prime效果
     */
    private void removeSwordWindPrimeEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 近战距离属性
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
        tooltip.add(Component.translatable("item.tcc.sword_wind_prime.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double meleeDistanceBoost = TaczCuriosConfig.COMMON.swordWindPrimeMeleeRangeBoost.get();
        tooltip.add(Component.translatable("item.tcc.sword_wind_prime.effect", String.format("%.0f", meleeDistanceBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.legendary"));
    }
}