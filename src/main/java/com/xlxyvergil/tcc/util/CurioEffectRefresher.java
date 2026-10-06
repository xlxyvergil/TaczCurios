package com.xlxyvergil.tcc.util;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.tcc.items.BaseCurioItem;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

/**
 * 统一重算佩戴者饰品属性效果的公共入口。
 * <p>
 * 背景：饰品的属性效果原先多数在 {@code curioTick} 中每 tick 重算，开销很大。
 * 改为事件驱动后，由本入口在「切枪 / 饰品槽变更 / 装备变更」等事件上统一重建，
 * 并对依赖其它属性值的饰品做低频兜底（Forge 1.20.1 没有属性变更事件）。
 */
public final class CurioEffectRefresher {

    private CurioEffectRefresher() {
    }

    /**
     * 刷新全部饰品（保留切枪时的全量刷新语义），末尾只触发一次 TACZ 缓存重算。
     */
    public static void refreshAll(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        if (rebuild(entity, item -> true)) {
            AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
        }
    }

    /**
     * 事件驱动刷新：重建所有已改为事件驱动的饰品。用于饰品槽变更、装备变更等场景。
     * <p>
     * 重建写方后立即重算依赖其它属性值的读方，保证跨饰品属性链（如虚数抗性 → 幸运）即时同步。
     */
    public static void refreshEventDriven(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        boolean changed = rebuild(entity, BaseCurioItem::isEventDriven);
        reapplyDerived(entity);
        if (changed) {
            AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
        }
    }

    /**
     * 低频兜底：仅重算依赖其它属性值的饰品。这类属性没有变更事件可监听，只能定期对齐。
     */
    public static void refreshDerivedAttributes(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        reapplyDerived(entity);
    }

    /**
     * 只重算修饰符、不做清理，避免误触发部分饰品 {@code removeEffects} 中的副作用。
     * 武器类型等会改变“是否生效”的输入由切枪事件负责清理，不依赖此处。
     */
    private static void reapplyDerived(LivingEntity entity) {
        CurioSearchHelper.forEachEquippedStack(entity, stack -> {
            if (stack.getItem() instanceof BaseCurioItem item && item.dependsOnOtherAttributes()) {
                item.reapplyEffects(entity, stack);
            }
        });
    }

    /**
     * 遍历佩戴者的饰品，对通过筛选的饰品重建属性效果。
     *
     * @return 是否至少重建了一个饰品
     */
    private static boolean rebuild(LivingEntity entity, Predicate<BaseCurioItem> filter) {
        if (entity == null) {
            return false;
        }
        boolean[] changed = {false};
        CurioSearchHelper.forEachEquippedStack(entity, stack -> {
            if (stack.getItem() instanceof BaseCurioItem item && filter.test(item)) {
                item.rebuildEffects(entity, stack);
                changed[0] = true;
            }
        });
        return changed[0];
    }
}
