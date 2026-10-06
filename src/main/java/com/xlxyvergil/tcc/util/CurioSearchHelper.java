package com.xlxyvergil.tcc.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CurioSearchHelper {
    private CurioSearchHelper() {
    }

    /**
     * 每个实体当前佩戴饰品的非空栈快照（按 tick 失效）。
     * <p>
     * 键为实体本身（WeakHashMap，实体回收后条目自动释放），值为某一 tick 内构建的饰品栈列表。
     * 同一 tick 内的多次扫描（多个饰品的 curioTick、tooltip 逐帧渲染等）共享同一次遍历，
     * 从而把「每 tick 全槽扫描」压缩为每实体每 tick 至多一次；跨 tick 自动重建，
     * 饰品槽位真正变化时另由事件主动失效。
     * <p>
     * 单机下客户端与服务端处于同一 JVM、共享该静态字段，故用同步包装保证线程安全。
     */
    private static final Map<LivingEntity, Snapshot> SNAPSHOTS =
            Collections.synchronizedMap(new WeakHashMap<>());

    private static final class Snapshot {
        final int tick;
        final List<ItemStack> stacks;

        Snapshot(int tick, List<ItemStack> stacks) {
            this.tick = tick;
            this.stacks = stacks;
        }
    }

    public static ItemStack findFirstEquippedStack(LivingEntity livingEntity, Predicate<ItemStack> predicate) {
        if (livingEntity == null || predicate == null) {
            return ItemStack.EMPTY;
        }
        for (ItemStack stack : snapshot(livingEntity)) {
            if (predicate.test(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    /** 遍历实体所有饰品槽位中的物品，对每个非空 ItemStack 执行回调。 */
    public static void forEachEquippedStack(LivingEntity livingEntity, Consumer<ItemStack> consumer) {
        if (livingEntity == null || consumer == null) {
            return;
        }
        for (ItemStack stack : snapshot(livingEntity)) {
            consumer.accept(stack);
        }
    }

    /** 饰品槽位内容变化后主动失效该实体的快照，保证同 tick 内的后续查询立即可见。 */
    public static void invalidate(LivingEntity livingEntity) {
        if (livingEntity != null) {
            SNAPSHOTS.remove(livingEntity);
        }
    }

    /** 清空全部快照，强制后续查询重建。 */
    public static void invalidateAll() {
        SNAPSHOTS.clear();
    }

    private static List<ItemStack> snapshot(LivingEntity livingEntity) {
        int tick = livingEntity.tickCount;
        Snapshot cached = SNAPSHOTS.get(livingEntity);
        if (cached != null && cached.tick == tick) {
            return cached.stacks;
        }

        List<ItemStack> stacks = new ArrayList<>();
        ICuriosItemHandler inv = CuriosApi.getCuriosInventory(livingEntity).orElse(null);
        if (inv != null) {
            for (var entry : inv.getCurios().entrySet()) {
                ICurioStacksHandler stacksHandler = entry.getValue();
                if (stacksHandler == null) {
                    continue;
                }
                var handler = stacksHandler.getStacks();
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        stacks.add(stack);
                    }
                }
            }
        }

        SNAPSHOTS.put(livingEntity, new Snapshot(tick, stacks));
        return stacks;
    }
}
