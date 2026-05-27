package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 串联弹匣Prime - 提升弹匣容量
 * 效果：提升弹匣容量，仅对手枪生效
 */
public class TandemMagazinePrime extends BaseCurioItem {

    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID MAGAZINE_UUID = UUID.fromString("6f7eb1f1-c846-47cc-bbc7-73813bb57e30");

    // 修饰符名
    private static final String MAGAZINE_NAME = "tcc.tandem_magazine_prime.magazine_capacity";

    public TandemMagazinePrime(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingPistol(livingEntity)) {
            double magazineBoost = TaczCuriosConfig.COMMON.tandemMagazinePrimeCapacityBoost.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MAGAZINE_CAPACITY, magazineBoost, MAGAZINE_UUID, MAGAZINE_NAME, AttributeModifier.Operation.ADDITION);
        }
    }

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
        double magazineBoost = TaczCuriosConfig.COMMON.tandemMagazinePrimeCapacityBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.tandem_magazine_prime.effect", String.format("%+.0f", magazineBoost))
            .withStyle(ChatFormatting.DARK_PURPLE));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.legendary"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
