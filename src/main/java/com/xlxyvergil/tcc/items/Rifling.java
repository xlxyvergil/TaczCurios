package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.network.chat.Component;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 膛线 - 提升特定枪械伤害
 * 效果：特定枪械伤害加成（加算
 */
public class Rifling extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID[] DAMAGE_UUIDS = {
        UUID.fromString("8da03d35-138b-4b16-8f58-afd8f296252f"),
        UUID.fromString("fcb27cd7-a90e-4e1c-8316-976ba894dd4a"),
        UUID.fromString("e20e4bfe-2343-42ac-89b8-78f3e47081f8"),
        UUID.fromString("a2257621-fad8-4670-8b58-2f253947a1c6"),
        UUID.fromString("35741afc-9a1f-458d-89f0-ffd97d2a4832")
    };
    
    // 修饰符名
    private static final String[] DAMAGE_NAMES = {
        "tcc.rifling.rifle_damage",
        "tcc.rifling.sniper_damage",
        "tcc.rifling.smg_damage",
        "tcc.rifling.lmg_damage",
        "tcc.rifling.launcher_damage"
    };
    
    public Rifling(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给生物添加伤害属性修改
        applyRiflingEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除生物的伤害属性修改
        removeRiflingEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * Rifling与MergedRifling互斥，不能同时装
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查生物是否已经装备了MergedRifling
        LivingEntity livingEntity = (LivingEntity) slotContext.entity();
        ICuriosItemHandler curiosHandler = livingEntity.getCapability(top.theillusivec4.curios.api.CuriosCapability.INVENTORY).orElse(null);
        if (curiosHandler != null) {
            ICurioStacksHandler tccSlotHandler = curiosHandler.getCurios().get("tcc_slot");
            if (tccSlotHandler != null) {
                for (int i = 0; i < tccSlotHandler.getSlots(); i++) {
                    ItemStack equippedStack = tccSlotHandler.getStacks().getStackInSlot(i);
                    if (equippedStack.getItem() instanceof MergedRifling) {
                        return false; // 如果已经装备了MergedRifling，则不能装备Rifling
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * 当物品在Curios插槽中时被右键点
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }
    
    /**
     * 应用膛线效果
     * 提升特定枪械伤害（加算）
     */
    public void applyRiflingEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        
        // 特定枪械类型
        String[] gunTypes = {
            "bullet_gundamage_rifle",
            "bullet_gundamage_sniper",
            "bullet_gundamage_smg",
            "bullet_gundamage_lmg",
            "bullet_gundamage_launcher"
        };
        
        // 获取配置中的伤害加成
        double damageBoost = TaczCuriosConfig.COMMON.riflingDamageBoost.get();
        
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
                
                // 添加特定枪械伤害加成（加算）
                var gunDamageModifier = new AttributeModifier(
                    DAMAGE_UUIDS[i],
                    DAMAGE_NAMES[i],
                    damageBoost,
                    AttributeModifier.Operation.ADDITION
                );
                gunDamageAttribute.addPermanentModifier(gunDamageModifier);
            }
        }
        // 不再主动调用缓存更新，由mod自主检测属性变更后触发
    }
    
    /**
     * 移除膛线效果
     */
    public void removeRiflingEffects(LivingEntity livingEntity) {
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
        tooltip.add(Component.translatable("item.tcc.rifling.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double damageBoost = TaczCuriosConfig.COMMON.riflingDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.rifling.effect", String.format("%.0f", damageBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyRiflingEffects(livingEntity);
    }
}
