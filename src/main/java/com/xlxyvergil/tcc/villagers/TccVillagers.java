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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
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

        
        sellForNetheriteScrap(1, TccItems.CORE_FUSION, 2, 1, 64, 2);
        sellForNetheriteScrap(1, TccItems.PROPHECY_PACT, 2, 1, 64, 2);
        sellForNetheriteScrap(1, TccItems.BURST_RELOAD, 2, 1, 64, 2);
        sellForNetheriteScrap(1, TccItems.SUSTAINED_FIRE, 2, 1, 64, 2);
        sellForNetheriteScrap(1, TccItems.OPPRESSION_POINT, 2, 1, 64, 2);
        sellForNetheriteScrap(1, TccItems.SWORD_WIND, 2, 1, 64, 2);
        sellForNetheriteScrap(1, TccItems.SHOTGUN_EXPANSION, 2, 1, 64, 2);
        sellForNetheriteScrap(1, TccItems.MAGAZINE_BOOST, 2, 1, 64, 2);
        sellForNetheriteScrap(1, TccItems.TANDEM_MAGAZINE, 2, 1, 64, 2);

        
        sellForNetheriteScrap(2, TccItems.LETHAL_CRIT, 6, 1, 64, 10);
        sellForNetheriteScrap(2, TccItems.THUNDER_BARREL, 6, 1, 64, 10);
        sellForNetheriteScrap(2, TccItems.PISTOL_MASTERY, 6, 1, 64, 10);
        sellForNetheriteScrap(2, TccItems.STEEL_SLASH, 6, 1, 64, 10);
        sellForNetheriteScrap(2, TccItems.DISMEMBERMENT, 6, 1, 64, 10);
        sellForNetheriteScrap(2, TccItems.FRAGMENT_SHOT, 6, 1, 64, 10);
        sellForNetheriteScrap(2, TccItems.HYDRAULIC_CROSSHAIR, 6, 1, 64, 10);

        
        sellForNetheriteScrap(3, TccItems.RIFLING, 12, 1, 64, 20);
        sellForNetheriteScrap(3, TccItems.CLOSE_RANGE_SHOT, 12, 1, 64, 20);
        sellForNetheriteScrap(3, TccItems.WASP_STINGER, 12, 1, 64, 20);
        sellForNetheriteScrap(3, TccItems.URAL_WOLF_TAG, 12, 1, 64, 20);
        sellForNetheriteScrap(3, TccItems.SOLDIER_BASIC_TAG, 12, 1, 64, 20);
        sellForNetheriteScrap(3, TccItems.RED_MOVEMENT_TAG, 12, 1, 64, 20);
        sellForNetheriteScrap(3, TccItems.LIMIT_SPEED, 12, 1, 64, 20);
        sellForNetheriteScrap(3, TccItems.FEROCIOUS_EXTENSION, 12, 1, 64, 20);
        sellForNetheriteScrap(3, TccItems.TACTICAL_RELOAD, 12, 1, 64, 20);

        
        sellForNetheriteScrap(4, TccItems.WEAKNESS_MASTERY, 24, 1, 64, 35);
        sellForNetheriteScrap(4, TccItems.SHARP_BULLET, 24, 1, 64, 35);
        sellForNetheriteScrap(4, TccItems.LASER_SCOPE, 24, 1, 64, 35);
        sellForNetheriteScrap(4, TccItems.SHARP_AMMO, 24, 1, 64, 35);

        
        sellForNetheriteScrap(5, TccItems.ALLOY_DRILL, 48, 1, 64, 50);
        sellForNetheriteScrap(5, TccItems.EVIL_ACCURACY, 48, 1, 64, 50);
        sellForNetheriteScrap(5, TccItems.CHAMBER, 48, 1, 64, 50);
        sellForNetheriteScrap(5, TccItems.SPLIT_CHAMBER, 48, 1, 64, 50);
        sellForNetheriteScrap(5, TccItems.BULLET_SPREAD, 48, 1, 64, 50);
        sellForNetheriteScrap(5, TccItems.WEAKNESS_SENSE, 48, 1, 64, 50);
        sellForNetheriteScrap(5, TccItems.DESTRUCTION, 48, 1, 64, 50);
        sellForNetheriteScrap(5, TccItems.RIFT_SILVER, 64, 1, 64, 50);
    }

    private static void sellForNetheriteScrap(int minLevel, Item soldItem, int scrapCost, int numberOfItems, int maxUses, int xp) {
        addOffers(minLevel, (trader, rand) -> {
            ItemStack currency = new ItemStack(Items.NETHERITE_SCRAP, scrapCost);
            ItemStack result = new ItemStack(soldItem, numberOfItems);
            return new MerchantOffer(currency, ItemStack.EMPTY, result, maxUses, xp, 0.05F);
        });
    }

    private static void addOffers(int minLevel, VillagerTrades.ItemListing... newOffers) {
        var offersByLevel = VillagerTrades.TRADES.computeIfAbsent(PROFESSION, key -> new Int2ObjectOpenHashMap<>());
        var entries = offersByLevel.computeIfAbsent(minLevel, key -> new VillagerTrades.ItemListing[0]);
        entries = ArrayUtils.addAll(entries, newOffers);
        offersByLevel.put(minLevel, entries);
    }
}
