package artifacts.forge.integration;

import lain.mods.cos.api.CosArmorAPI;
import net.minecraft.world.entity.player.Player;

public class CosmeticArmorCompat {

    public static boolean areBootsHidden(Player player) {
        return CosArmorAPI.getCAStacksClient(player.getUUID()).isSkinArmor(0);
    }
}
