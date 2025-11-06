package artifacts.item.wearable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MobEffectItem extends WearableArtifactItem {

    private final MobEffect mobEffect;
    private final int duration;
    protected final Supplier<Integer> amplifier;
    protected final Supplier<Boolean> isEnabled;

    public MobEffectItem(MobEffect mobEffect, Supplier<Boolean> isEnabled) {
        this(mobEffect, () -> 1, 40,  isEnabled);
    }

    public MobEffectItem(MobEffect mobEffect, int duration, Supplier<Boolean> isEnabled) {
        this(mobEffect, () -> 1, duration, isEnabled);
    }

    public MobEffectItem(MobEffect mobEffect, Supplier<Integer> amplifier, int duration) {
        this(mobEffect, amplifier, duration, () -> true);
    }

    private MobEffectItem(MobEffect mobEffect, Supplier<Integer> amplifier, int duration, Supplier<Boolean> isEnabled) {
        this.mobEffect = mobEffect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.isEnabled = isEnabled;
    }

    @Override
    protected boolean hasNonCosmeticEffects() {
        return isEnabled.get() && amplifier.get() > 0;
    }

    public boolean isEffectActive(LivingEntity entity) {
        if (!isEnabled.get() || amplifier.get() == 0) {
            return false;
        }
        return findAllEquippedBy(entity).anyMatch(WearableArtifactItem::isActivated);
    }

    private int getAmplifier() {
        return this.amplifier.get() - 1;
    }

    protected int getDuration(LivingEntity entity) {
        return duration;
    }

    @Nullable
    protected LivingEntity getTarget(LivingEntity entity) {
        return entity;
    }

    protected boolean shouldShowIcon() {
        return false;
    }

    protected boolean shouldShowParticles() {
        return false;
    }

    protected int getUpdateInterval() {
        return 1;
    }

    @Override
    public void wornTick(LivingEntity entity, ItemStack stack) {
        if (isEffectActive(entity) && !entity.level().isClientSide()) {
            LivingEntity target = getTarget(entity);
            if (target != null && entity.tickCount % getUpdateInterval() == 0) {
                target.addEffect(new MobEffectInstance(mobEffect, getDuration(target) - 1, getAmplifier(), false, shouldShowParticles(), shouldShowIcon()));
            }
        }
    }

    @Override
    public void onUnequip(LivingEntity entity, ItemStack stack) {
        removeRemainingEffect(entity);
    }

    private void removeRemainingEffect(LivingEntity entity) {
        if (isEnabled.get() && !entity.level().isClientSide() && getTarget(entity) == entity) {
            MobEffectInstance effectInstance = entity.getEffect(mobEffect);
            if (effectInstance != null && effectInstance.getAmplifier() == getAmplifier() && !effectInstance.isVisible() && effectInstance.getDuration() < getDuration(entity)) {
                entity.removeEffect(mobEffect);
            }
        }
    }

    @Override
    public void toggleItem(ServerPlayer player) {
        super.toggleItem(player);
        if (!isEffectActive(player)) {
            removeRemainingEffect(player);
        }
    }
}
