package artifacts.item.wearable.feet;

import artifacts.Artifacts;
import artifacts.item.wearable.ArtifactAttributeModifier;
import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModGameRules;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class SteadfastSpikesItem extends WearableArtifactItem {

    public SteadfastSpikesItem() {
        addAttributeModifier(ArtifactAttributeModifier.create(
                Attributes.KNOCKBACK_RESISTANCE,
                UUID.fromString("d5e712e8-3f85-436a-bd1d-506d791f7abd"),
                Artifacts.id("steadfast_spikes_knockback_resistance").toString(),
                () -> ModGameRules.STEADFAST_SPIKES_ENABLED.get() ? ModGameRules.STEADFAST_SPIKES_KNOCKBACK_RESISTANCE.get() : 0
            )
        );
    }
}
