package artifacts.mixin.item.wearable.chorustotem;

import artifacts.item.wearable.belt.ChorusTotemItem;
import artifacts.network.ChorusTotemUsedPacket;
import artifacts.network.NetworkHandler;
import artifacts.registry.ModGameRules;
import artifacts.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @SuppressWarnings("UnreachableCode")
    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void checkTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack totem = ChorusTotemItem.findTotem(entity);
        if (!totem.isEmpty()
                && entity.level() instanceof ServerLevel level
                && !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && ModGameRules.CHORUS_TOTEM_TELEPORTATION_CHANCE.get() > entity.getRandom().nextDouble()
        ) {
            ChorusTotemItem.teleport(entity, level);
            if (ModGameRules.CHORUS_TOTEM_DO_CONSUME_ON_USE.get()) {
                totem.shrink(1);
            } else {
                ModItems.CHORUS_TOTEM.get().addCooldown(entity, ModGameRules.CHORUS_TOTEM_COOLDOWN.get());
            }
            entity.setHealth(Math.min(entity.getMaxHealth(), Math.max(1, ModGameRules.CHORUS_TOTEM_HEALTH_RESTORED.get())));
            if (entity instanceof ServerPlayer player) {
                entity.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1, 1);
                NetworkHandler.CHANNEL.sendToPlayer(player, new ChorusTotemUsedPacket());
            }
            cir.setReturnValue(true); // early return intended!
        }
    }
}
