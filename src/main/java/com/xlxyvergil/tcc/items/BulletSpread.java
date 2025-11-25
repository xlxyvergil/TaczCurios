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
 * 弹头扩散 - 提升120%弹头数量
 * 效果：提升120%弹头数量（加算）
 */
public class BulletSpread extends ItemBaseCurio {

    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID BULLET_COUNT_UUID = UUID.fromString("77777777-7777-7777-7777-777777777777");

    // 修饰符名称
    private static final String BULLET_COUNT_NAME = "tcc.bullet_spread.bullet_count";

    // 效果参数
    private static final double BULLET_COUNT_BOOST = 1.20;       // 120%弹头数量提升（加算）

    public BulletSpread(Properties properties) {
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
            applyBulletSpreadEffects(player);
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
            removeBulletSpreadEffects(player);
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
     * 应用弹头扩散效果
     * 提升弹头数量（加算）
     */
    public void applyBulletSpreadEffects(Player player) {
        var attributes = player.getAttributes();

        // 获取弹头数量属性
        var bulletCountAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "bullet_count")
            )
        );

        if (bulletCountAttribute != null) {
            // 移除已存在的修饰符
            bulletCountAttribute.removeModifier(BULLET_COUNT_UUID);

            // 检查玩家是否持有手枪，只有持有手枪时才应用加成
            if (isHoldingPistol(player)) {
                // 添加120%的弹头数量加成（加算）
                var bulletCountModifier = new AttributeModifier(
                    BULLET_COUNT_UUID,
                    BULLET_COUNT_NAME,
                    BULLET_COUNT_BOOST,
                    AttributeModifier.Operation.ADDITION
                );
                bulletCountAttribute.addPermanentModifier(bulletCountModifier);
            }
        }
    }

    /**
     * 移除弹头扩散效果
     */
    public void removeBulletSpreadEffects(Player player) {
        var attributes = player.getAttributes();

        // 获取弹头数量属性
        var bulletCountAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "bullet_count")
            )
        );

        if (bulletCountAttribute != null) {
            bulletCountAttribute.removeModifier(BULLET_COUNT_UUID);
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
            applyBulletSpreadEffects(player);
        }
    }

    /**
     * 当物品被装备时，显示提示信息
     */
    @Override
    public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            player.displayClientMessage(
                Component.literal(
                    "§6弹头扩散已装备 - 提升120%弹头数量（加算）"
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
        tooltip.add(Component.translatable("item.tcc.bullet_spread.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));

        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.bullet_spread.effect")
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