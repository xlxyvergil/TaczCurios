package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotContext;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 持续火力 - 提升48%装填速度
 * 效果：提升48%装填速度（加算）
 */
public class SustainedFire extends ItemBaseCurio {

    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID RELOAD_UUID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    // 修饰符名称
    private static final String RELOAD_NAME = "tcc.sustained_fire.reload";

    // 效果参数
    private static final double RELOAD_BOOST = 0.48;       // 48%装填速度提升（加算）

    public SustainedFire(Properties properties) {
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
            applySustainedFireEffects(player);
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
            removeSustainedFireEffects(player);
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
     * 应用持续火力效果
     * 提升装填速度（加算）
     */
    public void applySustainedFireEffects(Player player) {
        var attributes = player.getAttributes();

        // 获取装填速度属性
        var reloadAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "reload_speed")
            )
        );

        if (reloadAttribute != null) {
            // 移除已存在的修饰符
            reloadAttribute.removeModifier(RELOAD_UUID);

            // 检查玩家是否持有手枪，只有持有手枪时才应用加成
            if (isHoldingPistol(player)) {
                // 添加48%的装填速度加成（加算）
                var reloadModifier = new AttributeModifier(
                    RELOAD_UUID,
                    RELOAD_NAME,
                    RELOAD_BOOST,
                    AttributeModifier.Operation.ADDITION
                );
                reloadAttribute.addPermanentModifier(reloadModifier);
            }
        }
    }

    /**
     * 移除持续火力效果
     */
    public void removeSustainedFireEffects(Player player) {
        var attributes = player.getAttributes();

        // 获取装填速度属性
        var reloadAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "reload_speed")
            )
        );

        if (reloadAttribute != null) {
            reloadAttribute.removeModifier(RELOAD_UUID);
        }
    }

    /**
     * 检查玩家是否持有手枪
     */
    private boolean isHoldingPistol(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHandItem);

        if (iGun != null) {
            // 获取枪械ID
            ResourceLocation gunId = iGun.getGunId(mainHandItem);

            // 通过TimelessAPI获取枪械索引
            return TimelessAPI.getCommonGunIndex(gunId)
                .map(CommonGunIndex::getType)
                .map(type -> type.equals("pistol"))
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
            applySustainedFireEffects(player);
        }
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.sustained_fire.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));

        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.sustained_fire.effect")
            .withStyle(net.minecraft.ChatFormatting.GREEN));

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
        applySustainedFireEffects(player);
    }
}