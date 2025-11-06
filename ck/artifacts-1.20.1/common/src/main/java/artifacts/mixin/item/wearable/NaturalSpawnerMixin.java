package artifacts.mixin.item.wearable;

import artifacts.Artifacts;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModLootTables;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {

    @Inject(method = "spawnCategoryForPosition(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V", shift = At.Shift.AFTER))
    private static void spawnCategoryForPosition(MobCategory mobCategory, ServerLevel serverLevel, ChunkAccess chunkAccess, BlockPos blockPos, NaturalSpawner.SpawnPredicate spawnPredicate, NaturalSpawner.AfterSpawnCallback afterSpawnCallback, CallbackInfo ci, @Local(ordinal = 0) Mob mob) {
        if (ModLootTables.ENTITY_EQUIPMENT.containsKey(mob.getType()) && mob.level() instanceof ServerLevel level) {
            ResourceLocation id = ModLootTables.ENTITY_EQUIPMENT.get(mob.getType());
            LootTable loottable = level.getServer().getLootData().getLootTable(id);
            LootParams.Builder params = new LootParams.Builder(level);

            LootParams lootparams = params.create(LootContextParamSets.EMPTY);
            loottable.getRandomItems(lootparams, mob.getLootTableSeed(), stack -> {
                if (!PlatformServices.platformHelper.tryEquipInFirstSlot(mob, stack)) {
                    Artifacts.LOGGER.warn("Could not equip item '{}' on spawned entity '{}'", stack, mob);
                }
            });
        }
    }
}
