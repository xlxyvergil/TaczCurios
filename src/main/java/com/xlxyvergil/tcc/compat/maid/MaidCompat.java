package com.xlxyvergil.tcc.compat.maid;

import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;

import java.util.List;
import java.util.function.Predicate;

/**
 * 女仆模组（TLM）兼容入口。
 */
public final class MaidCompat {

    private static final String TLM_MODID = "touhou_little_maid";

    private MaidCompat() {}

    public static boolean isMaid(Entity entity) {
        return isLoaded() && MaidCompatInternal.isMaid(entity);
    }

    public static List<LivingEntity> getMaids(Level level) {
        if (!isLoaded()) return List.of();
        return MaidCompatInternal.getMaids(level);
    }

    public static List<LivingEntity> getMaidsNear(Level level, BlockPos center, double radius) {
        return getMaidsNear(level, new AABB(center).inflate(radius), LivingEntity::isAlive);
    }

    public static List<LivingEntity> getMaidsNear(Level level, AABB box, Predicate<LivingEntity> filter) {
        if (!isLoaded()) return List.of();
        return MaidCompatInternal.getMaidsNear(level, box, filter);
    }

    /** 实体是否为光环可作用的「玩家或女仆」。 */
    public static boolean isPlayerOrMaid(Entity entity) {
        return entity instanceof Player || isMaid(entity);
    }

    public static LivingEntity findWearingMaid(Level level, Predicate<ItemStack> predicate) {
        if (!isLoaded()) return null;
        for (LivingEntity maid : getMaids(level)) {
            if (!CurioSearchHelper.findFirstEquippedStack(maid, predicate).isEmpty()) {
                return maid;
            }
        }
        return null;
    }

    /**
     * 通过当前打开的女仆界面直接取到它绑定的女仆（无需遍历整个世界）。
     * 仅当女仆 mod 已加载且当前屏幕为女仆容器界面时返回该女仆，否则返回 null。
     * 返回类型用 LivingEntity，避免调用方直接依赖女仆类。
     */
    @OnlyIn(Dist.CLIENT)
    public static LivingEntity resolveScreenMaid(Screen screen) {
        if (!isLoaded()) {
            return null;
        }
        return MaidCompatInternal.resolveScreenMaid(screen);
    }

    public static Component getDisplayName(LivingEntity entity) {
        if (isLoaded() && isMaid(entity)) {
            return MaidCompatInternal.getDisplayName(entity);
        }
        return entity.getName();
    }

    public static Player resolveOwnerPlayer(Entity killer) {
        if (killer instanceof Player player) {
            return player;
        }
        if (!isLoaded() || !isMaid(killer)) {
            return null;
        }
        return MaidCompatInternal.resolveOwner(killer);
    }

    public static String resolveAttackerUuid(Entity attacker) {
        return attacker != null ? attacker.getUUID().toString() : "";
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(TLM_MODID);
    }
}
