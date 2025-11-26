package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
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
 * 抵近射击Prime饰品 - 提供165%霰弹枪伤害加成
 * 效果：为玩家提供165%的霰弹枪伤害加成
 */
public class CloseCombatPrime extends ItemBaseCurio {
    
    // 霰弹枪伤害属性的UUID和配置
    private static final UUID SHOTGUN_DAMAGE_UUID = UUID.fromString("44345678-1234-1234-1234-123456789abe");
    private static final String SHOTGUN_DAMAGE_NAME = "tcc.close_combat_prime.shotgun_damage";
    
    public CloseCombatPrime(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加霰弹枪伤害属性加成
        if (slotContext.entity() instanceof Player player) {
            applyShotgunDamageBonus(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的霰弹枪伤害属性加成
        if (slotContext.entity() instanceof Player player) {
            removeShotgunDamageBonus(player);
        }
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * CloseCombatPrime与CloseRangeShot互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查玩家是否已经装备了CloseRangeShot
        if (slotContext.entity() instanceof Player player) {
            ICuriosItemHandler curiosHandler = player.getCapability(top.theillusivec4.curios.api.CuriosCapability.INVENTORY).orElse(null);
            if (curiosHandler != null) {
                ICurioStacksHandler tccSlotHandler = curiosHandler.getCurios().get("tcc_slot");
                if (tccSlotHandler != null) {
                    for (int i = 0; i < tccSlotHandler.getSlots(); i++) {
                        ItemStack equippedStack = tccSlotHandler.getStacks().getStackInSlot(i);
                        if (equippedStack.getItem() instanceof CloseRangeShot) {
                            return false; // 如果已经装备了CloseRangeShot，则不能装备CloseCombatPrime
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * 返回饰品槽位ID
     */
    public String getSlot() {
        return "tcc:tcc_slot";
    }
    
    /**
     * 应用霰弹枪伤害加成
     * 给玩家添加165%的霰弹枪伤害加成（加法）
     */
    private void applyShotgunDamageBonus(Player player) {
        var attributes = player.getAttributes();
        
        var damageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "bullet_gundamage_shotgun")
            )
        );
        
        if (damageAttribute != null) {
            // 检查是否已经存在相同的修饰符
            if (damageAttribute.getModifier(SHOTGUN_DAMAGE_UUID) == null) {
                // 添加165%的伤害加成 (1.65 = 165%)
                AttributeModifier modifier = new AttributeModifier(
                    SHOTGUN_DAMAGE_UUID,
                    SHOTGUN_DAMAGE_NAME,
                    1.65D,
                    AttributeModifier.Operation.ADDITION
                );
                damageAttribute.addPermanentModifier(modifier);
            }
        }
    }
    
    /**
     * 移除霰弹枪伤害加成
     */
    private void removeShotgunDamageBonus(Player player) {
        var attributes = player.getAttributes();
        
        var damageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "bullet_gundamage_shotgun")
            )
        );
        
        if (damageAttribute != null) {
            // 移除之前添加的修饰符
            damageAttribute.removeModifier(SHOTGUN_DAMAGE_UUID);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof Player player) {
            applyShotgunDamageBonus(player);
        }
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.close_combat_prime.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.close_combat_prime.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加霰弹枪伤害加成的详细列表
        tooltip.add(Component.literal("  §7• §6+165% §7霰弹枪伤害")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§f传说")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(Player player) {
        applyShotgunDamageBonus(player);
    }
}