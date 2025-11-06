package artifacts.item.wearable.head;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModGameRules;

public class VillagerHatItem extends WearableArtifactItem {

    @Override
    public boolean hasNonCosmeticEffects() {
        return ModGameRules.VILLAGER_HAT_REPUTATION_BONUS.get() > 0;
    }
}
