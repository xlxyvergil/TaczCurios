package com.xlxyvergil.tcc.network;

import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.evolution.KeyTierRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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
public record SyncDataFilesS2CPacket(String achievements, String evolution, String tiers) {

    /** 单个文本长度上限，防止异常数据导致解码越界。 */
    private static final int MAX_LENGTH = 1 << 20;

    /** 服务端调用：抓取三份配置文件的当前内容并打包。 */
    public static SyncDataFilesS2CPacket capture() {
        return new SyncDataFilesS2CPacket(
                AchievementDefinitions.readConfigText(),
                EvolutionRegistry.readConfigText(),
                KeyTierRegistry.readConfigText());
    }

    public void encode(FriendlyByteBuf buf) {
        writeText(buf, achievements);
        writeText(buf, evolution);
        writeText(buf, tiers);
    }

    public static SyncDataFilesS2CPacket decode(FriendlyByteBuf buf) {
        return new SyncDataFilesS2CPacket(readText(buf), readText(buf), readText(buf));
    }

    private static void writeText(FriendlyByteBuf buf, String text) {
        buf.writeBoolean(text != null);
        if (text != null) {
            buf.writeUtf(text, MAX_LENGTH);
        }
    }

    private static String readText(FriendlyByteBuf buf) {
        return buf.readBoolean() ? buf.readUtf(MAX_LENGTH) : null;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        SyncDataFilesS2CPacket packet = this;
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSyncDataFiles(packet))
        );
        ctx.get().setPacketHandled(true);
    }
}
