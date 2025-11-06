package artifacts.forge.mixin.item.wearable;

import artifacts.Artifacts;
import artifacts.item.ArtifactItem;
import artifacts.item.wearable.ArtifactAttributeModifier;
import artifacts.item.wearable.WearableArtifactItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

@Mixin(WearableArtifactItem.class)
public abstract class WearableArtifactItemMixin extends ArtifactItem {

    @Shadow
    public abstract List<ArtifactAttributeModifier> getAttributeModifiers();

    // mimic Curios' attribute tooltip
    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltipList, TooltipFlag flags) {
        super.appendHoverText(stack, world, tooltipList, flags);
        Set<String> curioTags = CuriosApi.getItemStackSlots(stack, true).keySet();
        List<String> slots = new ArrayList<>(curioTags);

        if (!Artifacts.CONFIG.client.showTooltips || isCosmetic() || slots.isEmpty() || getAttributeModifiers().isEmpty()) {
            return;
        }

        tooltipList.add(Component.empty());

        String identifier = slots.contains("curio") ? "curio" : slots.get(0);
        tooltipList.add(Component.translatable("curios.modifiers." + identifier));

        for (ArtifactAttributeModifier modifier : getAttributeModifiers()) {
            double amount = modifier.getAmount();

            if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                if (modifier.getAttribute().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                    amount *= 10;
                }
            } else {
                amount *= 100;
            }

            tooltipList.add((Component.translatable(
                    "attribute.modifier.plus." + modifier.getOperation().toValue(),
                    ATTRIBUTE_MODIFIER_FORMAT.format(amount),
                    Component.translatable(modifier.getAttribute().getDescriptionId())))
                    .withStyle(ChatFormatting.BLUE));
        }
    }
}
