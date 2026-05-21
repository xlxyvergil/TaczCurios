package com.xlxyvergil.tcc.mixin;

import net.minecraftforge.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Mixin 配置插件，用于条件性加载 Apotheosis 相关的 Mixin
 */
public class TccMixinPlugin implements IMixinConfigPlugin {
    
    private static final String APOTHEOSIS_MODID = "apotheosis";
    
    @Override
    public void onLoad(String mixinPackage) {
        // Nothing
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // 检查是否是 Apotheosis 相关的 Mixin
        if (mixinClassName.contains("apothiccurios")) {
            return ModList.get().isLoaded(APOTHEOSIS_MODID);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // Nothing
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // Nothing
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // Nothing
    }
}
