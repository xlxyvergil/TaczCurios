package artifacts.mixin.item.wearable.pocketpiston.client;

import artifacts.extensions.pocketpiston.LivingEntityExtensions;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityExtensions {

    @Unique
    private static final int RETRACTION_DURATION = 2;

    @Unique
    private static final int RETRACTION_DELAY = 4;

    @Shadow
    public int swingTime;

    @Unique
    private float pocketPistonLength;

    @Unique
    private int pocketPistonTimeRemaining;

    @Inject(method = "tick", at = @At("HEAD"))
    private void updatePocketPistonLength(CallbackInfo ci) {
        if (swingTime != 0) {
            pocketPistonTimeRemaining = RETRACTION_DELAY + RETRACTION_DURATION;
        }

        if (pocketPistonTimeRemaining > 0) {
            pocketPistonTimeRemaining -= 1;
        }

        float d = (pocketPistonTimeRemaining < RETRACTION_DURATION ? -1F : 1F) / RETRACTION_DURATION;
        pocketPistonLength = Math.max(0, Math.min(1, pocketPistonLength + d));
    }

    @Unique
    @Override
    public float artifacts$getPocketPistonLength() {
        Minecraft minecraft = Minecraft.getInstance();
        float partialTick = (minecraft.isPaused() ? 0 : minecraft.getFrameTime());
        float d = (pocketPistonTimeRemaining + partialTick - 1 < RETRACTION_DURATION ? -1F : 1F) / RETRACTION_DURATION;
        return Math.max(0, Math.min(1, pocketPistonLength + d * partialTick));
    }
}
