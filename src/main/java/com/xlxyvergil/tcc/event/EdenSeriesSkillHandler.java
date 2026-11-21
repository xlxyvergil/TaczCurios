package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.items.BaseCurioItem;
import com.xlxyvergil.tcc.items.curios.bound.CuiyaoZhiGe;
import com.xlxyvergil.tcc.items.curios.bound.Eden;
import com.xlxyvergil.tcc.items.curios.bound.Huangjin;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.EdenSeriesSkillCurios;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * 伊甸系列技能的<strong>服务端</strong>部分。
 *
 * 佩戴 eden / cuiyao_zhi_ge / huangjin 之一后，按下技能键（默认 V）即为范围内所有玩家与女仆
 * 随机施加一个正面 buff。旧实现在开枪命中时自动触发，冷却写在开枪事件里；
 * 现在触发点只剩按键这一处，冷却也搬到这里统一管理（三件共用同一条冷却记录）。
 *
 * 冷却用玩家的 persistentData 记录下次可用的 gameTime：跨维度、跨死亡都保留，
 * 与客户端 tick 无关，因此玩家离开再回来不会把冷却清零。
 */
public final class EdenSeriesSkillHandler {

    /** 冷却存储键：记录该玩家下次可用技能的游戏刻。 */
    private static final String COOLDOWN_KEY = "tcc_golden_skill_next";

    private EdenSeriesSkillHandler() {}

    /** 客户端按键后调用（此时已在服务端线程）。 */
    public static void activate(@Nullable ServerPlayer player) {
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(player, EdenSeriesSkillCurios::matches);
        if (equipped.isEmpty()) {
            // 正常情况下客户端已经拦掉这个包，走到这里说明两端饰品栏不同步，直接忽略
            return;
        }

        long now = level.getGameTime();
        long nextUse = player.getPersistentData().getLong(COOLDOWN_KEY);
        if (nextUse > now) {
            long remainingSeconds = (nextUse - now + 19L) / 20L;
            player.displayClientMessage(Component.translatable("tcc.skill.cooldown", remainingSeconds), true);
            // 1.20.1 的音符盒音效以 Holder 形式注册，需 get() 取出 SoundEvent
            player.playNotifySound(SoundEvents.NOTE_BLOCK_BASS.get(), SoundSource.PLAYERS, 0.5F, 0.8F);
            return;
        }
        if (equipped.getItem() instanceof BaseCurioItem curio && !curio.matchesRestriction(player)) {
            player.displayClientMessage(Component.translatable("tcc.skill.need_pistol"), true);
            return;
        }

        player.getPersistentData().putLong(COOLDOWN_KEY, now + cooldownSeconds(equipped) * 20L);
        applyAura(level, player, equipped);
        player.displayClientMessage(Component.translatable("tcc.skill.activated"), true);
        player.playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8F, 1.1F);
    }

    /** 该饰品配置的技能冷却（秒）。 */
    private static int cooldownSeconds(ItemStack stack) {
        if (stack.getItem() instanceof Eden) {
            return Eden.skillCooldownSeconds();
        }
        if (stack.getItem() instanceof CuiyaoZhiGe) {
            return CuiyaoZhiGe.skillCooldownSeconds();
        }
        if (stack.getItem() instanceof Huangjin) {
            return Huangjin.skillCooldownSeconds();
        }
        return 15;
    }

    /** 分发给具体饰品的光环结算逻辑。 */
    private static void applyAura(ServerLevel level, ServerPlayer player, ItemStack stack) {
        if (stack.getItem() instanceof Eden) {
            Eden.applyAura(level, player);
        } else if (stack.getItem() instanceof CuiyaoZhiGe) {
            CuiyaoZhiGe.applyAura(level, player);
        } else if (stack.getItem() instanceof Huangjin) {
            Huangjin.applyAura(level, player);
        }
    }
}
