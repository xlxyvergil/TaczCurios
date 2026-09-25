package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.evolution.KeyTierRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端的 JSON 数据同步包，携带 {@code config/tcc} 下三份配置文件的完整文本。
 *
 * <p>成就定义 / 进化规则 / 阶位表在客户端被 tooltip、成就进度条等显示逻辑读取，
 * 而实际结算发生在服务端。两边文件不一致时显示会与实际行为错位，
 * 因此登录时由服务端把自身实际生效的文件内容下发，客户端覆盖本地文件后重新加载。</p>
 *
 * @param achievements achievement_definitions.json 文本，无内容时为 null
 * @param evolution    evolution_rules.json 文本，无内容时为 null
 * @param tiers        key_tiers.json 文本，无内容时为 null
 */
public record SyncDataFilesS2CPacket(String achievements, String evolution, String tiers) implements CustomPacketPayload {

    /** 单个文本长度上限，防止异常数据导致解码越界。 */
    private static final int MAX_LENGTH = 1 << 20;

    public static final CustomPacketPayload.Type<SyncDataFilesS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "sync_data_files"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncDataFilesS2CPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncDataFilesS2CPacket decode(RegistryFriendlyByteBuf buf) {
            return new SyncDataFilesS2CPacket(readText(buf), readText(buf), readText(buf));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SyncDataFilesS2CPacket packet) {
            writeText(buf, packet.achievements());
            writeText(buf, packet.evolution());
            writeText(buf, packet.tiers());
        }
    };

    /** 服务端调用：抓取三份配置文件的当前内容并打包。 */
    public static SyncDataFilesS2CPacket capture() {
        return new SyncDataFilesS2CPacket(
                AchievementDefinitions.readConfigText(),
                EvolutionRegistry.readConfigText(),
                KeyTierRegistry.readConfigText());
    }

    private static void writeText(RegistryFriendlyByteBuf buf, String text) {
        buf.writeBoolean(text != null);
        if (text != null) {
            buf.writeUtf(text, MAX_LENGTH);
        }
    }

    private static String readText(RegistryFriendlyByteBuf buf) {
        return buf.readBoolean() ? buf.readUtf(MAX_LENGTH) : null;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncDataFilesS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleSyncDataFiles(packet));
    }
}
