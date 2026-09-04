package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
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
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DominanceKey extends BoundCurioItem {
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f1234567892");

    public DominanceKey(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ATTACK_DAMAGE_UUID, stack.getItem());
        double resistance = livingEntity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        double attackBonus = resistance * 0.5;
        AttributeHelper.applyModifier(livingEntity, Attributes.ATTACK_DAMAGE,
            attackBonus, ATTACK_DAMAGE_UUID,
            "tcc.dominance_key.attack_damage", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity(), stack);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof DominanceKey).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!TccAttributeEvents.isActiveAttackSource(event.getSource())) return;
        LivingEntity attacker = resolveAttacker(event);
        if (attacker == null || !isEquipped(attacker)) return;
        if (!GunTypeChecker.isHoldingMeleeWeapon(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;

        LivingEntity targetLiving = event.getEntity();
        if (targetLiving.isDeadOrDying()) return;

        double imaginaryResistance = attacker.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        float imaginaryBonus = (float) (Math.round(imaginaryResistance * 10000.0) / 10000.0);
        TccAttributeEvents.applyImaginaryDamage(
            targetLiving,
            TccDamageSources.imaginaryDamage(targetLiving.level(), attacker),
            imaginaryBonus
        );
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

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));



        double attackFromResistance = 0;
        double imaginaryDamage = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null && isEquipped(wearer)) {
                double resistance = wearer.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
                attackFromResistance = resistance * 0.5;
                imaginaryDamage = resistance;
            }
        }
        tooltip.add(formatModifierTooltip(attackFromResistance, "%.1f", Component.translatable(AttributeHelper.ATTACK_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.tcc.dominance_key.special_damage",
                String.format("%.2f", imaginaryDamage))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
