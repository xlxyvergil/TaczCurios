package artifacts.item.wearable.feet;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModGameRules;

public class FlippersItem extends WearableArtifactItem {

    @Override
    public boolean hasNonCosmeticEffects() {
        return ModGameRules.FLIPPERS_SWIM_SPEED_BONUS.get() > 0;
    }
}
