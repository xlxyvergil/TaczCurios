package com.xlxyvergil.tcc.evolution;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class EvolutionScriptApi {
    private EvolutionScriptApi() {
    }

    public static void triggerLivingDeath(Player player, LivingEntity killed, Entity otherEntity, DamageSource source, boolean playerKilled) {
        LivingDeathEventHandler.triggerLivingDeath(player, killed, otherEntity, source, playerKilled, true);
    }

    public static boolean tryEvolve(Player player, String ruleId) {
        return LivingDeathEventHandler.tryEvolveRule(player, ruleId, true);
    }

    public static boolean applyAttribute(Player player, LivingEntity killed, DamageSource source, String ruleId) {
        return LivingDeathEventHandler.applyAttributeRule(player, killed, source, ruleId, true);
    }

    public static boolean tryGrant(Player player, Entity otherEntity, DamageSource source, String ruleId) {
        return LivingDeathEventHandler.tryGrantRule(player, otherEntity, source, ruleId, true);
    }

    public static boolean evolveCurio(LivingEntity entity, String fromItemId, String toItemId, List<String> excludeNbtKeys) {
        return evolveCurio(entity, fromItemId, toItemId, excludeNbtKeys, false);
    }

    public static boolean evolveCurio(LivingEntity entity, String fromItemId, String toItemId, List<String> excludeNbtKeys, boolean postTaczChangeEvent) {
        if (entity == null) {
            return false;
        }
        Item fromItem = resolveItem(fromItemId);
        Item toItem = resolveItem(toItemId);
        if (fromItem == null || toItem == null) {
            return false;
        }
        Collection<String> excludes = excludeNbtKeys == null ? Collections.emptyList() : List.copyOf(excludeNbtKeys);
        return EvolutionExecutor.evolve(entity, stack -> stack.getItem() == fromItem,
                () -> new ItemStack(toItem),
                EvolutionExecutor.NbtMode.COPY_ALL, excludes, (oldStack, newStack) -> LivingDeathEventHandler.resetCapCountersForItem(toItemId, newStack), postTaczChangeEvent);
    }

    public static boolean evolveCurioByUuid(String entityUuid, String fromItemId, String toItemId, List<String> excludeNbtKeys) {
        return evolveCurioByUuid(entityUuid, fromItemId, toItemId, excludeNbtKeys, false);
    }

    public static boolean evolveCurioByUuid(String entityUuid, String fromItemId, String toItemId, List<String> excludeNbtKeys, boolean postTaczChangeEvent) {
        UUID uuid;
        try {
            uuid = UUID.fromString(entityUuid);
        } catch (Exception ignored) {
            return false;
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return false;
        }

        for (ServerLevel level : server.getAllLevels()) {
            Entity e = level.getEntity(uuid);
            if (e instanceof LivingEntity living) {
                return evolveCurio(living, fromItemId, toItemId, excludeNbtKeys, postTaczChangeEvent);
            }
        }
        return false;
    }

    private static Item resolveItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return null;
        }
        try {
            return ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        } catch (Exception ignored) {
            return null;
        }
    }
}
