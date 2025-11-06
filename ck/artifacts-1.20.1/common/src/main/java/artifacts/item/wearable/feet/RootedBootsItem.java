package artifacts.item.wearable.feet;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.network.PlaySoundAtPlayerPacket;
import artifacts.registry.ModGameRules;
import artifacts.registry.ModItems;
import artifacts.registry.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RootedBootsItem extends WearableArtifactItem {

    @Override
    protected boolean hasNonCosmeticEffects() {
        return ModGameRules.ROOTED_BOOTS_ENABLED.get();
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public void wornTick(LivingEntity entity, ItemStack stack) {
        if (entity instanceof ServerPlayer player
                && ModGameRules.ROOTED_BOOTS_ENABLED.get()
                && player.onGround()
                && player.getFoodData().needsFood()
                && entity.tickCount % Math.max(20, ModGameRules.ROOTED_BOOTS_HUNGER_REPLENISHING_DURATION.get()) == 0
                && entity.getBlockStateOn().is(ModTags.ROOTED_BOOTS_GRASS)
        ) {
            player.getFoodData().eat(1, 0.5F);
            PlaySoundAtPlayerPacket.sendSound(player, SoundEvents.GENERIC_EAT, 0.5F, 0.8F + entity.getRandom().nextFloat() * 0.4F);
        }
    }

    public static void applyBoneMeal(LivingEntity entity, FoodProperties properties) {
        if (!entity.level().isClientSide()
                && ModItems.ROOTED_BOOTS.get().isEquippedBy(entity)
                && properties.getNutrition() > 0
                && !properties.canAlwaysEat()
                && entity.onGround()
                && ModGameRules.ROOTED_BOOTS_ENABLED.get()
                && ModGameRules.ROOTED_BOOTS_DO_GROW_PLANTS_AFTER_EATING.get()
                && entity.getBlockStateOn().is(ModTags.ROOTED_BOOTS_GRASS)
        ) {
            BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), entity.level(), entity.getOnPos());
        }
    }
}
