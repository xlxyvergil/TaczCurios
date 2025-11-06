package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
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
 * 重口径标签 - 将玩家攻击力的10%转换为通用枪械伤害倍率，但增加150%枪械重量
 * 效果：每100点攻击力提供10倍通用枪械伤害加成，但枪械重量增加150%
 */
public class HeavyCaliberTag extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("32345678-1234-1234-1234-123456789abc");
    private static final UUID GUN_WEIGHT_UUID = UUID.fromString("32345678-1234-1234-1234-123456789abd");
    
    // 修饰符名称
    private static final String GUN_DAMAGE_NAME = "tcc.heavy_caliber_tag.gun_damage";
    private static final String GUN_WEIGHT_NAME = "tcc.heavy_caliber_tag.gun_weight";
    
    // 效果参数
    private static final double ATTACK_POWER_TO_DAMAGE_RATIO = 0.1; // 10%攻击力转换为伤害倍率
    private static final double WEIGHT_INCREASE_MULTIPLIER = 1.5;   // 150%枪械重量增加
    
    public HeavyCaliberTag(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加枪械伤害和重量属性加成
        if (slotContext.getWearer() instanceof Player player) {
            applyGunEffects(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的枪械伤害和重量属性加成
        if (slotContext.getWearer() instanceof Player player) {
            removeGunEffects(player);
        }
    }
    
    /**
     * 当物品在Curios插槽中时被右键点击
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
    
    /**
     * 应用枪械效果
     * 根据玩家攻击力动态计算枪械伤害加成，并增加枪械重量
     */
    private void applyGunEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 获取玩家攻击力属性值
        double attackDamage = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue();
        
        // 计算枪械伤害加成倍率（10%攻击力转换为倍率）
        double damageMultiplier = attackDamage * ATTACK_POWER_TO_DAMAGE_RATIO;
        
        // 应用通用枪械伤害加成
        var gunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "tacz.bullet_gundamage")
            )
        );
        
        if (gunDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            gunDamageAttribute.removeModifier(GUN_DAMAGE_UUID);
            
            // 添加动态计算的伤害加成倍率
            AttributeModifier damageModifier = new AttributeModifier(
                GUN_DAMAGE_UUID,
                GUN_DAMAGE_NAME,
                damageMultiplier,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            gunDamageAttribute.addPermanentModifier(damageModifier);
        }
        
        // 应用枪械重量增加
        var gunWeightAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "weight")
            )
        );
        
        if (gunWeightAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            gunWeightAttribute.removeModifier(GUN_WEIGHT_UUID);
            
            // 添加150%的重量加成
            AttributeModifier weightModifier = new AttributeModifier(
                GUN_WEIGHT_UUID,
                GUN_WEIGHT_NAME,
                WEIGHT_INCREASE_MULTIPLIER,
                AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            gunWeightAttribute.addPermanentModifier(weightModifier);
        }
    }
    
    /**
     * 移除枪械效果
     */
    private void removeGunEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 移除通用枪械伤害加成
        var gunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "tacz.bullet_gundamage")
            )
        );
        
        if (gunDamageAttribute != null) {
            gunDamageAttribute.removeModifier(GUN_DAMAGE_UUID);
        }
        
        // 移除枪械重量加成
        var gunWeightAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "weight")
            )
        );
        
        if (gunWeightAttribute != null) {
            gunWeightAttribute.removeModifier(GUN_WEIGHT_UUID);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效，动态更新基于攻击力的伤害加成
        if (slotContext.getWearer() instanceof Player player) {
            applyGunEffects(player);
        }
    }
    
    /**
     * 当物品被装备时，显示提示信息
     */
    @Override
    public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        if (slotContext.getWearer() instanceof Player player) {
            double attackDamage = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue();
            double damageMultiplier = attackDamage * ATTACK_POWER_TO_DAMAGE_RATIO;
            
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                    String.format("§6重口径标签已装备 - 枪械伤害+%.1fx (基于%.1f攻击力), 枪械重量+150%%", 
                        damageMultiplier, attackDamage)
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
        tooltip.add(Component.translatable("item.tcc.heavy_caliber_tag.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果标题
        tooltip.add(Component.translatable("item.tcc.heavy_caliber_tag.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 实时显示加成信息 - 参考Enigmatic Legacy的cursed_scroll实现
        if (net.minecraft.client.Minecraft.getInstance().player != null) {
            Player player = net.minecraft.client.Minecraft.getInstance().player;
            
            // 获取玩家当前攻击力
            double attackDamage = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue();
            
            // 计算实时加成
            double damageMultiplier = attackDamage * ATTACK_POWER_TO_DAMAGE_RATIO;
            
            // 显示实时加成信息
            tooltip.add(Component.literal("  §7当前攻击力: §a" + String.format("%.1f", attackDamage) + " §7→ ")
                .append(Component.literal("枪械伤害+§a" + String.format("%.1fx", damageMultiplier))
                    .withStyle(net.minecraft.ChatFormatting.GREEN))
                .append(Component.literal(", §7枪械重量+§c150%")
                    .withStyle(net.minecraft.ChatFormatting.RED)));
            
            // 添加换行
            tooltip.add(Component.literal(""));
            
            // 显示详细效果说明
            tooltip.add(Component.literal("  §7• §6将10%攻击力转换为枪械伤害倍率")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
            tooltip.add(Component.literal("  §7• §c枪械重量增加150%")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
            
            // 添加示例说明
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§7示例：100攻击力 → 10倍枪械伤害加成")
                .withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
        } else {
            // 如果无法获取玩家信息，显示静态说明
            tooltip.add(Component.literal("  §7• §6将10%攻击力转换为枪械伤害倍率")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
            tooltip.add(Component.literal("  §7• §c枪械重量增加150%")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        }
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§b稀有")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}