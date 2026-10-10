package com.xlxyvergil.tcc.util;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.tcc.items.BaseCurioItem;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

/**
 * 饰品属性效果的统一重算入口：在切枪、装备变更、击杀结算等事件上重建饰品效果。
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

    /** 仅重算依赖其它属性值的饰品，由击杀结算等事件触发。 */
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
