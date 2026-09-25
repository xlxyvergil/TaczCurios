package com.xlxyvergil.tcc.network;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务端 → 客户端的配置同步数据包。
 *
 * <p>玩家登录时由服务端把当前 COMMON 配置整份序列化为 TOML 文本下发，客户端解析后覆盖本地的
 * COMMON 配置值。这样客户端 tooltip 等读取配置的显示逻辑与服务端实际结算用的数值保持一致。</p>
 */
public record SyncConfigS2CPacket(String toml) implements CustomPacketPayload {

    /** TOML 文本长度上限，防止异常数据导致解码越界。 */
    private static final int MAX_LENGTH = 1 << 20;

    public static final CustomPacketPayload.Type<SyncConfigS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sync_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncConfigS2CPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncConfigS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new SyncConfigS2CPacket(buf.readUtf(MAX_LENGTH));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SyncConfigS2CPacket packet) {
            buf.writeUtf(packet.toml(), MAX_LENGTH);
        }
    };

    /** 服务端调用：抓取当前 COMMON 配置快照并打包。 */
    public static SyncConfigS2CPacket capture() {
        ModConfigSpec spec = TaczCuriosConfig.COMMON_SPEC;
        CommentedConfig snapshot = CommentedConfig.inMemory();
        collect(spec.getSpec().valueMap(), spec.getValues().valueMap(), new ArrayList<>(), snapshot);

        StringWriter writer = new StringWriter();
        new TomlWriter().write(snapshot, writer);
        return new SyncConfigS2CPacket(writer.toString());
    }

    /**
     * 递归收集配置项：{@code getValues()} 的节点要么是叶子 {@link ModConfigSpec.ConfigValue}，
     * 要么是分组 {@link Config}，与 {@code ModConfigSpec#resetCaches} 的遍历方式一致；
     * {@code getSpec()} 与之同构，用于取出注释。
     *
     * <p>注释必须一并写出：客户端载入时 {@code ModConfigSpec#acceptConfig} 会先做一次配置校验，
     * 校验同时比对注释，注释缺失会被判定为「配置不正确」并触发修正与告警日志。</p>
     */
    private static void collect(Map<String, Object> specNodes, Map<String, Object> valueNodes,
                                List<String> parentPath, CommentedConfig target) {
        for (Map.Entry<String, Object> entry : valueNodes.entrySet()) {
            List<String> path = new ArrayList<>(parentPath);
            path.add(entry.getKey());
            Object valueNode = entry.getValue();
            Object specNode = specNodes.get(entry.getKey());

            if (valueNode instanceof ModConfigSpec.ConfigValue<?> value) {
                Object raw = value.get();
                // 枚举无法直接被 TOML 序列化，写成名字，客户端由 EnumGetMethod 还原
                target.set(path, raw instanceof Enum<?> e ? e.name() : raw);
                if (specNode instanceof ModConfigSpec.ValueSpec valueSpec && valueSpec.getComment() != null) {
                    target.setComment(path, valueSpec.getComment());
                }
            } else if (valueNode instanceof Config valueSection && specNode instanceof Config specSection) {
                collect(specSection.valueMap(), valueSection.valueMap(), path, target);
                String comment = TaczCuriosConfig.COMMON_SPEC.getLevelComment(path);
                if (comment != null) {
                    target.setComment(path, comment);
                }
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncConfigS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleSyncConfig(packet));
    }
}
