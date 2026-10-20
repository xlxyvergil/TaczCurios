package com.xlxyvergil.tcc.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 效果表的受控视图，由 getActiveEffects() 返回。
 * 拦 Map 层的删除：remove / clear / entrySet().remove；增加效果（put）照常放行。
 *
 * 迭代器删除不拦：vanilla 的批量移除是先发 Remove 事件、再 onEffectRemoved、最后 iterator.remove()，
 * 事件层的取消发生在前面。这里再拦一次会留下"属性已清、实例还在"的状态。
 *
 * 字段本身没有被替换，所以 vanilla 内部逻辑（批量移除、到期、死亡、NBT 读档）走的还是原表。
 */
public final class GuardedEffectMap extends AbstractMap<MobEffect, MobEffectInstance> {

    private final LivingEntity owner;
    private final Map<MobEffect, MobEffectInstance> delegate;
    private Set<Entry<MobEffect, MobEffectInstance>> entries;

    public GuardedEffectMap(LivingEntity owner, Map<MobEffect, MobEffectInstance> delegate) {
        this.owner = owner;
        this.delegate = delegate;
    }

    /** 被包装的真实效果表，用于判断视图是否过期。 */
    public Map<MobEffect, MobEffectInstance> backingMap() {
        return delegate;
    }

    /** 是否拒绝删除这个 key。死亡放行，开关关闭时全部放行。 */
    private boolean protect(Object key) {
        if (!(key instanceof MobEffect effect)) {
            return false;
        }
        if (owner.isDeadOrDying() || !TccEffectGuard.mapGuardEnabled()) {
            return false;
        }
        return TccEffectGuard.isOwnEffect(effect);
    }

    // ---- 读 / 写：全部放行 ----

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public MobEffectInstance get(Object key) {
        return delegate.get(key);
    }

    @Override
    public MobEffectInstance put(MobEffect key, MobEffectInstance value) {
        return delegate.put(key, value);
    }

    @Override
    public void putAll(Map<? extends MobEffect, ? extends MobEffectInstance> map) {
        delegate.putAll(map);
    }

    // ---- 删：受控 ----

    @Override
    public MobEffectInstance remove(Object key) {
        if (protect(key)) {
            return null;
        }
        return delegate.remove(key);
    }

    @Override
    public void clear() {
        entrySet().clear();
    }

    @Override
    public Set<Entry<MobEffect, MobEffectInstance>> entrySet() {
        if (entries == null) {
            entries = new GuardedEntries();
        }
        return entries;
    }

    /** values() / keySet() 由 AbstractMap 基于它派生，getActiveEffects().clear() 也会走到这里。 */
    private final class GuardedEntries extends AbstractSet<Entry<MobEffect, MobEffectInstance>> {

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return delegate.entrySet().contains(o);
        }

        @Override
        public Iterator<Entry<MobEffect, MobEffectInstance>> iterator() {
            // 不拦迭代器删除：批量移除的闸门在事件层（取消发生在 onEffectRemoved 之前），
            // 这里再拦会留下"属性已清、实例还在"的状态；死亡清空和自然到期也走迭代器。
            return delegate.entrySet().iterator();
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Entry<?, ?> entry && protect(entry.getKey())) {
                return false;
            }
            return delegate.entrySet().remove(o);
        }

        @Override
        public void clear() {
            Iterator<Entry<MobEffect, MobEffectInstance>> it = delegate.entrySet().iterator();
            while (it.hasNext()) {
                if (!protect(it.next().getKey())) {
                    it.remove();
                }
            }
        }
    }
}
