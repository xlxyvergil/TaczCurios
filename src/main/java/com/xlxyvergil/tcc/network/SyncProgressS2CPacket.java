package com.xlxyvergil.tcc.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端的进度同步数据包。
 * key 格式：
 *   - 成就进度: "progress_tcc:my_island"
 *   - 维度访问: "visited_tcc_visited_dimensions#minecraft:the_end"
 *   - 群系访问: "visited_tcc_visited_biomes#minecraft:plains"
 * <p>
 * 客户端收到后将数据写入 {@code Minecraft.getInstance().player.getPersistentData()}，
 * 供 tooltip 显示时直接从玩家 NBT 读取。
 */
public record SyncProgressS2CPacket(String key, int value) {

    private static final String PROGRESS_PREFIX = "tcc_ach_progress_";

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(key);
        buf.writeInt(value);
    }

    public static SyncProgressS2CPacket decode(FriendlyByteBuf buf) {
        return new SyncProgressS2CPacket(buf.readUtf(), buf.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                var player = Minecraft.getInstance().player;
                if (player == null) return;
                CompoundTag data = player.getPersistentData();

                if (key.startsWith("progress_")) {
                    // 成就进度写入玩家 NBT
                    String achievementId = key.substring("progress_".length());
                    String nbtKey = PROGRESS_PREFIX + achievementId.replace(':', '_');
                    data.putInt(nbtKey, value);
                } else if (key.startsWith("visited_")) {
                    // 维度/群系访问记录写入玩家 NBT 列表
                    String rest = key.substring("visited_".length());
                    int hashIdx = rest.indexOf('#');
                    if (hashIdx >= 0) {
                        String listKey = rest.substring(0, hashIdx);
                        String id = rest.substring(hashIdx + 1);
                        ListTag list;
                        if (data.contains(listKey, net.minecraft.nbt.Tag.TAG_LIST)) {
                            list = data.getList(listKey, net.minecraft.nbt.Tag.TAG_STRING);
                        } else {
                            list = new ListTag();
                        }
                        // 去重
                        boolean found = false;
                        for (var tag : list) {
                            if (tag.getAsString().equals(id)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            list.add(StringTag.valueOf(id));
                            data.put(listKey, list);
                        }
                    }
                }
            })
        );
        ctx.get().setPacketHandled(true);
    }
}
