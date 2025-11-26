package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 恶性扩散 - 提升165%霰弹枪伤害，提高55%不精准度
 * 效果：提升165%霰弹枪伤害（加算），提高55%不精准度（乘算）
 */
public class MalignantSpread extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID DAMAGE_UUID = UUID.fromString("92345678-1234-1234-1234-123456789abc");
    private static final UUID INACCURACY_UUID = UUID.fromString("92345678-1234-1234-1234-123456789abd");
    
    // 修饰符名称
    private static final String DAMAGE_NAME = "tcc.malignant_spread.damage";
    private static final String INACCURACY_NAME = "tcc.malignant_spread.inaccuracy";
    
    // 效果参数
    private static final double DAMAGE_BOOST = 1.65;       // 165%霰弹枪伤害提升（加算）
    private static final double INACCURACY_BOOST = 0.55;   // 55%不精准度提升（乘算）
    
    public MalignantSpread(Properties properties) {
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
            applyMalignantSpreadEffects(player);
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
            removeMalignantSpreadEffects(player);
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
     * 应用恶性扩散效果
     * 提升霰弹枪伤害（加算）和不精准度（乘算）
     */
    public void applyMalignantSpreadEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 获取霰弹枪伤害属性
        var shotgunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_shotgun")
            )
        );
        
        // 移除已存在的修饰符
        if (shotgunDamageAttribute != null) {
            shotgunDamageAttribute.removeModifier(DAMAGE_UUID);
        }
        
        // 直接应用霰弹枪伤害加成，无需检查是否手持霰弹枪
        if (shotgunDamageAttribute != null) {
            // 添加165%的霰弹枪伤害加成（加算）
            var shotgunDamageModifier = new AttributeModifier(
                DAMAGE_UUID,
                DAMAGE_NAME,
                DAMAGE_BOOST,
                AttributeModifier.Operation.ADDITION
            );
            shotgunDamageAttribute.addPermanentModifier(shotgunDamageModifier);
        }
        
        // 应用不精准度提升（乘算）
        var inaccuracyAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "inaccuracy")
            )
        );
        
        if (inaccuracyAttribute != null) {
            // 移除已存在的修饰符
            inaccuracyAttribute.removeModifier(INACCURACY_UUID);
            
            // 检查玩家是否持有霰弹枪，只有持有霰弹枪时才应用不精准度加成
            if (isHoldingShotgun(player)) {
                // 添加55%的不精准度加成（乘算）
                var inaccuracyModifier = new AttributeModifier(
                    INACCURACY_UUID,
                    INACCURACY_NAME,
                    INACCURACY_BOOST,
                    AttributeModifier.Operation.MULTIPLY_BASE
                );
                inaccuracyAttribute.addPermanentModifier(inaccuracyModifier);
            }
        }
        // 不再主动调用缓存更新，由mod自主检测属性变更后触发
    }
    
    /**
     * 移除恶性扩散效果
     */
    public void removeMalignantSpreadEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 获取霰弹枪伤害属性
        var shotgunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_shotgun")
            )
        );
        
        if (shotgunDamageAttribute != null) {
            shotgunDamageAttribute.removeModifier(DAMAGE_UUID);
        }
        
        // 移除不精准度加成
        var inaccuracyAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "inaccuracy")
            )
        );
        
        if (inaccuracyAttribute != null) {
            inaccuracyAttribute.removeModifier(INACCURACY_UUID);
        }
    }
    
    /**
     * 检查玩家是否持有霰弹枪
     */
    private boolean isHoldingShotgun(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHandItem);
        
        if (iGun != null) {
            // 获取枪械ID
            net.minecraft.resources.ResourceLocation gunId = iGun.getGunId(mainHandItem);
            
            // 通过TimelessAPI获取枪械索引
            return TimelessAPI.getCommonGunIndex(gunId)
                .map(CommonGunIndex::getType)
                .map(type -> type.equals("shotgun"))
                .orElse(false);
        }
        
        return false;
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof Player player) {
            applyMalignantSpreadEffects(player);
        }
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.malignant_spread.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.malignant_spread.effect")
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
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(Player player) {
        applyMalignantSpreadEffects(player);
    }
}