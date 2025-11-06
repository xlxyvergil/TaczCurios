package artifacts.item.wearable.head;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModGameRules;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AnglersHatItem extends WearableArtifactItem {

    @Override
    protected boolean hasNonCosmeticEffects() {
        return ModGameRules.ANGLERS_HAT_LUCK_OF_THE_SEA_LEVEL_BONUS.get() > 0
                || ModGameRules.ANGLERS_HAT_LURE_LEVEL_BONUS.get() > 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    protected void addEffectsTooltip(ItemStack stack, List<MutableComponent> tooltip) {
        if (ModGameRules.ANGLERS_HAT_LUCK_OF_THE_SEA_LEVEL_BONUS.get() == 1) {
            tooltip.add(tooltipLine("luck_of_the_sea.single_level"));
        } else {
            tooltip.add(tooltipLine("luck_of_the_sea.multiple_levels", ModGameRules.ANGLERS_HAT_LUCK_OF_THE_SEA_LEVEL_BONUS.get()));
        }
        if (ModGameRules.ANGLERS_HAT_LURE_LEVEL_BONUS.get() == 1) {
            tooltip.add(tooltipLine("lure.single_level"));
        } else {
            tooltip.add(tooltipLine("lure.multiple_levels", ModGameRules.ANGLERS_HAT_LURE_LEVEL_BONUS.get()));
        }
    }
}
