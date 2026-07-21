package com.xlxyvergil.tcc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.xlxyvergil.tcc.helpers.LootTableHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.LootTableLoadEvent;

/**
 * 强制触发战利品表加载事件，确保我们的饰品能被添加到所有箱子中
 */
@Mixin(value = ForgeHooks.class, remap = false)
public class ForgeHooksMixin {

    @Inject(at = @At("RETURN"), method = "loadLootTable", cancellable = true, remap = false)
    private static void onLoadLootTable(Gson gson, ResourceLocation name, JsonElement data, boolean custom, CallbackInfoReturnable<LootTable> info) {
        LootTable returnedTable = info.getReturnValue();

        if (custom && returnedTable != null) {
            try {
                LootTableHelper.unfreezePlease(returnedTable);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            // 创建临时事件处理器来处理战利品表加载
            com.xlxyvergil.tcc.event.TccEventHandler handler = new com.xlxyvergil.tcc.event.TccEventHandler();
            LootTableLoadEvent event = new LootTableLoadEvent(name, returnedTable);
            handler.onLootTablesLoaded(event);

            if (event.isCanceled()) {
                returnedTable = LootTable.EMPTY;
            }

            returnedTable.freeze();

            info.setReturnValue(returnedTable);
        }
    }
}