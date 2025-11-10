package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 抵近射击Prime饰品 - 提供165%所有特定枪械伤害加成
 * 效果：为玩家提供165%的所有7种特定枪械伤害加成
 */
public class CloseCombatPrime extends ItemBaseCurio {
    
    // 7种特定枪械伤害属性的UUID和配置
    private static final Map<String, UUID> DAMAGE_UUIDS = new HashMap<>();
    private static final Map<String, String> DAMAGE_NAMES = new HashMap<>();
    private static final Map<String, String> DAMAGE_DISPLAY_NAMES = new HashMap<>();
    
    static {
        // 初始化7种特定枪械的UUID和名称
        DAMAGE_UUIDS.put("pistol", UUID.fromString("44345678-1234-1234-1234-123456789abc"));
        DAMAGE_UUIDS.put("rifle", UUID.fromString("44345678-1234-1234-1234-123456789abd"));
        DAMAGE_UUIDS.put("shotgun", UUID.fromString("44345678-1234-1234-1234-123456789abe"));
        DAMAGE_UUIDS.put("sniper", UUID.fromString("44345678-1234-1234-1234-123456789abf"));
        DAMAGE_UUIDS.put("smg", UUID.fromString("44345678-1234-1234-1234-123456789aba"));
        DAMAGE_UUIDS.put("lmg", UUID.fromString("44345678-1234-1234-1234-123456789abb"));
        DAMAGE_UUIDS.put("launcher", UUID.fromString("44345678-1234-1234-1234-123456789ab0"));
        
        DAMAGE_NAMES.put("pistol", "tcc.close_combat_prime.pistol_damage");
        DAMAGE_NAMES.put("rifle", "tcc.close_combat_prime.rifle_damage");
        DAMAGE_NAMES.put("shotgun", "tcc.close_combat_prime.shotgun_damage");
        DAMAGE_NAMES.put("sniper", "tcc.close_combat_prime.sniper_damage");
        DAMAGE_NAMES.put("smg", "tcc.close_combat_prime.smg_damage");
        DAMAGE_NAMES.put("lmg", "tcc.close_combat_prime.lmg_damage");
        DAMAGE_NAMES.put("launcher", "tcc.close_combat_prime.launcher_damage");
        
        DAMAGE_DISPLAY_NAMES.put("pistol", "手枪");
        DAMAGE_DISPLAY_NAMES.put("rifle", "步枪");
        DAMAGE_DISPLAY_NAMES.put("shotgun", "霰弹枪");
        DAMAGE_DISPLAY_NAMES.put("sniper", "狙击枪");
        DAMAGE_DISPLAY_NAMES.put("smg", "冲锋枪");
        DAMAGE_DISPLAY_NAMES.put("lmg", "轻机枪");
        DAMAGE_DISPLAY_NAMES.put("launcher", "发射器");
    }
    
    public CloseCombatPrime(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加所有7种特定枪械伤害属性加成
        if (slotContext.entity() instanceof Player player) {
            applyAllDamageBonuses(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的所有7种特定枪械伤害属性加成
        if (slotContext.entity() instanceof Player player) {
            removeAllDamageBonuses(player);
        }
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * 只能装备到tcc_slot槽位
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在指定的槽位
        return "tcc_slot".equals(slotContext.identifier());
    }
    
    /**
     * 返回饰品槽位ID
     */
    public String getSlot() {
        return "tcc:tcc_slot";
    }
    
    /**
     * 应用所有7种特定枪械伤害加成
     * 给玩家添加165%的所有7种特定枪械伤害加成
     */
    private void applyAllDamageBonuses(Player player) {
        var attributes = player.getAttributes();
        
        for (String gunType : DAMAGE_UUIDS.keySet()) {
            var damageAttribute = attributes.getInstance(
                net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                    new ResourceLocation("taa", "bullet_gundamage_" + gunType)
                )
            );
            
            if (damageAttribute != null) {
                // 检查是否已经存在相同的修饰符
                if (damageAttribute.getModifier(DAMAGE_UUIDS.get(gunType)) == null) {
                    // 添加165%的伤害加成 (1.65 = 165%)
                    AttributeModifier modifier = new AttributeModifier(
                        DAMAGE_UUIDS.get(gunType),
                        DAMAGE_NAMES.get(gunType),
                        1.65D,
                        AttributeModifier.Operation.MULTIPLY_BASE
                    );
                    damageAttribute.addPermanentModifier(modifier);
                }
            }
        }
    }
    
    /**
     * 移除所有7种特定枪械伤害加成
     */
    private void removeAllDamageBonuses(Player player) {
        var attributes = player.getAttributes();
        
        for (String gunType : DAMAGE_UUIDS.keySet()) {
            var damageAttribute = attributes.getInstance(
                net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                    new ResourceLocation("taa", "bullet_gundamage_" + gunType)
                )
            );
            
            if (damageAttribute != null) {
                // 移除之前添加的修饰符
                damageAttribute.removeModifier(DAMAGE_UUIDS.get(gunType));
            }
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof Player player) {
            applyAllDamageBonuses(player);
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
        
        // 添加7种特定枪械伤害加成的详细列表
        for (String gunType : DAMAGE_DISPLAY_NAMES.keySet()) {
            tooltip.add(Component.literal("  §7• §6+165% §7" + DAMAGE_DISPLAY_NAMES.get(gunType) + "伤害")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        }
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§f传说")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}