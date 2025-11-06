package artifacts.network;

import artifacts.registry.ModItems;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ChorusTotemUsedPacket {

    public ChorusTotemUsedPacket(FriendlyByteBuf buffer) {

    }

    public ChorusTotemUsedPacket() {

    }

    void encode(FriendlyByteBuf buffer) {

    }

    void apply(Supplier<NetworkManager.PacketContext> context) {
        Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(ModItems.CHORUS_TOTEM.get()));
        Player player = context.get().getPlayer();
        player.level().playSound(context.get().getPlayer(), context.get().getPlayer(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1, 1);
    }
}
