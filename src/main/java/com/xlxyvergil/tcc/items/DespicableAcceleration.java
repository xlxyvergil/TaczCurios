package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
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
 * 卑劣加速 - 提升90%射击速度，但降低15%通用伤害和全部7种特定枪械伤害
 * 效果：射击速度+90%，通用伤害-15%，特定枪械伤害-15%
 */
public class DespicableAcceleration extends ItemBaseCurio {
    
    // 7种特定枪械伤害属性的UUID和配置
    private static final Map<String, UUID> DAMAGE_UUIDS = new HashMap<>();
    private static final Map<String, String> DAMAGE_NAMES = new HashMap<>();
    private static final Map<String, String> DAMAGE_DISPLAY_NAMES = new HashMap<>();
    
    static {
        // 初始化7种特定枪械的UUID和名称
        DAMAGE_UUIDS.put("pistol", UUID.fromString("42345678-1234-1234-1234-123456789abe"));
        DAMAGE_UUIDS.put("rifle", UUID.fromString("42345678-1234-1234-1234-123456789abf"));
        DAMAGE_UUIDS.put("shotgun", UUID.fromString("42345678-1234-1234-1234-123456789ac0"));
        DAMAGE_UUIDS.put("sniper", UUID.fromString("42345678-1234-1234-1234-123456789ac1"));
        DAMAGE_UUIDS.put("smg", UUID.fromString("42345678-1234-1234-1234-123456789ac2"));
        DAMAGE_UUIDS.put("lmg", UUID.fromString("42345678-1234-1234-1234-123456789ac3"));
        DAMAGE_UUIDS.put("launcher", UUID.fromString("42345678-1234-1234-1234-123456789ac4"));
        
        DAMAGE_NAMES.put("pistol", "tcc.despicable_acceleration.pistol_damage");
        DAMAGE_NAMES.put("rifle", "tcc.despicable_acceleration.rifle_damage");
        DAMAGE_NAMES.put("shotgun", "tcc.despicable_acceleration.shotgun_damage");
        DAMAGE_NAMES.put("sniper", "tcc.despicable_acceleration.sniper_damage");
        DAMAGE_NAMES.put("smg", "tcc.despicable_acceleration.smg_damage");
        DAMAGE_NAMES.put("lmg", "tcc.despicable_acceleration.lmg_damage");
        DAMAGE_NAMES.put("launcher", "tcc.despicable_acceleration.launcher_damage");
        
        DAMAGE_DISPLAY_NAMES.put("pistol", "手枪");
        DAMAGE_DISPLAY_NAMES.put("rifle", "步枪");
        DAMAGE_DISPLAY_NAMES.put("shotgun", "霰弹枪");
        DAMAGE_DISPLAY_NAMES.put("sniper", "狙击枪");
        DAMAGE_DISPLAY_NAMES.put("smg", "冲锋枪");
        DAMAGE_DISPLAY_NAMES.put("lmg", "轻机枪");
        DAMAGE_DISPLAY_NAMES.put("launcher", "发射器");
    }
    
    // 射击速度和通用伤害的属性修饰符
    private static final UUID FIRING_SPEED_UUID = UUID.fromString("42345678-1234-1234-1234-123456789abc");
    private static final UUID GENERAL_DAMAGE_UUID = UUID.fromString("42345678-1234-1234-1234-123456789abd");
    
    // 修饰符名称
    private static final String FIRING_SPEED_NAME = "tcc.despicable_acceleration.firing_speed";
    private static final String GENERAL_DAMAGE_NAME = "tcc.despicable_acceleration.general_damage";
    
    // 效果参数
    private static final double FIRING_SPEED_BOOST = 0.9;       // 90%射击速度提升
    private static final double DAMAGE_REDUCTION = -0.15;      // 15%伤害降低
    
    public DespicableAcceleration(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加射击速度和伤害属性修改
        if (slotContext.entity() instanceof Player player) {
            applyAccelerationEffects(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的射击速度和伤害属性修改
        if (slotContext.entity() instanceof Player player) {
            removeAccelerationEffects(player);
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
     * 应用加速效果
     * 提升射击速度，降低通用伤害和7种特定枪械伤害
     */
    private void applyAccelerationEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 应用射击速度提升
        var firingSpeedAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "rounds_per_minute")
            )
        );
        
        if (firingSpeedAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            firingSpeedAttribute.removeModifier(FIRING_SPEED_UUID);
            
            // 添加90%的射击速度加成
            var firingSpeedModifier = new AttributeModifier(
                FIRING_SPEED_UUID,
                FIRING_SPEED_NAME,
                FIRING_SPEED_BOOST,
                AttributeModifier.Operation.ADDITION
            );
            firingSpeedAttribute.addPermanentModifier(firingSpeedModifier);
        }
        
        // 应用通用伤害降低
        var generalDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage")
            )
        );
        
        if (generalDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            generalDamageAttribute.removeModifier(GENERAL_DAMAGE_UUID);
            
            // 添加15%的通用伤害降低
            var generalDamageModifier = new AttributeModifier(
                GENERAL_DAMAGE_UUID,
                GENERAL_DAMAGE_NAME,
                DAMAGE_REDUCTION,
                AttributeModifier.Operation.ADDITION
            );
            generalDamageAttribute.addPermanentModifier(generalDamageModifier);
        }
        
        // 应用7种特定枪械伤害降低
        for (String gunType : DAMAGE_UUIDS.keySet()) {
            var specificDamageAttribute = attributes.getInstance(
                net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                    new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_" + gunType)
                )
            );
            
            if (specificDamageAttribute != null) {
                // 检查是否已经存在相同的修饰符，如果存在则移除
                specificDamageAttribute.removeModifier(DAMAGE_UUIDS.get(gunType));
                
                // 添加15%的特定枪械伤害降低
                var specificDamageModifier = new AttributeModifier(
                    DAMAGE_UUIDS.get(gunType),
                    DAMAGE_NAMES.get(gunType),
                    DAMAGE_REDUCTION,
                    AttributeModifier.Operation.ADDITION
                );
                specificDamageAttribute.addPermanentModifier(specificDamageModifier);
            }
        }
    }
    
    /**
     * 移除加速效果
     */
    private void removeAccelerationEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 移除射击速度加成
        var firingSpeedAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "rounds_per_minute")
            )
        );
        
        if (firingSpeedAttribute != null) {
            firingSpeedAttribute.removeModifier(FIRING_SPEED_UUID);
        }
        
        // 移除通用伤害降低
        var generalDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage")
            )
        );
        
        if (generalDamageAttribute != null) {
            generalDamageAttribute.removeModifier(GENERAL_DAMAGE_UUID);
        }
        
        // 移除7种特定枪械伤害降低
        for (String gunType : DAMAGE_UUIDS.keySet()) {
            var specificDamageAttribute = attributes.getInstance(
                net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                    new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_" + gunType)
                )
            );
            
            if (specificDamageAttribute != null) {
                specificDamageAttribute.removeModifier(DAMAGE_UUIDS.get(gunType));
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
            applyAccelerationEffects(player);
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
                    "§6卑劣加速已装备 - 射击速度+90%，通用伤害-15%，所有特定枪械伤害-15%"
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
        tooltip.add(Component.translatable("item.tcc.despicable_acceleration.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.despicable_acceleration.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§6稀有")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
    
    /**
     * 获取装备槽位
     */
    public String getSlot() {
        return "tcc:tcc_slot";
    }
}