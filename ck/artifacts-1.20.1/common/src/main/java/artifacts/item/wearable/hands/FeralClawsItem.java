package artifacts.item.wearable.hands;

import artifacts.Artifacts;
import artifacts.item.wearable.ArtifactAttributeModifier;
import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModGameRules;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class FeralClawsItem extends WearableArtifactItem {

    public FeralClawsItem() {
        addAttributeModifier(ArtifactAttributeModifier.create(
                Attributes.ATTACK_SPEED,
                UUID.fromString("9f08b24d-a700-4515-91d2-d32f6c8b4dfc"),
                Artifacts.id("feral_claws_attack_speed_bonus").toString(),
                ModGameRules.FERAL_CLAWS_ATTACK_SPEED_BONUS
        ));
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }
}
