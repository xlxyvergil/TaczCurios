package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

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

public class WeaknessMastery extends TccCurioItem {
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("be6ceb26-b908-4ce6-8319-3593b27dc542");

    private static final String CRIT_DAMAGE_NAME = "tcc.weakness_mastery.crit_damage";

    public WeaknessMastery(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(CRIT_DAMAGE_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double critDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.weaknessMasteryCritDamage.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, critDamageBoost, CRIT_DAMAGE_UUID, CRIT_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
    }

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("pistol");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double critDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.weaknessMasteryCritDamage.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.weakness_mastery.effect",
                String.format("%+.0f", critDamageBoost))
            .withStyle(ChatFormatting.AQUA));

        tooltip.add(Component.literal(""));
        
    }


}
