package artifacts.item.wearable.hands;

import artifacts.Artifacts;
import artifacts.item.wearable.WearableArtifactItem;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModGameRules;
import artifacts.registry.ModItems;
import artifacts.registry.ModTags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class DiggingClawsItem extends WearableArtifactItem {

    @Override
    public boolean hasNonCosmeticEffects() {
        return getToolTier().isPresent() || ModGameRules.DIGGING_CLAWS_DIG_SPEED_BONUS.get() > 0;
    }

    @Override
    protected void addEffectsTooltip(ItemStack stack, List<MutableComponent> tooltip) {
        getToolTier().ifPresent(tier -> tooltip.add(
                tooltipLine("mining_level", Component.translatable("%s.tooltip.tool_tier.%s".formatted(Artifacts.MOD_ID, tier.getLevel() + 1)))
        ));
        if (ModGameRules.DIGGING_CLAWS_DIG_SPEED_BONUS.get() > 0) {
            tooltip.add(tooltipLine("mining_speed"));
        }
    }

    public static Optional<Tier> getToolTier() {
        return switch (ModGameRules.DIGGING_CLAWS_TOOL_TIER.get()) {
            case 0 -> Optional.empty();
            case 1 -> Optional.of(Tiers.WOOD);
            case 2 -> Optional.of(Tiers.STONE);
            case 3 -> Optional.of(Tiers.IRON);
            case 4 -> Optional.of(Tiers.DIAMOND);
            default -> Optional.of(Tiers.NETHERITE);
        };
    }

    public static boolean canDiggingClawsHarvest(LivingEntity entity, BlockState state) {
        if (ModItems.DIGGING_CLAWS.get().isEquippedBy(entity)) {
            Optional<Tier> tier = DiggingClawsItem.getToolTier();
            return tier.isPresent()
                    && PlatformServices.platformHelper.isCorrectTierForDrops(tier.get(), state)
                    && state.is(ModTags.MINEABLE_WITH_DIGGING_CLAWS);
        }
        return false;
    }

    public static float getSpeedBonus(Player player, BlockState state) {
        if (ModItems.DIGGING_CLAWS.get().isEquippedBy(player) && player.hasCorrectToolForDrops(state)) {
            return ModGameRules.DIGGING_CLAWS_DIG_SPEED_BONUS.get().floatValue();
        }
        return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }
}
