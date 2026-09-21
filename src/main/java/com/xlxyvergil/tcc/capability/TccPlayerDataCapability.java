package com.xlxyvergil.tcc.capability;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;


public final class TccPlayerDataCapability {

    private static final String TAG_ACHIEVEMENT_PROGRESS = "achievementProgress";
    private static final String TAG_VISITED_BIOMES = "tcc_visited_biomes";
    private static final String TAG_VISITED_DIMENSIONS = "tcc_visited_dimensions";

    private static final String TAG_PLAY_TIME_GRISEO = "playTimeGriseo";
    private static final String TAG_PLAY_TIME_HUISHI_ZHIJUAN = "playTimeHuishiZhijuan";
    private static final String TAG_PLAY_TIME_FANXING = "playTimeFanxing";
    private static final String TAG_PLAY_TIME_QISHI_ZHIJIAN = "playTimeQishiZhijian";
    private static final String TAG_ZOMBIE_VILLAGER_CURED = "zombieVillagerCured";
    private static final String TAG_ITEMS_CRAFTED = "itemsCrafted";

    private TccPlayerDataCapability() {}

    // Handler

    public static class Handler implements INBTSerializable<CompoundTag> {
        private final Map<String, Integer> achievementProgress = new HashMap<>();
        private final Set<String> visitedBiomes = new HashSet<>();
        private final Set<String> visitedDimensions = new HashSet<>();
        private long playTimeGriseo;
        private long playTimeHuishiZhijuan;
        private long playTimeFanxing;
        private long playTimeQishiZhijian;
        private long zombieVillagerCured;
        private long itemsCrafted;

        

        public int getAchievementProgress(String achievementId) {
            return achievementProgress.getOrDefault(achievementId, 0);
        }

        public void setAchievementProgress(String achievementId, int progress) {
            achievementProgress.put(achievementId, progress);
        }

        

        public boolean hasVisitedBiome(String biomeId) {
            return visitedBiomes.contains(biomeId);
        }

        public boolean addVisitedBiome(String biomeId) {
            return visitedBiomes.add(biomeId);
        }

        public Set<String> getVisitedBiomes() {
            return visitedBiomes;
        }

        

        public boolean hasVisitedDimension(String dimensionId) {
            return visitedDimensions.contains(dimensionId);
        }

        public boolean addVisitedDimension(String dimensionId) {
            return visitedDimensions.add(dimensionId);
        }

        public Set<String> getVisitedDimensions() {
            return visitedDimensions;
        }

        

        public long getZombieVillagerCured() {
            return zombieVillagerCured;
        }

        public void setZombieVillagerCured(long value) {
            this.zombieVillagerCured = value;
        }

        public void incrementZombieVillagerCured(int delta) {
            this.zombieVillagerCured += delta;
        }

        public long getItemsCrafted() {
            return itemsCrafted;
        }

        public void setItemsCrafted(long value) {
            this.itemsCrafted = value;
        }

        public void incrementItemsCrafted(int delta) {
            this.itemsCrafted += delta;
        }

        

        public long getPlayTimeGriseo() {
            return playTimeGriseo;
        }

        public void setPlayTimeGriseo(long time) {
            this.playTimeGriseo = time;
        }

        public void incrementPlayTimeGriseo() {
            this.playTimeGriseo++;
        }

        public long getPlayTimeHuishiZhijuan() {
            return playTimeHuishiZhijuan;
        }

        public void setPlayTimeHuishiZhijuan(long time) {
            this.playTimeHuishiZhijuan = time;
        }

        public void incrementPlayTimeHuishiZhijuan() {
            this.playTimeHuishiZhijuan++;
        }

        public long getPlayTimeFanxing() {
            return playTimeFanxing;
        }

        public void setPlayTimeFanxing(long time) {
            this.playTimeFanxing = time;
        }

        public void incrementPlayTimeFanxing() {
            this.playTimeFanxing++;
        }

        public long getPlayTimeQishiZhijian() {
            return playTimeQishiZhijian;
        }

