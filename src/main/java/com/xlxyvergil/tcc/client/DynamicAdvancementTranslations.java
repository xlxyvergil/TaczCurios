package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.DescriptionGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;
import java.util.Optional;

/**
 * 动态注入 TCC 成就进度描述到 Language 系统。
 * 这样 advancement JSON 的 "translate" 键不需要在 lang 文件中写死数值。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class DynamicAdvancementTranslations {

    private DynamicAdvancementTranslations() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(DynamicAdvancementTranslations::inject);
    }

    /** 包装当前的 Language，拦截 TCC advancement description 键，返回动态生成的文本。 */
    private static void inject() {
        Language original = Language.getInstance();
        Language proxy = new Language() {
            @Override
            public String getOrDefault(String key, String fallback) {
                if (key.startsWith("advancement.tcc.") && key.endsWith(".description")) {
                    String generated = generate(key);
                    if (generated != null) return generated;
                }
                return original.getOrDefault(key, fallback);
            }

            @Override
            public boolean has(String key) {
                if (key.startsWith("advancement.tcc.") && key.endsWith(".description")) {
                    return true;
                }
                return original.has(key);
            }

            @Override
            public boolean isDefaultRightToLeft() {
                return original.isDefaultRightToLeft();
            }

            @Override
            public FormattedCharSequence getVisualOrder(FormattedText text) {
                return original.getVisualOrder(text);
            }

            @Override
            public Map<String, String> getLanguageData() {
                return original.getLanguageData();
            }
        };
        Language.inject(proxy);
    }

    /** 从 translate key 中提取成就 ID，生成描述文本（不含进度部分）。 */
    private static String generate(String key) {
        String idPart = key.substring("advancement.".length(), key.length() - ".description".length());
        Optional<AchievementDefinitions.AchievementDef> opt = AchievementDefinitions.get(idPart);
        if (opt.isEmpty()) return null;
        AchievementDefinitions.AchievementDef def = opt.get();

        String locale = "en_us";
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.getLanguageManager() != null) {
                locale = mc.getLanguageManager().getSelected();
            }
        } catch (Exception ignored) {}

        String full = DescriptionGenerator.generate(def, locale);
        int idx = full.lastIndexOf(" (");
        if (idx > 0) full = full.substring(0, idx);
        return full;
    }
}
