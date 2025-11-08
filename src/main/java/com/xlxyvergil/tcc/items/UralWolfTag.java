package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 乌拉尔银狼饰品 - 提供150%爆头倍率加成
 * 通过TaczAttributeAdd的headshot_multiplier属性实现
 */
public class UralWolfTag extends ItemBaseCurio implements ICurioItem {
    
    // 爆头倍率修饰符的UUID（确保唯一性）
    private static final UUID HEADSHOT_MULTIPLIER_MODIFIER_UUID = UUID.fromString("12345678-1234-1234-1234-123456789013");
    
    public UralWolfTag(Properties properties) {
        super(properties);
    }
    
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player) {
            applyHeadshotMultiplierEffect(slotContext.entity());
        }
        super.onEquip(slotContext, prevStack, stack);
    }
    
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player) {
            removeHeadshotMultiplierEffect(slotContext.entity());
        }
        super.onUnequip(slotContext, newStack, stack);
    }
    
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 每tick检查并确保爆头倍率效果生效
        if (slotContext.entity() instanceof Player) {
            ensureHeadshotMultiplierEffect(slotContext.entity());
        }
    }
    
    /**
     * 应用爆头倍率加成效果
     */
    private void applyHeadshotMultiplierEffect(LivingEntity entity) {
        var attributes = entity.getAttributes();
        
        // 应用爆头倍率加成
        var headshotMultiplierAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "headshot_multiplier")
            )
        );
        
        if (headshotMultiplierAttribute != null) {
            // 移除可能存在的旧修饰符，然后添加新的
            headshotMultiplierAttribute.removeModifier(HEADSHOT_MULTIPLIER_MODIFIER_UUID);
            headshotMultiplierAttribute.addTransientModifier(
                new AttributeModifier(
                    HEADSHOT_MULTIPLIER_MODIFIER_UUID,
                    "tcc_ural_wolf_headshot_boost",
                    1.50, // 150%爆头倍率加成
                    AttributeModifier.Operation.ADDITION
                )
            );
        }
    }
    
    /**
     * 移除爆头倍率加成效果
     */
    private void removeHeadshotMultiplierEffect(LivingEntity entity) {
        var attributes = entity.getAttributes();
        
        // 移除爆头倍率加成
        var headshotMultiplierAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "headshot_multiplier")
            )
        );
        
        if (headshotMultiplierAttribute != null) {
            headshotMultiplierAttribute.removeModifier(HEADSHOT_MULTIPLIER_MODIFIER_UUID);
        }
    }
    
    /**
     * 确保爆头倍率效果持续生效
     */
    private void ensureHeadshotMultiplierEffect(LivingEntity entity) {
        var attributes = entity.getAttributes();
        
        var headshotMultiplierAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "headshot_multiplier")
            )
        );
        
        if (headshotMultiplierAttribute != null) {
            // 检查修饰符是否还存在，如果不存在则重新添加
            var modifier = headshotMultiplierAttribute.getModifier(HEADSHOT_MULTIPLIER_MODIFIER_UUID);
            if (modifier == null) {
                headshotMultiplierAttribute.addTransientModifier(
                    new AttributeModifier(
                        HEADSHOT_MULTIPLIER_MODIFIER_UUID,
                        "tcc_ural_wolf_headshot_boost",
                        1.50,
                        AttributeModifier.Operation.ADDITION
                    )
                );
            }
        }
    }
    
    @Override
    public String getDescriptionId(ItemStack stack) {
        return "item.tcc.ural_wolf_tag";
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.ural_wolf_tag.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.ural_wolf_tag.effect")
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