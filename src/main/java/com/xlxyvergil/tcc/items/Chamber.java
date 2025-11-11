package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import com.tacz.guns.api.TimelessAPI;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 膛室 - 当玩家手持狙击枪时，提升40%伤害（乘算）
 * 效果：狙击枪伤害+40%（乘算）
 * 与ChamberPrime互斥
 */
public class Chamber extends ItemBaseCurio {

    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID DAMAGE_UUID = UUID.fromString("12345678-1234-1234-1234-123456789abc");
    
    // 修饰符名称
    private static final String DAMAGE_NAME = "tcc.chamber.damage";
    
    // 效果参数
    private static final double DAMAGE_BOOST = 0.40;       // 40%伤害提升（乘算）
    
    public Chamber(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加属性修改
        if (slotContext.entity() instanceof Player player) {
            applyChamberEffect(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的属性修改
        if (slotContext.entity() instanceof Player player) {
            removeChamberEffect(player);
        }
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * Chamber与ChamberPrime互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查玩家是否已经装备了ChamberPrime
        if (slotContext.entity() instanceof Player player) {
            ICuriosItemHandler curiosHandler = player.getCapability(top.theillusivec4.curios.api.CuriosCapability.INVENTORY).orElse(null);
            if (curiosHandler != null) {
                ICurioStacksHandler tccSlotHandler = curiosHandler.getCurios().get("tcc_slot");
                if (tccSlotHandler != null) {
                    for (int i = 0; i < tccSlotHandler.getSlots(); i++) {
                        ItemStack equippedStack = tccSlotHandler.getStacks().getStackInSlot(i);
                        if (equippedStack.getItem() instanceof ChamberPrime) {
                            return false; // 如果已经装备了ChamberPrime，则不能装备Chamber
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * 当物品在Curios插槽中时被右键点击
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }
    
    /**
     * 应用膛室效果
     * 提升狙击枪伤害（乘算）
     */
    private void applyChamberEffect(Player player) {
        var attributes = player.getAttributes();
        
        // 获取狙击枪伤害属性
        var gunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_sniper")
            )
        );
        
        // 移除已存在的修饰符
        if (gunDamageAttribute != null) {
            gunDamageAttribute.removeModifier(DAMAGE_UUID);
        }
        
        // 直接应用效果，不需要检查是否手持狙击枪
        if (gunDamageAttribute != null) {
            // 添加40%的伤害加成（乘算）
            var gunDamageModifier = new AttributeModifier(
                DAMAGE_UUID,
                DAMAGE_NAME,
                DAMAGE_BOOST,
                AttributeModifier.Operation.MULTIPLY_BASE
            );
            gunDamageAttribute.addPermanentModifier(gunDamageModifier);
        }
    }
    
    /**
     * 移除膛室效果
     */
    private void removeChamberEffect(Player player) {
        var attributes = player.getAttributes();
        
        // 获取狙击枪伤害属性
        var gunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_sniper")
            )
        );
        
        if (gunDamageAttribute != null) {
            gunDamageAttribute.removeModifier(DAMAGE_UUID);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof Player player) {
            applyChamberEffect(player);
        }
    }
    
    /**
     * 当物品被装备时，显示提示信息
     */
    @Override
    public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                    "§6膛室已装备 - 狙击枪伤害+40%（乘算）"
                ),
                true
            );
        }
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.chamber.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.chamber.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§b稀有")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}