package artifacts.fabric.mixin.item.wearable.snowshoes;

import artifacts.item.wearable.feet.SnowshoesItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("ConstantConditions")
    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"))
    public float travel(Block block, Operation<Float> original) {
        return SnowshoesItem.getModifiedFriction(original.call(block), (LivingEntity) (Object) this, block);
    }
}
