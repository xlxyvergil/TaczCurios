package com.xlxyvergil.tcc.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.AbstractMaidContainer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 女仆模组（TLM）类型隔离层。
 */
final class MaidCompatInternal {

    private MaidCompatInternal() {}

    static boolean isMaid(Entity entity) {
        return entity instanceof EntityMaid;
    }

    static List<LivingEntity> getMaids(Level level) {
        WorldBorder border = level.getWorldBorder();
        double cx = border.getCenterX(), cz = border.getCenterZ();
        double r = Math.max(border.getSize(), 32.0D) / 2.0D;
        AABB box = new AABB(cx - r, level.getMinBuildHeight(), cz - r,
                cx + r, level.getMaxBuildHeight(), cz + r);
        return new ArrayList<>(level.getEntitiesOfClass(EntityMaid.class, box, LivingEntity::isAlive));
    }

    static List<LivingEntity> getMaidsNear(Level level, AABB box, Predicate<LivingEntity> filter) {
        return new ArrayList<>(level.getEntitiesOfClass(EntityMaid.class, box, filter));
    }

    @OnlyIn(Dist.CLIENT)
    static LivingEntity resolveScreenMaid(Screen screen) {
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return null;
        }
        if (containerScreen.getMenu() instanceof AbstractMaidContainer maidContainer) {
            return maidContainer.getMaid();
        }
        return null;
    }

    static Component getDisplayName(LivingEntity entity) {
        if (entity instanceof EntityMaid maid) {
            return maid.getDisplayName();
        }
        return entity.getName();
    }

    static Player resolveOwner(Entity entity) {
        if (entity instanceof EntityMaid maid && maid.getOwner() instanceof Player owner) {
            return owner;
        }
        return null;
    }
}
