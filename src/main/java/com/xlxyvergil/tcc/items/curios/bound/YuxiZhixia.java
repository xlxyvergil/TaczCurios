package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.event.CurioAbsorptionEventHandler;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class YuxiZhixia extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("b8c9d0e1-f2a3-4567-bcde-f01234567890");

    public YuxiZhixia(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_UUID, stack.getItem());
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof YuxiZhixia);
        CompoundTag tag = equipped.getTag();
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            total, IMAGINARY_RESISTANCE_UUID,
            "tcc.yuxi_zhixia.imaginary_resistance", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof YuxiZhixia).isEmpty();
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (!isEquipped(entity)) return;
        if (!GunTypeChecker.isHoldingHeavyWeapon(entity)) return;

        CurioAbsorptionEventHandler.tryTriggerAbsorption(
            entity,
            TccItems.YUXI_ZHIXIA,
            TaczCuriosConfig.COMMON.yuxiZhixiaTriggerHpRatio.get(),
            TaczCuriosConfig.COMMON.yuxiZhixiaAbsorptionLevel.get(),
            TaczCuriosConfig.COMMON.yuxiZhixiaAbsorptionDuration.get(),
            TaczCuriosConfig.COMMON.yuxiZhixiaCooldownSeconds.get()
        );
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("rpg", "mg");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        appendImaginaryResistance(stack, tooltip);

        double triggerHpRatio = TaczCuriosConfig.COMMON.yuxiZhixiaTriggerHpRatio.get() * 100;
        int absorptionLevel = TaczCuriosConfig.COMMON.yuxiZhixiaAbsorptionLevel.get();
        int cooldownSeconds = TaczCuriosConfig.COMMON.yuxiZhixiaCooldownSeconds.get().intValue();

        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("item.tcc.yuxi_zhixia.effect",
                (int) triggerHpRatio,
                absorptionLevel,
                cooldownSeconds)
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
