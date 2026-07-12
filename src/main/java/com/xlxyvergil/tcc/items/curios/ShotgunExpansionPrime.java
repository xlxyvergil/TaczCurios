package com.xlxyvergil.tcc.items.curios;

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
 * 霰弹扩充Prime - 提升弹匣容量
 * 效果：提升弹匣容量，仅对霰弹枪生效
 */
public class ShotgunExpansionPrime extends BaseCurioItem {

    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID MAGAZINE_UUID = UUID.fromString("8c17e10f-b8fe-41b6-b0e7-ae214bed3dd4");

    // 修饰符名
    private static final String MAGAZINE_NAME = "tcc.shotgun_expansion_prime.magazine_capacity";

    public ShotgunExpansionPrime(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingShotgun(livingEntity)) {
            double magazineBoost = TaczCuriosConfig.COMMON.shotgunExpansionPrimeCapacityBoost.get();
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
        double magazineBoost = TaczCuriosConfig.COMMON.shotgunExpansionPrimeCapacityBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.shotgun_expansion_prime.effect", String.format("%+.0f", magazineBoost))
            .withStyle(ChatFormatting.WHITE));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
