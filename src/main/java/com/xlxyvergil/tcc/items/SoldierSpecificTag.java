package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.network.chat.Component;
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
 * 士兵特定挂牌 - 提供通用枪械伤害加成（乘法）
 * 效果：为玩家提供通用枪械伤害加成（乘法）
 */
public class SoldierSpecificTag extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰符
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("bbd020e4-a079-46e1-b236-3eea2c13da4f");
    
    // 修饰符名称
    private static final String GUN_DAMAGE_NAME = "tcc.soldier_specific_tag.gun_damage";
    
    public SoldierSpecificTag(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加枪械伤害属性加成
        if (slotContext.entity() instanceof Player player) {
            applyGunDamageBonus(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的枪械伤害属性加成
        if (slotContext.entity() instanceof Player player) {
            removeGunDamageBonus(player);
        }
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * SoldierSpecificTag与SoldierBasicTag互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查玩家是否已经装备了SoldierBasicTag
        if (slotContext.entity() instanceof Player player) {
            ICuriosItemHandler curiosHandler = player.getCapability(top.theillusivec4.curios.api.CuriosCapability.INVENTORY).orElse(null);
            if (curiosHandler != null) {
                ICurioStacksHandler tccSlotHandler = curiosHandler.getCurios().get("tcc_slot");
                if (tccSlotHandler != null) {
                    for (int i = 0; i < tccSlotHandler.getSlots(); i++) {
                        ItemStack equippedStack = tccSlotHandler.getStacks().getStackInSlot(i);
                        if (equippedStack.getItem() instanceof SoldierBasicTag) {
                            return false; // 如果已经装备了SoldierBasicTag，则不能装备SoldierSpecificTag
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
     * 应用枪械伤害加成
     * 给玩家添加配置中的通用枪械伤害加成（乘法）
     */
    private void applyGunDamageBonus(Player player) {
        // 使用TaczAttributeAdd中的通用枪械伤害属性
        var attributes = player.getAttributes();
        var gunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage")
            )
        );
        
        if (gunDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符
            if (gunDamageAttribute.getModifier(GUN_DAMAGE_UUID) == null) {
                // 获取配置中的伤害加成值
                double damageBoost = TaczCuriosConfig.COMMON.soldierSpecificTagDamageBoost.get();
                // 添加配置中的伤害加成，使用乘法操作
                AttributeModifier modifier = new AttributeModifier(
                    GUN_DAMAGE_UUID,
                    GUN_DAMAGE_NAME,
                    damageBoost,
                    AttributeModifier.Operation.MULTIPLY_BASE
                );
                gunDamageAttribute.addPermanentModifier(modifier);
            }
        }
    }
    
    /**
     * 移除枪械伤害加成
     */
    private void removeGunDamageBonus(Player player) {
        var attributes = player.getAttributes();
        var gunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage")
            )
        );
        
        if (gunDamageAttribute != null) {
            // 移除之前添加的修饰符
            gunDamageAttribute.removeModifier(GUN_DAMAGE_UUID);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof Player player) {
            applyGunDamageBonus(player);
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
        
        // 添加装备效果
        double damageBoost = TaczCuriosConfig.COMMON.soldierSpecificTagDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.soldier_specific_tag.effect", String.format("%.0f", damageBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.legendary"));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(Player player) {
        applyGunDamageBonus(player);
    }
}