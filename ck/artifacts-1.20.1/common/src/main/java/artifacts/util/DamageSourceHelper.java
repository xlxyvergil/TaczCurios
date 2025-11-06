package artifacts.util;

import artifacts.mixin.accessors.LivingEntityAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

public class DamageSourceHelper {

    @Nullable
    public static LivingEntity getAttacker(DamageSource source) {
        if (source.getEntity() instanceof LivingEntity entity) {
            return entity;
        }
        return null;
    }

    public static boolean isMeleeAttack(DamageSource source) {
        return !source.isIndirect()
                && (source.is(DamageTypes.MOB_ATTACK)
                || source.is(DamageTypes.PLAYER_ATTACK)
                || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO));
    }

    public static boolean shouldDestroyWornItemsOnDeath(LivingEntity entity) {
        return entity instanceof Mob && !wasLastHurtByPlayer(entity);
    }

    public static boolean wasLastHurtByPlayer(LivingEntity entity) {
        if (entity instanceof LivingEntityAccessor mob) {
            return mob.getLastHurtByPlayerTime() > 0 && mob.getLastHurtByPlayer() != null;
        }
        return false;
    }
}
