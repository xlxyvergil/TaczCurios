package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 耗竭装填 - 降低弹匣容量，提升装填速度
 * 效果：降低60%弹匣容量，提升48%装填速度，仅对狙击枪生效
 */
public class DepletedReload extends BaseCurioItem {

    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID MAGAZINE_UUID = UUID.fromString("17c2b815-8561-4354-a395-d03c4ac4e029");
    private static final UUID RELOAD_UUID = UUID.fromString("68cef118-0938-46f4-881f-698e812abf70");

    // 修饰符名称
    private static final String MAGAZINE_NAME = "tcc.depleted_reload.magazine_capacity";
    private static final String RELOAD_NAME = "tcc.depleted_reload.reload_speed";

    public DepletedReload(Properties properties) {
        super(properties);
    }

    /**
     * 应用耗竭装填效果
     * 降低弹匣容量，提升装填速度
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 检查生物是否持有支持的枪械类型，只有持有支持的枪械时才应用加成
        if (GunTypeChecker.isHoldingSniper(livingEntity)) {
            double magazinePenalty = TaczCuriosConfig.COMMON.depletedReloadMagazineCapacityPenalty.get();
            double reloadBoost = TaczCuriosConfig.COMMON.depletedReloadReloadSpeedBoost.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazinePenalty, MAGAZINE_UUID, MAGAZINE_NAME, AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.RELOAD_TIME, reloadBoost, RELOAD_UUID, RELOAD_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

    /**
     * 移除耗竭装填效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, MAGAZINE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.RELOAD_TIME, RELOAD_UUID);
    }


    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);



        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        double magazinePenalty = Math.abs(TaczCuriosConfig.COMMON.depletedReloadMagazineCapacityPenalty.get() * 100);
        double reloadBoost = TaczCuriosConfig.COMMON.depletedReloadReloadSpeedBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.depleted_reload.effect", 
                                          String.format("%+.0f", magazinePenalty), 
                                          String.format("%+.0f", reloadBoost))
            .withStyle(ChatFormatting.GOLD));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));
    }

    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}