package com.xlxyvergil.tcc.compat.lootr;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Lootr 兼容入口。
 *
 * <p>本 mod 对 Lootr 是<strong>软依赖（compileOnly）</strong>：只有在 Lootr 已安装时该功能才生效。
 * 所有 {@code noobanidus.mods.lootr.*} 类型引用均隔离在 {@link LootrCompatInternal}，
 * 本类只在 {@link #isLoaded()} 为真时才会触碰它，避免 Lootr 缺失时触发 {@link NoClassDefFoundError}。</p>
 */
public final class LootrCompat {

    private static final String LOOTR_MODID = "lootr";

    private LootrCompat() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded(LOOTR_MODID);
    }

    public static boolean isLootrContainer(@Nullable BlockEntity be) {
        return isLoaded() && LootrCompatInternal.isLootrContainer(be);
    }

    @Nullable
    public static UUID getTileId(@Nullable BlockEntity be) {
        if (!isLoaded()) {
            return null;
        }
        return LootrCompatInternal.getTileId(be);
    }

    /**
     * 判断指定玩家是否<strong>尚未开过</strong>这个 Lootr 箱子。
     *
     * <p>只有当前相（{@code ChestData}）存在、且该玩家还没有生成过独立库存时才返回 {@code true}。</p>
     */
    public static boolean isUnopened(ServerLevel level, BlockPos pos, @Nullable BlockEntity be, UUID playerId) {
        if (!isLoaded()) {
            return false;
        }
        return LootrCompatInternal.isUnopened(level, pos, be, playerId);
    }
}
