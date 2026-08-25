package com.xlxyvergil.tcc.entity;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 逐火之蛾「真我」结界地面特效实体（纯视觉）。
 * <p>
 * 无碰撞、不可交互；服务端每 tick 跟随绑定玩家脚底（以玩家脚底中心为特效中心），
 * 存活 durationTicks 后自动消失；玩家死亡/消失时立即消失。
 * 客户端由 ZhenWoBarrierRenderer 渲染 zhenwo.png 平铺贴图。
 */
public class ZhenWoBarrierEntity extends Entity {

    private static final EntityDataAccessor<Integer> REMAINING_TICKS =
        SynchedEntityData.defineId(ZhenWoBarrierEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TOTAL_TICKS =
        SynchedEntityData.defineId(ZhenWoBarrierEntity.class, EntityDataSerializers.INT);

    /** 跟随玩家的 UUID */
    private static final String BOUND_PLAYER = "BoundPlayer";
    private static final String REMAIN_TICKS = "RemainingTicks";
    private static final String TOTAL = "TotalTicks";

    /** 实体 UUID 存于饰品 NBT 的 key */
    public static final String STACK_ENTITY_KEY = TaczCurios.MODID + "_zhen_wo_barrier_entity";

    /** 特效高于地面一点，避免与地面 z-fighting */
    private static final double LIFT = 0.05D;

    @Nullable
    private UUID boundPlayer;

    public ZhenWoBarrierEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    /**
     * 确保绑定玩家的脚下存在结界地面特效实体；已存在则不做任何事，不存在则生成。
     * 仅在服务端调用（ZhenWo.curioTick 已过滤客户端）。
     */
    public static void ensureActive(Level level, Player player, CompoundTag stackTag, int durationTicks) {
        if (level.isClientSide) return;
        String uuidStr = stackTag.getString(STACK_ENTITY_KEY);
        if (!uuidStr.isEmpty()) {
            try {
                Entity existing = ((ServerLevel) level).getEntity(UUID.fromString(uuidStr));
                if (existing instanceof ZhenWoBarrierEntity barrier && barrier.isAlive()) {
                    return;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        ZhenWoBarrierEntity barrier = new ZhenWoBarrierEntity(TccEntities.ZHEN_WO_BARRIER.get(), level);
        barrier.boundPlayer = player.getUUID();
        barrier.setRemainingTicks(durationTicks);
        barrier.setTotalTicks(durationTicks);
        barrier.setPos(player.getX(), player.getY() + LIFT, player.getZ());
        if (level.addFreshEntity(barrier)) {
            stackTag.putString(STACK_ENTITY_KEY, barrier.getStringUUID());
        }
    }

    public int getRemainingTicks() {
        return entityData.get(REMAINING_TICKS);
    }

    public int getTotalTicks() {
        return entityData.get(TOTAL_TICKS);
    }

    private void setRemainingTicks(int ticks) {
        entityData.set(REMAINING_TICKS, ticks);
    }

    private void setTotalTicks(int ticks) {
        entityData.set(TOTAL_TICKS, ticks);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (boundPlayer == null) {
            this.discard();
            return;
        }
        int remain = getRemainingTicks() - 1;
        if (remain <= 0) {
            this.discard();
            return;
        }
        setRemainingTicks(remain);
        Player player = level().getPlayerByUUID(boundPlayer);
        if (player == null || !player.isAlive()) {
            this.discard();
            return;
        }
        setPos(player.getX(), player.getY() + LIFT, player.getZ());
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(REMAINING_TICKS, 0);
        entityData.define(TOTAL_TICKS, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID(BOUND_PLAYER)) {
            boundPlayer = tag.getUUID(BOUND_PLAYER);
        }
        setRemainingTicks(tag.getInt(REMAIN_TICKS));
        setTotalTicks(tag.getInt(TOTAL));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (boundPlayer != null) {
            tag.putUUID(BOUND_PLAYER, boundPlayer);
        }
        tag.putInt(REMAIN_TICKS, getRemainingTicks());
        tag.putInt(TOTAL, getTotalTicks());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
