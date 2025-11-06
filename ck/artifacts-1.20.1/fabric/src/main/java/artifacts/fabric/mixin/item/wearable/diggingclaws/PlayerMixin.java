package artifacts.fabric.mixin.item.wearable.diggingclaws;

import artifacts.item.wearable.hands.DiggingClawsItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        throw new IllegalStateException();
    }

    @ModifyReturnValue(method = "hasCorrectToolForDrops", at = @At("RETURN"))
    private boolean increaseBaseToolTier(boolean original, BlockState state) {
        return original || DiggingClawsItem.canDiggingClawsHarvest(this, state);
    }

    /**
     * This is identical to the forge version but might not be ideal
     * It adds the speed after the vanilla method does all the calculations on the base modifier such as haste and underwater
     */
    // TODO: identical artifacts-forge behaviour but could do this on the speed multiplier instead of end result
    @SuppressWarnings("ConstantConditions")
    @ModifyReturnValue(method = "getDestroySpeed", at = @At("RETURN"))
    private float increaseMiningSpeed(float original, BlockState state) {
        return original + DiggingClawsItem.getSpeedBonus((Player) (Object) this, state);
    }
}
