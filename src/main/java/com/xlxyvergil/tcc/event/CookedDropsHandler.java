package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.AoMie;
import com.xlxyvergil.tcc.items.curios.bound.HuajieZhiyan;
import com.xlxyvergil.tcc.items.curios.bound.Kalpas;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Optional;

/**
 * 烧烤掉落物统一监听器。
 * 击杀实体时，把该实体掉落的可食用掉落物按熔炉配方替换为产物，
 * 相当于掉落物已经被熔炉烤过。仅对存在熔炉配方（可烧烤）的可食用掉落物生效。
 */
@EventBusSubscriber(modid = TaczCurios.MODID)
public final class CookedDropsHandler {

    private CookedDropsHandler() {
    }

    private static boolean hasCookedDrops(LivingEntity entity) {
        return Kalpas.isEquipped(entity)
                || HuajieZhiyan.isEquipped(entity)
                || AoMie.isEquipped(entity);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity killer) || killer.level().isClientSide) {
            return;
        }
        if (!hasCookedDrops(killer)) {
            return;
        }
        Level level = killer.level();
        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (stack.isEmpty() || !stack.has(DataComponents.FOOD)) {
                continue;
            }
            SingleRecipeInput input = new SingleRecipeInput(stack.copyWithCount(1));
            Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, input, level);
            if (recipe.isEmpty()) {
                continue;
            }
            ItemStack result = recipe.get().value().getResultItem(level.registryAccess());
            if (result.isEmpty()) {
                continue;
            }
            drop.setItem(result.copyWithCount(stack.getCount()));
        }
    }
}
