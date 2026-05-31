package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 弹匣增幅 - 提升弹匣容量
 * 效果：提0%弹匣容量，仅对步枪、狙击枪、冲锋枪、机枪、发射器生效
 */
public class MagazineBoost extends BaseCurioItem {

    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID MAGAZINE_UUID = UUID.fromString("1b4da84f-2d2a-44e4-bed2-986811a6d6ea");

    // 修饰符名
    private static final String MAGAZINE_NAME = "tcc.magazine_boost.magazine_capacity";

    public MagazineBoost(Properties properties) {
        super(properties
            .stacksTo(1));
    }

    /**
     * 应用弹匣增幅效果
     * 提升弹匣容量
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        // 检查实体是否持有支持的枪械类型，只有持有支持的枪械时才应用加成
        if (GunTypeChecker.isHoldingDmgBoostGunType(livingEntity)) {
            double magazineBoost = TaczCuriosConfig.COMMON.magazineBoostCapacityBoost.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazineBoost, MAGAZINE_UUID, MAGAZINE_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

    /**
     * 移除弹匣增幅效果
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, MAGAZINE_UUID);
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
        double magazineBoost = TaczCuriosConfig.COMMON.magazineBoostCapacityBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.magazine_boost.effect", String.format("%+.0f", magazineBoost))
            .withStyle(ChatFormatting.BLUE));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.common"));
    }
    
    /**
     * 当实体切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}