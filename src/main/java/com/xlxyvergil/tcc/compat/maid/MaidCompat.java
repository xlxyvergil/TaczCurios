package com.xlxyvergil.tcc.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.ModList;

import java.util.List;
import java.util.function.Predicate;

/**
 * 东方女仆（Touhou Little Maid）兼容工具类。
 * 当佩戴者（主人）的女仆执行 kill 相关操作时，把击杀计数归属到佩戴饰品的玩家身上。
 * 所有方法都内置 ModList.get().isLoaded() 检查，确保女仆模组未安装时不会引发类加载错误。
 */
public final class MaidCompat {

    private static final String TLM_MODID = "touhou_little_maid";

    private MaidCompat() {}

    /**
     * 判断实体是否为东方女仆模组的女仆。
     */
    public static boolean isMaid(Entity entity) {
        return isLoaded() && entity instanceof EntityMaid;
    }

    /**
     * 获取某一维度内所有加载中的女仆（仅当女仆模组加载时，否则返回空列表）。
     * 用于遍历「女仆作为饰品佩戴者」的光环/范围效果。
     */
    public static List<EntityMaid> getMaids(Level level) {
        if (!isLoaded()) return List.of();
        WorldBorder border = level.getWorldBorder();
        double cx = border.getCenterX(), cz = border.getCenterZ();
        double r = Math.max(border.getSize(), 32.0D) / 2.0D;
        AABB box = new AABB(cx - r, level.getMinBuildHeight(), cz - r,
                cx + r, level.getMaxBuildHeight(), cz + r);
        return level.getEntitiesOfClass(EntityMaid.class, box, LivingEntity::isAlive);
    }

    /**
     * 获取以某个坐标为中心、给定半径内的女仆（用于瞬移拦截等以佩戴者为中心的局部判定）。
     */
    public static List<EntityMaid> getMaidsNear(Level level, BlockPos center, double radius) {
        if (!isLoaded()) return List.of();
        return level.getEntitiesOfClass(EntityMaid.class,
                new AABB(center).inflate(radius), LivingEntity::isAlive);
    }

    /**
     * 查找佩戴满足条件饰品的女仆（用于客户端 tooltip 展示「佩戴者的真实数值」）。
     */
    public static LivingEntity findWearingMaid(Level level, Predicate<ItemStack> predicate) {
        if (!isLoaded()) return null;
        for (EntityMaid maid : getMaids(level)) {
            if (!CurioSearchHelper.findFirstEquippedStack(maid, predicate).isEmpty()) {
                return maid;
            }
        }
        return null;
    }

    /**
     * 将击杀者归一化为最终应计数的主人玩家：
     * - 击杀者是玩家：直接返回该玩家；
     * - 击杀者是女仆：返回其主人（主人必须是玩家才计数）；
     * - 其他情况返回 null。
     */
    public static Player resolveOwnerPlayer(Entity killer) {
        if (killer instanceof Player player) {
            return player;
        }
        if (!isLoaded() || !(killer instanceof EntityMaid maid)) {
            return null;
        }
        if (maid.getOwner() instanceof Player owner) {
            return owner;
        }
        return null;
    }

    /**
     * 计算枪杀数据中用于匹配死亡源的攻击者 UUID。
     * 玩家击杀记玩家 UUID；女仆击杀记女仆 UUID（其本体即死亡源实体）。
     */
    public static String resolveAttackerUuid(Entity attacker) {
        return attacker != null ? attacker.getUUID().toString() : "";
    }

    private static boolean isLoaded() {
        return ModList.get().isLoaded(TLM_MODID);
    }
}
