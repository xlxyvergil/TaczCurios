package artifacts.forge.registry;

import artifacts.Artifacts;
import artifacts.item.wearable.ArtifactAttributeModifier;
import artifacts.registry.ModGameRules;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class ModItemsForge {

    public static void register() {
        artifacts.registry.ModItems.FLIPPERS.get().addAttributeModifier(ArtifactAttributeModifier.create(
                ForgeMod.SWIM_SPEED.get(),
                UUID.fromString("83f4e257-cd5c-4a36-ba4b-c052422ce7cf"),
                Artifacts.id("flippers_swim_speed_bonus").toString(),
                ModGameRules.FLIPPERS_SWIM_SPEED_BONUS
        ));
    }
}
