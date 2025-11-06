package artifacts.fabric.trinket;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.util.DamageSourceHelper;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class WearableArtifactTrinket implements Trinket {

    private final WearableArtifactItem item;

    public WearableArtifactTrinket(WearableArtifactItem item) {
        this.item = item;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            item.onEquip(entity, stack);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            item.onUnequip(entity, stack);
        }
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (DamageSourceHelper.shouldDestroyWornItemsOnDeath(entity)) {
            return TrinketEnums.DropRule.DESTROY;
        }
        return Trinket.super.getDropRule(stack, slot, entity);
    }
}
