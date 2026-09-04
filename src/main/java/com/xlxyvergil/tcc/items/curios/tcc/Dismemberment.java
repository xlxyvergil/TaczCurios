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

public class Dismemberment extends TccCurioItem {
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("7a63d998-e734-417b-ad83-b58be5cd0a9f");

    private static final String CRIT_DAMAGE_NAME = "tcc.dismemberment.crit_damage";

    public Dismemberment(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(CRIT_DAMAGE_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            double critDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.dismembermentCritDamage.get());
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, critDamageBoost, CRIT_DAMAGE_UUID, CRIT_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));



        double critDamageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.dismembermentCritDamage.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.dismemberment.effect",
                String.format("%+.0f", critDamageBoost))
            .withStyle(ChatFormatting.BLUE));

        tooltip.add(Component.literal(""));
        
    }

}
