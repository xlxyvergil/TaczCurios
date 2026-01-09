package com.xlxyvergil.tcc.handlers;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.helpers.LootTableHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class TccEventHandler {
    private static final TccEventHandler INSTANCE = new TccEventHandler();
    private static final Random RANDOM = new Random();
    
    public static TccEventHandler getInstance() {
        return INSTANCE;
    }
    
    // 定义要添加饰品的原版箱子列表
    private static final List<ResourceLocation> VANILLA_CHESTS = new ArrayList<>();
    
    static {
        // 所有原版箱子战利品表
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/abandoned_mineshaft"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/ancient_city"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/ancient_city_ice_box"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/bastion_bridge"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/bastion_hoglin_stable"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/bastion_other"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/bastion_treasure"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/buried_treasure"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/desert_pyramid"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/end_city_treasure"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/igloo_chest"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/jungle_temple"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/jungle_temple_dispenser"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/nether_bridge"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/pillager_outpost"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/ruined_portal"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/shipwreck_map"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/shipwreck_supply"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/shipwreck_treasure"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/simple_dungeon"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/spawn_bonus_chest"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/stronghold_corridor"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/stronghold_crossing"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/stronghold_library"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/intersection"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/intersection_barrel"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/entrance"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/corridor"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/supply"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_common"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_rare"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_unique"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_ominous"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_ominous_common"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_ominous_rare"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_ominous_unique"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/underwater_ruin_big"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/underwater_ruin_small"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_armorer"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_butcher"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_cartographer"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_desert_house"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_fisher"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_fletcher"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_mason"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_plains_house"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_savanna_house"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_shepherd"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_snowy_house"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_taiga_house"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_tannery"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_temple"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_toolsmith"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_weaponsmith"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/woodland_mansion"));
    }
    
    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public void onLootTablesLoaded(LootTableLoadEvent event) {
        ResourceLocation tableName = event.getName();
        
        // 只处理原版箱子战利品表
        if (VANILLA_CHESTS.contains(tableName)) {
            // 根据配置的几率决定是否生成裂隙碎银
            if (RANDOM.nextFloat() <= com.xlxyvergil.tcc.config.TaczCuriosConfig.COMMON.riftSilverChestSpawnChance.get()) {
                // 获取裂隙碎银物品
                ItemStack riftSilverStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(TaczCurios.MODID, "rift_silver")));
                
                // 如果成功获取到裂隙碎银物品，则添加到战利品表中
                if (!riftSilverStack.isEmpty()) {
                    // 根据概率确定裂隙碎银的数量: 70%概率1个，20%概率2个，10%概率3个
                    int count = 1;
                    float chance = RANDOM.nextFloat();
                    if (chance < 0.1f) { // 10%概率
                        count = 3;
                    } else if (chance < 0.3f) { // 20%概率
                        count = 2;
                    }
                    // 70%概率保持count=1
                    
                    // 使用固定数量而非范围，因为数量已经根据概率确定
                    LootTableHelper.addLootEntryToChest(event.getTable(), riftSilverStack, 1, count, 15); // 权重15，数量根据概率确定
                }
            }
        }
    }
}