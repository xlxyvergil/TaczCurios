package artifacts.fabric.mixin.config;

import artifacts.config.ModConfig;
import artifacts.fabric.extensions.ClientConfigFabric;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModConfig.Client.class)
public abstract class ClientConfigMixin implements ConfigData, ClientConfigFabric {

    @ConfigEntry.Gui.Tooltip(count = 2)
    @Comment("Whether the cosmetics toggle tooltip should be shown even when cosmetics are toggled on")
    public boolean alwaysShowCosmeticsToggleTooltip = true;

    @Override
    public boolean alwaysShowCosmeticsToggleTooltip() {
        return alwaysShowCosmeticsToggleTooltip;
    }
}
