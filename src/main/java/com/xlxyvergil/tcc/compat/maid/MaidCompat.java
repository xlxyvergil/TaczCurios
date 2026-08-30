package com.xlxyvergil.tcc.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

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
