package artifacts.item.wearable.hands;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModGameRules;
import artifacts.registry.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;

public class OnionRingItem extends WearableArtifactItem {

    public OnionRingItem() {
        super(new Properties().food(new FoodProperties.Builder().nutrition(2).build()));
    }

    @Override
    public boolean hasNonCosmeticEffects() {
        return ModGameRules.ONION_RING_HASTE_DURATION_PER_FOOD_POINT.get() > 0 && ModGameRules.ONION_RING_HASTE_LEVEL.get() > 0;
    }

    public static void applyMiningSpeedBuff(LivingEntity entity, FoodProperties properties) {
        if (!ModItems.ONION_RING.get().isCosmetic()
                && ModItems.ONION_RING.get().isEquippedBy(entity)
                && properties.getNutrition() > 0
                && !properties.canAlwaysEat()
        ) {
            int duration = ModGameRules.ONION_RING_HASTE_DURATION_PER_FOOD_POINT.get() * properties.getNutrition();
            int level = ModGameRules.ONION_RING_HASTE_LEVEL.get();
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, duration, level - 1, false, false, true));
        }
    }
}
