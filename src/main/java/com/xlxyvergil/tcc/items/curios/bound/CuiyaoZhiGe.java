package com.xlxyvergil.tcc.items.curios.bound;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
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
import net.minecraft.world.damagesource.DamageSource;
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
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CuiyaoZhiGe extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("9f4d2e7b-3a8c-4f1e-b6d5-8c2e1a7f4b30");

    /** 光环范围（格） */
    private static double auraRange() {
        return TaczCuriosConfig.COMMON.cuiyaoZhiGeAuraRange.get();
    }

    /** 施加间隔（tick） */
    private static long interval() {
        return (long) TaczCuriosConfig.COMMON.cuiyaoZhiGeIntervalSeconds.get() * 20L;
    }

    /** buff 时长（tick） */
    private static int buffDuration() {
        return TaczCuriosConfig.COMMON.cuiyaoZhiGeBuffDurationSeconds.get() * 20;
    }

    /** buff 等级 */
    private static int buffAmplifier() {
        return TaczCuriosConfig.COMMON.cuiyaoZhiGeBuffAmplifier.get();
    }

    public CuiyaoZhiGe(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof CuiyaoZhiGe);
        CompoundTag tag = equipped.getTag();
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                total, IMAGINARY_RESISTANCE_UUID,
                "tcc.cuiyao_zhi_ge.imaginary_resistance", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("pistol");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof CuiyaoZhiGe).isEmpty();
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
            for (EntityMaid maid : MaidCompat.getMaids(level)) {
                applyAura(players, maid);
            }
        }
    }

    /** 以单个佩戴者（玩家或女仆）为中心，为其光环范围内的玩家统一施加同一个随机增益 buff。 */
    private static void applyAura(List<ServerPlayer> players, LivingEntity wearer) {
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(wearer,
                stack -> stack.getItem() instanceof CuiyaoZhiGe);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((CuiyaoZhiGe) equipped.getItem()).matchesRestriction(wearer)) {
            return;
        }
        // 统一随机：整个光环内所有人获得同一个 buff（避免每人各自独立随机）
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
        tooltip.add(Component.translatable("item.tcc.golden.curio_effect",
                (int) auraRange(), buffAmplifier() + 1)
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
