package com.xlxyvergil.tcc.creativetab;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TaczItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * TaczCurios模组的创造模式物品栏标签页
 * 用于在创造模式中组织模组的所有饰品物品
 */
public class TaczCreativeTab {
    
    // 创造模式标签页的注册器
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TaczCurios.MODID);
    
    // 创建TaczCurios模组的创造模式标签页
    public static final RegistryObject<CreativeModeTab> TACZ_CURIO_TAB = CREATIVE_MODE_TABS.register("tacz_curio_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tcc.tacz_curio_tab"))
            .icon(() -> new ItemStack(TaczItems.SOLDIER_BASIC_TAG.get()))
            .displayItems((parameters, output) -> {
                // 添加所有TaczCurios饰品到创造模式标签页
                output.accept(TaczItems.SOLDIER_BASIC_TAG.get());
                output.accept(TaczItems.SOLDIER_SPECIFIC_TAG.get());
                output.accept(TaczItems.HEAVY_CALIBER_TAG.get());
                output.accept(TaczItems.RED_MOVEMENT_TAG.get());
                output.accept(TaczItems.URAL_WOLF_TAG.get());
            })
            .build());
}