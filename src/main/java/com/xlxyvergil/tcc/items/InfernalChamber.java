package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 地狱弹膛 - 提升弹头数量
 * 效果：提升弹头数量（加算）
 */
public class InfernalChamber extends ItemBaseCurio {

    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID BULLET_COUNT_UUID = UUID.fromString("50d58834-a161-4b25-a13d-e56a375cd970");

    // 修饰符名称
    private static final String BULLET_COUNT_NAME = "tcc.infernal_chamber.bullet_count";

    public InfernalChamber(Properties properties) {
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
            applyInfernalChamberEffects(player);
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
            removeInfernalChamberEffects(player);
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
     * 应用地狱弹膛效果
     * 提升弹头数量（加算）
     */
    public void applyInfernalChamberEffects(Player player) {
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

            // 检查玩家是否持有霰弹枪，只有持有霰弹枪时才应用加成
            if (GunTypeChecker.isHoldingShotgun(player)) {
                // 获取配置中的弹头数量加成值
                double bulletCountBoost = TaczCuriosConfig.COMMON.infernalChamberBulletCountBoost.get();
                // 添加配置中的弹头数量加成（加算）
                var bulletCountModifier = new AttributeModifier(
                    BULLET_COUNT_UUID,
                    BULLET_COUNT_NAME,
                    bulletCountBoost,
                    AttributeModifier.Operation.ADDITION
                );
                bulletCountAttribute.addPermanentModifier(bulletCountModifier);
            }
        }
    }

    /**
     * 移除地狱弹膛效果
     */
    public void removeInfernalChamberEffects(Player player) {
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
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        if (slotContext.entity() instanceof Player player) {
            applyInfernalChamberEffects(player);
        }
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.infernal_chamber.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));

        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        double bulletCountBoost = TaczCuriosConfig.COMMON.infernalChamberBulletCountBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.infernal_chamber.effect", String.format("%.0f", bulletCountBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(Player player) {
        applyInfernalChamberEffects(player);
    }
}