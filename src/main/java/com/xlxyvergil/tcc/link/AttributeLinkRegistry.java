package com.xlxyvergil.tcc.link;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据包驱动的属性联动规则注册表。
 * <p>
 * 扫描 data/&lt;命名空间&gt;/tcc_links/*.json，文件形如：
 * <pre>
 * {
 *   "slot": "tcc_slot",
 *   "links": [
 *     { "curio": "tcc:xxx", "attribute": "attributeslib:crit_damage", "ratio": 0.5, "operation": "multiply_base" }
 *   ]
 * }
 * </pre>
 * slot 为固定读取的槽位类型；每个饰品 id 至多一条规则，重复的以首次出现为准。
 * 未提供任何文件时规则为空，此时空白之键不产生任何效果。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttributeLinkRegistry extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "tcc_links";

    private static volatile String slot = null;
    private static volatile Map<String, AttributeLinkData> links = Collections.emptyMap();

    public AttributeLinkRegistry() {
        super(GSON, DIRECTORY);
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new AttributeLinkRegistry());
    }

    /** 固定读取的槽位类型，未配置时为 null。 */
    public static String getSlot() {
        return slot;
    }

    /** 按饰品 id 查询其唯一规则，未配置时返回 null。 */
    public static AttributeLinkData getLink(String curio) {
        return links.get(curio);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
        String parsedSlot = null;
        Map<String, AttributeLinkData> parsedLinks = new LinkedHashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
            try {
                JsonObject root = entry.getValue().getAsJsonObject();
                if (parsedSlot == null && root.has("slot")) {
                    parsedSlot = root.get("slot").getAsString();
                }
                JsonArray array = root.getAsJsonArray("links");
                if (array == null) {
                    continue;
                }
                for (JsonElement element : array) {
                    AttributeLinkData data = parseLink(element.getAsJsonObject());
                    if (data == null) {
                        continue;
                    }
                    // 每个 item 仅允许一条规则，重复的以首次出现为准
                    if (parsedLinks.putIfAbsent(data.curio(), data) != null) {
                        LOGGER.warn("[TCC] 属性联动规则存在重复的 curio: {}（来源 {}），已忽略后续重复项", data.curio(), entry.getKey());
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("[TCC] 解析属性联动数据包失败: {}", entry.getKey(), e);
            }
        }

        if (parsedSlot != null && parsedSlot.isBlank()) {
            parsedSlot = null;
        }
        slot = parsedSlot;
        links = Collections.unmodifiableMap(parsedLinks);
        LOGGER.info("[TCC] 属性联动规则已加载: slot={}, 规则数={}", parsedSlot, parsedLinks.size());
    }

    private AttributeLinkData parseLink(JsonObject obj) {
        if (!obj.has("curio") || !obj.has("attribute") || !obj.has("ratio")) {
            return null;
        }
        String curio = obj.get("curio").getAsString();
        String attribute = obj.get("attribute").getAsString();
        double ratio = obj.get("ratio").getAsDouble();
        if (ratio == 0) {
            return null;
        }
        AttributeModifier.Operation operation = AttributeModifier.Operation.MULTIPLY_BASE;
        if (obj.has("operation")) {
            AttributeModifier.Operation parsed = parseOperation(obj.get("operation").getAsString());
            if (parsed != null) {
                operation = parsed;
            }
        }
        return new AttributeLinkData(curio, attribute, ratio, operation);
    }

    private AttributeModifier.Operation parseOperation(String name) {
        return switch (name.toLowerCase(java.util.Locale.ROOT)) {
            case "addition", "add" -> AttributeModifier.Operation.ADDITION;
            case "multiply_base" -> AttributeModifier.Operation.MULTIPLY_BASE;
            case "multiply_total" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
            default -> null;
        };
    }
}
