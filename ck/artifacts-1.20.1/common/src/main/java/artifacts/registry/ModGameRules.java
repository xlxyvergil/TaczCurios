package artifacts.registry;

import artifacts.Artifacts;
import artifacts.mixin.gamerule.BooleanValueInvoker;
import artifacts.mixin.gamerule.IntegerValueInvoker;
import artifacts.network.BooleanGameRuleChangedPacket;
import artifacts.network.IntegerGameRuleChangedPacket;
import artifacts.network.NetworkHandler;
import com.google.common.base.CaseFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.GameRules;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModGameRules {

    private static final Map<String, BooleanValue> BOOLEAN_VALUES = new HashMap<>();
    private static final Map<String, IntegerValue> INTEGER_VALUES = new HashMap<>();

    public static final BooleanValue
            ANTIDOTE_VESSEL_ENABLED = booleanValue(createName(ModItems.ANTIDOTE_VESSEL, "enabled")),
            AQUA_DASHERS_ENABLED = booleanValue(createName(ModItems.AQUA_DASHERS, "enabled")),
            CHARM_OF_SINKING_ENABLED = booleanValue(createName(ModItems.CHARM_OF_SINKING, "enabled")),
            CLOUD_IN_A_BOTTLE_ENABLED = booleanValue(createName(ModItems.CLOUD_IN_A_BOTTLE, "enabled")),
            ETERNAL_STEAK_ENABLED = booleanValue(createName(ModItems.ETERNAL_STEAK, "enabled")),
            EVERLASTING_BEEF_ENABLED = booleanValue(createName(ModItems.EVERLASTING_BEEF, "enabled")),
            KITTY_SLIPPERS_ENABLED = booleanValue(createName(ModItems.KITTY_SLIPPERS, "enabled")),
            NIGHT_VISION_GOGGLES_ENABLED = booleanValue(createName(ModItems.NIGHT_VISION_GOGGLES, "enabled")), // TODO remove 1.20.2
            PICKAXE_HEATER_ENABLED = booleanValue(createName(ModItems.PICKAXE_HEATER, "enabled")),
            ROOTED_BOOTS_ENABLED = booleanValue(createName(ModItems.ROOTED_BOOTS, "enabled")),
            SCARF_OF_INVISIBILITY_ENABLED = booleanValue(createName(ModItems.SCARF_OF_INVISIBILITY, "enabled")),
            SNORKEL_ENABLED = booleanValue(createName(ModItems.SNORKEL, "enabled")), // TODO remove 1.20.2
            STEADFAST_SPIKES_ENABLED = booleanValue(createName(ModItems.STEADFAST_SPIKES, "enabled")), // TODO remove 1.20.2
            UNIVERSAL_ATTRACTOR_ENABLED = booleanValue(createName(ModItems.UNIVERSAL_ATTRACTOR, "enabled")),

            BUNNY_HOPPERS_DO_CANCEL_FALL_DAMAGE = booleanValue(createName(ModItems.BUNNY_HOPPERS, "doCancelFallDamage")),
            CHORUS_TOTEM_DO_CONSUME_ON_USE = booleanValue(createName(ModItems.CHORUS_TOTEM, "doConsumeOnUse")),
            FLAME_PENDANT_DO_GRANT_FIRE_RESISTANCE = booleanValue(createName(ModItems.FLAME_PENDANT, "doGrantFireResistance")),
            ROOTED_BOOTS_DO_GROW_PLANTS_AFTER_EATING = booleanValue(createName(ModItems.ROOTED_BOOTS, "doGrowPlantsAfterEating")),
            RUNNING_SHOES_DO_INCREASE_STEP_HEIGHT = booleanValue(createName(ModItems.RUNNING_SHOES, "doIncreaseStepHeight")),
            SHOCK_PENDANT_DO_CANCEL_LIGHTNING_DAMAGE = booleanValue(createName(ModItems.SHOCK_PENDANT, "doCancelLightningDamage")),
            SNORKEL_IS_INFINITE = booleanValue(createName(ModItems.SNORKEL, "isInfinite"), false),
            SNOWSHOES_ALLOW_WALKING_ON_POWDER_SNOW = booleanValue(createName(ModItems.SNOWSHOES, "allowWalkingOnPowderSnow")),
            UMBRELLA_IS_SHIELD = booleanValue(createName(ModItems.UMBRELLA, "isShield")),
            UMBRELLA_IS_GLIDER = booleanValue(createName(ModItems.UMBRELLA, "isGlider"));

    public static final IntegerValue
            ANGLERS_HAT_LUCK_OF_THE_SEA_LEVEL_BONUS = integerValue(createName(ModItems.ANGLERS_HAT, "luckOfTheSeaLevelBonus"), 1),
            ANGLERS_HAT_LURE_LEVEL_BONUS = integerValue(createName(ModItems.ANGLERS_HAT, "lureLevelBonus"), 1),
            CROSS_NECKLACE_BONUS_INVINCIBILITY_TICKS = integerValue(createName(ModItems.CROSS_NECKLACE, "bonusInvincibilityTicks"), 20, 60 * 20),
            CHORUS_TOTEM_HEALTH_RESTORED = integerValue(createName(ModItems.CHORUS_TOTEM, "healthRestored"), 10),
            CRYSTAL_HEART_HEALTH_BONUS = integerValue(createName(ModItems.CRYSTAL_HEART, "healthBonus"), 10, 100),
            DIGGING_CLAWS_TOOL_TIER = integerValue(createName(ModItems.DIGGING_CLAWS, "toolTier"), Tiers.STONE.getLevel() + 1, Tiers.NETHERITE.getLevel() + 1),
            LUCKY_SCARF_FORTUNE_BONUS = integerValue(createName(ModItems.LUCKY_SCARF, "fortuneBonus"), 1, 100),
            POWER_GLOVE_ATTACK_DAMAGE_BONUS = integerValue(createName(ModItems.POWER_GLOVE, "attackDamageBonus"), 4),
            SUPERSTITIOUS_HAT_LOOTING_LEVEL_BONUS = integerValue(createName(ModItems.SUPERSTITIOUS_HAT, "lootingLevelBonus"), 1, 100),
            THORN_PENDANT_MAX_DAMAGE = integerValue(createName(ModItems.THORN_PENDANT, "maxDamage"), 6),
            THORN_PENDANT_MIN_DAMAGE = integerValue(createName(ModItems.THORN_PENDANT, "minDamage"), 2),
            VAMPIRIC_GLOVE_MAX_HEALING_PER_HIT = integerValue(createName(ModItems.VAMPIRIC_GLOVE, "maxHealingPerHit"), 6),
            VILLAGER_HAT_REPUTATION_BONUS = integerValue(createName(ModItems.VILLAGER_HAT, "reputationBonus"), 100),

            ANTIDOTE_VESSEL_MAX_EFFECT_DURATION = durationSeconds(createName(ModItems.ANTIDOTE_VESSEL, "maxEffectDuration"), 5),
            CHORUS_TOTEM_COOLDOWN = durationSeconds(createName(ModItems.CHORUS_TOTEM, "cooldown"), 0),
            CROSS_NECKLACE_COOLDOWN = durationSeconds(createName(ModItems.CROSS_NECKLACE, "cooldown"), 0),
            ETERNAL_STEAK_COOLDOWN = durationSeconds(createName(ModItems.ETERNAL_STEAK, "cooldown"), 15),
            EVERLASTING_BEEF_COOLDOWN = durationSeconds(createName(ModItems.EVERLASTING_BEEF, "cooldown"), 15),
            FIRE_GAUNTLET_FIRE_DURATION = durationSeconds(createName(ModItems.FIRE_GAUNTLET, "fireDuration"), 8),
            FLAME_PENDANT_COOLDOWN = durationSeconds(createName(ModItems.FLAME_PENDANT, "cooldown"), 0),
            FLAME_PENDANT_FIRE_DURATION = durationSeconds(createName(ModItems.FLAME_PENDANT, "fireDuration"), 10),
            HELIUM_FLAMINGO_FLIGHT_DURATION = durationSeconds(createName(ModItems.HELIUM_FLAMINGO, "flightDuration"), 8),
            HELIUM_FLAMINGO_RECHARGE_DURATION = durationSeconds(createName(ModItems.HELIUM_FLAMINGO, "rechargeDuration"), 15),
            OBSIDIAN_SKULL_FIRE_RESISTANCE_COOLDOWN = durationSeconds(createName(ModItems.OBSIDIAN_SKULL, "fireResistanceCooldown"), 60),
            OBSIDIAN_SKULL_FIRE_RESISTANCE_DURATION = durationSeconds(createName(ModItems.OBSIDIAN_SKULL, "fireResistanceDuration"), 30),
            ONION_RING_HASTE_DURATION_PER_FOOD_POINT = durationSeconds(createName(ModItems.ONION_RING, "hasteDurationPerFoodPoint"), 6),
            PANIC_NECKLACE_COOLDOWN = durationSeconds(createName(ModItems.PANIC_NECKLACE, "cooldown"), 0),
            PANIC_NECKLACE_SPEED_DURATION = durationSeconds(createName(ModItems.PANIC_NECKLACE, "speedDuration"), 8),
            ROOTED_BOOTS_HUNGER_REPLENISHING_DURATION = durationSeconds(createName(ModItems.ROOTED_BOOTS, "hungerReplenishingDuration"), 15),
            SHOCK_PENDANT_COOLDOWN = durationSeconds(createName(ModItems.SHOCK_PENDANT, "cooldown"), 0),
            SNORKEL_WATER_BREATHING_DURATION = durationSeconds(createName(ModItems.SNORKEL, "waterBreathingDuration"), 15),
            THORN_PENDANT_COOLDOWN = durationSeconds(createName(ModItems.THORN_PENDANT, "cooldown"), 0),

            BUNNY_HOPPERS_JUMP_BOOST_LEVEL = mobEffectLevel(createName(ModItems.BUNNY_HOPPERS, "jumpBoostLevel"), 2),
            COWBOY_HAT_SPEED_LEVEL = mobEffectLevel(createName(ModItems.COWBOY_HAT, "speedLevel"), 2),
            ONION_RING_HASTE_LEVEL = mobEffectLevel(createName(ModItems.ONION_RING, "hasteLevel"), 2),
            PANIC_NECKLACE_SPEED_LEVEL = mobEffectLevel(createName(ModItems.PANIC_NECKLACE, "speedLevel"), 1);

    public static final DoubleValue
            CHORUS_TOTEM_TELEPORTATION_CHANCE = percentage(createName(ModItems.CHORUS_TOTEM, "teleportationChance"), 100),
            CLOUD_IN_A_BOTTLE_SPRINT_JUMP_VERTICAL_VELOCITY = doubleValue(createName(ModItems.CLOUD_IN_A_BOTTLE, "sprintJumpVerticalVelocity"), 50, 100 * 100, 100),
            CLOUD_IN_A_BOTTLE_SPRINT_JUMP_HORIZONTAL_VELOCITY = doubleValue(createName(ModItems.CLOUD_IN_A_BOTTLE, "sprintJumpHorizontalVelocity"), 50, 100 * 100, 100),
            DIGGING_CLAWS_DIG_SPEED_BONUS = doubleValue(createName(ModItems.DIGGING_CLAWS, "digSpeedBonus"), 32, 10),
            FERAL_CLAWS_ATTACK_SPEED_BONUS = percentage(createName(ModItems.FERAL_CLAWS, "attackSpeedBonus"), 40),
            FLAME_PENDANT_STRIKE_CHANCE = percentage(createName(ModItems.FLAME_PENDANT, "strikeChance"), 40),
            FLIPPERS_SWIM_SPEED_BONUS = doubleValue(createName(ModItems.FLIPPERS, "swimSpeedBonus"), 100, 100 * 100, 100),
            GOLDEN_HOOK_EXPERIENCE_BONUS = doubleValue(createName(ModItems.GOLDEN_HOOK, "experienceBonus"), 75, 10 * 100, 100),
            NIGHT_VISION_GOGGLES_STRENGTH = percentage(createName(ModItems.NIGHT_VISION_GOGGLES, "strength"), 25),
            NOVELTY_DRINKING_HAT_DRINKING_DURATION_MULTIPLIER = percentage(createName(ModItems.NOVELTY_DRINKING_HAT, "drinkingDurationMultiplier"), 30),
            NOVELTY_DRINKING_HAT_EATING_DURATION_MULTIPLIER = percentage(createName(ModItems.NOVELTY_DRINKING_HAT, "eatingDurationMultiplier"), 60),
            PLASTIC_DRINKING_HAT_DRINKING_DURATION_MULTIPLIER = percentage(createName(ModItems.PLASTIC_DRINKING_HAT, "drinkingDurationMultiplier"), 30),
            PLASTIC_DRINKING_HAT_EATING_DURATION_MULTIPLIER = percentage(createName(ModItems.PLASTIC_DRINKING_HAT, "eatingDurationMultiplier"), 60),
            POCKET_PISTON_KNOCKBACK_STRENGTH = doubleValue(createName(ModItems.POCKET_PISTON, "knockbackStrength"), 15, 10),
            RUNNING_SHOES_SPEED_BONUS = doubleValue(createName(ModItems.RUNNING_SHOES, "speedBonus"), 40, 100 * 100, 100),
            SHOCK_PENDANT_STRIKE_CHANCE = percentage(createName(ModItems.SHOCK_PENDANT, "strikeChance"), 25),
            STEADFAST_SPIKES_KNOCKBACK_RESISTANCE = doubleValue(createName(ModItems.STEADFAST_SPIKES, "knockbackResistance"), 10, 10, 10),
            SNOWSHOES_SLIPPERINESS_REDUCTION = percentage(createName(ModItems.SNOWSHOES, "slipperinessReduction"), 100),
            THORN_PENDANT_STRIKE_CHANCE = percentage(createName(ModItems.THORN_PENDANT, "strikeChance"), 50),
            VAMPIRIC_GLOVE_ABSORPTION_CHANCE = percentage(createName(ModItems.VAMPIRIC_GLOVE, "absorptionChance"), 100),
            VAMPIRIC_GLOVE_ABSORPTION_RATIO = doubleValue(createName(ModItems.VAMPIRIC_GLOVE, "absorptionRatio"), 20, 100),
            WHOOPEE_CUSHION_FART_CHANCE = percentage(createName(ModItems.WHOOPEE_CUSHION, "fartChance"), 12);

    private static String createName(RegistrySupplier<? extends Item> item, String name) {
        return String.format("%s.%s.%s",
                Artifacts.MOD_ID,
                CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, item.getId().getPath()),
                name
        );
    }

    private static BooleanValue booleanValue(String name) {
        return booleanValue(name, true);
    }

    private static BooleanValue booleanValue(String name, boolean defaultValue) {
        BooleanValue result = new BooleanValue();
        result.update(defaultValue);
        GameRules.Type<GameRules.BooleanValue> type = BooleanValueInvoker.invokeCreate(defaultValue, (server, value) -> {
            result.update(value.get());
            NetworkHandler.CHANNEL.sendToPlayers(server.getPlayerList().getPlayers(), new BooleanGameRuleChangedPacket(name, value.get()));
        });
        result.key = GameRules.register(name, GameRules.Category.PLAYER, type);
        BOOLEAN_VALUES.put(name, result);
        return result;
    }

    private static IntegerValue integerValue(String name, int defaultValue) {
        return integerValue(name, defaultValue, Integer.MAX_VALUE);
    }

    private static IntegerValue integerValue(String name, int defaultValue, int maxValue) {
        return integerValue(name, defaultValue, maxValue, 1);
    }

    private static IntegerValue integerValue(String name, int defaultValue, int maxValue, int multiplier) {
        IntegerValue result = new IntegerValue(defaultValue, maxValue, multiplier);
        result.update(defaultValue);
        GameRules.Type<GameRules.IntegerValue> type = IntegerValueInvoker.invokeCreate(defaultValue, (server, value) -> {
            result.update(value.get());
            NetworkHandler.CHANNEL.sendToPlayers(server.getPlayerList().getPlayers(), new IntegerGameRuleChangedPacket(name, value.get()));
        });
        result.key = GameRules.register(name, GameRules.Category.PLAYER, type);

        INTEGER_VALUES.put(name, result);
        return result;
    }

    private static IntegerValue durationSeconds(String name, int defaultValue) {
        return integerValue(name, defaultValue, 20 * 60 * 60, 20);
    }

    private static IntegerValue mobEffectLevel(String name, int defaultValue) {
        return integerValue(name, defaultValue, 128);
    }

    private static DoubleValue doubleValue(String name, int defaultValue, int maxValue, double factor) {
        return new DoubleValue(integerValue(name, defaultValue, maxValue), factor);
    }

    private static DoubleValue doubleValue(String name, int defaultValue, int factor) {
        return doubleValue(name, defaultValue, Integer.MAX_VALUE, factor);
    }

    private static DoubleValue percentage(String name, int defaultValue) {
        return doubleValue(name, defaultValue, 100, 100);
    }

    public static void updateValue(String key, boolean value) {
        BOOLEAN_VALUES.get(key).update(value);
    }

    public static void updateValue(String key, int value) {
        INTEGER_VALUES.get(key).update(value);
    }

    public static void onPlayerJoinLevel(ServerPlayer player) {
        BOOLEAN_VALUES.forEach((key, value) -> NetworkHandler.CHANNEL.sendToPlayer(player, new BooleanGameRuleChangedPacket(key, value.value)));
        INTEGER_VALUES.forEach((key, value) -> NetworkHandler.CHANNEL.sendToPlayer(player, new IntegerGameRuleChangedPacket(key, value.value)));
    }

    public static void onServerStarted(MinecraftServer server) {
        BOOLEAN_VALUES.values().forEach(value -> value.update(server));
        INTEGER_VALUES.values().forEach(value -> value.update(server));
    }

    public static class BooleanValue implements Supplier<Boolean> {

        private Boolean value = true;
        private GameRules.Key<GameRules.BooleanValue> key;

        @Override
        public Boolean get() {
            return value;
        }

        private void update(MinecraftServer server) {
            update(server.getGameRules().getBoolean(key));
        }

        private void update(boolean value) {
            this.value = value;
        }
    }

    public static class IntegerValue implements Supplier<Integer> {

        private final int max;
        private final int multiplier;
        private int value;
        private GameRules.Key<GameRules.IntegerValue> key;

        private IntegerValue(int defaultValue, int max, int multiplier) {
            this.value = defaultValue;
            this.max = max;
            this.multiplier = multiplier;
        }

        @Override
        public Integer get() {
            return Math.min(max, Math.max(0, value)) * multiplier;
        }

        private void update(MinecraftServer server) {
            update(server.getGameRules().getInt(key));
        }

        private void update(int value) {
            this.value = value;
        }
    }

    public record DoubleValue(IntegerValue integerValue, double factor) implements Supplier<Double> {

        @Override
        public Double get() {
            return integerValue.get() / factor;
        }

        public boolean fuzzyEquals(double a) {
            return Math.abs(get() - a) < 1e-10;
        }
    }
}
