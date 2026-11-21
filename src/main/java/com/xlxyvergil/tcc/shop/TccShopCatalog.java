package com.xlxyvergil.tcc.shop;

import com.xlxyvergil.tcc.registries.TccItems;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 泰辛商店的商品总表：取代原职业村民的五级交易。
 * 价格完全沿用原村民交易表（2 / 6 / 12 / 24 / 48 / 64）。
 * 不再有职业等级、经验、使用次数上限与随机涨价。
 */
public final class TccShopCatalog {

    private static final List<TccShopEntry> ENTRIES = buildEntries();

    private TccShopCatalog() {
    }

    private static List<TccShopEntry> buildEntries() {
        List<TccShopEntry> entries = new ArrayList<>(37);
        // 与原交易表 tier1 完全同序
        addTier(entries, 2,
                TccItems.CORE_FUSION,
                TccItems.PROPHECY_PACT,
                TccItems.BURST_RELOAD,
                TccItems.SUSTAINED_FIRE,
                TccItems.OPPRESSION_POINT,
                TccItems.SWORD_WIND,
                TccItems.SHOTGUN_EXPANSION,
                TccItems.MAGAZINE_BOOST,
                TccItems.TANDEM_MAGAZINE);
        addTier(entries, 6,
                TccItems.LETHAL_CRIT,
                TccItems.THUNDER_BARREL,
                TccItems.PISTOL_MASTERY,
                TccItems.STEEL_SLASH,
                TccItems.DISMEMBERMENT,
                TccItems.FRAGMENT_SHOT,
                TccItems.HYDRAULIC_CROSSHAIR);
        addTier(entries, 12,
                TccItems.RIFLING,
                TccItems.CLOSE_RANGE_SHOT,
                TccItems.WASP_STINGER,
                TccItems.URAL_WOLF_TAG,
                TccItems.SOLDIER_BASIC_TAG,
                TccItems.RED_MOVEMENT_TAG,
                TccItems.LIMIT_SPEED,
                TccItems.FEROCIOUS_EXTENSION,
                TccItems.TACTICAL_RELOAD);
        addTier(entries, 24,
                TccItems.WEAKNESS_MASTERY,
                TccItems.SHARP_BULLET,
                TccItems.LASER_SCOPE,
                TccItems.SHARP_AMMO);
        addTier(entries, 48,
                TccItems.ALLOY_DRILL,
                TccItems.EVIL_ACCURACY,
                TccItems.CHAMBER,
                TccItems.SPLIT_CHAMBER,
                TccItems.BULLET_SPREAD,
                TccItems.WEAKNESS_SENSE,
                TccItems.DESTRUCTION);
        addTier(entries, 64,
                TccItems.RIFT_SILVER);

        // 稳定排序：同价保持上面的原始顺序
        entries.sort(Comparator.comparingInt(TccShopEntry::cost));
        return List.copyOf(entries);
    }

    private static void addTier(List<TccShopEntry> entries, int cost, Item... items) {
        for (Item item : items) {
            entries.add(TccShopEntry.of(item, cost));
        }
    }

    public static List<TccShopEntry> entries() {
        return ENTRIES;
    }

    public static int size() {
        return ENTRIES.size();
    }

    public static Optional<TccShopEntry> get(int index) {
        return index >= 0 && index < ENTRIES.size() ? Optional.of(ENTRIES.get(index)) : Optional.empty();
    }
}
