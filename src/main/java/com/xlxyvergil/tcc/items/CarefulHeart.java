package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;

import java.util.List;
import java.util.UUID;



/**
 * 我小心海也绝非鳝类 - 提升300%发射器伤害加成，300%爆炸伤害加成，300%爆炸范围加成
 * 效果：发射器伤害+300%，爆炸伤害+300%，爆炸范围+300%
 */
public class CarefulHeart extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID LAUNCHER_DAMAGE_UUID = UUID.fromString("81343572-fe9a-4cf2-bdf9-253af5a153c1");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("2fd98843-cf54-4ca7-949d-e8d50e295c86");
    private static final UUID EXPLOSION_RADIUS_UUID = UUID.fromString("e966cd29-d1c2-4770-a422-0f71c2ef4409");
    
    // 修饰符名称
    private static final String LAUNCHER_DAMAGE_NAME = "tcc.careful_heart.launcher_damage";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.careful_heart.explosion_damage";
    private static final String EXPLOSION_RADIUS_NAME = "tcc.careful_heart.explosion_radius";
    private static final UUID EXPLOSION_ENABLED_UUID = UUID.fromString("248f06a5-5144-4770-b56a-6d830ade21b9");
    private static final String EXPLOSION_ENABLED_NAME = "tcc.careful_heart.explosion_enabled";
    
    
    public CarefulHeart(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加发射器伤害、爆炸伤害和爆炸范围属性加成
        if (slotContext.entity() instanceof Player player) {
            applyHeartEffects(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的发射器伤害、爆炸伤害和爆炸范围属性加成
        if (slotContext.entity() instanceof Player player) {
            removeHeartEffects(player);
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
     * 应用心海效果
     * 提升发射器伤害、爆炸伤害和爆炸范围
     */
    private void applyHeartEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 应用发射器伤害提升
        var launcherDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_launcher")
            )
        );
        
        if (launcherDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            launcherDamageAttribute.removeModifier(LAUNCHER_DAMAGE_UUID);
            
            // 获取配置中的发射器伤害加成值
            double launcherDamageBoost = TaczCuriosConfig.COMMON.carefulHeartLauncherDamageBoost.get();
            // 添加配置的发射器伤害加成
            var launcherDamageModifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                LAUNCHER_DAMAGE_UUID,
                LAUNCHER_DAMAGE_NAME,
                launcherDamageBoost,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION
            );
            launcherDamageAttribute.addPermanentModifier(launcherDamageModifier);
        }
        
        // 应用爆炸伤害提升
        var explosionDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "explosion_damage")
            )
        );
        
        if (explosionDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            explosionDamageAttribute.removeModifier(EXPLOSION_DAMAGE_UUID);
            
            // 获取配置中的爆炸伤害加成值
            double explosionDamageBoost = TaczCuriosConfig.COMMON.carefulHeartExplosionDamageBoost.get();
            // 添加配置的爆炸伤害加成
            var explosionDamageModifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                EXPLOSION_DAMAGE_UUID,
                EXPLOSION_DAMAGE_NAME,
                explosionDamageBoost,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION
            );
            explosionDamageAttribute.addPermanentModifier(explosionDamageModifier);
        }
        
        // 应用爆炸范围提升
        var explosionRadiusAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "explosion_radius")
            )
        );
        
        if (explosionRadiusAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            explosionRadiusAttribute.removeModifier(EXPLOSION_RADIUS_UUID);
            
            // 获取配置中的爆炸范围加成值
            double explosionRadiusBoost = TaczCuriosConfig.COMMON.carefulHeartExplosionRadiusBoost.get();
            // 添加配置的爆炸范围加成
            var explosionRadiusModifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                EXPLOSION_RADIUS_UUID,
                EXPLOSION_RADIUS_NAME,
                explosionRadiusBoost,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION
            );
            explosionRadiusAttribute.addPermanentModifier(explosionRadiusModifier);
        }
        
        // 应用爆炸启用属性
        var explosionEnabledAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "explosion_enabled")
            )
        );
        
        if (explosionEnabledAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            explosionEnabledAttribute.removeModifier(EXPLOSION_ENABLED_UUID);
            
            // 获取配置中的爆炸启用属性值
            double explosionEnabled = TaczCuriosConfig.COMMON.carefulHeartExplosionEnabled.get();
            // 添加配置的爆炸启用属性
            var explosionEnabledModifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                EXPLOSION_ENABLED_UUID,
                EXPLOSION_ENABLED_NAME,
                explosionEnabled,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION
            );
            explosionEnabledAttribute.addPermanentModifier(explosionEnabledModifier);
        }
    }
    
    /**
     * 移除心海效果
     */
    private void removeHeartEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 移除发射器伤害加成
        var launcherDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_launcher")
            )
        );
        
        if (launcherDamageAttribute != null) {
            launcherDamageAttribute.removeModifier(LAUNCHER_DAMAGE_UUID);
        }
        
        // 移除爆炸伤害加成
        var explosionDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "explosion_damage")
            )
        );
        
        if (explosionDamageAttribute != null) {
            explosionDamageAttribute.removeModifier(EXPLOSION_DAMAGE_UUID);
        }
        
        // 移除爆炸范围加成
        var explosionRadiusAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "explosion_radius")
            )
        );
        
        if (explosionRadiusAttribute != null) {
            explosionRadiusAttribute.removeModifier(EXPLOSION_RADIUS_UUID);
        }
        
        // 移除爆炸启用属性
        var explosionEnabledAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "explosion_enabled")
            )
        );
        
        if (explosionEnabledAttribute != null) {
            explosionEnabledAttribute.removeModifier(EXPLOSION_ENABLED_UUID);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof Player player) {
            applyHeartEffects(player);
        }
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.careful_heart.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double launcherDamageBoost = TaczCuriosConfig.COMMON.carefulHeartLauncherDamageBoost.get() * 100;
        double explosionDamageBoost = TaczCuriosConfig.COMMON.carefulHeartExplosionDamageBoost.get() * 100;
        double explosionRadiusBoost = TaczCuriosConfig.COMMON.carefulHeartExplosionRadiusBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.careful_heart.effect", 
                String.format("%.0f", launcherDamageBoost), String.format("%.0f", explosionDamageBoost), String.format("%.0f", explosionRadiusBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.legendary"));
    }
    
    /**
     * 获取装备槽位
     */
    public String getSlot() {
        return "tcc:tcc_slot";
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(Player player) {
        applyHeartEffects(player);
    }
}