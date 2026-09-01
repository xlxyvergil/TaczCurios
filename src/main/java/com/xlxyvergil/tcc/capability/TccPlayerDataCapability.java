package com.xlxyvergil.tcc.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public final class TccPlayerDataCapability {

    public static final ResourceLocation ID = new ResourceLocation("tcc", "player_data");

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

    public static class Handler {
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

        
        public void copyFrom(Handler other) {
            this.achievementProgress.clear();
            this.achievementProgress.putAll(other.achievementProgress);
            this.visitedBiomes.clear();
            this.visitedBiomes.addAll(other.visitedBiomes);
            this.visitedDimensions.clear();
            this.visitedDimensions.addAll(other.visitedDimensions);
            this.playTimeGriseo = other.playTimeGriseo;
            this.playTimeHuishiZhijuan = other.playTimeHuishiZhijuan;
            this.playTimeFanxing = other.playTimeFanxing;
            this.playTimeQishiZhijian = other.playTimeQishiZhijian;
            this.zombieVillagerCured = other.zombieVillagerCured;
            this.itemsCrafted = other.itemsCrafted;
        }

        

        CompoundTag serializeNBT() {
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

        void deserializeNBT(CompoundTag tag) {
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

    

    public static final Capability<Handler> CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});

    public static class Provider implements ICapabilitySerializable<CompoundTag> {

        private final Handler handler = new Handler();
        private final LazyOptional<Handler> lazy = LazyOptional.of(() -> handler);

        @Override
        @NotNull
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == CAPABILITY ? lazy.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return handler.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            handler.deserializeNBT(nbt);
        }
    }

    

    public static int getAchievementProgress(Player player, String achievementId) {
        var opt = player.getCapability(CAPABILITY);
        if (opt.isPresent()) {
            return opt.orElse(null).getAchievementProgress(achievementId);
        }
        return 0;
    }

    public static void setAchievementProgress(Player player, String achievementId, int progress) {
        player.getCapability(CAPABILITY).ifPresent(h -> h.setAchievementProgress(achievementId, progress));
    }

    public static boolean hasVisitedBiome(Player player, String biomeId) {
        var opt = player.getCapability(CAPABILITY);
        return opt.isPresent() && opt.orElse(null).hasVisitedBiome(biomeId);
    }

    
    public static boolean addVisitedBiome(Player player, String biomeId) {
        var opt = player.getCapability(CAPABILITY);
        if (opt.isPresent()) {
            return opt.orElse(null).addVisitedBiome(biomeId);
        }
        return false;
    }

    public static boolean hasVisitedDimension(Player player, String dimensionId) {
        var opt = player.getCapability(CAPABILITY);
        return opt.isPresent() && opt.orElse(null).hasVisitedDimension(dimensionId);
    }

    
    public static boolean addVisitedDimension(Player player, String dimensionId) {
        var opt = player.getCapability(CAPABILITY);
        if (opt.isPresent()) {
            return opt.orElse(null).addVisitedDimension(dimensionId);
        }
        return false;
    }

    public static int getCustomStat(Player player, String statKey) {
        var opt = player.getCapability(CAPABILITY);
        if (opt.isPresent()) {
            Handler h = opt.orElse(null);
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
        return 0;
    }

    public static void setCustomStat(Player player, String statKey, int value) {
        player.getCapability(CAPABILITY).ifPresent(h -> {
            switch (statKey) {
                case "tcc:zombie_villager_cured" -> h.setZombieVillagerCured(value);
                case "tcc:items_crafted" -> h.setItemsCrafted(value);
                case "tcc:play_time_griseo" -> h.setPlayTimeGriseo(value);
                case "tcc:play_time_huishi_zhijuan" -> h.setPlayTimeHuishiZhijuan(value);
                case "tcc:play_time_fanxing" -> h.setPlayTimeFanxing(value);
                case "tcc:play_time_qishi_zhijian" -> h.setPlayTimeQishiZhijian(value);
                default -> { }
            }
        });
    }

    public static void incrementCustomStat(Player player, String statKey, int delta) {
        player.getCapability(CAPABILITY).ifPresent(h -> {
            switch (statKey) {
                case "tcc:zombie_villager_cured" -> h.incrementZombieVillagerCured(delta);
                case "tcc:items_crafted" -> h.incrementItemsCrafted(delta);
                default -> { }
            }
        });
    }

    

    public static long getPlayTimeGriseo(Player player) {
        var opt = player.getCapability(CAPABILITY);
        return opt.isPresent() ? opt.orElse(null).getPlayTimeGriseo() : 0;
    }

    public static void setPlayTimeGriseo(Player player, long time) {
        player.getCapability(CAPABILITY).ifPresent(h -> h.setPlayTimeGriseo(time));
    }

    public static void incrementPlayTimeGriseo(Player player) {
        player.getCapability(CAPABILITY).ifPresent(Handler::incrementPlayTimeGriseo);
    }

    public static long getPlayTimeHuishiZhijuan(Player player) {
        var opt = player.getCapability(CAPABILITY);
        return opt.isPresent() ? opt.orElse(null).getPlayTimeHuishiZhijuan() : 0;
    }

    public static void setPlayTimeHuishiZhijuan(Player player, long time) {
        player.getCapability(CAPABILITY).ifPresent(h -> h.setPlayTimeHuishiZhijuan(time));
    }

    public static void incrementPlayTimeHuishiZhijuan(Player player) {
        player.getCapability(CAPABILITY).ifPresent(Handler::incrementPlayTimeHuishiZhijuan);
    }

    public static long getPlayTimeFanxing(Player player) {
        var opt = player.getCapability(CAPABILITY);
        return opt.isPresent() ? opt.orElse(null).getPlayTimeFanxing() : 0;
    }

    public static void setPlayTimeFanxing(Player player, long time) {
        player.getCapability(CAPABILITY).ifPresent(h -> h.setPlayTimeFanxing(time));
    }

    public static void incrementPlayTimeFanxing(Player player) {
        player.getCapability(CAPABILITY).ifPresent(Handler::incrementPlayTimeFanxing);
    }

    public static long getPlayTimeQishiZhijian(Player player) {
        var opt = player.getCapability(CAPABILITY);
        return opt.isPresent() ? opt.orElse(null).getPlayTimeQishiZhijian() : 0;
    }

    public static void setPlayTimeQishiZhijian(Player player, long time) {
        player.getCapability(CAPABILITY).ifPresent(h -> h.setPlayTimeQishiZhijian(time));
    }

    public static void incrementPlayTimeQishiZhijian(Player player) {
        player.getCapability(CAPABILITY).ifPresent(Handler::incrementPlayTimeQishiZhijian);
    }
}
