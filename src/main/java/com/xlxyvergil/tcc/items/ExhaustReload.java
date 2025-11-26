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
import java.util.Set;
import java.util.UUID;

/**
 * 耗竭装填 - +30%装填速度
 * 效果：提升30%装填速度（乘算），仅对步枪、狙击枪、冲锋枪、机枪、发射器生效
 */
public class ExhaustReload extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID RELOAD_UUID = UUID.fromString("12345678-1234-1234-1234-123456789ab1");
    
    // 修饰符名称
    private static final String RELOAD_NAME = "tcc.exhaust_reload.reload_speed";
    
    // 支持的枪械类型
    private static final Set<String> VALID_GUN_TYPES = Set.of("rifle", "sniper", "smg", "lmg", "launcher");
    
    // 效果参数
    private static final double RELOAD_BOOST = 0.30;       // 30%装填速度提升（乘算）
    
    public ExhaustReload(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加装填速度属性修改
        if (slotContext.entity() instanceof Player player) {
            applyExhaustReloadEffects(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的装填速度属性修改
        if (slotContext.entity() instanceof Player player) {
            removeExhaustReloadEffects(player);
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
     * 应用耗竭装填效果
     * 提升装填速度（乘算）
     */
    public void applyExhaustReloadEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 装填速度属性（不带枪械类型）
        var reloadAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "reload_speed")
            )
        );
        
        if (reloadAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            reloadAttribute.removeModifier(RELOAD_UUID);
            
            // 检查玩家是否持有支持的枪械类型，只有持有支持的枪械时才应用加成
            if (isHoldingValidGunType(player)) {
                // 添加30%的装填速度加成（乘算）
                var reloadModifier = new AttributeModifier(
                    RELOAD_UUID,
                    RELOAD_NAME,
                    RELOAD_BOOST,
                    AttributeModifier.Operation.MULTIPLY_BASE
                );
                reloadAttribute.addPermanentModifier(reloadModifier);
            }
        }
        // 不再主动调用缓存更新，由mod自主检测属性变更后触发
    }
    
    /**
     * 移除耗竭装填效果
     */
    public void removeExhaustReloadEffects(Player player) {
        var attributes = player.getAttributes();
        
        // 装填速度属性（不带枪械类型）
        var reloadAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "reload_speed")
            )
        );
        
        if (reloadAttribute != null) {
            reloadAttribute.removeModifier(RELOAD_UUID);
        }
    }
    
    /**
     * 检查玩家是否持有有效的枪械类型
     */
    private boolean isHoldingValidGunType(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHandItem);
        
        if (iGun != null) {
            // 获取枪械ID
            net.minecraft.resources.ResourceLocation gunId = iGun.getGunId(mainHandItem);
            
            // 通过TimelessAPI获取枪械索引
            return TimelessAPI.getCommonGunIndex(gunId)
                .map(CommonGunIndex::getType)
                .map(VALID_GUN_TYPES::contains)
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
            applyExhaustReloadEffects(player);
        }
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.exhaust_reload.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.exhaust_reload.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§f常见")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(Player player) {
        applyExhaustReloadEffects(player);
    }
}