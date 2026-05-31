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
                output.accept(TaczItems.SUMMER_BEACH.get());
                output.accept(TaczItems.BRAHMA_BEASTS.get());
                output.accept(TaczItems.SALVATION.get());
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
                output.accept(TaczItems.BURST_RELOAD.get());
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
                
                // 添加新饰品
                output.accept(TaczItems.DEPLETED_RELOAD.get());
                output.accept(TaczItems.BURST_RELOAD_PRIME.get());
                output.accept(TaczItems.TACTICAL_RELOAD_PRIME.get());
                output.accept(TaczItems.SHOTGUN_EXPANSION_PRIME.get());
                output.accept(TaczItems.MAGAZINE_BOOST_PRIME.get());
                output.accept(TaczItems.TANDEM_MAGAZINE_PRIME.get());
                
                // 添加新的普通饰品
                output.accept(TaczItems.SHOTGUN_EXPANSION.get());
                output.accept(TaczItems.MAGAZINE_BOOST.get());
                output.accept(TaczItems.TANDEM_MAGAZINE.get());
                
                // 添加裂隙级饰品
                output.accept(TaczItems.KIKAKU_ICHIJIN.get());
                output.accept(TaczItems.HEAVEN_FIRE_APOCALYPSE_ENDLESS.get());
                
                // 添加Phase 2 新增饰品 (16个常驻属性饰品)
                output.accept(TaczItems.CRITICAL_DELAY.get());       // G-01 关键延迟
                output.accept(TaczItems.LETHAL_CRIT.get());           // R-01 致命一击
                output.accept(TaczItems.WEAKNESS_SENSE.get());        // R-02 弱点感应
                output.accept(TaczItems.DESTRUCTION.get());           // S-01 破灭
                output.accept(TaczItems.DESTRUCTION_PRIME.get());     // S-02 破灭Prime
                output.accept(TaczItems.THUNDER_BARREL.get());        // S-03 雷筒
                output.accept(TaczItems.THUNDER_BARREL_PRIME.get());  // S-04 雷筒Prime
                output.accept(TaczItems.WEAKNESS_MASTERY.get());      // P-01 弱点专精
                output.accept(TaczItems.WEAKNESS_MASTERY_PRIME.get());// P-02 弱点专精Prime
                output.accept(TaczItems.HOLLOW_POINT.get());          // P-03 空尖弹
                output.accept(TaczItems.PISTOL_MASTERY.get());        // P-04 手枪精通
                output.accept(TaczItems.PISTOL_MASTERY_PRIME.get());  // P-05 手枪精通Prime
                output.accept(TaczItems.STEEL_SLASH.get());           // M-01 斩铁
                output.accept(TaczItems.DISMEMBERMENT.get());         // M-02 肢解
                output.accept(TaczItems.SACRIFICE_OPPRESSION.get());  // M-03 牺牲压迫点
                output.accept(TaczItems.SACRIFICE_STEEL.get());       // M-04 牺牲斩铁

                // 添加Phase 3 新增饰品 (12个Buff触发饰品)
                output.accept(TaczItems.ARGON_SCOPE.get());              // R-03 氩晶瞄具
                output.accept(TaczItems.GILDED_ARGON_SCOPE.get());       // R-04 镀层氩晶瞄具
                output.accept(TaczItems.SHARP_BULLET.get());             // R-05 尖刃弹头
                output.accept(TaczItems.GILDED_SPLIT_CHAMBER.get());     // R-07 镀层分裂膛室
                output.accept(TaczItems.LASER_SCOPE.get());              // S-05 雷射瞄具
                output.accept(TaczItems.FRAGMENT_SHOT.get());            // S-06 破片射击
                output.accept(TaczItems.GILDED_INFERNAL_CHAMBER.get());  // S-08 镀层地狱弹膛
                output.accept(TaczItems.HYDRAULIC_CROSSHAIR.get());      // P-06 液压准心
                output.accept(TaczItems.GILDED_HYDRAULIC_CROSSHAIR.get());// P-07 镀层液压准心
                output.accept(TaczItems.SHARP_AMMO.get());               // P-08 尖锐子弹
                output.accept(TaczItems.GILDED_BULLET_SPREAD.get());     // P-10 镀层弹头扩散
                output.accept(TaczItems.GILDED_STEEL_SLASH.get());       // M-05 镀层斩铁

                // 添加Phase 4 新增饰品 (5个特殊饰品)
                output.accept(TaczItems.GILDED_RIFLE_APTITUDE.get());   // R-06 镀层步枪才能
                output.accept(TaczItems.GILDED_SHOTGUN_SAVVY.get());    // S-07 镀层通晓霰弹枪
                output.accept(TaczItems.GILDED_MARKSMAN.get());         // P-09 镀层准确射手
                output.accept(TaczItems.CONDITION_OVERLOAD.get());      // M-06 异况超量
                output.accept(TaczItems.SACRIFICE_SET_BONUS.get());     // M-07 牺牲套装组合

                // 添加材料物品
                output.accept(TaczItems.CORE_FUSION.get());
                output.accept(TaczItems.RIFT_SILVER.get());
                // 添加工作方块
                output.accept(TaczBlocks.TESHIN_WORKBENCH_ITEM.get());
            })
            .build());
}