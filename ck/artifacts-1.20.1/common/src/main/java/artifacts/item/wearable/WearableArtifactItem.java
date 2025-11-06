package artifacts.item.wearable;

import artifacts.Artifacts;
import artifacts.client.ToggleKeyHandler;
import artifacts.item.ArtifactItem;
import artifacts.platform.PlatformServices;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class WearableArtifactItem extends ArtifactItem {

    public WearableArtifactItem(Properties properties) {
        super(properties);
    }

    public WearableArtifactItem() {
        this(new Properties());
    }

    private final List<ArtifactAttributeModifier> attributeModifiers = new ArrayList<>();

    public void addAttributeModifier(ArtifactAttributeModifier attributeModifier) {
        attributeModifiers.add(attributeModifier);
    }

    public List<ArtifactAttributeModifier> getAttributeModifiers() {
        return attributeModifiers;
    }

    public boolean isEquippedBy(@Nullable LivingEntity entity) {
        return PlatformServices.platformHelper.isEquippedBy(entity, this);
    }

    public Stream<ItemStack> findAllEquippedBy(LivingEntity entity) {
        return PlatformServices.platformHelper.findAllEquippedBy(entity, this);
    }

    public void onEquip(LivingEntity entity, ItemStack stack) {
        if (entity.level().isClientSide()) {
            return;
        }
        for (ArtifactAttributeModifier modifier : attributeModifiers) {
            AttributeInstance attributeInstance = entity.getAttribute(modifier.getAttribute());
            if (attributeInstance != null) {
                attributeInstance.removeModifier(modifier.getModifierId());
                AttributeModifier attributeModifier = modifier.createModifier();
                attributeInstance.addPermanentModifier(attributeModifier);
                modifier.onAttributeUpdated(entity);
            }
        }
    }

    public void onUnequip(LivingEntity entity, ItemStack stack) {
        if (entity.level().isClientSide()) {
            return;
        }
        for (ArtifactAttributeModifier modifier : attributeModifiers) {
            AttributeInstance attributeInstance = entity.getAttribute(modifier.getAttribute());
            if (attributeInstance != null) {
                attributeInstance.removeModifier(modifier.getModifierId());
                modifier.onAttributeUpdated(entity);
            }
        }
    }

    public void wornTick(LivingEntity entity, ItemStack stack) {
        if (entity.level().isClientSide()) {
            return;
        }
        for (ArtifactAttributeModifier modifier : attributeModifiers) {
            AttributeInstance attributeInstance = entity.getAttribute(modifier.getAttribute());
            if (attributeInstance != null) {
                AttributeModifier existingModifier = attributeInstance.getModifier(modifier.getModifierId());
                if (existingModifier == null || existingModifier.getAmount() != modifier.getAmount()) {
                    attributeInstance.removeModifier(modifier.getModifierId());
                    attributeInstance.addPermanentModifier(modifier.createModifier());
                    modifier.onAttributeUpdated(entity);
                }
            }
        }
    }

    @Override
    public final boolean isCosmetic() {
        for (ArtifactAttributeModifier modifier : attributeModifiers) {
            if (modifier.getAmount() != 0) {
                return false;
            }
        }
        return getFortuneLevel() == 0 && getLootingLevel() == 0 && !canWalkOnPowderedSnow() && !hasNonCosmeticEffects();
    }

    protected boolean hasNonCosmeticEffects() {
        return false;
    }

    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GENERIC;
    }

    public int getFortuneLevel() {
        return 0;
    }

    public int getLootingLevel() {
        return 0;
    }

    public boolean makesPiglinsNeutral() {
        return false;
    }

    public boolean canWalkOnPowderedSnow() {
        return false;
    }

    public void toggleItem(ServerPlayer player) {
        findAllEquippedBy(player).forEach(stack -> setActivated(stack, !isActivated(stack)));
    }

    public static boolean isActivated(ItemStack stack) {
        return !stack.hasTag()
                || !stack.getOrCreateTag().contains("isActivated")
                || stack.getOrCreateTag().getBoolean("isActivated");
    }

    public static void setActivated(ItemStack stack, boolean active) {
        stack.getOrCreateTag().putBoolean("isActivated", active);
    }

    @Override
    protected void addEffectsTooltip(ItemStack stack, List<MutableComponent> tooltip) {
        super.addEffectsTooltip(stack, tooltip);
        KeyMapping key = ToggleKeyHandler.getToggleKey(this);
        if (key != null && (!key.isUnbound() || !isActivated(stack))) {
            tooltip.add(Component.translatable("%s.tooltip.toggle_keymapping".formatted(Artifacts.MOD_ID), key.getTranslatedKeyMessage()));
        }
    }
}
