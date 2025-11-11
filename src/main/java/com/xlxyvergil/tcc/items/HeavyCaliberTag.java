package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import com.tacz.guns.api.TimelessAPI;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 重口径 - 提升165%步枪、狙击枪、冲锋枪、机枪、发射器伤害，增加55%不精准度
 * 效果：提升165%步枪、狙击枪、冲锋枪、机枪、发射器伤害（加算），增加55%不精准度（乘算）
 */
public class HeavyCaliberTag extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID[] DAMAGE_UUIDS = {
        UUID.fromString("52345678-1234-1234-1234-123456789abc"),
        UUID.fromString("52345678-1234-1234-1234-123456789abd"),
        UUID.fromString("52345678-1234-1234-1234-123456789abe"),
        UUID.fromString("52345678-1234-1234-1234-123456789abf"),
        UUID.fromString("52345678-1234-1234-1234-123456789ab0")
    };
    
    // 修饰符名称
    private static final String[] DAMAGE_NAMES = {
        "tcc.heavy_caliber.rifle_damage",
        "tcc.heavy_caliber.sniper_damage",
        "tcc.heavy_caliber.smg_damage",
        "tcc.heavy_caliber.lmg_damage",
        "tcc.heavy_caliber.launcher_damage"
    };
    
    // 效果参数
    private static final double DAMAGE_BOOST = 1.65;       // 165%特定枪械伤害提升（加算）
    private static final double INACCURACY_BOOST = 0.55;   // 55%不精准度提升（乘算）
    
    public HeavyCaliberTag(Properties properties) {
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
            applyHeavyCaliberEffects(player);
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
            removeHeavyCaliberEffects(player);
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
     * 应用重口径效果
     * 提升特定枪械伤害（加算）和不精准度（乘算）
     */
    private void applyHeavyCaliberEffects(Player player) {
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
        
        // 应用不精准度提升（乘算）
        var inaccuracyAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_inaccuracy")
            )
        );
        
        if (inaccuracyAttribute != null) {
            // 移除已存在的修饰符
            inaccuracyAttribute.removeModifier(DAMAGE_UUIDS[0]);
            
            // 添加55%的不精准度加成（乘算）
            var inaccuracyModifier = new AttributeModifier(
                DAMAGE_UUIDS[0],
                "tcc.heavy_caliber.inaccuracy",
                INACCURACY_BOOST,
                AttributeModifier.Operation.MULTIPLY_BASE
            );
            inaccuracyAttribute.addPermanentModifier(inaccuracyModifier);
        }
    }
    
    /**
     * 移除重口径效果
     */
    private void removeHeavyCaliberEffects(Player player) {
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
        
        // 移除不精准度加成
        var inaccuracyAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_inaccuracy")
            )
        );
        
        if (inaccuracyAttribute != null) {
            inaccuracyAttribute.removeModifier(DAMAGE_UUIDS[0]);
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof Player player) {
            applyHeavyCaliberEffects(player);
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
                    "§6重口径已装备 - 提升165%步枪、狙击枪、冲锋枪、机枪、发射器伤害（加算），增加55%不精准度（乘算）"
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
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.heavy_caliber_tag.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§6稀有")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}