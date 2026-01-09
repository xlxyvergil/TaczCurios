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
 * 重口- 提升特定枪械伤害，增加不精准
 * 效果：提升特定枪械伤害（加算），增加不精准度（加算）
 */
public class HeavyCaliberTag extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID[] DAMAGE_UUIDS = {
        UUID.fromString("0de3ed5d-9cb1-4c22-8bd1-c9b68ac13e9f"),
        UUID.fromString("86c52112-49e1-4d80-84b1-5a327ffbc971"),
        UUID.fromString("216b141e-17b3-44f0-a03c-ddfc5758a15e"),
        UUID.fromString("7df0af83-2c3e-4680-b17f-4c37dc55dea8"),
        UUID.fromString("006a5e24-258e-487f-9301-dfb07c08caa3")
    };
    
    // 修饰符名
    private static final String[] DAMAGE_NAMES = {
        "tcc.heavy_caliber.rifle_damage",
        "tcc.heavy_caliber.sniper_damage",
        "tcc.heavy_caliber.smg_damage",
        "tcc.heavy_caliber.lmg_damage",
        "tcc.heavy_caliber.launcher_damage"
    };
    
    public HeavyCaliberTag(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给实体添加伤害属性修改
        applyHeavyCaliberEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除实体的伤害属性修改
        removeHeavyCaliberEffects((LivingEntity) slotContext.entity());
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
     * 应用重口径效     * 提升特定枪械伤害（加算）和不精准度（加算     */
    public void applyHeavyCaliberEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 特定枪械类型
        String[] gunTypes = {
            "bullet_gundamage_rifle",
            "bullet_gundamage_sniper",
            "bullet_gundamage_smg",
            "bullet_gundamage_lmg",
            "bullet_gundamage_launcher"
        };
        
        // 获取配置中的伤害加成值和不精准度加成
        double damageBoost = TaczCuriosConfig.COMMON.heavyCaliberTagDamageBoost.get();
        double inaccuracyBoost = TaczCuriosConfig.COMMON.heavyCaliberTagInaccuracyBoost.get();
        
        // 应用特定枪械伤害提升（加算）
        for (int i = 0; i < gunTypes.length; i++) {
            var gunDamageAttribute = attributes.getInstance(
                net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                    new net.minecraft.resources.ResourceLocation("taa", gunTypes[i])
                )
            );
            
            if (gunDamageAttribute != null) {
                // 检查是否已经存在相同的修饰符，如果存在则移
                gunDamageAttribute.removeModifier(DAMAGE_UUIDS[i]);
                
                // 添加配置中的特定枪械伤害加成（加算）
                var gunDamageModifier = new AttributeModifier(
                    DAMAGE_UUIDS[i],
                    DAMAGE_NAMES[i],
                    damageBoost,
                    AttributeModifier.Operation.ADDITION
                );
                gunDamageAttribute.addPermanentModifier(gunDamageModifier);
            }
        }
        
        // 应用不精准度提升（加算）
        var inaccuracyAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "inaccuracy")
            )
        );
        
        if (inaccuracyAttribute != null) {
            // 移除已存在的修饰
            inaccuracyAttribute.removeModifier(DAMAGE_UUIDS[0]);
            
            // 检查实体是否持有支持的枪械类型，只有持有支持的枪械时才应用不精准度加成
            if (GunTypeChecker.isHoldingDmgBoostGunType(livingEntity)) {
                // 添加配置中的不精准度加成（乘算）
                var inaccuracyModifier = new AttributeModifier(
                    DAMAGE_UUIDS[0],
                    "tcc.heavy_caliber.inaccuracy",
                    inaccuracyBoost,
                    AttributeModifier.Operation.ADDITION
                );
                inaccuracyAttribute.addPermanentModifier(inaccuracyModifier);
            }
        }
        // 不再主动调用缓存更新，由mod自主检测属性变更后触发
    }
    
    /**
     * 移除重口径效     */
    public void removeHeavyCaliberEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 特定枪械类型
        String[] gunTypes = {
            "bullet_gundamage_rifle",
            "bullet_gundamage_sniper",
            "bullet_gundamage_smg",
            "bullet_gundamage_lmg",
            "bullet_gundamage_launcher"
        };
        
        // 移除特定枪械伤害加成
        for (int i = 0; i < gunTypes.length; i++) {
            var gunDamageAttribute = attributes.getInstance(
                net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                    new net.minecraft.resources.ResourceLocation("taa", gunTypes[i])
                )
            );
            
            if (gunDamageAttribute != null) {
                gunDamageAttribute.removeModifier(DAMAGE_UUIDS[i]);
            }
        }
        
        // 移除不精准度加成
        var inaccuracyAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "inaccuracy")
            )
        );
        
        if (inaccuracyAttribute != null) {
            inaccuracyAttribute.removeModifier(DAMAGE_UUIDS[0]);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        applyHeavyCaliberEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.heavy_caliber_tag.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double damageBoost = TaczCuriosConfig.COMMON.heavyCaliberTagDamageBoost.get() * 100;
        double inaccuracyBoost = TaczCuriosConfig.COMMON.heavyCaliberTagInaccuracyBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.heavy_caliber_tag.effect", 
                String.format("%.0f", damageBoost), String.format("%.0f", inaccuracyBoost))
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
        applyHeavyCaliberEffects(livingEntity);
    }
}

