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
 * 黄蜂蜇刺 - 提升手枪伤害
 * 效果：手枪伤害加成（加算
 */
public class WaspStinger extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰
    private static final UUID DAMAGE_UUID = UUID.fromString("e1d2fcde-7ee0-4607-ade2-5b24292f8a52");
    
    // 修饰符名
    private static final String DAMAGE_NAME = "tcc.wasp_stinger.pistol_damage";
    
    public WaspStinger(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给实体添加伤害属性修改
        applyWaspStingerEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除实体的伤害属性修改
        removeWaspStingerEffects((LivingEntity) slotContext.entity());
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
    private void applyWaspStingerEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 获取配置中的手枪伤害加成
        double damageBoost = TaczCuriosConfig.COMMON.waspStingerDamageBoost.get();
        
        // 手枪伤害属
        var pistolDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_pistol")
            )
        );
        
        if (pistolDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移
            pistolDamageAttribute.removeModifier(DAMAGE_UUID);
            
            // 添加配置中的手枪伤害加成（加算）
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
    private void removeWaspStingerEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 手枪伤害属
        var pistolDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_pistol")
            )
        );
        
        if (pistolDamageAttribute != null) {
            // 移除修饰
            pistolDamageAttribute.removeModifier(DAMAGE_UUID);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        applyWaspStingerEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.wasp_stinger.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double damageBoost = TaczCuriosConfig.COMMON.waspStingerDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.wasp_stinger.effect", String.format("%.0f", damageBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }
    
    /**
     * 当实体切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyWaspStingerEffects(livingEntity);
    }
}


