package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.GunTypeChecker;
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
 * 恶性扩- 提升165%霰弹枪伤害，提高55%不精准度
 * 效果：提65%霰弹枪伤害（加算），提高55%不精准度（加算）
 */
public class MalignantSpread extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识修饰
    private static final UUID DAMAGE_UUID = UUID.fromString("5bfabff0-b8df-48cd-9ecb-95027aafbf69");
    private static final UUID INACCURACY_UUID = UUID.fromString("03755bb2-350f-47ee-821f-db51a2a7f149");
    
    // 修饰符名
    private static final String DAMAGE_NAME = "tcc.malignant_spread.damage";
    private static final String INACCURACY_NAME = "tcc.malignant_spread.inaccuracy";
    
    
    public MalignantSpread(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给实体添加属性修改
        applyMalignantSpreadEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除实体的属性修改
        removeMalignantSpreadEffects((LivingEntity) slotContext.entity());
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
     * 应用恶性扩散效     * 提升霰弹枪伤害（加算）和不精准度（乘算）
     */
    public void applyMalignantSpreadEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 获取霰弹枪伤害属
        var shotgunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_shotgun")
            )
        );
        
        // 移除已存在的修饰
        if (shotgunDamageAttribute != null) {
            shotgunDamageAttribute.removeModifier(DAMAGE_UUID);
        }
        
        // 从配置文件获取霰弹枪伤害加成值和不精准度
        double damageBoost = TaczCuriosConfig.COMMON.malignantSpreadDamageBoost.get();
        double inaccuracyBoost = TaczCuriosConfig.COMMON.malignantSpreadAccuracyReduction.get();
        
        // 直接应用霰弹枪伤害加成，无需检查是否手持霰弹枪
        if (shotgunDamageAttribute != null) {
            // 添加配置的霰弹枪伤害加成（加算）
            var shotgunDamageModifier = new AttributeModifier(
                DAMAGE_UUID,
                DAMAGE_NAME,
                damageBoost,
                AttributeModifier.Operation.ADDITION
            );
            shotgunDamageAttribute.addPermanentModifier(shotgunDamageModifier);
        }
        
        // 应用不精准度提升（加算）
        var inaccuracyAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "inaccuracy")
            )
        );
        
        if (inaccuracyAttribute != null) {
            // 移除已存在的修饰
            inaccuracyAttribute.removeModifier(INACCURACY_UUID);
            
            // 检查实体是否持有霰弹枪，只有持有霰弹枪时才应用不精准度加成
            if (GunTypeChecker.isHoldingShotgun(livingEntity)) {
                // 添加配置的不精准度加成（乘算
                var inaccuracyModifier = new AttributeModifier(
                    INACCURACY_UUID,
                    INACCURACY_NAME,
                    inaccuracyBoost,
                    AttributeModifier.Operation.ADDITION
                );
                inaccuracyAttribute.addPermanentModifier(inaccuracyModifier);
            }
        }
        // 不再主动调用缓存更新，由mod自主检测属性变更后触发
    }
    
    /**
     * 移除恶性扩散效     */
    public void removeMalignantSpreadEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 获取霰弹枪伤害属
        var shotgunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_shotgun")
            )
        );
        
        if (shotgunDamageAttribute != null) {
            shotgunDamageAttribute.removeModifier(DAMAGE_UUID);
        }
        
        // 移除不精准度加成
        var inaccuracyAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "inaccuracy")
            )
        );
        
        if (inaccuracyAttribute != null) {
            inaccuracyAttribute.removeModifier(INACCURACY_UUID);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
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
        tooltip.add(Component.translatable("item.tcc.malignant_spread.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double damageBoost = TaczCuriosConfig.COMMON.malignantSpreadDamageBoost.get() * 100;
        double inaccuracyBoost = TaczCuriosConfig.COMMON.malignantSpreadAccuracyReduction.get() * 100;
        tooltip.add(Component.translatable("item.tcc.malignant_spread.effect", String.format("%.0f", damageBoost), String.format("%.0f", inaccuracyBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    /**
     * 当实体切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyMalignantSpreadEffects(livingEntity);
    }
}

