package com.xlxyvergil.tcc.compat.lootr;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Lootr 兼容入口。
 *
 * <p>本 mod 对 Lootr 是<strong>软依赖（compileOnly）</strong>，未安装时必须完全不触碰 Lootr 类型。
 * 因此按 {@code MaidCompat} / {@code MaidCompatInternal} 的同样方式做类型隔离：
 * 本类<strong>不引用任何 {@code noobanidus.mods.lootr.*} 类型</strong>，可以安全进出；
 * 所有真正调用 Lootr API 的代码都放在 {@link LootrCompatInternal} 里，
 * 且只在本类的 {@link #isLoaded()} 判断通过后才会被解析到（JVM 对类引用的解析是惰性的，
 * 只有执行到那一行才会加载 {@link LootrCompatInternal}）。</p>
 *
 * <p>反面例子：把 {@code isLoaded()} 写在引用 Lootr 类型的类里是无效守卫——调用该类的任何静态方法，
 * JVM 都要先加载并校验整个类，校验方法体时会解析 {@code LootrAPI.getData(ILootrInfoProvider)}
 * 这类描述符中的缺失类型，直接在调用处抛 {@link NoClassDefFoundError}，守卫根本没机会执行。</p>
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
        return isLoaded() ? LootrCompatInternal.getTileId(be) : null;
    }

    /**
     * 判断指定玩家是否<strong>尚未开过</strong>这个 Lootr 箱子。
     *
     * <p>只有当前相存在、且该玩家还没有生成过独立库存时才返回 {@code true}。</p>
     */
    public static boolean isUnopened(ServerLevel level, BlockPos pos, @Nullable BlockEntity be, UUID playerId) {
        return isLoaded() && LootrCompatInternal.isUnopened(level, pos, be, playerId);
    }
}
