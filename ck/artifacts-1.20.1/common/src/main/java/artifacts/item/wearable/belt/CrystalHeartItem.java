package artifacts.item.wearable.belt;

import artifacts.Artifacts;
import artifacts.item.wearable.ArtifactAttributeModifier;
import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModGameRules;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class CrystalHeartItem extends WearableArtifactItem {

    public CrystalHeartItem() {
        addAttributeModifier(new AttributeModifier());
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_DIAMOND;
    }

    private static class AttributeModifier extends ArtifactAttributeModifier {

        public AttributeModifier() {
            super(Attributes.MAX_HEALTH, UUID.fromString("99fa0537-90b9-481a-bc76-4650987faba3"), Artifacts.id("crystal_heart_health_bonus").toString());
        }

        @Override
        public double getAmount() {
            return ModGameRules.CRYSTAL_HEART_HEALTH_BONUS.get();
        }

        @Override
        protected void onAttributeUpdated(LivingEntity entity) {
            if (entity.getHealth() > entity.getMaxHealth()) {
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }
}
