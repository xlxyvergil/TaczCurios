package com.xlxyvergil.tcc.capability;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.*;
import java.util.function.Supplier;

/**
 * 饰品适应效果 Capability：每个 LivingEntity 持有一份，由饰品 register/unregister 控制，
 * 死亡自动重置、超时无伤自动清空。
 */
public class CurioAdaptationCapability {

    // 适应实例

    /**
     * 单个适应效果实例，每个饰品注册一个，互不干扰。
     */
    public static class AdaptInstance {
        final int maxSlots;
        final double adaptFactor;
        final int decayTicks;          // 无伤害多少 tick 后重置
        final int maxAdaptCount;       // 同类型适应最大叠加次数
        long lastHitTick = -1;        // 上次受伤的游戏刻 (-1 表示未初始化)

        final ArrayList<String> memory = new ArrayList<>();
        final HashMap<String, Integer> counts = new HashMap<>();

        AdaptInstance(int maxSlots, double adaptFactor, int decaySeconds) {
            this(maxSlots, adaptFactor, decaySeconds,
                TaczCuriosConfig.COMMON.adaptationMaxCount.get());
        }

        AdaptInstance(int maxSlots, double adaptFactor, int decaySeconds, int maxAdaptCount) {
            this.maxSlots = maxSlots;
            this.adaptFactor = adaptFactor;
            this.decayTicks = decaySeconds * 20;
            this.maxAdaptCount = maxAdaptCount;
        }

        /** 返回是否因超时而被清空。 */
        boolean checkDecay(long currentTick) {
            if (lastHitTick < 0) return false;
            if (currentTick - lastHitTick > decayTicks) {
                memory.clear();
                counts.clear();
                lastHitTick = -1;
                return true;
            }
            return false;
        }

        /**
         * 处理一次伤害，先做衰减检查再执行适应逻辑。
         */
        void process(String msgId, float[] amountRef, long tick) {
            checkDecay(tick);

            lastHitTick = tick;

            if (memory.contains(msgId)) {
                // 命中已记忆类型 → 移到队首 + 累加计数（上限 maxAdaptCount） + 减免
                memory.remove(msgId);
                memory.add(0, msgId);
                int rawCount = counts.getOrDefault(msgId, 0) + 1;
                int count = Math.min(rawCount, maxAdaptCount);
                counts.put(msgId, count);
                // adaptFactor 为每层减伤比例，如 0.2 = 减免 20%
                double factor = Math.pow(1.0 - adaptFactor, count - 1);
                amountRef[0] *= (float) factor;
            } else {
                // 新类型 → 插入队首 + 初始化 + 淘汰最旧
                memory.add(0, msgId);
                counts.put(msgId, 1);
                if (memory.size() > maxSlots) {
                    String old = memory.remove(memory.size() - 1);
                    counts.remove(old);
                }
            }
        }

        // NBT 序列化

        CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("maxSlots", maxSlots);
            tag.putDouble("adaptFactor", adaptFactor);
            tag.putInt("decayTicks", decayTicks);
            tag.putInt("maxAdaptCount", maxAdaptCount);
            tag.putLong("lastHitTick", lastHitTick);
            if (!memory.isEmpty()) {
                ListTag memTag = new ListTag();
                for (String s : memory) memTag.add(StringTag.valueOf(s));
                tag.put("memory", memTag);
            }
            if (!counts.isEmpty()) {
                CompoundTag countTag = new CompoundTag();
                counts.forEach((k, v) -> countTag.putInt(k, v));
                tag.put("counts", countTag);
            }
            return tag;
        }

        static AdaptInstance deserialize(CompoundTag tag) {
            AdaptInstance inst = new AdaptInstance(
                tag.getInt("maxSlots"),
                tag.getDouble("adaptFactor"),
                tag.getInt("decayTicks") / 20,
                tag.getInt("maxAdaptCount")
            );
            inst.lastHitTick = tag.getLong("lastHitTick");
            if (tag.contains("memory")) {
                ListTag memTag = tag.getList("memory", Tag.TAG_STRING);
                for (int i = 0; i < memTag.size(); i++)
                    inst.memory.add(memTag.getString(i));
            }
            if (tag.contains("counts")) {
                CompoundTag countTag = tag.getCompound("counts");
                for (String key : countTag.getAllKeys())
                    inst.counts.put(key, countTag.getInt(key));
            }
            return inst;
        }
    }

    // Handler

    /**
     * 每个实体的适应数据容器，饰品通过 register/unregister 管理实例。
     */
    public static class Handler implements INBTSerializable<CompoundTag> {
        final LivingEntity owner;
        final LinkedHashMap<String, AdaptInstance> instances = new LinkedHashMap<>();

        Handler(LivingEntity owner) {
            this.owner = owner;
        }

        /** 注册一个适应效果（饰品 equip 时调用） */
        public void register(String id, int maxSlots, double adaptFactor, int decaySeconds) {
            instances.put(id, new AdaptInstance(maxSlots, adaptFactor, decaySeconds));
        }

        /** 注销一个适应效果（饰品 unequip 时调用） */
        public void unregister(String id) {
            instances.remove(id);
        }

        /** 清空所有适应数据（死亡/超时时调用） */
        public void clear() {
            instances.clear();
        }

        public boolean hasAny() {
            return !instances.isEmpty();
        }

        public void processAll(String msgId, float[] amountRef) {
            long tick = owner.level().getGameTime();
            for (AdaptInstance inst : instances.values()) {
                inst.process(msgId, amountRef, tick);
            }
        }

        // NBT

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag tag = new CompoundTag();
            if (!instances.isEmpty()) {
                CompoundTag map = new CompoundTag();
                instances.forEach((k, v) -> map.put(k, v.serialize()));
                tag.put("instances", map);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
            instances.clear();
            if (tag.contains("instances")) {
                CompoundTag map = tag.getCompound("instances");
                for (String key : map.getAllKeys()) {
                    instances.put(key, AdaptInstance.deserialize(map.getCompound(key)));
                }
            }
        }
    }

    // Data Attachment 注册

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TaczCurios.MODID);

    /** 饰品适应数据，绑定在 LivingEntity 上；死亡不继承（还原为初始状态）。 */
    public static final Supplier<AttachmentType<Handler>> ADAPTATION = ATTACHMENT_TYPES.register("adaptation",
        () -> AttachmentType.serializable(holder -> new Handler((LivingEntity) holder)).build());

    /** 取实体的适应数据，不存在时会自动创建默认值。 */
    public static Handler of(LivingEntity entity) {
        return entity.getData(ADAPTATION);
    }
}
