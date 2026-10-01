package com.xlxyvergil.tcc.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 客户端保存的"当前玩家最近伤害实体的侵蚀进度"。由 {@code SyncErosionProgressS2CPacket}
 * 从服务端推送而来，供 {@code ErosionProgressOverlay} 渲染。超过 {@link #TIMEOUT_MS}
 * 未收到更新则自动隐藏（视为本轮战斗已结束）。
 */
@OnlyIn(Dist.CLIENT)
public final class ErosionHudClientData {

    private static final long TIMEOUT_MS = 5000L;

    private static String name = "";
    private static float progress;
    private static long lastUpdateMs;

    private ErosionHudClientData() {}

    /** 用服务端下发的最新数据覆盖当前显示。 */
    public static void update(String entityName, float value) {
        name = entityName != null ? entityName : "";
        progress = Math.max(0.0F, Math.min(1.0F, value));
        lastUpdateMs = System.currentTimeMillis();
    }

    /** 是否仍在显示有效期（当前有观察目标且未超时）。 */
    public static boolean isActive() {
        return !name.isEmpty() && System.currentTimeMillis() - lastUpdateMs < TIMEOUT_MS;
    }

    public static String getName() {
        return name;
    }

    /** 剩余进度：1.0 = 满值，0.0 = 归零消亡。 */
    public static float getProgress() {
        return progress;
    }
}
