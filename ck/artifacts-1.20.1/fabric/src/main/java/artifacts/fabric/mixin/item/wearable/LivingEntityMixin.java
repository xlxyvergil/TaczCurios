package artifacts.fabric.mixin.item.wearable;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.item.wearable.feet.RootedBootsItem;
import artifacts.item.wearable.hands.OnionRingItem;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected ItemStack useItem;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        TrinketsApi.getTrinketComponent(entity).ifPresent(component -> {
            for (Map<String, TrinketInventory> group : component.getInventory().values()) {
                for (TrinketInventory inventory : group.values()) {
                    for (int i = 0; i < inventory.getContainerSize(); i++) {
                        ItemStack stack = inventory.getItem(i);
                        if (!stack.isEmpty() && stack.getItem() instanceof WearableArtifactItem item) {
                            item.wornTick(entity, stack);
                        }
                    }
                }
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "completeUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;triggerItemUseEffects(Lnet/minecraft/world/item/ItemStack;I)V"))
    protected void completeUsingItem(CallbackInfo ci) {
        FoodProperties properties = useItem.getItem().getFoodProperties();
        if (properties != null) {
            LivingEntity entity = (LivingEntity) (Object) this;
            OnionRingItem.applyMiningSpeedBuff(entity, properties);
            RootedBootsItem.applyBoneMeal(entity, properties);
        }
    }
}
