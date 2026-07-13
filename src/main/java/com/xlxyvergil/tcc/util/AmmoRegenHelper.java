package com.xlxyvergil.tcc.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.xlxyvergil.taa.api.ExtendedGunProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 统一弹药恢复工具类。
 * 所有饰品的弹药恢复逻辑统一调用此方法，确保：
 * 1. 从玩家缓存读取经全部计算（含 TaczAttributeAdd 的 magazine_capacity modifier）后的弹匣容量
 * 2. 使用 IGun.setCurrentAmmoCount 写入弹药数（而非直接操作 NBT）
 * 3. 闭膛待击枪械先补膛内子弹
 */
public final class AmmoRegenHelper {

    private AmmoRegenHelper() {}

    /**
     * 恢复手持枪械的弹药。
     *
     * @param player       玩家
     * @param held         手持枪械物品
     * @param iGun         枪械接口实例
     * @param regenPercent  恢复比例 (0.0 ~ 1.0)，外部已截取到两位小数
     */
    public static void regenAmmo(Player player, ItemStack held, IGun iGun, double regenPercent) {
        var gunInfo = TimelessAPI.getCommonGunIndex(iGun.getGunId(held));
        if (gunInfo.isEmpty()) return;
        var gunData = gunInfo.get().getGunData();

        // 从玩家缓存获取经过全部计算后的弹匣容量
        var cacheProperty = IGunOperator.fromLivingEntity(player).getCacheProperty();
        if (cacheProperty == null) return;
        Integer maxAmmo = cacheProperty.getCache(ExtendedGunProperties.MAGAZINE_CAPACITY);
        if (maxAmmo == null || maxAmmo <= 0) return;

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
