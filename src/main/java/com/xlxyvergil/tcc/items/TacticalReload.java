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
 * 战术上膛 - 提升装填速度
 * 效果：提升装填速度（加算）
 */
public class TacticalReload extends ItemBaseCurio {

    // 属性修饰符UUID - 用于唯一标识修饰符
    private static final UUID RELOAD_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    // 修饰符名称
    private static final String RELOAD_NAME = "tcc.tactical_reload.reload";

    public TacticalReload(Properties properties) {
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
            applyTacticalReloadEffects(player);
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
            removeTacticalReloadEffects(player);
        }
    }

    /**
     * 检查是否可以装备到指定插槽
     * TacticalReload与TacticalReloadPrime互斥，不能同时装备
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在TCC饰品槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查是否已经装备了TacticalReloadPrime
        return !top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem() instanceof TacticalReloadPrime))
            .orElse(java.util.Optional.empty()).isPresent();
    }

    /**
     * 当物品在Curios插槽中时被右键点击
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    /**
     * 应用战术上膛效果
     * 提升装填速度（加算）
     */
    public void applyTacticalReloadEffects(Player player) {
        var attributes = player.getAttributes();

        // 获取装填速度属性
        var reloadAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "reload_time")
            )
        );

        if (reloadAttribute != null) {
            // 移除已存在的修饰符
            reloadAttribute.removeModifier(RELOAD_UUID);

            // 检查玩家是否持有霰弹枪，只有持有霰弹枪时才应用加成
            if (GunTypeChecker.isHoldingShotgun(player)) {
                // 获取配置中的装填速度加成值
                double reloadBoost = TaczCuriosConfig.COMMON.tacticalReloadSpeedBoost.get();
                // 添加装填速度加成（加算）
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
     * 移除战术上膛效果
     */
    public void removeTacticalReloadEffects(Player player) {
        var attributes = player.getAttributes();

        // 获取装填速度属性
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
        if (slotContext.entity() instanceof Player player) {
            applyTacticalReloadEffects(player);
        }
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.tactical_reload.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));

        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        double reloadBoost = TaczCuriosConfig.COMMON.tacticalReloadSpeedBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.tactical_reload.effect", String.format("%.0f", reloadBoost))
            .withStyle(net.minecraft.ChatFormatting.BLUE));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(Player player) {
        applyTacticalReloadEffects(player);
    }
}