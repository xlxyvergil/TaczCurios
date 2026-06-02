package com.xlxyvergil.tcc.evolution;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class EvolutionExecutor {
    private EvolutionExecutor() {
    }

    public enum NbtMode {
        COPY_ALL,
        KEEP_NEW
    }

    public static boolean evolve(LivingEntity entity, String slotId, Predicate<ItemStack> fromMatcher, Supplier<ItemStack> toStackSupplier,
                                 NbtMode nbtMode, Collection<String> excludeNbtKeys,
                                 BiConsumer<ItemStack, ItemStack> nbtMutator, boolean postTaczChangeEvent) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(slotId, "slotId");
        Objects.requireNonNull(fromMatcher, "fromMatcher");
        Objects.requireNonNull(toStackSupplier, "toStackSupplier");
        Objects.requireNonNull(nbtMode, "nbtMode");

        Collection<String> excludes = excludeNbtKeys == null ? Collections.emptyList() : excludeNbtKeys;

        LazyOptional<ICuriosItemHandler> invOpt = CuriosApi.getCuriosInventory(entity);
        return invOpt.map(inv -> evolve(inv, entity, slotId, fromMatcher, toStackSupplier, nbtMode, excludes, nbtMutator, postTaczChangeEvent))
                .orElse(false);
    }

    public static boolean evolve(LivingEntity entity, Predicate<ItemStack> fromMatcher, Supplier<ItemStack> toStackSupplier,
                                 NbtMode nbtMode, Collection<String> excludeNbtKeys,
                                 BiConsumer<ItemStack, ItemStack> nbtMutator, boolean postTaczChangeEvent) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(fromMatcher, "fromMatcher");
        Objects.requireNonNull(toStackSupplier, "toStackSupplier");
        Objects.requireNonNull(nbtMode, "nbtMode");

        Collection<String> excludes = excludeNbtKeys == null ? Collections.emptyList() : excludeNbtKeys;

        LazyOptional<ICuriosItemHandler> invOpt = CuriosApi.getCuriosInventory(entity);
        return invOpt.map(inv -> evolve(inv, entity, fromMatcher, toStackSupplier, nbtMode, excludes, nbtMutator, postTaczChangeEvent))
                .orElse(false);
    }

    private static boolean evolve(ICuriosItemHandler inv, LivingEntity entity, String slotId, Predicate<ItemStack> fromMatcher,
                                  Supplier<ItemStack> toStackSupplier, NbtMode nbtMode, Collection<String> excludeNbtKeys,
                                  BiConsumer<ItemStack, ItemStack> nbtMutator, boolean postTaczChangeEvent) {
        Optional<ICurioStacksHandler> stacksHandlerOpt = inv.getStacksHandler(slotId);
        if (stacksHandlerOpt.isEmpty()) {
            return false;
        }
        return evolve(stacksHandlerOpt.get(), entity, slotId, fromMatcher, toStackSupplier, nbtMode, excludeNbtKeys, nbtMutator, postTaczChangeEvent);
    }

    private static boolean evolve(ICuriosItemHandler inv, LivingEntity entity, Predicate<ItemStack> fromMatcher,
                                  Supplier<ItemStack> toStackSupplier, NbtMode nbtMode, Collection<String> excludeNbtKeys,
                                  BiConsumer<ItemStack, ItemStack> nbtMutator, boolean postTaczChangeEvent) {
        for (var entry : inv.getCurios().entrySet()) {
            String slotId = entry.getKey();
            ICurioStacksHandler stacksHandler = entry.getValue();
            if (stacksHandler == null) {
                continue;
            }
            if (evolve(stacksHandler, entity, slotId, fromMatcher, toStackSupplier, nbtMode, excludeNbtKeys, nbtMutator, postTaczChangeEvent)) {
                return true;
            }
        }
        return false;
    }

    private static boolean evolve(ICurioStacksHandler stacksHandler, LivingEntity entity, String slotId, Predicate<ItemStack> fromMatcher,
                                  Supplier<ItemStack> toStackSupplier, NbtMode nbtMode, Collection<String> excludeNbtKeys,
                                  BiConsumer<ItemStack, ItemStack> nbtMutator, boolean postTaczChangeEvent) {
        top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler stackHandler = stacksHandler.getStacks();
        for (int i = 0; i < stackHandler.getSlots(); i++) {
            ItemStack oldStack = stackHandler.getStackInSlot(i);
            if (!fromMatcher.test(oldStack)) {
                continue;
            }

            ItemStack newStack = toStackSupplier.get();
            if (nbtMode == NbtMode.COPY_ALL && oldStack.hasTag()) {
                newStack.setTag(Objects.requireNonNull(oldStack.getTag()).copy());
            }
            if (!excludeNbtKeys.isEmpty() && newStack.hasTag()) {
                CompoundTag tag = newStack.getOrCreateTag();
                for (String key : excludeNbtKeys) {
                    tag.remove(key);
                }
            }
            if (nbtMutator != null) {
                nbtMutator.accept(oldStack, newStack);
            }

            boolean hasRenderer = stacksHandler.getRenders().size() > i && stacksHandler.getRenders().get(i);
            SlotContext slotContext = new SlotContext(slotId, entity, i, false, hasRenderer);

            if (oldStack.getItem() instanceof ICurioItem oldCurio) {
                oldCurio.onUnequip(slotContext, ItemStack.EMPTY, oldStack);
            }
            stackHandler.setStackInSlot(i, newStack);
            if (newStack.getItem() instanceof ICurioItem newCurio) {
                newCurio.onEquip(slotContext, oldStack, newStack);
            }
            if (postTaczChangeEvent) {
                AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
            }
            return true;
        }
        return false;
    }
}
