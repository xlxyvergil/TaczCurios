package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.MobEffectPoolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 黄金系列·人物线（tcc_3rd）：黄金。
 * 36 格内玩家每 5 秒获得 1 个随机正面 buff（30 秒，III 级）。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Huangjin extends BoundCurioItem {

    /** 光环范围（格） */
    private static double auraRange() {
        return TaczCuriosConfig.COMMON.huangjinAuraRange.get();
    }

    /** 施加间隔（tick） */
    private static long interval() {
        return (long) TaczCuriosConfig.COMMON.huangjinIntervalSeconds.get() * 20L;
    }

    /** buff 时长（tick） */
    private static int buffDuration() {
        return TaczCuriosConfig.COMMON.huangjinBuffDurationSeconds.get() * 20;
    }

    /** buff 等级 */
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
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
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
                ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(player,
                        stack -> stack.getItem() instanceof Huangjin);
                if (equipped.isEmpty()) {
                    continue;
                }
                if (!((Huangjin) equipped.getItem()).matchesRestriction(player)) {
                    continue;
                }
                // 统一随机：整个光环内所有人获得同一个 buff（避免每人各自独立随机）
                MobEffect effect = MobEffectPoolHelper.randomBeneficial(player.getRandom());
                for (ServerPlayer other : players) {
                    if (player.distanceToSqr(other) > auraRange() * auraRange()) {
                        continue;
                    }
                    MobEffectPoolHelper.applyEffect(other, effect, buffDuration(), buffAmplifier(), player);
                }
            }
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
