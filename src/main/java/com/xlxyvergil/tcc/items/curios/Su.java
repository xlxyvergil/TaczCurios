package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.DamageResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Su extends BaseCurioItem {

    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f1234567891");

    public Su(Properties properties) {
        super(properties);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        if (slotContext.entity() instanceof Player player) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.getBoolean("IsBound")) {
                tag.putBoolean("IsBound", true);
                tag.putString("BoundPlayer", player.getStringUUID());
                tag.putString("BoundPlayerName", player.getGameProfile().getName());
            }
        }
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                stack -> stack.getItem() instanceof Su);
        CompoundTag tag = equipped.getTag();
        double total = TaczCuriosConfig.COMMON.suImaginaryResistance.get()
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
            total, IMAGINARY_RESISTANCE_UUID,
            "tcc.su.imaginary_resistance", AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, Attributes.MAX_HEALTH,
            TaczCuriosConfig.COMMON.suMaxHealthReduction.get(), MAX_HEALTH_UUID,
            "tcc.su.max_health", AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, Attributes.MAX_HEALTH, MAX_HEALTH_UUID);
        DamageResistanceHelper.clearDamageCap(livingEntity);
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerUUID = tag.getString("BoundPlayer");
            if (slotContext.entity() instanceof Player player) {
                return player.getStringUUID().equals(boundPlayerUUID);
            }
            return false;
        }
        return super.canEquip(slotContext, stack);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof Su).isEmpty();
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (!isEquipped(player)) return;
        if (!GunTypeChecker.isHoldingRifle(player)) return;
        if (entity.level().isClientSide()) return;

        float cap = event.getAmount() * TaczCuriosConfig.COMMON.suDamageTakenFactor.get().floatValue();
        DamageResistanceHelper.setDamageCap(entity, cap);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        String gunTypes = GunTypeChecker.formatGunTypes(List.of("rifle"));
        tooltip.add(Component.translatable("tcc.tooltip.restricted_gun_types", gunTypes));

        tooltip.add(Component.translatable("item.tcc.su.effect",
                String.format("%.2f", TaczCuriosConfig.COMMON.suImaginaryResistance.get()),
                String.format("%.2f", TaczCuriosConfig.COMMON.suMaxHealthReduction.get() * 100),
                String.format("%.2f", TaczCuriosConfig.COMMON.suDamageTakenFactor.get() * 100))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
    }
}