        public void setPlayTimeQishiZhijian(long time) {
            this.playTimeQishiZhijian = time;
        }

        public void incrementPlayTimeQishiZhijian() {
            this.playTimeQishiZhijian++;
        }

        

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag tag = new CompoundTag();

            CompoundTag progressTag = new CompoundTag();
            for (var entry : achievementProgress.entrySet()) {
                progressTag.putInt(entry.getKey(), entry.getValue());
            }
            tag.put(TAG_ACHIEVEMENT_PROGRESS, progressTag);

            ListTag biomesTag = new ListTag();
            for (String biome : visitedBiomes) {
                biomesTag.add(StringTag.valueOf(biome));
            }
            tag.put(TAG_VISITED_BIOMES, biomesTag);

            ListTag dimensionsTag = new ListTag();
            for (String dim : visitedDimensions) {
                dimensionsTag.add(StringTag.valueOf(dim));
            }
            tag.put(TAG_VISITED_DIMENSIONS, dimensionsTag);

            tag.putLong(TAG_PLAY_TIME_GRISEO, playTimeGriseo);
            tag.putLong(TAG_PLAY_TIME_HUISHI_ZHIJUAN, playTimeHuishiZhijuan);
            tag.putLong(TAG_PLAY_TIME_FANXING, playTimeFanxing);
            tag.putLong(TAG_PLAY_TIME_QISHI_ZHIJIAN, playTimeQishiZhijian);
            tag.putLong(TAG_ZOMBIE_VILLAGER_CURED, zombieVillagerCured);
            tag.putLong(TAG_ITEMS_CRAFTED, itemsCrafted);

            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
            achievementProgress.clear();
            visitedBiomes.clear();
            visitedDimensions.clear();

            if (tag.contains(TAG_ACHIEVEMENT_PROGRESS, Tag.TAG_COMPOUND)) {
                CompoundTag progressTag = tag.getCompound(TAG_ACHIEVEMENT_PROGRESS);
                for (String key : progressTag.getAllKeys()) {
                    achievementProgress.put(key, progressTag.getInt(key));
                }
            }

            if (tag.contains(TAG_VISITED_BIOMES, Tag.TAG_LIST)) {
                ListTag list = tag.getList(TAG_VISITED_BIOMES, Tag.TAG_STRING);
                for (int i = 0; i < list.size(); i++) {
                    visitedBiomes.add(list.getString(i));
                }
            }

            if (tag.contains(TAG_VISITED_DIMENSIONS, Tag.TAG_LIST)) {
                ListTag list = tag.getList(TAG_VISITED_DIMENSIONS, Tag.TAG_STRING);
                for (int i = 0; i < list.size(); i++) {
                    visitedDimensions.add(list.getString(i));
                }
            }

