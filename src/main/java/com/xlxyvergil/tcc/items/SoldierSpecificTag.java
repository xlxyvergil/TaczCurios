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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 士兵特定挂牌 - 提供50%所有特定枪械伤害加成
 * 效果：为玩家提供50%的所有7种特定枪械伤害加成
 */
public class SoldierSpecificTag extends ItemBaseCurio {
    
    // 7种特定枪械伤害属性的UUID和配置
    private static final Map<String, UUID> DAMAGE_UUIDS = new HashMap<>();
    private static final Map<String, String> DAMAGE_NAMES = new HashMap<>();
    private static final Map<String, String> DAMAGE_DISPLAY_NAMES = new HashMap<>();
    
    static {
        // 初始化7种特定枪械的UUID和名称
        DAMAGE_UUIDS.put("pistol", UUID.fromString("22345678-1234-1234-1234-123456789abc"));
        DAMAGE_UUIDS.put("rifle", UUID.fromString("22345678-1234-1234-1234-123456789abd"));
        DAMAGE_UUIDS.put("shotgun", UUID.fromString("22345678-1234-1234-1234-123456789abe"));
        DAMAGE_UUIDS.put("sniper", UUID.fromString("22345678-1234-1234-1234-123456789abf"));
        DAMAGE_UUIDS.put("smg", UUID.fromString("22345678-1234-1234-1234-123456789aba"));
        DAMAGE_UUIDS.put("lmg", UUID.fromString("22345678-1234-1234-1234-123456789abb"));
        DAMAGE_UUIDS.put("launcher", UUID.fromString("22345678-1234-1234-1234-123456789abc"));
        
        DAMAGE_NAMES.put("pistol", "tcc.soldier_specific_tag.pistol_damage");
        DAMAGE_NAMES.put("rifle", "tcc.soldier_specific_tag.rifle_damage");
        DAMAGE_NAMES.put("shotgun", "tcc.soldier_specific_tag.shotgun_damage");
        DAMAGE_NAMES.put("sniper", "tcc.soldier_specific_tag.sniper_damage");
        DAMAGE_NAMES.put("smg", "tcc.soldier_specific_tag.smg_damage");
        DAMAGE_NAMES.put("lmg", "tcc.soldier_specific_tag.lmg_damage");
        DAMAGE_NAMES.put("launcher", "tcc.soldier_specific_tag.launcher_damage");
        
        DAMAGE_DISPLAY_NAMES.put("pistol", "手枪");
        DAMAGE_DISPLAY_NAMES.put("rifle", "步枪");
        DAMAGE_DISPLAY_NAMES.put("shotgun", "霰弹枪");
        DAMAGE_DISPLAY_NAMES.put("sniper", "狙击枪");
        DAMAGE_DISPLAY_NAMES.put("smg", "冲锋枪");
        DAMAGE_DISPLAY_NAMES.put("lmg", "轻机枪");
        DAMAGE_DISPLAY_NAMES.put("launcher", "发射器");
    }
    
    public SoldierSpecificTag(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加所有7种特定枪械伤害属性加成
        if (slotContext.getWearer() instanceof Player player) {
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
        if (slotContext.getWearer() instanceof Player player) {
            removeAllDamageBonuses(player);
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
     * 应用所有7种特定枪械伤害加成
     * 给玩家添加50%的所有7种特定枪械伤害加成
     */
    private void applyAllDamageBonuses(Player player) {
        var attributes = player.getAttributes();
        
        for (String gunType : DAMAGE_UUIDS.keySet()) {
            var damageAttribute = attributes.getInstance(
                net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                    new net.minecraft.resources.ResourceLocation("taa", "tacz.bullet_gundamage_" + gunType)
                )
            );
            
            if (damageAttribute != null) {
                // 检查是否已经存在相同的修饰符
                if (damageAttribute.getModifier(DAMAGE_UUIDS.get(gunType)) == null) {
                    // 添加50%的伤害加成 (0.5 = 50%)
                    AttributeModifier modifier = new AttributeModifier(
                        DAMAGE_UUIDS.get(gunType),
                        DAMAGE_NAMES.get(gunType),
                        0.5D,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
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
                    new net.minecraft.resources.ResourceLocation("taa", "tacz.bullet_gundamage_" + gunType)
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
        if (slotContext.getWearer() instanceof Player player) {
            applyAllDamageBonuses(player);
        }
    }
    
    /**
     * 当物品被装备时，显示提示信息
     */
    @Override
    public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        if (slotContext.getWearer() instanceof Player player) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§6士兵特定挂牌已装备 - 所有7种特定枪械伤害+50%"),
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
        tooltip.add(Component.translatable("item.tcc.soldier_specific_tag.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果标题
        tooltip.add(Component.translatable("item.tcc.soldier_specific_tag.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加7种特定枪械伤害加成的详细列表
        for (String gunType : DAMAGE_DISPLAY_NAMES.keySet()) {
            tooltip.add(Component.literal("  §7• §6+50% §7" + DAMAGE_DISPLAY_NAMES.get(gunType) + "伤害")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        }
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§b常见")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}