package com.xlxyvergil.tcc.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 虚数/崩解伤害的死亡进度账本。
 * 每个 LivingEntity attach 一份，起点为首次命中时的血量上限（maxHealth），之后只由虚数/崩解伤害累计扣减。
 * 不写 vanilla 血量字段，因此不受 setHealth 限伤/锁血影响；归零后由调用方收尾。
 */
public final class ImaginaryHealthLedgerCapability {

    public static final ResourceLocation ID = new ResourceLocation("tcc", "imaginary_health_ledger");

    private static final String TAG_LEDGER = "ledger";
    private static final String TAG_INITIAL = "initial";
    private static final String TAG_INITIALIZED = "initialized";

    private ImaginaryHealthLedgerCapability() {}

    public static class Handler {
        private float ledger;
        /** 起点：首次命中时的血量上限（maxHealth），用于计算剩余百分比。 */
        private float initial;
        private boolean initialized;

        public boolean isInitialized() {
            return initialized;
        }

        public float getLedger() {
            return ledger;
        }

        public float getInitial() {
            return initial;
        }

        /** 当前账本 / 起点，返回 0.0~1.0 的比例（起点非正时为 0）。 */
        public float getProgress() {
            return initial > 0.0F ? Math.max(0.0F, ledger / initial) : 0.0F;
        }

        /** 用血量上限（maxHealth）作为账本起点，只在首次命中时调用一次。 */
        public void init(float health) {
            this.ledger = health;
            this.initial = health;
            this.initialized = true;
        }

        /** 扣减账本，返回扣除后的剩余值（可能 &lt;= 0）。 */
        public float reduce(float amount) {
            this.ledger -= amount;
            return this.ledger;
        }

        /** 归 0 分派后复位，避免同一个体重复触发死亡分派。 */
        public void reset() {
            this.ledger = 0.0F;
            this.initial = 0.0F;
            this.initialized = false;
        }

        CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putFloat(TAG_LEDGER, ledger);
            tag.putFloat(TAG_INITIAL, initial);
            tag.putBoolean(TAG_INITIALIZED, initialized);
            return tag;
        }

        void deserializeNBT(CompoundTag tag) {
            ledger = tag.getFloat(TAG_LEDGER);
            initial = tag.getFloat(TAG_INITIAL);
            initialized = tag.getBoolean(TAG_INITIALIZED);
        }
    }

    public static final Capability<Handler> CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});

    public static class Provider implements ICapabilitySerializable<CompoundTag> {

        private final Handler handler = new Handler();
        private final LazyOptional<Handler> lazy = LazyOptional.of(() -> handler);

        @Override
        @NotNull
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == CAPABILITY ? lazy.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return handler.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            handler.deserializeNBT(nbt);
        }
    }

    /** 取得账本；未 attach（如客户端实体）时返回 null。 */
    @Nullable
    public static Handler get(LivingEntity entity) {
        return entity.getCapability(CAPABILITY).orElse(null);
    }
}
