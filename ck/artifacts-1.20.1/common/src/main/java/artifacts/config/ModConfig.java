package artifacts.config;

import artifacts.Artifacts;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = Artifacts.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/mossy_cobblestone.png")
public class ModConfig extends PartitioningSerializer.GlobalData {

    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.TransitiveObject()
    public Common common = new Common();

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject()
    public Client client = new Client();

    @Config(name = "common")
    public static final class Common implements ConfigData {

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.Tooltip(count = 4)
        @Comment("""
                Affects how common artifacts are in chests.
                Values above 1 will make artifacts rarer, values between 0 and 1 will make artifacts more common.
                Doubling this value will make artifacts approximately twice as hard to find, and vice versa.
                To prevent artifacts from appearing as chest loot, set this to 10000.
                
                To disable or change the effects of specific items, the /gamerule command can be used.
                A list of available game rules and their effects can be found on the wiki on GitHub:
                https://github.com/ochotonida/artifacts/wiki
                """)
        double artifactRarity = 1;

        public double getArtifactRarity() {
            return Math.max(0, artifactRarity);
        }

        @ConfigEntry.Gui.Tooltip(count = 2)
        @Comment("The chance everlasting beef drops when a cow or mooshroom is killed by a player")
        public double everlastingBeefChance = 1 / 500D;

        @ConfigEntry.Gui.Tooltip(count = 2)
        @Comment("The chance that a skeleton, zombie or piglin spawns with an artifact equipped")
        public double entityEquipmentChance = 1 / 1000D;

        @ConfigEntry.Gui.Tooltip
        @Comment("The chance that an artifact generates in suspicious sand or gravel")
        public double archaeologyChance = 1 / 16D;

        @ConfigEntry.Gui.Tooltip(count = 2)
        @Comment("Whether the Kitty Slippers and Bunny Hoppers change the player's hurt sounds")
        public boolean modifyHurtSounds = true;

        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        public Campsite campsite = new Campsite();

        public static final class Campsite implements ConfigData {

            @ConfigEntry.Gui.Tooltip(count = 2)
            @Comment("""
                    How many times a campsite will attempt to generate per chunk
                    Set this to 0 to prevent campsites from generating
                    """)
            public int count = 4;

            public int getCount() {
                return Math.max(0, count);
            }

            @ConfigEntry.Gui.Tooltip
            @Comment("The minimum height campsites can spawn at")
            public int minY = -60;

            @ConfigEntry.Gui.Tooltip
            @Comment("The maximum height campsites can spawn at")
            public int maxY = 40;

            @ConfigEntry.Gui.Tooltip
            @Comment("Probability that a campsite has a mimic instead of a chest")
            public double mimicChance = 0.3;

            public double getMimicChance() {
                return Math.max(0, Math.min(1, mimicChance));
            }

            @ConfigEntry.Gui.Tooltip
            @Comment("Whether to use wooden chests from other mods when generating campsites")
            public boolean useModdedChests = true;

            @ConfigEntry.Gui.Tooltip
            @Comment("Whether campsites can contain blocks that emit light")
            public boolean allowLightSources = true;
        }
    }

    @Config(name = "client")
    public static final class Client implements ConfigData {

        @ConfigEntry.Gui.Tooltip
        @Comment("Whether models for gloves should be shown in first person")
        public boolean showFirstPersonGloves = true;

        @ConfigEntry.Gui.Tooltip
        @Comment("Whether artifacts should have tooltips explaining their effects")
        public boolean showTooltips = true;

        @ConfigEntry.Gui.Tooltip
        @Comment("Whether mimics can use textures from Lootr or Quark")
        public boolean useModdedMimicTextures = true;

        @ConfigEntry.Gui.Tooltip
        @Comment("Display artifacts on cooldown next to the hotbar")
        public boolean enableCooldownOverlay = false;

        @ConfigEntry.Gui.Tooltip(count = 2)
        @Comment("""
                Location of the artifact cooldown gui element
                Distance from the hotbar measured in pixels
                Negative values place the element left of the hotbar
                """)
        public int cooldownOverlayOffset = 10;
    }
}
