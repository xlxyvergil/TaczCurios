package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.items.curios.bound.Juezhe;
import com.xlxyvergil.tcc.items.curios.bound.Su;
import com.xlxyvergil.tcc.items.curios.bound.Tianhui;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.ImmunityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.Set;

/**
 * 苏系列逐火之蛾（苏 / 善法之瞳 / 天慧）佩戴者的免疫统一监听器。
 * 仅对玩家与女仆生效，佩戴后常驻免疫弹射物：
 * 原版弹射物根本无法命中（如同佩戴者不存在般穿过），并免疫弹射物伤害。
 */
@EventBusSubscriber(modid = TaczCurios.MODID)
public class SuSeriesImmunityProtectionHandler {

    /** 苏系列逐火之蛾提供的免疫类别。 */
    private static final Set<ImmunityHelper.Type> IMMUNITIES = Set.of(ImmunityHelper.Type.PROJECTILE);

    /** 是否为受保护的佩戴者：玩家或女仆，且佩戴任一苏系列逐火之蛾。 */
    private static boolean isProtected(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        if (!(entity instanceof Player) && !MaidCompat.isMaid(entity)) {
            return false;
        }
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                SuSeriesImmunityProtectionHandler::isSuSeries).isEmpty();
    }

    private static boolean isSuSeries(ItemStack stack) {
        return stack.getItem() instanceof Su
                || stack.getItem() instanceof Juezhe
                || stack.getItem() instanceof Tianhui;
    }

    /** 弹射物无法命中：命中判定指向佩戴者时，让弹射物如同该实体不存在般穿过。 */
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        ImmunityHelper.skipProjectileHit(event, SuSeriesImmunityProtectionHandler::isProtected);
    }

    /** 免疫弹射物伤害。 */
    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
        ImmunityHelper.cancelDamage(event, IMMUNITIES, SuSeriesImmunityProtectionHandler::isProtected);
    }
}
