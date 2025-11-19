package com.xlxyvergil.tcc.villagers;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TaczItems;
import com.xlxyvergil.tcc.registries.TaczPoiTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class TaczVillagers {
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = 
        DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, TaczCurios.MODID);
    
    // 注册Teshin村民职业
    public static final RegistryObject<VillagerProfession> TESHIN = PROFESSIONS.register("teshin", 
        () -> new VillagerProfession(
            "teshin", 
            holder -> holder.is(TaczPoiTypes.TESHIN_WORKBENCH_POI.getKey()),
            holder -> holder.is(TaczPoiTypes.TESHIN_WORKBENCH_POI.getKey()),
            ImmutableSet.of(),
            ImmutableSet.of(),
            SoundEvents.VILLAGER_WORK_LIBRARIAN
        )
    );
    
    public static void init() {}
    
    /**
     * 初始化村民交易
     */
    public static void registerTrades() {
        // 第1级交易 - 8个绿宝石，1个物品，最多使用32次，获得15点经验
        List<VillagerTrades.ItemListing> tradesLevel1 = new ArrayList<>();
        tradesLevel1.add(new ItemsForEmeralds(TaczItems.SOLDIER_BASIC_TAG.get(), 8, 1, 32, 15));
        tradesLevel1.add(new ItemsForEmeralds(TaczItems.PROPHECY_PACT.get(), 8, 1, 32, 15));
        
        // 第2级交易 - 16个绿宝石，1个物品，最多使用32次，获得30点经验
        List<VillagerTrades.ItemListing> tradesLevel2 = new ArrayList<>();
        tradesLevel2.add(new ItemsForEmeralds(TaczItems.RED_MOVEMENT_TAG.get(), 16, 1, 32, 30));
        tradesLevel2.add(new ItemsForEmeralds(TaczItems.URAL_WOLF_TAG.get(), 16, 1, 32, 30));
        
        // 第3级交易 - 16个绿宝石，1个物品，最多使用32次，获得30点经验
        List<VillagerTrades.ItemListing> tradesLevel3 = new ArrayList<>();
        tradesLevel3.add(new ItemsForEmeralds(TaczItems.LIMIT_SPEED.get(), 16, 1, 32, 30));
        tradesLevel3.add(new ItemsForEmeralds(TaczItems.FEROCIOUS_EXTENSION.get(), 16, 1, 32, 30));
        
        // 第4级交易 - 16个绿宝石，1个物品，最多使用32次，获得30点经验
        List<VillagerTrades.ItemListing> tradesLevel4 = new ArrayList<>();
        tradesLevel4.add(new ItemsForEmeralds(TaczItems.WASP_STINGER.get(), 16, 1, 32, 30));
        tradesLevel4.add(new ItemsForEmeralds(TaczItems.CLOSE_RANGE_SHOT.get(), 16, 1, 32, 30));
        
        // 第5级交易 - 裂隙碎银24个绿宝石，膛线16个绿宝石，1个物品，最多使用32次，获得30点经验
        List<VillagerTrades.ItemListing> tradesLevel5 = new ArrayList<>();
        tradesLevel5.add(new ItemsForEmeralds(TaczItems.RIFLING.get(), 16, 1, 32, 30));
        tradesLevel5.add(new ItemsForEmeralds(TaczItems.RIFT_SILVER.get(), 24, 1, 32, 30));
        
        // 注册所有交易到Teshin职业
        VillagerTrades.TRADES.put(TESHIN.get(), toIntMap(ImmutableMap.of(
            1, tradesLevel1.toArray(new VillagerTrades.ItemListing[0]),
            2, tradesLevel2.toArray(new VillagerTrades.ItemListing[0]),
            3, tradesLevel3.toArray(new VillagerTrades.ItemListing[0]),
            4, tradesLevel4.toArray(new VillagerTrades.ItemListing[0]),
            5, tradesLevel5.toArray(new VillagerTrades.ItemListing[0])
        )));
    }

    /**
     * 自定义交易类，替代无法访问的ItemsForEmeralds
     */
    static class ItemsForEmeralds implements VillagerTrades.ItemListing {
        private final net.minecraft.world.item.Item item;
        private final int emeraldCost;     // 玩家需要支付的绿宝石数量
        private final int itemCount;       // 村民提供的物品数量
        private final int maxUses;         // 该交易的最大使用次数
        private final int villagerXp;      // 村民获得的经验值

        /**
         * 创建一个新的物品交易
         * @param pItem 村民出售的物品
         * @param pEmeraldCost 玩家需要支付的绿宝石数量
         * @param pItemCount 村民提供的物品数量
         * @param pMaxUses 该交易的最大使用次数
         * @param pVillagerXp 村民完成交易后获得的经验值
         */
        public ItemsForEmeralds(net.minecraft.world.item.Item pItem, int pEmeraldCost, int pItemCount, int pMaxUses, int pVillagerXp) {
            this.item = pItem;
            this.emeraldCost = pEmeraldCost;
            this.itemCount = pItemCount;
            this.maxUses = pMaxUses;
            this.villagerXp = pVillagerXp;
        }

        public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
            ItemStack itemstack = new ItemStack(this.item, this.itemCount);
            return new MerchantOffer(
                new ItemStack(Items.EMERALD, this.emeraldCost),
                itemstack,
                this.maxUses,
                this.villagerXp,
                0.05F
            );
        }
    }
    
    private static Int2ObjectMap<VillagerTrades.ItemListing[]> toIntMap(ImmutableMap<Integer, VillagerTrades.ItemListing[]> pMap) {
        return new Int2ObjectOpenHashMap<>(pMap);
    }
}