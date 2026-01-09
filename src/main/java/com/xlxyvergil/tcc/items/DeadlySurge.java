package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 致命洪流 - 提升60%射速和60%弹头数量
 * 效果：提升60%射速（加算），提升60%弹头数量（加算）
 */
public class DeadlySurge extends ItemBaseCurio {

    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID ROUNDS_PER_MINUTE_UUID = UUID.fromString("d8e4852c-2b0c-4a77-a9b3-a2a84683ae93");
    private static final UUID BULLET_COUNT_UUID = UUID.fromString("b00e1320-1674-4bdb-8456-6fe4b80791fc");

    // 修饰符名称
    private static final String ROUNDS_PER_MINUTE_NAME = "tcc.deadly_surge.rounds_per_minute";
    private static final String BULLET_COUNT_NAME = "tcc.deadly_surge.bullet_count";


    public DeadlySurge(Properties properties) {
        super(properties);
    }

    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);

        // 给生物添加属性修改
        applyDeadlySurgeEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);

        // 移除生物的属性修改
        removeDeadlySurgeEffects((LivingEntity) slotContext.entity());
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
     * 应用致命洪流效果
     * 提升射速（加算）和弹头数量（加算）
     */
    public void applyDeadlySurgeEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();

        // 获取射速属性
        var rpmAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "rounds_per_minute")
            )
        );

        // 移除已存在的修饰符
        if (rpmAttribute != null) {
            rpmAttribute.removeModifier(ROUNDS_PER_MINUTE_UUID);
        }

        // 获取弹头数量属性
        var bulletCountAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_count")
            )
        );

        if (bulletCountAttribute != null) {
            bulletCountAttribute.removeModifier(BULLET_COUNT_UUID);
        }

        // 检查生物是否持有手枪，只有持有手枪时才应用加成
        if (GunTypeChecker.isHoldingPistol(livingEntity)) {
            // 应用射速加成
            if (rpmAttribute != null) {
                // 获取配置中的射速加成值
                double roundsPerMinuteBoost = TaczCuriosConfig.COMMON.deadlySurgeFireRateBoost.get();
                // 添加配置的射速加成（加算）
                var roundsPerMinuteModifier = new AttributeModifier(
                    ROUNDS_PER_MINUTE_UUID,
                    ROUNDS_PER_MINUTE_NAME,
                    roundsPerMinuteBoost,
                    AttributeModifier.Operation.ADDITION
                );
                rpmAttribute.addPermanentModifier(roundsPerMinuteModifier);
            }

            // 应用弹头数量加成
            if (bulletCountAttribute != null) {
                // 获取配置中的弹头数量加成值
                double bulletCountBoost = TaczCuriosConfig.COMMON.deadlySurgeBulletCountBoost.get();
                // 添加配置的弹头数量加成（加算）
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
     * 移除致命洪流效果
     */
    public void removeDeadlySurgeEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();

        // 获取射速属性
        var roundsPerMinuteAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "rounds_per_minute")
            )
        );

        if (roundsPerMinuteAttribute != null) {
            roundsPerMinuteAttribute.removeModifier(ROUNDS_PER_MINUTE_UUID);
        }

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
        applyDeadlySurgeEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.deadly_surge.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));

        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        double roundsPerMinuteBoost = TaczCuriosConfig.COMMON.deadlySurgeFireRateBoost.get() * 100;
        double bulletCountBoost = TaczCuriosConfig.COMMON.deadlySurgeBulletCountBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.deadly_surge.effect", String.format("%.0f", roundsPerMinuteBoost), String.format("%.0f", bulletCountBoost))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyDeadlySurgeEffects(livingEntity);
    }
}