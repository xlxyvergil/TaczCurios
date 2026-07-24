package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.curios.HeavenFireApocalypse;
import com.xlxyvergil.tcc.items.curios.HeavenFireApocalypseEndless;
import com.xlxyvergil.tcc.items.curios.HeavenFireJudgment;
import com.xlxyvergil.tcc.items.curios.JudgementKey;
import com.xlxyvergil.tcc.items.curios.MetaMorph;
import com.xlxyvergil.tcc.items.curios.ShijieFanyan;
import com.xlxyvergil.tcc.items.curios.XukongWancangYZTH;
import com.xlxyvergil.tcc.items.curios.YinguoZhuanlun;
import net.minecraft.world.entity.LivingEntity;

/**
 * 虚数侵染饰品等级映射工具。
 * 新增虚数伤害饰品时，只需在此处添加一行即可，无需修改 TccAttributeEvents。
 */
public final class ImaginaryInfectionHelper {

    private ImaginaryInfectionHelper() {}

    /**
     * 根据攻击者装备的饰品，确定虚数侵染的最大等级和是否可施加崩解。
     *
     * @param attacker 攻击者
     * @return 侵染配置信息
     */
    public static InfectionInfo resolve(LivingEntity attacker) {
        if (HeavenFireApocalypseEndless.hasHeavenFireApocalypseEndlessEquipped(attacker)) {
            return new InfectionInfo(
                TaczCuriosConfig.COMMON.endlessImaginaryInfectionMaxLevel.get(),
                true
            );
        }
        if (HeavenFireApocalypse.hasHeavenFireApocalypseEquipped(attacker)) {
            return new InfectionInfo(
                TaczCuriosConfig.COMMON.apocalypseImaginaryInfectionMaxLevel.get(),
                false
            );
        }
        if (HeavenFireJudgment.hasHeavenFireJudgmentEquipped(attacker)) {
            return new InfectionInfo(
                TaczCuriosConfig.COMMON.judgmentImaginaryInfectionMaxLevel.get(),
                false
            );
        }
        if (JudgementKey.hasEquipped(attacker)) {
            return new InfectionInfo(
                TaczCuriosConfig.COMMON.judgementKeyImaginaryInfectionMaxLevel.get(),
                false
            );
        }
        if (ShijieFanyan.hasEquipped(attacker)) {
            return new InfectionInfo(
                TaczCuriosConfig.COMMON.shijieFanyanImaginaryInfectionMaxLevel.get(),
                false
            );
        }
        if (XukongWancangYZTH.isEquipped(attacker)) {
            return new InfectionInfo(
                TaczCuriosConfig.COMMON.xukongWancangYZTHImaginaryInfectionMaxLevel.get(),
                false
            );
        }
        if (YinguoZhuanlun.hasEquipped(attacker)) {
            return new InfectionInfo(
                TaczCuriosConfig.COMMON.yinguoZhuanlunImaginaryInfectionMaxLevel.get(),
                false
            );
        }
        if (MetaMorph.isEquipped(attacker)) {
            return new InfectionInfo(
                TaczCuriosConfig.COMMON.metaMorphImaginaryInfectionMaxLevel.get(),
                false
            );
        }
        // 未定义饰品的虚数伤害来源，默认1级侵染
        return new InfectionInfo(1, false);
    }

    public record InfectionInfo(int maxLevel, boolean canApplyCollapse) {
        public boolean isValid() {
            return maxLevel > 0;
        }
    }
}
