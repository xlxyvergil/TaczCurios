package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 预言契约 - 提升手枪90%伤害
 * 效果：手枪伤害+90%（加算）
 */
public class ProphecyPact extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰符
    private static final UUID DAMAGE_UUID = UUID.fromString("a2345678-1234-1234-1234-123456789abc");
    
    // 修饰符名称
    private static final String DAMAGE_NAME = "tcc.prophecy_pact.pistol_damage";
    
    // 效果参数
    private static final double DAMAGE_BOOST = 0.90;       // 90%手枪伤害提升（加算）
    
    public ProphecyPact(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加伤害属性修改
        if (slotContext.entity() instanceof Player player) {
            applyEffects(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的伤害属性修改
        if (slotContext.entity() instanceof Player player) {
            removeEffects(player);
        }
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
     * 当物品在Curios插槽中时被右键点击
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }
    
    /**
     * 应用效果
     * 提升手枪伤害（加算）
     */
    private void applyEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 手枪伤害属性
        var pistolDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_pistol")
            )
        );
        
        if (pistolDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            pistolDamageAttribute.removeModifier(DAMAGE_UUID);
            
            // 添加90%的手枪伤害加成（加算）
            var damageModifier = new AttributeModifier(
                DAMAGE_UUID,
                DAMAGE_NAME,
                DAMAGE_BOOST,
                AttributeModifier.Operation.ADDITION
            );
            pistolDamageAttribute.addPermanentModifier(damageModifier);
        }
    }
    
    /**
     * 移除效果
     */
    private void removeEffects(Player player) {
        var attributes = player.getAttributes();
        
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
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof Player player) {
            applyEffects(player);
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
                    "§6预言契约已装备 - 手枪伤害+90%（加算）"
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
        tooltip.add(Component.translatable("item.tcc.prophecy_pact.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.prophecy_pact.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§9常见")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}