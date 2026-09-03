package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class QianjieYicheng extends BoundCurioItem {
    private static final UUID LUCK_UUID = UUID.fromString("d94c75ed-8fa7-4cdd-94a8-13c5df10776e");

    public QianjieYicheng(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        int luck = TaczCuriosConfig.COMMON.qianjieYichengLuck.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.LUCK,
            luck, LUCK_UUID,
            "tcc.qianjie_yicheng.luck", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.LUCK, LUCK_UUID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("smg");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        int luck = TaczCuriosConfig.COMMON.qianjieYichengLuck.get();

        tooltip.add(formatModifierTooltip(luck, "%.0f", Component.translatable(AttributeHelper.LUCK.getDescriptionId()))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
