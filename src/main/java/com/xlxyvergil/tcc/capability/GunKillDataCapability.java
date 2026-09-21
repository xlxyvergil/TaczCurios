package com.xlxyvergil.tcc.capability;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * 在枪械伤害事件与死亡事件之间传递击杀信息；替代 getPersistentData() NBT 方案以兼容重写该方法返回空 NBT
 * 的模组（如 RevelationFix、Apollyon）。
 */
public final class GunKillDataCapability {

    private GunKillDataCapability() {}

    // 数据容器

    /**
     * 单个实体的枪杀数据，写入后保持一段时间供死亡事件读取。
     */
    public static class GunKillData {
        // 枪械伤害数据
        public String attacker = "";
        public String gunId = "";
        public long tick = -1;
        public String victim = "";

        // 爆头数据
        public String headshotAttacker = "";
        public long headshotTime = -1;
        public String headshotGunId = "";

        boolean hasGunData() {
            return !attacker.isEmpty() && tick >= 0;
        }

        void setGunData(String attackerUuid, String gunIdStr, long gameTime, String victimUuid) {
            this.attacker = attackerUuid;
            this.gunId = gunIdStr != null ? gunIdStr : "";
            this.tick = gameTime;
            this.victim = victimUuid;
        }

        void setHeadshotData(String attackerUuid, long gameTime, String gunIdStr) {
            this.headshotAttacker = attackerUuid;
            this.headshotTime = gameTime;
            this.headshotGunId = gunIdStr != null ? gunIdStr : "";
        }

        void clear() {
            attacker = "";
            gunId = "";
            tick = -1;
            victim = "";
            headshotAttacker = "";
            headshotTime = -1;
            headshotGunId = "";
        }

        CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putString("attacker", attacker);
            tag.putString("gunId", gunId);
            tag.putLong("tick", tick);
            tag.putString("victim", victim);
            tag.putString("headshotAttacker", headshotAttacker);
            tag.putLong("headshotTime", headshotTime);
            tag.putString("headshotGunId", headshotGunId);
            return tag;
        }

        static GunKillData deserialize(CompoundTag tag) {
            GunKillData data = new GunKillData();
            data.attacker = tag.getString("attacker");
            data.gunId = tag.getString("gunId");
            data.tick = tag.getLong("tick");
            data.victim = tag.getString("victim");
            data.headshotAttacker = tag.getString("headshotAttacker");
            data.headshotTime = tag.getLong("headshotTime");
            data.headshotGunId = tag.getString("headshotGunId");
            return data;
        }
    }

    // Handler

    public static class Handler implements INBTSerializable<CompoundTag> {
        private final GunKillData data = new GunKillData();

        public GunKillData data() { return data; }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) { return data.serialize(); }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
            GunKillData loaded = GunKillData.deserialize(tag);
            data.attacker = loaded.attacker;
            data.gunId = loaded.gunId;
            data.tick = loaded.tick;
            data.victim = loaded.victim;
            data.headshotAttacker = loaded.headshotAttacker;
            data.headshotTime = loaded.headshotTime;
            data.headshotGunId = loaded.headshotGunId;
        }
    }

    // Data Attachment 注册

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TaczCurios.MODID);

    public static final Supplier<AttachmentType<Handler>> GUN_KILL_DATA = ATTACHMENT_TYPES.register("gun_kill_data",
        () -> AttachmentType.serializable(Handler::new).build());

    // 便捷静态方法

    public static void setGunData(LivingEntity target, String attackerUuid, String gunIdStr,
                                   long gameTime, String victimUuid) {
        target.getData(GUN_KILL_DATA).data().setGunData(attackerUuid, gunIdStr, gameTime, victimUuid);
    }

    public static void setHeadshotData(LivingEntity target, String attackerUuid,
                                        long gameTime, String gunIdStr) {
        target.getData(GUN_KILL_DATA).data().setHeadshotData(attackerUuid, gameTime, gunIdStr);
    }

    @Nullable
    public static GunKillData getData(LivingEntity target) {
        GunKillData src = target.getData(GUN_KILL_DATA).data();
        if (src.hasGunData()) return src;
        return null;
    }

    public static void clearData(LivingEntity target) {
        target.getData(GUN_KILL_DATA).data().clear();
    }
}
