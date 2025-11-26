package com.xlxyvergil.tcc.items;

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
 * 膛线 - 提升特定枪械165%伤害
 * 效果：特定枪械伤害+165%（加算）
 */
public class Rifling extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID[] DAMAGE_UUIDS = {
        UUID.fromString("62345678-1234-1234-1234-123456789abc"),
        UUID.fromString("62345678-1234-1234-1234-123456789abd"),
        UUID.fromString("62345678-1234-1234-1234-123456789abe"),
        UUID.fromString("62345678-1234-1234-1234-123456789abf"),
        UUID.fromString("62345678-1234-1234-1234-123456789ab0")
    };
    
    // 修饰符名称
    private static final String[] DAMAGE_NAMES = {
        "tcc.rifling.rifle_damage",
        "tcc.rifling.sniper_damage",
        "tcc.rifling.smg_damage",
        "tcc.rifling.lmg_damage",
        "tcc.rifling.launcher_damage"
    };
    
    // 效果参数
    private static final double DAMAGE_BOOST = 1.65;       // 165%特定枪械伤害提升（加算）
    
    public Rifling(Properties properties) {
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
            applyRiflingEffects(player);
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
            removeRiflingEffects(player);
        }
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * Rifling与MergedRifling互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查玩家是否已经装备了MergedRifling
        if (slotContext.entity() instanceof Player player) {
            ICuriosItemHandler curiosHandler = player.getCapability(top.theillusivec4.curios.api.CuriosCapability.INVENTORY).orElse(null);
            if (curiosHandler != null) {
                ICurioStacksHandler tccSlotHandler = curiosHandler.getCurios().get("tcc_slot");
                if (tccSlotHandler != null) {
                    for (int i = 0; i < tccSlotHandler.getSlots(); i++) {
                        ItemStack equippedStack = tccSlotHandler.getStacks().getStackInSlot(i);
                        if (equippedStack.getItem() instanceof MergedRifling) {
                            return false; // 如果已经装备了MergedRifling，则不能装备Rifling
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
     * 应用膛线效果
     * 提升特定枪械伤害（加算）
     */
    public void applyRiflingEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 特定枪械类型
        String[] gunTypes = {
            "bullet_gundamage_rifle",
            "bullet_gundamage_sniper",
            "bullet_gundamage_smg",
            "bullet_gundamage_lmg",
            "bullet_gundamage_launcher"
        };
        
        // 应用特定枪械伤害提升（加算）
        for (int i = 0; i < gunTypes.length; i++) {
            var gunDamageAttribute = attributes.getInstance(
                net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                    new net.minecraft.resources.ResourceLocation("taa", gunTypes[i])
                )
            );
            
            if (gunDamageAttribute != null) {
                // 检查是否已经存在相同的修饰符，如果存在则移除
                gunDamageAttribute.removeModifier(DAMAGE_UUIDS[i]);
                
                // 添加165%的特定枪械伤害加成（加算）
                var gunDamageModifier = new AttributeModifier(
                    DAMAGE_UUIDS[i],
                    DAMAGE_NAMES[i],
                    DAMAGE_BOOST,
                    AttributeModifier.Operation.ADDITION
                );
                gunDamageAttribute.addPermanentModifier(gunDamageModifier);
            }
        }
        // 不再主动调用缓存更新，由mod自主检测属性变更后触发
    }
    
    /**
     * 移除膛线效果
     */
    public void removeRiflingEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 特定枪械类型
        String[] gunTypes = {
            "bullet_gundamage_rifle",
            "bullet_gundamage_sniper",
            "bullet_gundamage_smg",
            "bullet_gundamage_lmg",
            "bullet_gundamage_launcher"
        };
        
        // 移除特定枪械伤害加成
        for (int i = 0; i < gunTypes.length; i++) {
            var gunDamageAttribute = attributes.getInstance(
                net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                    new net.minecraft.resources.ResourceLocation("taa", gunTypes[i])
                )
            );
            
            if (gunDamageAttribute != null) {
                gunDamageAttribute.removeModifier(DAMAGE_UUIDS[i]);
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
            applyRiflingEffects(player);
        }
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.rifling.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.rifling.effect")
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
        applyRiflingEffects(player);
    }
}