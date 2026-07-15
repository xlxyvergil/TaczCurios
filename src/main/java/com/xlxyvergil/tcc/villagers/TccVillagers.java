package com.xlxyvergil.tcc.villagers;

import com.google.common.collect.ImmutableSet;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.registries.TccPoiTypes;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class TccVillagers {
    private TccVillagers() {
    }

    public static final ResourceLocation ID = new ResourceLocation(TaczCurios.MODID, "teshin");

    public static final VillagerProfession PROFESSION = new VillagerProfession(
            ID.toString(),
            e -> e.is(TccPoiTypes.POI_KEY),
            e -> e.is(TccPoiTypes.POI_KEY),
            ImmutableSet.of(),
            ImmutableSet.of(),
            SoundEvents.VILLAGER_WORK_LIBRARIAN
    );

    public static void init() {
        ForgeRegistries.VILLAGER_PROFESSIONS.register(ID, PROFESSION);

        // 第1级：基础标签
        sellItems(1, TccItems.SOLDIER_BASIC_TAG, 8, 1, 16, 2);
        sellItems(1, TccItems.PROPHECY_PACT, 8, 1, 16, 2);

        // 第2级：中级标签
        sellItems(2, TccItems.RED_MOVEMENT_TAG, 16, 1, 16, 10);
        sellItems(2, TccItems.URAL_WOLF_TAG, 16, 1, 16, 10);

        // 第3级：高级标签
        sellItems(3, TccItems.LIMIT_SPEED, 16, 1, 16, 20);
        sellItems(3, TccItems.FEROCIOUS_EXTENSION, 16, 1, 16, 20);

        // 第4级：高级物品
        sellItems(4, TccItems.WASP_STINGER, 16, 1, 16, 35);
        sellItems(4, TccItems.CLOSE_RANGE_SHOT, 16, 1, 16, 35);

        // 第5级：顶级物品
        sellItems(5, TccItems.RIFLING, 16, 1, 16, 50);
        sellItems(5, TccItems.RIFT_SILVER, 24, 1, 16, 50);
    }

    private static void sellItems(int minLevel, Item soldItem, int emeraldCost, int numberOfItems, int maxUses, int xp) {
        addOffers(minLevel, new VillagerTrades.ItemsForEmeralds(soldItem, emeraldCost, numberOfItems, maxUses, xp));
    }

    private static void addOffers(int minLevel, VillagerTrades.ItemListing... newOffers) {
        var offersByLevel = VillagerTrades.TRADES.computeIfAbsent(PROFESSION, key -> new Int2ObjectOpenHashMap<>());
        var entries = offersByLevel.computeIfAbsent(minLevel, key -> new VillagerTrades.ItemListing[0]);
        entries = ArrayUtils.addAll(entries, newOffers);
        offersByLevel.put(minLevel, entries);
    }
}
