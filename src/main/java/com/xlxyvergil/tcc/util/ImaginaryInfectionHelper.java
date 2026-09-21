package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.evolution.KeyTierRegistry;
import com.xlxyvergil.tcc.evolution.KeyTierRegistry.KeyTier;
import net.minecraft.world.entity.LivingEntity;

/**
 * 虚数侵染上限映射：按神之键阶位（由 key_tiers.json 决定）返回目标可堆叠到的最大层数。
 * 新增神之键只需在 key_tiers.json 中登记阶位，无需改动此文件或其他 Java 代码。
 */
public final class ImaginaryInfectionHelper {

    private ImaginaryInfectionHelper() {}

    /** 根据攻击者身上已装备神之键的最高阶位，确定对目标施加的虚数侵染允许堆叠到的最大层数。 */
    public static int resolveMaxLevel(LivingEntity attacker) {
        if (attacker == null) {
            return TaczCuriosConfig.COMMON.tier1ImaginaryInfectionMaxLevel.get();
        }
        KeyTier[] best = { KeyTier.NONE };
        CurioSearchHelper.forEachEquippedStack(attacker, stack -> {
            KeyTier tier = KeyTierRegistry.tierOf(stack);
            if (tier.ordinal() > best[0].ordinal()) {
                best[0] = tier;
            }
        });
        return switch (best[0]) {
            case SPECIAL -> TaczCuriosConfig.COMMON.specialImaginaryInfectionMaxLevel.get();
            case T3 -> TaczCuriosConfig.COMMON.tier3ImaginaryInfectionMaxLevel.get();
            case T2 -> TaczCuriosConfig.COMMON.tier2ImaginaryInfectionMaxLevel.get();
            default -> TaczCuriosConfig.COMMON.tier1ImaginaryInfectionMaxLevel.get();
        };
    }
}
