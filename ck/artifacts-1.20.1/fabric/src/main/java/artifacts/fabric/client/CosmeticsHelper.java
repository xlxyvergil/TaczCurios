package artifacts.fabric.client;

import artifacts.item.ArtifactItem;
import net.minecraft.world.item.ItemStack;

public class CosmeticsHelper {

    public static boolean areCosmeticsToggledOffByPlayer(ItemStack stack) {
        return stack.hasTag()
                && stack.getOrCreateTag().getBoolean("CosmeticsDisabled")
                && !isCosmeticOnly(stack); // Always enable cosmetics when item is disabled by game rule
    }

    public static void toggleCosmetics(ItemStack stack) {
        if (!isCosmeticOnly(stack)) {
            stack.getOrCreateTag().putBoolean("CosmeticsDisabled", !areCosmeticsToggledOffByPlayer(stack));
        }
    }

    private static boolean isCosmeticOnly(ItemStack stack) {
        return stack.getItem() instanceof ArtifactItem item && item.isCosmetic();
    }
}