            playTimeGriseo = tag.getLong(TAG_PLAY_TIME_GRISEO);
            playTimeHuishiZhijuan = tag.getLong(TAG_PLAY_TIME_HUISHI_ZHIJUAN);
            playTimeFanxing = tag.getLong(TAG_PLAY_TIME_FANXING);
            playTimeQishiZhijian = tag.getLong(TAG_PLAY_TIME_QISHI_ZHIJIAN);
            zombieVillagerCured = tag.getLong(TAG_ZOMBIE_VILLAGER_CURED);
            itemsCrafted = tag.getLong(TAG_ITEMS_CRAFTED);
        }
    }

    

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TaczCurios.MODID);

    /** 玩家进度数据，玩家死亡重生后保留。 */
    public static final Supplier<AttachmentType<Handler>> PLAYER_DATA = ATTACHMENT_TYPES.register("player_data",
            () -> AttachmentType.serializable(Handler::new).copyOnDeath().build());

    /** 取玩家进度数据，不存在时会自动创建默认值。 */
    public static Handler of(Player player) {
        return player.getData(PLAYER_DATA);
    }

    

    public static int getAchievementProgress(Player player, String achievementId) {
        return of(player).getAchievementProgress(achievementId);
    }

    public static void setAchievementProgress(Player player, String achievementId, int progress) {
        of(player).setAchievementProgress(achievementId, progress);
    }

    public static boolean hasVisitedBiome(Player player, String biomeId) {
        return of(player).hasVisitedBiome(biomeId);
    }

    
    public static boolean addVisitedBiome(Player player, String biomeId) {
        return of(player).addVisitedBiome(biomeId);
    }

    public static boolean hasVisitedDimension(Player player, String dimensionId) {
        return of(player).hasVisitedDimension(dimensionId);
    }

    
    public static boolean addVisitedDimension(Player player, String dimensionId) {
        return of(player).addVisitedDimension(dimensionId);
    }

    public static int getCustomStat(Player player, String statKey) {
        Handler h = of(player);
        return (int) switch (statKey) {
            case "tcc:zombie_villager_cured" -> h.getZombieVillagerCured();
            case "tcc:items_crafted" -> h.getItemsCrafted();
            case "tcc:play_time_griseo" -> h.getPlayTimeGriseo();
            case "tcc:play_time_huishi_zhijuan" -> h.getPlayTimeHuishiZhijuan();
            case "tcc:play_time_fanxing" -> h.getPlayTimeFanxing();
            case "tcc:play_time_qishi_zhijian" -> h.getPlayTimeQishiZhijian();
            default -> 0;
        };
    }

    public static void setCustomStat(Player player, String statKey, int value) {
        Handler h = of(player);
        switch (statKey) {
            case "tcc:zombie_villager_cured" -> h.setZombieVillagerCured(value);
            case "tcc:items_crafted" -> h.setItemsCrafted(value);
            case "tcc:play_time_griseo" -> h.setPlayTimeGriseo(value);
            case "tcc:play_time_huishi_zhijuan" -> h.setPlayTimeHuishiZhijuan(value);
            case "tcc:play_time_fanxing" -> h.setPlayTimeFanxing(value);
            case "tcc:play_time_qishi_zhijian" -> h.setPlayTimeQishiZhijian(value);
            default -> { }
        }
    }

    public static void incrementCustomStat(Player player, String statKey, int delta) {
        Handler h = of(player);
        switch (statKey) {
            case "tcc:zombie_villager_cured" -> h.incrementZombieVillagerCured(delta);
            case "tcc:items_crafted" -> h.incrementItemsCrafted(delta);
            default -> { }
        }
    }

    

    public static long getPlayTimeGriseo(Player player) {
        return of(player).getPlayTimeGriseo();
    }

    public static void setPlayTimeGriseo(Player player, long time) {
        of(player).setPlayTimeGriseo(time);
    }

    public static void incrementPlayTimeGriseo(Player player) {
        of(player).incrementPlayTimeGriseo();
    }

    public static long getPlayTimeHuishiZhijuan(Player player) {
        return of(player).getPlayTimeHuishiZhijuan();
    }

    public static void setPlayTimeHuishiZhijuan(Player player, long time) {
        of(player).setPlayTimeHuishiZhijuan(time);
    }

    public static void incrementPlayTimeHuishiZhijuan(Player player) {
        of(player).incrementPlayTimeHuishiZhijuan();
    }

    public static long getPlayTimeFanxing(Player player) {
        return of(player).getPlayTimeFanxing();
    }

    public static void setPlayTimeFanxing(Player player, long time) {
        of(player).setPlayTimeFanxing(time);
    }

    public static void incrementPlayTimeFanxing(Player player) {
        of(player).incrementPlayTimeFanxing();
    }

    public static long getPlayTimeQishiZhijian(Player player) {
        return of(player).getPlayTimeQishiZhijian();
    }

    public static void setPlayTimeQishiZhijian(Player player, long time) {
        of(player).setPlayTimeQishiZhijian(time);
    }

    public static void incrementPlayTimeQishiZhijian(Player player) {
        of(player).incrementPlayTimeQishiZhijian();
    }
}
