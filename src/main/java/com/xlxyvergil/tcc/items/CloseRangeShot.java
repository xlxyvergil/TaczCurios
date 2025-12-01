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
 * 抵近射击 - 提升霰弹枪90%伤害
 * 效果：霰弹枪伤害+90%（加算）
 */
public class CloseRangeShot extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这个修饰符
    private static final UUID DAMAGE_UUID = UUID.fromString("72345678-1234-1234-1234-123456789abc");
    
    // 修饰符名称
    private static final String DAMAGE_NAME = "tcc.close_range_shot.shotgun_damage";
    
    // 效果参数
    // private static final double DAMAGE_BOOST = 0.90;       // 90%霰弹枪伤害提升（加算） - 现在从配置文件读取
    
    public CloseRangeShot(Properties properties) {
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
     * CloseRangeShot与CloseCombatPrime互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查玩家是否已经装备了CloseCombatPrime
        if (slotContext.entity() instanceof Player player) {
            ICuriosItemHandler curiosHandler = player.getCapability(top.theillusivec4.curios.api.CuriosCapability.INVENTORY).orElse(null);
            if (curiosHandler != null) {
                ICurioStacksHandler tccSlotHandler = curiosHandler.getCurios().get("tcc_slot");
                if (tccSlotHandler != null) {
                    for (int i = 0; i < tccSlotHandler.getSlots(); i++) {
                        ItemStack equippedStack = tccSlotHandler.getStacks().getStackInSlot(i);
                        if (equippedStack.getItem() instanceof CloseCombatPrime) {
                            return false; // 如果已经装备了CloseCombatPrime，则不能装备CloseRangeShot
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
     * 应用效果
     * 提升霰弹枪伤害（加算）
     */
    private void applyEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 霰弹枪伤害属性
        var shotgunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_shotgun")
            )
        );
        
        if (shotgunDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            shotgunDamageAttribute.removeModifier(DAMAGE_UUID);
            
            // 从配置文件获取伤害加成值
            double damageBoost = TaczCuriosConfig.COMMON.closeRangeShotDamageBoost.get();
            
            // 添加配置的霰弹枪伤害加成（加算）
            var damageModifier = new AttributeModifier(
                DAMAGE_UUID,
                DAMAGE_NAME,
                damageBoost,
                AttributeModifier.Operation.ADDITION
            );
            shotgunDamageAttribute.addPermanentModifier(damageModifier);
        }
    }
    
    /**
     * 移除效果
     */
    private void removeEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 霰弹枪伤害属性
        var shotgunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage_shotgun")
            )
        );
        
        if (shotgunDamageAttribute != null) {
            // 移除修饰符
            shotgunDamageAttribute.removeModifier(DAMAGE_UUID);
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
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.close_range_shot.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        double damageBoost = TaczCuriosConfig.COMMON.closeRangeShotDamageBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.close_range_shot.effect", String.format("%.0f", damageBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§9罕见")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(Player player) {
        applyEffects(player);
    }
}