package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.util.ZhenWoGuard;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 在 Level 的范围取实体入口过滤：真我结界激活期间把佩戴者从结果中剔除，
 * 使依赖范围查询的敌人/技能取不到佩戴者。
 * 覆盖 getEntities(Entity, AABB, Predicate) 与 getEntities(EntityTypeTest, AABB, Predicate)
 * 两个底层入口，走 getEntitiesOfClass / getNearbyEntities / 非玩家 getNearestEntity 的查询
 * 都会经过它们。玩家索敌走 getNearestPlayer -> players()，不经过这里，
 * 由 ServerLevelPlayersMixin 单独处理。
 */
@Mixin(Level.class)
public abstract class LevelGetEntitiesMixin {

    @Inject(
        method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void tcc$excludeBarrierWearer(@Nullable Entity entity, AABB aabb, Predicate<? super Entity> predicate,
                                          CallbackInfoReturnable<List<Entity>> cir) {
        List<Entity> result = cir.getReturnValue();
        if (result == null || result.isEmpty()) {
            return;
        }

        // 命中佩戴者时才复制列表
        List<Entity> filtered = null;
        for (Entity e : result) {
            if (e instanceof LivingEntity living && ZhenWoGuard.isBarrierActiveWearer(living)) {
                if (filtered == null) {
                    filtered = new ArrayList<>(result);
                }
                filtered.remove(e);
            }
        }

        if (filtered != null) {
            cir.setReturnValue(filtered);
        }
    }

    @Inject(
        method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void tcc$excludeBarrierWearerOfType(EntityTypeTest<Entity, ?> typeTest, AABB aabb, Predicate<?> predicate,
                                                CallbackInfoReturnable<List<?>> cir) {
        List<?> result = cir.getReturnValue();
        if (result == null || result.isEmpty()) {
            return;
        }

        List<Object> filtered = null;
        for (Object e : result) {
            if (e instanceof LivingEntity living && ZhenWoGuard.isBarrierActiveWearer(living)) {
                if (filtered == null) {
                    filtered = new ArrayList<>(result);
                }
                filtered.remove(e);
            }
        }

        if (filtered != null) {
            cir.setReturnValue(filtered);
        }
    }
}
