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
            .icon(() -> new ItemStack(TccItems.SOLDIER_BASIC_TAG))
            .displayItems((parameters, output) -> {
                // 添加所有TaczCurios饰品到创造模式标签页
                output.accept(TccItems.SOLDIER_BASIC_TAG);
                output.accept(TccItems.SOLDIER_SPECIFIC_TAG);
                output.accept(TccItems.HEAVY_CALIBER_TAG);
                output.accept(TccItems.RED_MOVEMENT_TAG);
                output.accept(TccItems.SUMMER_BEACH);
                output.accept(TccItems.BRAHMA_BEASTS);
                output.accept(TccItems.SALVATION);
                output.accept(TccItems.XIORA);
                output.accept(TccItems.RAVEN);
                output.accept(TccItems.ISLAND_BOOM_RAVEN);
                output.accept(TccItems.URAL_WOLF_TAG);
                output.accept(TccItems.ALLOY_DRILL);
                output.accept(TccItems.CAREFUL_HEART);
                output.accept(TccItems.MERGED_RIFLING);
                output.accept(TccItems.DESPICABLE_ACCELERATION);
                output.accept(TccItems.HEAVEN_FIRE_APOCALYPSE);
                output.accept(TccItems.HEAVEN_FIRE_JUDGMENT);
                output.accept(TccItems.SEVEN_THUNDERS);
                output.accept(TccItems.SEVEN_THUNDERS_THUNDER_SEEN);
                output.accept(TccItems.JUDGEMENT_KEY);
                // 新增的饰品
                output.accept(TccItems.BLAZE_STORM);
                output.accept(TccItems.BLAZE_STORM_PRIME);
                output.accept(TccItems.RIPPING_PRIME);
                output.accept(TccItems.CLOSE_COMBAT_PRIME);
                output.accept(TccItems.EVIL_ACCURACY);
                output.accept(TccItems.LIMIT_SPEED);
                output.accept(TccItems.FEROCIOUS_EXTENSION);
                // 新增饰品
                output.accept(TccItems.RIFLING);
                output.accept(TccItems.CLOSE_RANGE_SHOT);
                output.accept(TccItems.HEAVY_FIREPOWER);
                output.accept(TccItems.WASP_STINGER);
                output.accept(TccItems.PROPHECY_PACT);
                output.accept(TccItems.MALIGNANT_SPREAD);
                output.accept(TccItems.CHAMBER);
                output.accept(TccItems.CHAMBER_PRIME);
                // 新增的三个饰品
                output.accept(TccItems.BURST_RELOAD);
                output.accept(TccItems.CORRUPT_MAGAZINE);
                output.accept(TccItems.SPLIT_CHAMBER);
                // 霰弹枪饰品
                output.accept(TccItems.TACTICAL_RELOAD);
                output.accept(TccItems.OVERLOADED_MAGAZINE);
                output.accept(TccItems.INFERNAL_CHAMBER);
                // 手枪饰品
                output.accept(TccItems.SUSTAINED_FIRE);
                output.accept(TccItems.INFECTED_MAGAZINE);
                output.accept(TccItems.DEADLY_SURGE);
                output.accept(TccItems.BULLET_SPREAD);
                // 添加新增的饰品
                output.accept(TccItems.OPPRESSION_POINT);
                output.accept(TccItems.OPPRESSION_POINT_PRIME);
                output.accept(TccItems.SWORD_WIND);
                output.accept(TccItems.SWORD_WIND_PRIME);
                
                // 添加新饰品
                output.accept(TccItems.DEPLETED_RELOAD);
                output.accept(TccItems.BURST_RELOAD_PRIME);
                output.accept(TccItems.TACTICAL_RELOAD_PRIME);
                output.accept(TccItems.SHOTGUN_EXPANSION_PRIME);
                output.accept(TccItems.MAGAZINE_BOOST_PRIME);
                output.accept(TccItems.TANDEM_MAGAZINE_PRIME);
                
                // 添加新的普通饰品
                output.accept(TccItems.SHOTGUN_EXPANSION);
                output.accept(TccItems.MAGAZINE_BOOST);
                output.accept(TccItems.TANDEM_MAGAZINE);
                
                // 添加裂隙级饰品
                output.accept(TccItems.KIKAKU_ICHIJIN);
                output.accept(TccItems.HEAVEN_FIRE_APOCALYPSE_ENDLESS);
                
                // 添加Phase 2 新增饰品 (16个常驻属性饰品)
                output.accept(TccItems.CRITICAL_DELAY);       // G-01 关键延迟
                output.accept(TccItems.LETHAL_CRIT);           // R-01 致命一击
                output.accept(TccItems.WEAKNESS_SENSE);        // R-02 弱点感应
                output.accept(TccItems.DESTRUCTION);           // S-01 破灭
                output.accept(TccItems.DESTRUCTION_PRIME);     // S-02 破灭Prime
                output.accept(TccItems.THUNDER_BARREL);        // S-03 雷筒
                output.accept(TccItems.THUNDER_BARREL_PRIME);  // S-04 雷筒Prime
                output.accept(TccItems.WEAKNESS_MASTERY);      // P-01 弱点专精
                output.accept(TccItems.WEAKNESS_MASTERY_PRIME);// P-02 弱点专精Prime
                output.accept(TccItems.HOLLOW_POINT);          // P-03 空尖弹
                output.accept(TccItems.PISTOL_MASTERY);        // P-04 手枪精通
                output.accept(TccItems.PISTOL_MASTERY_PRIME);  // P-05 手枪精通Prime
                output.accept(TccItems.STEEL_SLASH);           // M-01 斩铁
                output.accept(TccItems.DISMEMBERMENT);         // M-02 肢解
                output.accept(TccItems.SACRIFICE_OPPRESSION);  // M-03 牺牲压迫点
                output.accept(TccItems.SACRIFICE_STEEL);       // M-04 牺牲斩铁

                // 添加Phase 3 新增饰品 (12个Buff触发饰品)
                output.accept(TccItems.ARGON_SCOPE);              // R-03 氩晶瞄具
                output.accept(TccItems.GILDED_ARGON_SCOPE);       // R-04 镀层氩晶瞄具
                output.accept(TccItems.SHARP_BULLET);             // R-05 尖刃弹头
                output.accept(TccItems.GILDED_SPLIT_CHAMBER);     // R-07 镀层分裂膛室
                output.accept(TccItems.LASER_SCOPE);              // S-05 雷射瞄具
                output.accept(TccItems.FRAGMENT_SHOT);            // S-06 破片射击
                output.accept(TccItems.GILDED_INFERNAL_CHAMBER);  // S-08 镀层地狱弹膛
                output.accept(TccItems.HYDRAULIC_CROSSHAIR);      // P-06 液压准心
                output.accept(TccItems.GILDED_HYDRAULIC_CROSSHAIR);// P-07 镀层液压准心
                output.accept(TccItems.SHARP_AMMO);               // P-08 尖锐子弹
                output.accept(TccItems.GILDED_BULLET_SPREAD);     // P-10 镀层弹头扩散
                output.accept(TccItems.GILDED_STEEL_SLASH);       // M-05 镀层斩铁

                // 添加Phase 4 新增饰品 (5个特殊饰品)
                output.accept(TccItems.GILDED_RIFLE_APTITUDE);   // R-06 镀层步枪才能
                output.accept(TccItems.GILDED_SHOTGUN_SAVVY);    // S-07 镀层通晓霰弹枪
                output.accept(TccItems.GILDED_MARKSMAN);         // P-09 镀层准确射手
                output.accept(TccItems.CONDITION_OVERLOAD);      // M-06 异况超量

                // 添加材料物品
                output.accept(TccItems.CORE_FUSION);
                output.accept(TccItems.RIFT_SILVER);
                output.accept(TccItems.COLLAPSE_CRYSTAL);

                // 添加格蕾修 新饰品
                output.accept(TccItems.GRISEO);
                output.accept(TccItems.QIANJIE_YICHENG);
                output.accept(TccItems.HUISHI_ZHIJUAN);
                output.accept(TccItems.YONGJIE_ZHIJIAN);
                output.accept(TccItems.FANXING);
                output.accept(TccItems.SHIJIE_FANYAN);

                // 添加v2v系列
                output.accept(TccItems.VILL_V);
                output.accept(TccItems.XUKONG_WANCANG);
                output.accept(TccItems.YUXI_ZHIXIA);
                output.accept(TccItems.QISHI_ZHIJIAN);
                output.accept(TccItems.LUOXUAN);
                output.accept(TccItems.XUKONG_WANCANG_YZTH);

                // 添加千劫/伊默尔系列饰品
                output.accept(TccItems.KALPAS);
                output.accept(TccItems.IMER);
                output.accept(TccItems.HUAJIE_ZHIYAN);
                output.accept(TccItems.DOMINANCE_KEY);
                output.accept(TccItems.AOMIE);
                output.accept(TccItems.META_MORPH);

                // 添加苏系列饰品
                output.accept(TccItems.SU);
                output.accept(TccItems.WANWU_XIUMIAN);
                output.accept(TccItems.JUEZHE);
                output.accept(TccItems.TINGZHI_ZHIJIAN);
                output.accept(TccItems.TIANHUI);
                output.accept(TccItems.YINGUO_ZHUANLUN);

                // 添加工作方块
                output.accept(TccBlocks.TESHIN_WORKBENCH_ITEM);
            })
            .build());
}
