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
 * 弹匣增幅 - 提升弹匣容量
 * 效果：提0%弹匣容量，仅对步枪、狙击枪、冲锋枪、机枪、发射器生效
 */
public class MagazineBoost extends ItemBaseCurio {

    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID MAGAZINE_UUID = UUID.fromString("1b4da84f-2d2a-44e4-bed2-986811a6d6ea");

    // 修饰符名
    private static final String MAGAZINE_NAME = "tcc.magazine_boost.magazine_capacity";

    public MagazineBoost(Properties properties) {
        super(properties
            .stacksTo(1)
            .rarity(Rarity.COMMON));
    }

    /**
     * 当饰品被装备时调
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);

        // 给实体添加属性修改
        applyMagazineBoostEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 当饰品被卸下时调
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);

        // 移除实体的属性修改
        removeMagazineBoostEffects((LivingEntity) slotContext.entity());
    }

    /**
     * 检查是否可以装备到指定插槽
     * MagazineBoost与MagazineBoostPrime互斥，不能同时装
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查是否已经装备了MagazineBoostPrime
        return !top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem() instanceof MagazineBoostPrime))
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
     * 应用弹匣增幅效果
     * 提升弹匣容量
     */
    public void applyMagazineBoostEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();

        // 弹匣容量属
        var magazineAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "magazine_capacity")
            )
        );

        // 移除已存在的修饰
        if (magazineAttribute != null) {
            magazineAttribute.removeModifier(MAGAZINE_UUID);
        }

        // 检查实体是否持有支持的枪械类型，只有持有支持的枪械时才应用加成
        if (GunTypeChecker.isHoldingDmgBoostGunType(livingEntity)) {
            // 获取配置中的弹匣容量加成
            double magazineBoost = TaczCuriosConfig.COMMON.magazineBoostCapacityBoost.get();
            // 添加配置的弹匣容量加成（加算
            if (magazineAttribute != null) {
                var magazineModifier = new AttributeModifier(
                    MAGAZINE_UUID,
                    MAGAZINE_NAME,
                    magazineBoost,
                    AttributeModifier.Operation.ADDITION
                );
                magazineAttribute.addPermanentModifier(magazineModifier);
            }
        }
    }

    /**
     * 移除弹匣增幅效果
     */
    public void removeMagazineBoostEffects(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();

        // 弹匣容量属
        var magazineAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "magazine_capacity")
            )
        );

        if (magazineAttribute != null) {
            magazineAttribute.removeModifier(MAGAZINE_UUID);
        }
    }

    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 属性修饰符是持久的，不需要每tick刷新
        // 效果在 onEquip/onUnequip/applyGunSwitchEffect 中管理
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.magazine_boost.desc")
            .withStyle(ChatFormatting.GRAY));

        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        double magazineBoost = TaczCuriosConfig.COMMON.magazineBoostCapacityBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.magazine_boost.effect", String.format("%.0f", magazineBoost))
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.common"));
    }

    /**
     * 当实体切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyMagazineBoostEffects(livingEntity);
    }
}

