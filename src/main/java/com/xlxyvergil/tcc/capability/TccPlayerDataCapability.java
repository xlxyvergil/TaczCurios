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

/**
 * 玩家数据 Capability，替代 {@code player.getPersistentData()} 方案。
 * <p>
 * 存储以下数据：
 * <ul>
 *   <li>成就进度（achievementId → progress）</li>
 *   <li>已访问群系 ID 集合</li>
 *   <li>已访问维度 ID 集合</li>
 * </ul>
 * <p>
 * 使用 Capability 而非 {@code persistentData} 的主要优势是
 * Capability 随玩家 NBT 自动持久化（player.dat），
 * 死亡复活后新玩家加载时自动恢复，且被 Forge 的 {@code PlayerEvent.Clone} 事件机制正确复制。
 */
public final class TccPlayerDataCapability {

    public static final ResourceLocation ID = new ResourceLocation("tcc", "player_data");

    private static final String TAG_ACHIEVEMENT_PROGRESS = "achievementProgress";
    private static final String TAG_VISITED_BIOMES = "tcc_visited_biomes";
    private static final String TAG_VISITED_DIMENSIONS = "tcc_visited_dimensions";

    private TccPlayerDataCapability() {}

    // ==================== Handler ====================

    public static class Handler {
        private final Map<String, Integer> achievementProgress = new HashMap<>();
        private final Set<String> visitedBiomes = new HashSet<>();
        private final Set<String> visitedDimensions = new HashSet<>();

        // ===== Achievement Progress =====

        public int getAchievementProgress(String achievementId) {
            return achievementProgress.getOrDefault(achievementId, 0);
        }

        public void setAchievementProgress(String achievementId, int progress) {
            achievementProgress.put(achievementId, progress);
        }

        // ===== Visited Biomes =====

        public boolean hasVisitedBiome(String biomeId) {
            return visitedBiomes.contains(biomeId);
        }

        public boolean addVisitedBiome(String biomeId) {
            return visitedBiomes.add(biomeId);
        }

        public Set<String> getVisitedBiomes() {
            return visitedBiomes;
        }

        // ===== Visited Dimensions =====

        public boolean hasVisitedDimension(String dimensionId) {
            return visitedDimensions.contains(dimensionId);
        }

        public boolean addVisitedDimension(String dimensionId) {
            return visitedDimensions.add(dimensionId);
        }

        public Set<String> getVisitedDimensions() {
            return visitedDimensions;
        }

        /**
         * 从另一个 Handler 复制所有数据（用于 PlayerEvent.Clone 死亡复活）。
         * Forge 的 Capability NBT 持久化在复活流程中不一定可靠，需要显式复制。
         */
        public void copyFrom(Handler other) {
            this.achievementProgress.clear();
            this.achievementProgress.putAll(other.achievementProgress);
            this.visitedBiomes.clear();
            this.visitedBiomes.addAll(other.visitedBiomes);
            this.visitedDimensions.clear();
            this.visitedDimensions.addAll(other.visitedDimensions);
        }

        // ===== NBT Serialization =====

        CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();

            // Achievement progress
            CompoundTag progressTag = new CompoundTag();
            for (var entry : achievementProgress.entrySet()) {
                progressTag.putInt(entry.getKey(), entry.getValue());
            }
            tag.put(TAG_ACHIEVEMENT_PROGRESS, progressTag);

            // Visited biomes
            ListTag biomesTag = new ListTag();
            for (String biome : visitedBiomes) {
                biomesTag.add(StringTag.valueOf(biome));
            }
            tag.put(TAG_VISITED_BIOMES, biomesTag);

            // Visited dimensions
            ListTag dimensionsTag = new ListTag();
            for (String dim : visitedDimensions) {
                dimensionsTag.add(StringTag.valueOf(dim));
            }
            tag.put(TAG_VISITED_DIMENSIONS, dimensionsTag);

            return tag;
        }

        void deserializeNBT(CompoundTag tag) {
            achievementProgress.clear();
            visitedBiomes.clear();
            visitedDimensions.clear();

            // Achievement progress
            if (tag.contains(TAG_ACHIEVEMENT_PROGRESS, Tag.TAG_COMPOUND)) {
                CompoundTag progressTag = tag.getCompound(TAG_ACHIEVEMENT_PROGRESS);
                for (String key : progressTag.getAllKeys()) {
                    achievementProgress.put(key, progressTag.getInt(key));
                }
            }

            // Visited biomes
            if (tag.contains(TAG_VISITED_BIOMES, Tag.TAG_LIST)) {
                ListTag list = tag.getList(TAG_VISITED_BIOMES, Tag.TAG_STRING);
                for (int i = 0; i < list.size(); i++) {
                    visitedBiomes.add(list.getString(i));
                }
            }

            // Visited dimensions
            if (tag.contains(TAG_VISITED_DIMENSIONS, Tag.TAG_LIST)) {
                ListTag list = tag.getList(TAG_VISITED_DIMENSIONS, Tag.TAG_STRING);
                for (int i = 0; i < list.size(); i++) {
                    visitedDimensions.add(list.getString(i));
                }
            }
        }
    }

    // ==================== Forge Capability 注册 ====================

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

    // ==================== 便捷静态方法 ====================

    /** 获取玩家成就进度 */
    public static int getAchievementProgress(Player player, String achievementId) {
        var opt = player.getCapability(CAPABILITY);
        if (opt.isPresent()) {
            return opt.orElse(null).getAchievementProgress(achievementId);
        }
        return 0;
    }

    /** 设置玩家成就进度 */
    public static void setAchievementProgress(Player player, String achievementId, int progress) {
        player.getCapability(CAPABILITY).ifPresent(h -> h.setAchievementProgress(achievementId, progress));
    }

    /** 检查玩家是否访问过某群系 */
    public static boolean hasVisitedBiome(Player player, String biomeId) {
        var opt = player.getCapability(CAPABILITY);
        return opt.isPresent() && opt.orElse(null).hasVisitedBiome(biomeId);
    }

    /**
     * 记录群系访问。
     * @return true 如果是新记录（首次访问）
     */
    public static boolean addVisitedBiome(Player player, String biomeId) {
        var opt = player.getCapability(CAPABILITY);
        if (opt.isPresent()) {
            return opt.orElse(null).addVisitedBiome(biomeId);
        }
        return false;
    }

    /** 检查玩家是否访问过某维度 */
    public static boolean hasVisitedDimension(Player player, String dimensionId) {
        var opt = player.getCapability(CAPABILITY);
        return opt.isPresent() && opt.orElse(null).hasVisitedDimension(dimensionId);
    }

    /**
     * 记录维度访问。
     * @return true 如果是新记录（首次访问）
     */
    public static boolean addVisitedDimension(Player player, String dimensionId) {
        var opt = player.getCapability(CAPABILITY);
        if (opt.isPresent()) {
            return opt.orElse(null).addVisitedDimension(dimensionId);
        }
        return false;
    }
}
