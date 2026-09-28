package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.items.curios.bound.ZhenWo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
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
 * 在 Level.getEntities(Entity, AABB, Predicate) 返回处过滤：真我结界激活期间把佩戴者从结果中剔除，
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
            if (e instanceof LivingEntity living && ZhenWo.isBarrierActiveWearer(living)) {
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
