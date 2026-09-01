package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DizangYuhun extends BoundCurioItem {
    private static final UUID ARMOR_STRIP_UUID = UUID.fromString("1b28a6a4-747b-40aa-887d-2492faca2b99");
    private static final UUID TOUGHNESS_STRIP_UUID = UUID.fromString("3ad28e89-46d1-4dfd-a3b0-e669ae311df2");

    private static double stripPercent() {
        return TaczCuriosConfig.COMMON.dizangYuhunStripPercent.get();
    }

    public DizangYuhun(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof DizangYuhun).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel)) {
            return;
        }
        LivingEntity attacker = resolveAttacker(event);
        if (attacker == null) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(attacker,
                stack -> stack.getItem() instanceof DizangYuhun);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((DizangYuhun) equipped.getItem()).matchesRestriction(attacker)) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target.isDeadOrDying()) {
            return;
        }
        double stripArmor = Math.round(target.getAttributeValue(Attributes.ARMOR) * stripPercent() * 100.0) / 100.0;
        double stripToughness = Math.round(target.getAttributeValue(Attributes.ARMOR_TOUGHNESS) * stripPercent() * 100.0) / 100.0;
        if (stripArmor > 0) {
            AttributeHelper.applyStackingModifier(target, Attributes.ARMOR,
                    -stripArmor, ARMOR_STRIP_UUID,
                    "tcc.dawn_key.armor_strip", AttributeModifier.Operation.ADDITION);
        }
        if (stripToughness > 0) {
            AttributeHelper.applyStackingModifier(target, Attributes.ARMOR_TOUGHNESS,
                    -stripToughness, TOUGHNESS_STRIP_UUID,
                    "tcc.dawn_key.toughness_strip", AttributeModifier.Operation.ADDITION);
        }
    }

    private static LivingEntity resolveAttacker(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity living) {
            return living;
        }
        if (event.getSource().getDirectEntity() instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.dawn.key_effect",
                String.format("%.0f", stripPercent() * 100))
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
