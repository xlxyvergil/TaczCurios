package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.world.entity.LivingEntity;

public final class LuckHelper {
    private LuckHelper() {}

    public static final String LUCK_KEY = TaczCurios.MODID + ":luck_value";
    public static final String LUCK_FANXING_KEY = TaczCurios.MODID + ":luck_fanxing";

    public static int getLuck(LivingEntity entity) {
        int base = entity.getPersistentData().getInt(LUCK_KEY);
        int fanxing = entity.getPersistentData().getInt(LUCK_FANXING_KEY);
        return base + fanxing;
    }

    public static void setLuck(LivingEntity entity, int value) {
        entity.getPersistentData().putInt(LUCK_KEY, Math.max(0, value));
    }

    public static void addLuck(LivingEntity entity, int amount) {
        setLuck(entity, getLuck(entity) + amount);
    }

    public static void setFanxingLuck(LivingEntity entity, int value) {
        entity.getPersistentData().putInt(LUCK_FANXING_KEY, Math.max(0, value));
    }

    public static void clearFanxingLuck(LivingEntity entity) {
        entity.getPersistentData().putInt(LUCK_FANXING_KEY, 0);
    }
}
