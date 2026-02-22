package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
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
 * 预言契约 - 提升手枪90%伤害
 * 效果：手枪伤90%（加算）
 */
public class ProphecyPact extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰
    private static final UUID DAMAGE_UUID = UUID.fromString("6edbaedf-2502-4fe0-8e2c-9054d6a9ecc1");
    
    // 修饰符名
    private static final String DAMAGE_NAME = "tcc.prophecy_pact.pistol_damage";
    
    // 效果参数
    // private static final double DAMAGE_BOOST = 0.90;       // 90%手枪伤害提升（加算） - 现在从配置文件读
    
    public ProphecyPact(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给生物添加伤害属性修改
        applyProphecyPactEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除生物的伤害属性修改
        removeProphecyPactEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 检查是否可以装备到指定插槽
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        return slotContext.identifier().equals("tcc_slot");
    }
    
    /**
     * 当物品在Curios插槽中时被右键点
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }
    
    /**
     * 应用效果
     * 提升手枪伤害（加算）
     */
    private void applyProphecyPactEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 手枪伤害属性
        var pistolDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_pistol")
            )
        );
        
        if (pistolDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移
            pistolDamageAttribute.removeModifier(DAMAGE_UUID);
            
            // 从配置文件获取手枪伤害加成
            double damageBoost = TaczCuriosConfig.COMMON.prophecyPactDamageBoost.get();
            
            // 添加配置的手枪伤害加成（加算）
            var damageModifier = new AttributeModifier(
                DAMAGE_UUID,
                DAMAGE_NAME,
                damageBoost,
                AttributeModifier.Operation.ADDITION
            );
            pistolDamageAttribute.addPermanentModifier(damageModifier);
        }
    }
    
    /**
     * 移除效果
     */
    private void removeProphecyPactEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 手枪伤害属性
        var pistolDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_pistol")
            )
        );
        
        if (pistolDamageAttribute != null) {
            // 移除修饰符
            pistolDamageAttribute.removeModifier(DAMAGE_UUID);
        }
    }
    
    /**
     * 当生物持有时，每tick更新效果
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
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.prophecy_pact.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double damageBoost = TaczCuriosConfig.COMMON.prophecyPactDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.prophecy_pact.effect", String.format("%.0f", damageBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.common"));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyProphecyPactEffects(livingEntity);
    }
}
