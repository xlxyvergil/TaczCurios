package com.xlxyvergil.tcc.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.xlxyvergil.taa.util.AmmoCapacityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 统一弹药恢复入口。用 TAA 的弹匣容量兼容链算出最终容量（含玩家属性、GunsmithLib、KuvaLich 加成）写入弹药数；闭膛待击枪械先补膛内子弹。
 */
public final class AmmoRegenHelper {

    private AmmoRegenHelper() {}

    public static void regenAmmo(LivingEntity entity, ItemStack held, IGun iGun, double regenPercent) {
        var gunInfo = TimelessAPI.getCommonGunIndex(iGun.getGunId(held));
        if (gunInfo.isEmpty()) return;
        var gunData = gunInfo.get().getGunData();

        // 以 TACZ 原生容量（含扩容弹匣）为基数，套用玩家属性与兼容链算出最终容量，与 Z 面板/HUD 一致
        int maxAmmo = AmmoCapacityHelper.applyCapacity(
            AmmoCapacityHelper.resolveBaseCapacity(held, gunData), held, entity
        );

        int currentAmmo = iGun.getCurrentAmmoCount(held);
        if (currentAmmo >= maxAmmo) return;

        int regenAmmo = (int) Math.max(1, Math.round(maxAmmo * regenPercent));

        // 闭膛待击：膛内无弹时先补膛内
        if (gunData.getBolt() != Bolt.OPEN_BOLT && !iGun.hasBulletInBarrel(held)) {
            iGun.setBulletInBarrel(held, true);
            regenAmmo -= 1;
        }

        int newAmmo = Math.min(currentAmmo + regenAmmo, maxAmmo);
        iGun.setCurrentAmmoCount(held, newAmmo);
    }
}
