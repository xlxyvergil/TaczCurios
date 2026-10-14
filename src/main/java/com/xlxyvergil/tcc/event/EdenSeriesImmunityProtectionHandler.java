package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.CuiyaoZhiGe;
import com.xlxyvergil.tcc.items.curios.bound.Eden;
import com.xlxyvergil.tcc.items.curios.bound.Huangjin;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.ImmunityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.EnumSet;
import java.util.Set;

/**
 * 伊甸系列逐火之蛾（伊甸 / 璀耀之歌 / 黄金）佩戴者的免疫统一监听器。
 * 仅对玩家与女仆生效，佩戴后常驻免疫窒息（卡方块）、溺水与实体挤压伤害。
 */
@EventBusSubscriber(modid = TaczCurios.MODID)
public class EdenSeriesImmunityProtectionHandler {

    /** 伊甸系列逐火之蛾提供的免疫类别。 */
    private static final Set<ImmunityHelper.Type> IMMUNITIES = EnumSet.of(
            ImmunityHelper.Type.SUFFOCATION,
            ImmunityHelper.Type.DROWNING,
            ImmunityHelper.Type.CRAMMING);

    /** 是否佩戴任一伊甸系列逐火之蛾（调用方已先过滤玩家/女仆）。 */
    private static boolean isProtected(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                EdenSeriesImmunityProtectionHandler::isEdenSeries).isEmpty();
    }

    private static boolean isEdenSeries(ItemStack stack) {
        return stack.getItem() instanceof Eden
                || stack.getItem() instanceof CuiyaoZhiGe
                || stack.getItem() instanceof Huangjin;
    }

    /** 免疫窒息（卡方块）、溺水与实体挤压伤害。 */
    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
        ImmunityHelper.cancelDamage(event, IMMUNITIES, EdenSeriesImmunityProtectionHandler::isProtected);
    }
}
