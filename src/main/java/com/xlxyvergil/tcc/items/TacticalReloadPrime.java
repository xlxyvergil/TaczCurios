package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 战术上膛Prime - 提升装填速度
 * 效果：提00%装填速度，仅对霰弹枪生效
 */
public class TacticalReloadPrime extends ItemBaseCurio {

    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID RELOAD_UUID = UUID.fromString("d2f96b0d-cb4b-4cef-a71c-19930ba0ebff");

    // 修饰符名
    private static final String RELOAD_NAME = "tcc.tactical_reload_prime.reload_speed";

    public TacticalReloadPrime(Properties properties) {
        super(properties
            .stacksTo(1)
            .rarity(Rarity.EPIC));
    }

    /**
     * 当饰品被装备时调
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);

        // 给实体添加属性修改
        applyTacticalReloadPrimeEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 当饰品被卸下时调
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);

        // 移除实体的属性修改
        removeTacticalReloadPrimeEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 检查是否可以装备到指定插槽
     * TacticalReloadPrime与TacticalReload互斥，不能同时装
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查是否已经装备了TacticalReload
        return !top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem() instanceof TacticalReload))
            .orElse(java.util.Optional.empty()).isPresent();
    }

    /**
     * 当物品在Curios插槽中时被右键点
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    /**
     * 应用战术上膛Prime效果
     * 提升装填速度
     */
    public void applyTacticalReloadPrimeEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();

        // 装填速度属
        var reloadAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "reload_time")
            )
        );

        // 移除已存在的修饰
        if (reloadAttribute != null) {
            reloadAttribute.removeModifier(RELOAD_UUID);
        }

        // 检查实体是否持有支持的枪械类型，只有持有支持的枪械时才应用加成
        if (GunTypeChecker.isHoldingShotgun(livingEntity)) {
            // 获取配置中的装填速度加成
            double reloadBoost = TaczCuriosConfig.COMMON.tacticalReloadPrimeReloadSpeedBoost.get();
            // 添加配置的装填速度加成（加算）
            if (reloadAttribute != null) {
                var reloadModifier = new AttributeModifier(
                    RELOAD_UUID,
                    RELOAD_NAME,
                    reloadBoost,
                    AttributeModifier.Operation.ADDITION
                );
                reloadAttribute.addPermanentModifier(reloadModifier);
            }
        }
    }

    /**
     * 移除战术上膛Prime效果
     */
    public void removeTacticalReloadPrimeEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();

        // 装填速度属
        var reloadAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "reload_time")
            )
        );

        if (reloadAttribute != null) {
            reloadAttribute.removeModifier(RELOAD_UUID);
        }
    }

    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效
        applyTacticalReloadPrimeEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.tactical_reload_prime.desc")
            .withStyle(ChatFormatting.GRAY));

        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        double reloadBoost = TaczCuriosConfig.COMMON.tacticalReloadPrimeReloadSpeedBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.tactical_reload_prime.effect", String.format("%.0f", reloadBoost))
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
    }

    /**
     * 当实体切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyTacticalReloadPrimeEffects(livingEntity);
    }
}

