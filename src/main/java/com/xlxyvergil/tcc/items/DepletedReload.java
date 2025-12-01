package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 耗竭装填 - 降低弹匣容量，提升装填速度
 * 效果：降低60%弹匣容量，提升48%装填速度，仅对狙击枪生效
 */
public class DepletedReload extends ItemBaseCurio {

    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID MAGAZINE_UUID = UUID.fromString("17c2b815-8561-4354-a395-d03c4ac4e029");
    private static final UUID RELOAD_UUID = UUID.fromString("68cef118-0938-46f4-881f-698e812abf70");

    // 修饰符名称
    private static final String MAGAZINE_NAME = "tcc.depleted_reload.magazine_capacity";
    private static final String RELOAD_NAME = "tcc.depleted_reload.reload_speed";

    public DepletedReload(Properties properties) {
        super(properties
            .stacksTo(1)
            .rarity(Rarity.RARE));
    }

    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);

        // 给玩家添加属性修改
        if (slotContext.entity() instanceof Player player) {
            applyDepletedReloadEffects(player);
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
            removeDepletedReloadEffects(player);
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
     * 降低弹匣容量，提升装填速度
     */
    public void applyDepletedReloadEffects(Player player) {
        var attributes = player.getAttributes();

        // 弹匣容量属性
        var magazineAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "magazine_capacity")
            )
        );

        // 装填速度属性
        var reloadAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "reload_time")
            )
        );

        // 移除已存在的修饰符
        if (magazineAttribute != null) {
            magazineAttribute.removeModifier(MAGAZINE_UUID);
        }
        if (reloadAttribute != null) {
            reloadAttribute.removeModifier(RELOAD_UUID);
        }

        // 检查玩家是否持有支持的枪械类型，只有持有支持的枪械时才应用加成
        if (GunTypeChecker.isHoldingSniper(player)) {
            // 获取配置中的弹匣容量减少值
            double magazinePenalty = TaczCuriosConfig.COMMON.depletedReloadMagazineCapacityPenalty.get();
            // 添加配置的弹匣容量减少（加算）
            if (magazineAttribute != null) {
                var magazineModifier = new AttributeModifier(
                    MAGAZINE_UUID,
                    MAGAZINE_NAME,
                    magazinePenalty,
                    AttributeModifier.Operation.ADDITION
                );
                magazineAttribute.addPermanentModifier(magazineModifier);
            }

            // 获取配置中的装填速度加成值
            double reloadBoost = TaczCuriosConfig.COMMON.depletedReloadReloadSpeedBoost.get();
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
     * 移除耗竭装填效果
     */
    public void removeDepletedReloadEffects(Player player) {
        var attributes = player.getAttributes();

        // 弹匣容量属性
        var magazineAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "magazine_capacity")
            )
        );

        // 装填速度属性
        var reloadAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "reload_time")
            )
        );

        if (magazineAttribute != null) {
            magazineAttribute.removeModifier(MAGAZINE_UUID);
        }
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
        if (slotContext.entity() instanceof Player player) {
            applyDepletedReloadEffects(player);
        }
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.depleted_reload.desc")
            .withStyle(ChatFormatting.GRAY));

        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        double magazinePenalty = Math.abs(TaczCuriosConfig.COMMON.depletedReloadMagazineCapacityPenalty.get() * 100);
        double reloadBoost = TaczCuriosConfig.COMMON.depletedReloadReloadSpeedBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.depleted_reload.effect", 
                                          String.format("%.0f", magazinePenalty), 
                                          String.format("%.0f", reloadBoost))
            .withStyle(ChatFormatting.LIGHT_PURPLE));

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
        applyDepletedReloadEffects(player);
    }
}