package artifacts.item.wearable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;
import java.util.function.Supplier;

public abstract class ArtifactAttributeModifier {

    private final Attribute attribute;
    private final UUID modifierId;
    private final String modifierName;

    public ArtifactAttributeModifier(Attribute attribute, UUID modifierId, String modifierName) {
        this.attribute = attribute;
        this.modifierId = modifierId;
        this.modifierName = modifierName;
    }

    protected AttributeModifier createModifier() {
        return new AttributeModifier(modifierId, modifierName, getAmount(), getOperation());
    }

    public Attribute getAttribute() {
        return attribute;
    }

    protected UUID getModifierId() {
        return modifierId;
    }

    public abstract double getAmount();

    public AttributeModifier.Operation getOperation() {
        return AttributeModifier.Operation.ADDITION;
    }

    protected void onAttributeUpdated(LivingEntity entity) {

    }

    public static ArtifactAttributeModifier create(Attribute attribute, UUID modifierId, String modifierName, Supplier<Double> amount) {
        return new ArtifactAttributeModifier(attribute, modifierId, modifierName) {
            @Override
            public double getAmount() {
                return amount.get();
            }
        };
    }
}
