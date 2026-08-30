package com.xlxyvergil.tcc.items.curios.bound;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EdenStar extends BoundCurioItem {
    /** 瞬移拦截范围（格） */
    private static double teleportRange() {
        return TaczCuriosConfig.COMMON.edenStarTeleportRange.get();
    }

    public EdenStar(Properties properties) {
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
                stack -> stack.getItem() instanceof EdenStar).isEmpty();
    }

    // 瞬移拦截

    @SubscribeEvent
    public static void onEnderEntityTeleport(EntityTeleportEvent.EnderEntity event) {
        handleTeleport(event);
    }

    @SubscribeEvent
    public static void onEnderPearlTeleport(EntityTeleportEvent.EnderPearl event) {
        handleTeleport(event);
    }

    @SubscribeEvent
    public static void onChorusFruitTeleport(EntityTeleportEvent.ChorusFruit event) {
        handleTeleport(event);
    }

    @SubscribeEvent
    public static void onTeleportCommand(EntityTeleportEvent.TeleportCommand event) {
        handleTeleport(event);
    }

    @SubscribeEvent
    public static void onSpreadPlayersCommand(EntityTeleportEvent.SpreadPlayersCommand event) {
        handleTeleport(event);
    }

    private static void handleTeleport(EntityTeleportEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player || MaidCompat.isMaid(entity)) {
            return; // 仅拦截非玩家、非女仆实体
        }
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (ServerPlayer player : serverLevel.players()) {
            ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(player,
                    stack -> stack.getItem() instanceof EdenStar);
            if (equipped.isEmpty()) {
                continue;
            }
            if (!((EdenStar) equipped.getItem()).matchesRestriction(player)) {
                continue;
            }
            if (player.distanceToSqr(entity) <= teleportRange() * teleportRange()) {
                event.setCanceled(true);
                return;
            }
        }
        // 女仆佩戴者同样可以拦截
        for (EntityMaid maid : MaidCompat.getMaidsNear(serverLevel, entity.blockPosition(), teleportRange())) {
            ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(maid,
                    stack -> stack.getItem() instanceof EdenStar);
            if (equipped.isEmpty()) {
                continue;
            }
            if (!((EdenStar) equipped.getItem()).matchesRestriction(maid)) {
                continue;
            }
            if (maid.distanceToSqr(entity) <= teleportRange() * teleportRange()) {
                event.setCanceled(true);
                return;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.golden.key_effect",
                (int) teleportRange())
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
