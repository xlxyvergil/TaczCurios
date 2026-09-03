package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.MobEffectPoolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Huangjin extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("4a8c1e6f-2b9d-4f7e-9c3a-5d1b8e2f6c47");
    private static final UUID BASE_GUN_DAMAGE_UUID = UUID.fromString("8b2e4f1a-3c6d-4b5e-9a7f-2d8c1e4b6a39");

    private static double auraRange() {
        return TaczCuriosConfig.COMMON.huangjinAuraRange.get();
    }

    private static long interval() {
        return (long) TaczCuriosConfig.COMMON.huangjinIntervalSeconds.get() * 20L;
    }

    private static int buffDuration() {
        return TaczCuriosConfig.COMMON.huangjinBuffDurationSeconds.get() * 20;
    }

    private static int buffAmplifier() {
        return TaczCuriosConfig.COMMON.huangjinBuffAmplifier.get();
    }

    public Huangjin(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Huangjin);
        CompoundTag tag = equipped.getTag();
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                total, IMAGINARY_RESISTANCE_UUID,
                "tcc.huangjin.imaginary_resistance", AttributeModifier.Operation.ADDITION);
        // 基乘算法：基础枪械伤害倍率降低（默认 -0.8 = 降低80%）
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE,
                TaczCuriosConfig.COMMON.huangjinGunDamageReduction.get(),
                BASE_GUN_DAMAGE_UUID, "tcc.huangjin.gun_damage", AttributeModifier.Operation.MULTIPLY_BASE);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, BASE_GUN_DAMAGE_UUID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("pistol");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof Huangjin).isEmpty();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        MinecraftServer server = event.getServer();
        if (server == null) {
            return;
        }
        long gameTime = server.overworld().getGameTime();
        if (gameTime % interval() != 0) {
            return;
        }
        for (ServerLevel level : server.getAllLevels()) {
            List<ServerPlayer> players = level.players();
            for (ServerPlayer player : players) {
                applyAura(players, player);
            }
            for (LivingEntity maid : MaidCompat.getMaids(level)) {
                applyAura(players, maid);
            }
        }
    }

    private static void applyAura(List<ServerPlayer> players, LivingEntity wearer) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(wearer,
                stack -> stack.getItem() instanceof Huangjin);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((Huangjin) equipped.getItem()).matchesRestriction(wearer)) {
            return;
        }
        MobEffect effect = MobEffectPoolHelper.randomBeneficial(wearer.getRandom());
        for (ServerPlayer other : players) {
            if (wearer.distanceToSqr(other) > auraRange() * auraRange()) {
                continue;
            }
            MobEffectPoolHelper.applyEffect(other, effect, buffDuration(), buffAmplifier(), wearer);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        appendImaginaryResistance(stack, tooltip);
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.huangjinGunDamageReduction.get() * 100,
                "%.0f%%", Component.translatable(AttributeHelper.BULLET_GUNDAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.golden.curio_effect",
                (int) auraRange(), buffAmplifier() + 1)
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
