package com.xlxyvergil.tcc.creativetab;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TaczItems;
import com.xlxyvergil.tcc.registries.TaczBlocks;
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
                output.accept(TaczItems.ALLOY_DRILL.get());
                output.accept(TaczItems.CAREFUL_HEART.get());
                output.accept(TaczItems.MERGED_RIFLING.get());
                output.accept(TaczItems.DESPICABLE_ACCELERATION.get());
                output.accept(TaczItems.HEAVEN_FIRE_APOCALYPSE.get());
                output.accept(TaczItems.HEAVEN_FIRE_JUDGMENT.get());
                // 新增的饰品
                output.accept(TaczItems.BLAZE_STORM.get());
                output.accept(TaczItems.BLAZE_STORM_PRIME.get());
                output.accept(TaczItems.RIPPING_PRIME.get());
                output.accept(TaczItems.CLOSE_COMBAT_PRIME.get());
                output.accept(TaczItems.EVIL_ACCURACY.get());
                output.accept(TaczItems.LIMIT_SPEED.get());
                output.accept(TaczItems.FEROCIOUS_EXTENSION.get());
                // 新增饰品
                output.accept(TaczItems.RIFLING.get());
                output.accept(TaczItems.CLOSE_RANGE_SHOT.get());
                output.accept(TaczItems.HEAVY_FIREPOWER.get());
                output.accept(TaczItems.WASP_STINGER.get());
                output.accept(TaczItems.PROPHECY_PACT.get());
                output.accept(TaczItems.MALIGNANT_SPREAD.get());
                output.accept(TaczItems.CHAMBER.get());
                output.accept(TaczItems.CHAMBER_PRIME.get());
                // 新增的三个饰品
                output.accept(TaczItems.MAGAZINE_BOOST.get());
                output.accept(TaczItems.CORRUPT_MAGAZINE.get());
                output.accept(TaczItems.SPLIT_CHAMBER.get());
                // 霰弹枪饰品
                output.accept(TaczItems.TACTICAL_RELOAD.get());
                output.accept(TaczItems.OVERLOADED_MAGAZINE.get());
                output.accept(TaczItems.INFERNAL_CHAMBER.get());
                // 手枪饰品
                output.accept(TaczItems.SUSTAINED_FIRE.get());
                output.accept(TaczItems.INFECTED_MAGAZINE.get());
                output.accept(TaczItems.DEADLY_SURGE.get());
                output.accept(TaczItems.BULLET_SPREAD.get());
                // 添加新增的饰品
                output.accept(TaczItems.OPPRESSION_POINT.get());
                output.accept(TaczItems.OPPRESSION_POINT_PRIME.get());
                output.accept(TaczItems.SWORD_WIND.get());
                output.accept(TaczItems.SWORD_WIND_PRIME.get());
                
                // 添加材料物品
                output.accept(TaczItems.CORE_FUSION.get());
                output.accept(TaczItems.RIFT_SILVER.get());
                // 添加工作方块
                output.accept(TaczBlocks.TESHIN_WORKBENCH_ITEM.get());
            })
            .build());
}