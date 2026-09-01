package com.xlxyvergil.tcc.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class MaidCompat {

    private static final String TLM_MODID = "touhou_little_maid";

    private MaidCompat() {}

    public static boolean isMaid(Entity entity) {
        return isLoaded() && entity instanceof EntityMaid;
    }

    public static List<LivingEntity> getMaids(Level level) {
        if (!isLoaded()) return List.of();
        WorldBorder border = level.getWorldBorder();
        double cx = border.getCenterX(), cz = border.getCenterZ();
        double r = Math.max(border.getSize(), 32.0D) / 2.0D;
        AABB box = new AABB(cx - r, level.getMinBuildHeight(), cz - r,
                cx + r, level.getMaxBuildHeight(), cz + r);
        return new ArrayList<>(level.getEntitiesOfClass(EntityMaid.class, box, LivingEntity::isAlive));
    }

    public static List<LivingEntity> getMaidsNear(Level level, BlockPos center, double radius) {
        return getMaidsNear(level, new AABB(center).inflate(radius), LivingEntity::isAlive);
    }

    public static List<LivingEntity> getMaidsNear(Level level, AABB box, Predicate<LivingEntity> filter) {
        if (!isLoaded()) return List.of();
        return new ArrayList<>(level.getEntitiesOfClass(EntityMaid.class, box, filter));
    }

    public static LivingEntity findWearingMaid(Level level, Predicate<ItemStack> predicate) {
        if (!isLoaded()) return null;
        for (LivingEntity maid : getMaids(level)) {
            if (!CurioSearchHelper.findFirstEquippedStack(maid, predicate).isEmpty()) {
                return maid;
            }
        }
        return null;
    }

    public static Component getDisplayName(LivingEntity entity) {
        if (isLoaded() && entity instanceof EntityMaid maid) {
            return maid.getDisplayName();
        }
        return entity.getName();
    }

    public static Player resolveOwnerPlayer(Entity killer) {
        if (killer instanceof Player player) {
            return player;
        }
        if (!isLoaded() || !(killer instanceof EntityMaid maid)) {
            return null;
        }
        if (maid.getOwner() instanceof Player owner) {
            return owner;
        }
        return null;
    }

    public static String resolveAttackerUuid(Entity attacker) {
        return attacker != null ? attacker.getUUID().toString() : "";
    }

    private static boolean isLoaded() {
        return ModList.get().isLoaded(TLM_MODID);
    }
}
