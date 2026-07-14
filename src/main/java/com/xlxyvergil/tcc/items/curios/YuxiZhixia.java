package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.event.CurioAbsorptionEventHandler;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class YuxiZhixia extends BaseCurioItem {

    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("b8c9d0e1-f2a3-4567-bcde-f01234567890");

    public YuxiZhixia(Properties properties) {
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
                stack -> stack.getItem() instanceof YuxiZhixia);
        CompoundTag tag = equipped.getTag();
        double total = TaczCuriosConfig.COMMON.villVImaginaryResistance.get()
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
            stack -> stack.getItem() instanceof YuxiZhixia).isEmpty();
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (!isEquipped(player)) return;
        if (!GunTypeChecker.isHoldingHeavyWeapon(player)) return;

        CurioAbsorptionEventHandler.tryTriggerAbsorption(
            entity,
            TccItems.YUXI_ZHIXIA.get(),
            TaczCuriosConfig.COMMON.yuxiZhixiaTriggerHpRatio.get(),
            TaczCuriosConfig.COMMON.yuxiZhixiaAbsorptionLevel.get(),
            TaczCuriosConfig.COMMON.yuxiZhixiaAbsorptionDuration.get(),
            TaczCuriosConfig.COMMON.yuxiZhixiaCooldownSeconds.get()
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        CompoundTag tag = stack.getTag();

        // 虚数抗性显示
        double baseValue = TaczCuriosConfig.COMMON.villVImaginaryResistance.get();
        double total = baseValue + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        tooltip.add(Component.literal(""));

        double triggerHpRatio = TaczCuriosConfig.COMMON.yuxiZhixiaTriggerHpRatio.get() * 100;
        int absorptionLevel = TaczCuriosConfig.COMMON.yuxiZhixiaAbsorptionLevel.get();
        int cooldownSeconds = TaczCuriosConfig.COMMON.yuxiZhixiaCooldownSeconds.get().intValue();

        tooltip.add(Component.translatable("tcc.tooltip.imaginary_resistance", String.format("%.0f", total))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

        String gunTypes = GunTypeChecker.formatGunTypes(List.of("rpg", "mg"));
        tooltip.add(Component.translatable("tcc.tooltip.restricted_gun_types", gunTypes));

        tooltip.add(Component.translatable("item.tcc.yuxi_zhixia.effect",
                (int) triggerHpRatio,
                absorptionLevel,
                cooldownSeconds)
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.epic"));

        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
    }
}
