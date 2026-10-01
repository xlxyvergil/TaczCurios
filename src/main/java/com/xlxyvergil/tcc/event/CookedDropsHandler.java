package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.bound.AoMie;
import com.xlxyvergil.tcc.items.curios.bound.HuajieZhiyan;
import com.xlxyvergil.tcc.items.curios.bound.Kalpas;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

/**
 * 烧烤掉落物统一监听器。
 * 击杀实体时，把该实体掉落的可食用掉落物按熔炉配方替换为产物，
 * 相当于掉落物已经被熔炉烤过。仅对存在熔炉配方（可烧烤）的可食用掉落物生效。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class CookedDropsHandler {

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
            if (stack.isEmpty() || !stack.isEdible()) {
                continue;
            }
            SimpleContainer container = new SimpleContainer(stack.copyWithCount(1));
            Optional<SmeltingRecipe> recipe = level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, container, level);
            if (recipe.isEmpty()) {
                continue;
            }
            ItemStack result = recipe.get().getResultItem(level.registryAccess());
            if (result.isEmpty()) {
                continue;
            }
            drop.setItem(result.copyWithCount(stack.getCount()));
        }
    }
}
