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

public class TccCreativeTab {
    
    // 创造模式标签页的注册器
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TaczCurios.MODID);
    
    public static final RegistryObject<CreativeModeTab> TACZ_CURIO_TAB = CREATIVE_MODE_TABS.register("tacz_curio_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tcc.tacz_curio_tab"))
            .icon(() -> new ItemStack(TccItems.SOLDIER_BASIC_TAG))
            .displayItems((parameters, output) -> {
                // 1. 工作方块
                output.accept(TccBlocks.TESHIN_WORKBENCH_ITEM);

                // 2. 材料
                output.accept(TccItems.CORE_FUSION);
                output.accept(TccItems.FUSION_VESSEL);
                output.accept(TccItems.RIFT_SILVER);
                output.accept(TccItems.COLLAPSE_CRYSTAL);

                // 3. tcc 常见稀有度
                output.accept(TccItems.PROPHECY_PACT);
                output.accept(TccItems.BURST_RELOAD);
                output.accept(TccItems.SUSTAINED_FIRE);
                output.accept(TccItems.OPPRESSION_POINT);
                output.accept(TccItems.SWORD_WIND);
                output.accept(TccItems.SHOTGUN_EXPANSION);
                output.accept(TccItems.MAGAZINE_BOOST);
                output.accept(TccItems.TANDEM_MAGAZINE);
                output.accept(TccItems.LETHAL_CRIT);
                output.accept(TccItems.THUNDER_BARREL);
                output.accept(TccItems.PISTOL_MASTERY);
                output.accept(TccItems.STEEL_SLASH);
                output.accept(TccItems.DISMEMBERMENT);
                output.accept(TccItems.FRAGMENT_SHOT);
                output.accept(TccItems.HYDRAULIC_CROSSHAIR);

                // 4. tcc 罕见稀有度
                output.accept(TccItems.SOLDIER_BASIC_TAG);
                output.accept(TccItems.RED_MOVEMENT_TAG);
                output.accept(TccItems.URAL_WOLF_TAG);
                output.accept(TccItems.LIMIT_SPEED);
                output.accept(TccItems.FEROCIOUS_EXTENSION);
                output.accept(TccItems.RIFLING);
                output.accept(TccItems.CLOSE_RANGE_SHOT);
                output.accept(TccItems.WASP_STINGER);
                output.accept(TccItems.TACTICAL_RELOAD);
                output.accept(TccItems.WEAKNESS_MASTERY);
                output.accept(TccItems.SHARP_BULLET);
                output.accept(TccItems.LASER_SCOPE);
                output.accept(TccItems.SHARP_AMMO);

                // 5. tcc 稀有稀有度
                output.accept(TccItems.HEAVY_CALIBER_TAG);
                output.accept(TccItems.DESPICABLE_ACCELERATION);
                output.accept(TccItems.ALLOY_DRILL);
                output.accept(TccItems.BLAZE_STORM);
                output.accept(TccItems.EVIL_ACCURACY);
                output.accept(TccItems.HEAVY_FIREPOWER);
                output.accept(TccItems.MALIGNANT_SPREAD);
                output.accept(TccItems.CHAMBER);
                output.accept(TccItems.CORRUPT_MAGAZINE);
                output.accept(TccItems.SPLIT_CHAMBER);
                output.accept(TccItems.OVERLOADED_MAGAZINE);
                output.accept(TccItems.INFERNAL_CHAMBER);
                output.accept(TccItems.INFECTED_MAGAZINE);
                output.accept(TccItems.DEADLY_SURGE);
                output.accept(TccItems.BULLET_SPREAD);
                output.accept(TccItems.DEPLETED_RELOAD);
                output.accept(TccItems.CRITICAL_DELAY);
                output.accept(TccItems.WEAKNESS_SENSE);
                output.accept(TccItems.DESTRUCTION);
                output.accept(TccItems.HOLLOW_POINT);
                output.accept(TccItems.ARGON_SCOPE);
                output.accept(TccItems.CONDITION_OVERLOAD);

                // 6. tcc 传说稀有度
                output.accept(TccItems.SOLDIER_SPECIFIC_TAG);
                output.accept(TccItems.MERGED_RIFLING);
                output.accept(TccItems.CAREFUL_HEART);
                output.accept(TccItems.BLAZE_STORM_PRIME);
                output.accept(TccItems.RIPPING_PRIME);
                output.accept(TccItems.CLOSE_COMBAT_PRIME);
                output.accept(TccItems.CHAMBER_PRIME);
                output.accept(TccItems.OPPRESSION_POINT_PRIME);
                output.accept(TccItems.SWORD_WIND_PRIME);
                output.accept(TccItems.BURST_RELOAD_PRIME);
                output.accept(TccItems.TACTICAL_RELOAD_PRIME);
                output.accept(TccItems.SHOTGUN_EXPANSION_PRIME);
                output.accept(TccItems.MAGAZINE_BOOST_PRIME);
                output.accept(TccItems.TANDEM_MAGAZINE_PRIME);
                output.accept(TccItems.DESTRUCTION_PRIME);
                output.accept(TccItems.THUNDER_BARREL_PRIME);
                output.accept(TccItems.WEAKNESS_MASTERY_PRIME);
                output.accept(TccItems.PISTOL_MASTERY_PRIME);
                output.accept(TccItems.SACRIFICE_OPPRESSION);
                output.accept(TccItems.SACRIFICE_STEEL);

                // 7. tcc 镀层饰品
                output.accept(TccItems.GILDED_ARGON_SCOPE);
                output.accept(TccItems.GILDED_SPLIT_CHAMBER);
                output.accept(TccItems.GILDED_INFERNAL_CHAMBER);
                output.accept(TccItems.GILDED_HYDRAULIC_CROSSHAIR);
                output.accept(TccItems.GILDED_BULLET_SPREAD);
                output.accept(TccItems.GILDED_STEEL_SLASH);
                output.accept(TccItems.GILDED_RIFLE_APTITUDE);
                output.accept(TccItems.GILDED_SHOTGUN_SAVVY);
                output.accept(TccItems.GILDED_MARKSMAN);

                // 8. tcc 裂隙稀有度
                output.accept(TccItems.KIKAKU_ICHIJIN);

                // 9. 神之键/逐火之蛾（按 achievement_definitions.json 进化链路排序）
                // 系列1 涤罪七雷/渡鸦
                output.accept(TccItems.SEVEN_THUNDERS);
                output.accept(TccItems.XIORA);
                output.accept(TccItems.RAVEN);
                output.accept(TccItems.SEVEN_THUNDERS_THUNDER_SEEN);
                output.accept(TccItems.ISLAND_BOOM_RAVEN);
                output.accept(TccItems.JUDGEMENT_KEY);
                // 系列2 天火
                output.accept(TccItems.HEAVEN_FIRE_JUDGMENT);
                output.accept(TccItems.SUMMER_BEACH);
                output.accept(TccItems.HEAVEN_FIRE_APOCALYPSE);
                output.accept(TccItems.BRAHMA_BEASTS);
                output.accept(TccItems.SALVATION);
                output.accept(TccItems.HEAVEN_FIRE_APOCALYPSE_ENDLESS);
                // 系列3 格蕾修
                output.accept(TccItems.GRISEO);
                output.accept(TccItems.QIANJIE_YICHENG);
                output.accept(TccItems.HUISHI_ZHIJUAN);
                output.accept(TccItems.FANXING);
                output.accept(TccItems.YONGJIE_ZHIJIAN);
                output.accept(TccItems.SHIJIE_FANYAN);
                // 系列4 千劫
                output.accept(TccItems.KALPAS);
                output.accept(TccItems.IMER);
                output.accept(TccItems.HUAJIE_ZHIYAN);
                output.accept(TccItems.DOMINANCE_KEY);
                output.accept(TccItems.AOMIE);
                output.accept(TccItems.META_MORPH);
                // 系列5 苏
                output.accept(TccItems.SU);
                output.accept(TccItems.WANWU_XIUMIAN);
                output.accept(TccItems.JUEZHE);
                output.accept(TccItems.TINGZHI_ZHIJIAN);
                output.accept(TccItems.TIANHUI);
                output.accept(TccItems.YINGUO_ZHUANLUN);
                // 系列6 维尔薇
                output.accept(TccItems.VILL_V);
                output.accept(TccItems.XUKONG_WANCANG);
                output.accept(TccItems.YUXI_ZHIXIA);
                output.accept(TccItems.QISHI_ZHIJIAN);
                output.accept(TccItems.LUOXUAN);
                output.accept(TccItems.XUKONG_WANCANG_YZTH);
                // 系列7 阿波尼亚
                output.accept(TccItems.APONIA);
                output.accept(TccItems.WANGSHI_DE_KUQIU);
                output.accept(TccItems.SHENZUI_ZHIJIAN);
                output.accept(TccItems.WANGSHI_DE_KUQIU_MINGZHIQI);
                output.accept(TccItems.JIELV);
                output.accept(TccItems.SHENEN_JIEJIE);
                // 系列8 伊甸
                output.accept(TccItems.EDEN);
                output.accept(TccItems.EDEN_STAR);
                output.accept(TccItems.CUIYAO_ZHI_GE);
                output.accept(TccItems.TUNTIAN_ZHIJIAN);
                output.accept(TccItems.HUANGJIN);
                output.accept(TccItems.QIDIAN_CHONGGOU);
                // 系列9 科斯魔
                output.accept(TccItems.KOSMA);
                output.accept(TccItems.DIZANG_YUHUN);
                output.accept(TccItems.LIMING_ZHI_SHAO);
                output.accept(TccItems.QINSHI_ZHIJIAN);
                output.accept(TccItems.XUGUANG);
                output.accept(TccItems.YUHUN_SHIXIAN);
                // 系列10 梅比乌斯
                output.accept(TccItems.MEBIUS);
                output.accept(TccItems.WANGSHI_DE_SHEYING);
                output.accept(TccItems.SHIJIE_ZHI_SHE);
                output.accept(TccItems.SI_ZHI_YI);
                output.accept(TccItems.WUXIAN);
                output.accept(TccItems.SHESHA);
                // 系列11 华
                output.accept(TccItems.HUA);
                output.accept(TccItems.YUDUCHEN);
                output.accept(TccItems.DUCHEN_ZHI_YU);
                output.accept(TccItems.FANCHEN_NANDU);
                output.accept(TccItems.FUSHENG);
                output.accept(TccItems.BUSHI_SHIWU);
                // 系列12 帕朵
                output.accept(TccItems.PADO_PHILIPIS);
                output.accept(TccItems.WANGSHI_DE_HUANMENG);
                output.accept(TccItems.LUEJI_ZHI_SHOU);
                output.accept(TccItems.YE_ZHI_TONG);
                output.accept(TccItems.KONGMENG);
                output.accept(TccItems.LA_ZHI_YAN);
                // 最终阶段
                output.accept(TccItems.ZEN_WO);
                output.accept(TccItems.HEIYUAN_BAIHUA);
            })
            .build());
}
