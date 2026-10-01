package com.xlxyvergil.tcc.capability;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * 虚数/崩解伤害的死亡进度账本。
 * 每个 LivingEntity attach 一份。一旦进入账本模式，账本就是该实体血量的权威镜像：
 * 起点取首次命中时的当前血量，之后由「本次攻击实收伤害（护甲/抗性/吸收减免后）」与
 * 虚数/崩解特殊伤害共同扣减，并由调用方强行写回血量字段（绕过限伤/锁血）。
 */
public final class ImaginaryHealthLedgerCapability {

    private static final String TAG_LEDGER = "ledger";
    private static final String TAG_INITIAL = "initial";
    private static final String TAG_INITIALIZED = "initialized";

    private ImaginaryHealthLedgerCapability() {}

    public static class Handler implements INBTSerializable<CompoundTag> {
        private float ledger;
        /** 起点：首次命中时的血量上限（maxHealth），用于计算剩余百分比。 */
        private float initial;
        private boolean initialized;
        /** 最近一次记账的伤害来源（仅内存态，不序列化），账本归零时用于 die() 归属。 */
        private transient DamageSource lastSource;

        public boolean isInitialized() {
            return initialized;
        }

        public float getLedger() {
            return ledger;
        }

        public float getInitial() {
            return initial;
        }

        public DamageSource getLastSource() {
            return lastSource;
        }

        public void setLastSource(DamageSource source) {
            this.lastSource = source;
        }

        /** 当前账本 / 起点，返回 0.0~1.0 的比例（起点非正时为 0）。即剩余进度：满值 1.0 → 归零 0.0。 */
        public float getProgress() {
            return initial > 0.0F ? Math.max(0.0F, ledger / initial) : 0.0F;
        }

        /**
         * 进入账本模式：initial 为血量上限（用于进度百分比），ledger 为当前血量（权威镜像起点）。
         * 只在首次命中时调用一次。
         */
        public void init(float initial, float ledger) {
            this.initial = initial;
            this.ledger = ledger;
            this.initialized = true;
        }

        /** 扣减账本，返回扣除后的剩余值（可能 &lt;= 0）。 */
        public float reduce(float amount) {
            this.ledger -= amount;
            return this.ledger;
        }

        /** 回血恢复：账本增加 amount，但不超过起点（initial），即侵蚀进度可被治疗抹回。 */
        public void recover(float amount) {
            if (!initialized || amount <= 0.0F) return;
            this.ledger = Math.min(this.initial, this.ledger + amount);
        }

        /** 归 0 分派后复位，避免同一个体重复触发死亡分派。 */
        public void reset() {
            this.ledger = 0.0F;
            this.initial = 0.0F;
            this.initialized = false;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag tag = new CompoundTag();
            tag.putFloat(TAG_LEDGER, ledger);
            tag.putFloat(TAG_INITIAL, initial);
            tag.putBoolean(TAG_INITIALIZED, initialized);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
            ledger = tag.getFloat(TAG_LEDGER);
            initial = tag.getFloat(TAG_INITIAL);
            initialized = tag.getBoolean(TAG_INITIALIZED);
        }
    }

    // Data Attachment 注册

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TaczCurios.MODID);

    /** 虚数死亡进度账本，绑定在 LivingEntity 上。 */
    public static final Supplier<AttachmentType<Handler>> LEDGER = ATTACHMENT_TYPES.register(
        "imaginary_health_ledger", () -> AttachmentType.serializable(Handler::new).build());

    /** 取得账本（不存在时自动创建默认值）。 */
    public static Handler get(LivingEntity entity) {
        return entity.getData(LEDGER);
    }
}
