package artifacts.item.wearable.head;

import artifacts.Artifacts;
import artifacts.client.ToggleKeyHandler;
import artifacts.item.wearable.MobEffectItem;
import artifacts.registry.ModGameRules;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class NightVisionGogglesItem extends MobEffectItem {

    public NightVisionGogglesItem() {
        super(MobEffects.NIGHT_VISION, 40, () -> ModGameRules.NIGHT_VISION_GOGGLES_ENABLED.get() && ModGameRules.NIGHT_VISION_GOGGLES_STRENGTH.get() > 0);
    }

    @Override
    protected void addEffectsTooltip(ItemStack stack, List<MutableComponent> tooltip) {
        if (ModGameRules.NIGHT_VISION_GOGGLES_STRENGTH.get() > 0.5) {
            tooltip.add(tooltipLine("full"));
        } else {
            tooltip.add(tooltipLine("partial"));
        }
        KeyMapping key = ToggleKeyHandler.getToggleKey(this);
        if (key != null && (!key.isUnbound() || !isActivated(stack))) {
            tooltip.add(Component.translatable("%s.tooltip.toggle_keymapping".formatted(Artifacts.MOD_ID), key.getTranslatedKeyMessage()));
        }
    }
}
