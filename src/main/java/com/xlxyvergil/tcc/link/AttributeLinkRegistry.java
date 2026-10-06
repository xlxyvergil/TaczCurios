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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 数据包驱动的属性联动规则注册表。
 * <p>
 * 扫描 data/&lt;命名空间&gt;/tcc_links/*.json，文件形如：
 * <pre>
 * {
 *   "links": [
 *     {
 *       "slot": "tcc_slot",
 *       "curio": "tcc:xxx",
 *       "source": "attributeslib:crit_damage",
 *       "target": "tacz:bullet_gundamage",
 *       "ratio": 0.5,
 *       "operation": "multiply_base",
 *       "uuid": "8b3f2c14-6d5a-4e79-9c1b-2f4a6d8e0b31"
 *     }
 *   ]
 * }
 * </pre>
 * 每条规则自带槽位、来源饰品、源属性、目标属性、倍率与独立 uuid，
 * 同一槽位可配置多条规则，彼此 uuid 不同即可同时生效。
 * 未提供任何文件时规则为空，此时空白之键不产生任何效果。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttributeLinkRegistry extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "tcc_links";

    private static volatile Map<String, List<AttributeLinkData>> linksBySlot = Collections.emptyMap();

    /** reload / 世界加载重建规则表后自增；佩戴者据此判断自身 NBT 快照是否过期。 */
    private static volatile int version = 0;

    public AttributeLinkRegistry() {
        super(GSON, DIRECTORY);
    }

    /**
     * 规则表版本号：每次重建后自增，初始为 0。
     * 佩戴者把该值随规则快照一并写入物品 NBT，之后版本不相等即说明规则表已变化，需要重新解析。
     */
    public static int getVersion() {
        return version;
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new AttributeLinkRegistry());
    }

    /** 槽位 → 该槽位下全部规则（可能多条）。 */
    public static Map<String, List<AttributeLinkData>> getLinksBySlot() {
        return linksBySlot;
    }

    /** 按槽位查询规则列表，未配置时返回空列表。 */
    public static List<AttributeLinkData> getLinks(String slot) {
        return linksBySlot.getOrDefault(slot, Collections.emptyList());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<String, List<AttributeLinkData>> parsed = new LinkedHashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
            try {
                JsonObject root = entry.getValue().getAsJsonObject();
                JsonArray array = root.getAsJsonArray("links");
                if (array == null) {
                    continue;
                }
                for (JsonElement element : array) {
                    AttributeLinkData data = parseLink(element.getAsJsonObject(), entry.getKey());
                    if (data == null) {
                        continue;
                    }
                    parsed.computeIfAbsent(data.slot(), k -> new ArrayList<>()).add(data);
                }
            } catch (Exception e) {
                LOGGER.warn("[TCC] 解析属性联动数据包失败: {}", entry.getKey(), e);
            }
        }

        Map<String, List<AttributeLinkData>> frozen = new LinkedHashMap<>();
        parsed.forEach((slot, list) -> frozen.put(slot, List.copyOf(list)));
        linksBySlot = Collections.unmodifiableMap(frozen);
        int total = frozen.values().stream().mapToInt(List::size).sum();
        LOGGER.info("[TCC] 属性联动规则已加载: 槽位数={}, 规则数={}", frozen.size(), total);
        // 版本自增：服务端据此触发在线玩家重算，佩戴者也据此判断 NBT 快照是否过期
        version++;
    }

    private AttributeLinkData parseLink(JsonObject obj, ResourceLocation sourceFile) {
        if (!obj.has("slot") || !obj.has("curio") || !obj.has("source") || !obj.has("target")
                || !obj.has("ratio") || !obj.has("uuid")) {
            LOGGER.warn("[TCC] 属性联动规则缺少必要字段（slot/curio/source/target/ratio/uuid），来源 {}，已忽略", sourceFile);
            return null;
        }
        String slot = obj.get("slot").getAsString();
        String curio = obj.get("curio").getAsString();
        String source = obj.get("source").getAsString();
        String target = obj.get("target").getAsString();
        double ratio = obj.get("ratio").getAsDouble();
        if (slot.isBlank() || curio.isBlank() || source.isBlank() || target.isBlank()) {
            return null;
        }
        if (ratio == 0) {
            return null;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (IllegalArgumentException e) {
            LOGGER.warn("[TCC] 属性联动规则 uuid 非法: {}，来源 {}，已忽略", obj.get("uuid").getAsString(), sourceFile);
            return null;
        }

        AttributeModifier.Operation operation = AttributeModifier.Operation.MULTIPLY_BASE;
        if (obj.has("operation")) {
            AttributeModifier.Operation parse = parseOperation(obj.get("operation").getAsString());
            if (parse != null) {
                operation = parse;
            }
        }
        return new AttributeLinkData(slot, curio, source, target, ratio, operation, uuid);
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
