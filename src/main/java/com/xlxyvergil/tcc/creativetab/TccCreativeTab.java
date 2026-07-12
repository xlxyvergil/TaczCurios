package com.xlxyvergil.tcc.creativetab;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.registries.TccBlocks;
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
public class TccCreativeTab {
    
    // 创造模式标签页的注册器
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TaczCurios.MODID);
    
    // 创建TaczCurios模组的创造模式标签页
    public static final RegistryObject<CreativeModeTab> TACZ_CURIO_TAB = CREATIVE_MODE_TABS.register("tacz_curio_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tcc.tacz_curio_tab"))
            .icon(() -> new ItemStack(TccItems.SOLDIER_BASIC_TAG.get()))
            .displayItems((parameters, output) -> {
                // 添加所有TaczCurios饰品到创造模式标签页
                output.accept(TccItems.SOLDIER_BASIC_TAG.get());
                output.accept(TccItems.SOLDIER_SPECIFIC_TAG.get());
                output.accept(TccItems.HEAVY_CALIBER_TAG.get());
                output.accept(TccItems.RED_MOVEMENT_TAG.get());
                output.accept(TccItems.SUMMER_BEACH.get());
                output.accept(TccItems.BRAHMA_BEASTS.get());
                output.accept(TccItems.SALVATION.get());
                output.accept(TccItems.XIORA.get());
                output.accept(TccItems.RAVEN.get());
                output.accept(TccItems.ISLAND_BOOM_RAVEN.get());
                output.accept(TccItems.URAL_WOLF_TAG.get());
                output.accept(TccItems.ALLOY_DRILL.get());
                output.accept(TccItems.CAREFUL_HEART.get());
                output.accept(TccItems.MERGED_RIFLING.get());
                output.accept(TccItems.DESPICABLE_ACCELERATION.get());
                output.accept(TccItems.HEAVEN_FIRE_APOCALYPSE.get());
                output.accept(TccItems.HEAVEN_FIRE_JUDGMENT.get());
                output.accept(TccItems.SEVEN_THUNDERS.get());
                output.accept(TccItems.SEVEN_THUNDERS_THUNDER_SEEN.get());
                output.accept(TccItems.JUDGEMENT_KEY.get());
                // 新增的饰品
                output.accept(TccItems.BLAZE_STORM.get());
                output.accept(TccItems.BLAZE_STORM_PRIME.get());
                output.accept(TccItems.RIPPING_PRIME.get());
                output.accept(TccItems.CLOSE_COMBAT_PRIME.get());
                output.accept(TccItems.EVIL_ACCURACY.get());
                output.accept(TccItems.LIMIT_SPEED.get());
                output.accept(TccItems.FEROCIOUS_EXTENSION.get());
                // 新增饰品
                output.accept(TccItems.RIFLING.get());
                output.accept(TccItems.CLOSE_RANGE_SHOT.get());
                output.accept(TccItems.HEAVY_FIREPOWER.get());
                output.accept(TccItems.WASP_STINGER.get());
                output.accept(TccItems.PROPHECY_PACT.get());
                output.accept(TccItems.MALIGNANT_SPREAD.get());
                output.accept(TccItems.CHAMBER.get());
                output.accept(TccItems.CHAMBER_PRIME.get());
                // 新增的三个饰品
                output.accept(TccItems.BURST_RELOAD.get());
                output.accept(TccItems.CORRUPT_MAGAZINE.get());
                output.accept(TccItems.SPLIT_CHAMBER.get());
                // 霰弹枪饰品
                output.accept(TccItems.TACTICAL_RELOAD.get());
                output.accept(TccItems.OVERLOADED_MAGAZINE.get());
                output.accept(TccItems.INFERNAL_CHAMBER.get());
                // 手枪饰品
                output.accept(TccItems.SUSTAINED_FIRE.get());
                output.accept(TccItems.INFECTED_MAGAZINE.get());
                output.accept(TccItems.DEADLY_SURGE.get());
                output.accept(TccItems.BULLET_SPREAD.get());
                // 添加新增的饰品
                output.accept(TccItems.OPPRESSION_POINT.get());
                output.accept(TccItems.OPPRESSION_POINT_PRIME.get());
                output.accept(TccItems.SWORD_WIND.get());
                output.accept(TccItems.SWORD_WIND_PRIME.get());
                
                // 添加新饰品
                output.accept(TccItems.DEPLETED_RELOAD.get());
                output.accept(TccItems.BURST_RELOAD_PRIME.get());
                output.accept(TccItems.TACTICAL_RELOAD_PRIME.get());
                output.accept(TccItems.SHOTGUN_EXPANSION_PRIME.get());
                output.accept(TccItems.MAGAZINE_BOOST_PRIME.get());
                output.accept(TccItems.TANDEM_MAGAZINE_PRIME.get());
                
                // 添加新的普通饰品
                output.accept(TccItems.SHOTGUN_EXPANSION.get());
                output.accept(TccItems.MAGAZINE_BOOST.get());
                output.accept(TccItems.TANDEM_MAGAZINE.get());
                
                // 添加裂隙级饰品
                output.accept(TccItems.KIKAKU_ICHIJIN.get());
                output.accept(TccItems.HEAVEN_FIRE_APOCALYPSE_ENDLESS.get());
                
                // 添加Phase 2 新增饰品 (16个常驻属性饰品)
                output.accept(TccItems.CRITICAL_DELAY.get());       // G-01 关键延迟
                output.accept(TccItems.LETHAL_CRIT.get());           // R-01 致命一击
                output.accept(TccItems.WEAKNESS_SENSE.get());        // R-02 弱点感应
                output.accept(TccItems.DESTRUCTION.get());           // S-01 破灭
                output.accept(TccItems.DESTRUCTION_PRIME.get());     // S-02 破灭Prime
                output.accept(TccItems.THUNDER_BARREL.get());        // S-03 雷筒
                output.accept(TccItems.THUNDER_BARREL_PRIME.get());  // S-04 雷筒Prime
                output.accept(TccItems.WEAKNESS_MASTERY.get());      // P-01 弱点专精
                output.accept(TccItems.WEAKNESS_MASTERY_PRIME.get());// P-02 弱点专精Prime
                output.accept(TccItems.HOLLOW_POINT.get());          // P-03 空尖弹
                output.accept(TccItems.PISTOL_MASTERY.get());        // P-04 手枪精通
                output.accept(TccItems.PISTOL_MASTERY_PRIME.get());  // P-05 手枪精通Prime
                output.accept(TccItems.STEEL_SLASH.get());           // M-01 斩铁
                output.accept(TccItems.DISMEMBERMENT.get());         // M-02 肢解
                output.accept(TccItems.SACRIFICE_OPPRESSION.get());  // M-03 牺牲压迫点
                output.accept(TccItems.SACRIFICE_STEEL.get());       // M-04 牺牲斩铁

                // 添加Phase 3 新增饰品 (12个Buff触发饰品)
                output.accept(TccItems.ARGON_SCOPE.get());              // R-03 氩晶瞄具
                output.accept(TccItems.GILDED_ARGON_SCOPE.get());       // R-04 镀层氩晶瞄具
                output.accept(TccItems.SHARP_BULLET.get());             // R-05 尖刃弹头
                output.accept(TccItems.GILDED_SPLIT_CHAMBER.get());     // R-07 镀层分裂膛室
                output.accept(TccItems.LASER_SCOPE.get());              // S-05 雷射瞄具
                output.accept(TccItems.FRAGMENT_SHOT.get());            // S-06 破片射击
                output.accept(TccItems.GILDED_INFERNAL_CHAMBER.get());  // S-08 镀层地狱弹膛
                output.accept(TccItems.HYDRAULIC_CROSSHAIR.get());      // P-06 液压准心
                output.accept(TccItems.GILDED_HYDRAULIC_CROSSHAIR.get());// P-07 镀层液压准心
                output.accept(TccItems.SHARP_AMMO.get());               // P-08 尖锐子弹
                output.accept(TccItems.GILDED_BULLET_SPREAD.get());     // P-10 镀层弹头扩散
                output.accept(TccItems.GILDED_STEEL_SLASH.get());       // M-05 镀层斩铁

                // 添加Phase 4 新增饰品 (5个特殊饰品)
                output.accept(TccItems.GILDED_RIFLE_APTITUDE.get());   // R-06 镀层步枪才能
                output.accept(TccItems.GILDED_SHOTGUN_SAVVY.get());    // S-07 镀层通晓霰弹枪
                output.accept(TccItems.GILDED_MARKSMAN.get());         // P-09 镀层准确射手
                output.accept(TccItems.CONDITION_OVERLOAD.get());      // M-06 异况超量

                // 添加材料物品
                output.accept(TccItems.CORE_FUSION.get());
                output.accept(TccItems.RIFT_SILVER.get());
                // 添加工作方块
                output.accept(TccBlocks.TESHIN_WORKBENCH_ITEM.get());
            })
            .build());
}
