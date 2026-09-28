package com.xlxyvergil.tcc.util;

/**
 * 由 SynchedEntityDataMixin 实现的鸭子接口，用于让外部主动清理同步数据里已经写进去的负 Float 残留。
 * 放在 mixin 包之外，避免被 Mixin 的包访问限制拦截。
 */
public interface ITccSynchedEntityData {

    /** 把该实体同步数据里所有负 Float（含 NaN）就地归零。 */
    void tcc$clearNegativeFloat();
}
