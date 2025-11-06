package artifacts.item.wearable.feet;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModGameRules;
import artifacts.registry.ModItems;
import artifacts.registry.ModTags;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class SnowshoesItem extends WearableArtifactItem {

    @Override
    public boolean hasNonCosmeticEffects() {
        return ModGameRules.SNOWSHOES_SLIPPERINESS_REDUCTION.get() > 0;
    }

    @Override
    protected void addEffectsTooltip(ItemStack stack, List<MutableComponent> tooltip) {
        tooltip.add(tooltipLine("powder_snow"));
        tooltip.add(tooltipLine("slipperiness"));
    }

    public static float getModifiedFriction(float friction, LivingEntity entity, Block block) {
        if (ModItems.SNOWSHOES.get().isEquippedBy(entity)
                && ModTags.isInTag(block, BlockTags.ICE)
                && friction > 0.6F
        ) {
            return Mth.lerp(ModGameRules.SNOWSHOES_SLIPPERINESS_REDUCTION.get().floatValue(), friction, 0.6F);
        }
        return friction;
    }

    @Override
    public boolean canWalkOnPowderedSnow() {
        return ModGameRules.SNOWSHOES_ALLOW_WALKING_ON_POWDER_SNOW.get();
    }
}
