package com.xlxyvergil.tcc.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 击杀事件去重器。
 * 多弹头武器（霰弹 / 多弹头狙击）会在同一次击杀中连续触发多次 {@code EntityKillByGunEvent}，
 * 按被杀实体做短窗口去重可保证一次击杀只计一次。
 * 每个触发器必须使用独立实例，否则同一份记录会让第二个触发器把首次事件误判为重复。
 */
public final class KillEventDedupe {

    /** 去重窗口：同一实体在该 tick 数内再次触发击杀事件视为重复 */
    private static final long WINDOW_TICKS = 2L;
    /** 记录保留时长，超过后清理，避免长期运行 map 膨胀 */
    private static final long KEEP_TICKS = 200L;
    /** 触发清理的容量阈值 */
    private static final int PURGE_THRESHOLD = 256;

    private final Map<UUID, Long> recent = new HashMap<>();

    /**
     * 判断是否为重复事件；首次（非重复）调用会记录本次 tick。
     *
     * @return true 表示本次事件应跳过
     */
    public boolean isDuplicate(UUID victimUuid, long nowTick) {
        Long last = recent.get(victimUuid);
        if (last != null && nowTick - last <= WINDOW_TICKS) {
            return true;
        }
        recent.put(victimUuid, nowTick);
        if (recent.size() >= PURGE_THRESHOLD) {
            recent.entrySet().removeIf(e -> nowTick - e.getValue() > KEEP_TICKS);
        }
        return false;
    }
}
