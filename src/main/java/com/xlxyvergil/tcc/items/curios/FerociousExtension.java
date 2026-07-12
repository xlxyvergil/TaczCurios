package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;

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
 * 凶恶延伸饰品
 * 效果：提高子弹射程（乘算）
 */
public class FerociousExtension extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID EFFECTIVE_RANGE_UUID = UUID.fromString("2774a4d4-b53c-4799-bb4c-fd7dc117264f");
    
    // 修饰符名称
    private static final String EFFECTIVE_RANGE_NAME = "tcc.ferocious_extension.effective_range";
    
    public FerociousExtension(Properties properties) {
        super(properties);
    }
    
    /**
     * 应用所有效果加成
     * 提高配置中的子弹射程（乘算）
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        double rangeBoost = TaczCuriosConfig.COMMON.ferociousExtensionRangeBoost.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.EFFECTIVE_RANGE, rangeBoost, EFFECTIVE_RANGE_UUID, EFFECTIVE_RANGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    
    /**
     * 移除所有效果加成
     */
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EFFECTIVE_RANGE, EFFECTIVE_RANGE_UUID);
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
        double rangeBoost = TaczCuriosConfig.COMMON.ferociousExtensionRangeBoost.get() * 100;
        tooltip.add(Component.translatable("item.tcc.ferocious_extension.effect", String.format("%+.0f", rangeBoost))
            .withStyle(ChatFormatting.AQUA));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.uncommon"));
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}