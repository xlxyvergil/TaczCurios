package com.xlxyvergil.tcc.affix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

/**
 * TCC 枪械触发型药水效果词缀。
 * 完全基于 Apotheosis 原生 API
 * 词缀在 {@link EntityHurtByGunEvent.Post} 中按 probability rate 触发。
 */
public class GunMobEffectAffix extends Affix {

    public static final Codec<GunMobEffectAffix> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(
            ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("mob_effect").forGetter(a -> a.effect),
            LootRarity.mapCodec(GunEffectData.CODEC).fieldOf("values").forGetter(a -> a.values),
            PlaceboCodecs.nullableField(Codec.INT, "cooldown", 0).forGetter(a -> a.cooldown),
            LootCategory.SET_CODEC.fieldOf("types").forGetter(a -> a.types),
            PlaceboCodecs.nullableField(Codec.BOOL, "stack_on_reapply", false).forGetter(a -> a.stackOnReapply))
        .apply(inst, GunMobEffectAffix::new));

    protected final MobEffect effect;
    protected final Map<LootRarity, GunEffectData> values;
    protected final int cooldown;
    protected final Set<LootCategory> types;
    protected final boolean stackOnReapply;

    public GunMobEffectAffix(MobEffect effect, Map<LootRarity, GunEffectData> values, int cooldown,
                             Set<LootCategory> types, boolean stackOnReapply) {
        super(AffixType.ABILITY);
        this.effect = effect;
        this.values = values;
        this.cooldown = cooldown;
        this.types = types;
        this.stackOnReapply = stackOnReapply;
    }

    /**
     * 枪械射击命中时触发药水效果。
     *
     * @param stack   饰品 ItemStack
     * @param instance 词缀实例（含 rarity & level）
     * @param event   TACZ Post 事件
     */
    public void onGunshotPost(ItemStack stack, AffixInstance instance, EntityHurtByGunEvent.Post event) {
        LootRarity rarity = instance.rarity().get();
        float level = instance.level();
        Entity hurtEntity = event.getHurtEntity();
        if (!(hurtEntity instanceof LivingEntity target)) return;
        if (target.level().isClientSide()) return;

        GunEffectData data = this.values.get(rarity);
        if (data == null) return;

        // 概率检定
        float rate = data.rate().get(level);
        if (rate < 1.0f && target.getRandom().nextFloat() >= rate) return;

        int cd = data.cooldown() >= 0 ? data.cooldown() : this.cooldown;
        if (cd > 0 && isOnCooldown(this.getId(), cd, target)) return;

        var existing = target.getEffect(this.effect);
        if (this.stackOnReapply && existing != null) {
            int newDuration = (int) Math.max(existing.getDuration(), data.duration().get(level));
            int newAmp = existing.getAmplifier() + 1 + data.amplifier().getInt(level);
            target.addEffect(new MobEffectInstance(this.effect, newDuration, newAmp));
        } else {
            target.addEffect(data.build(this.effect, level));
        }
        startCooldown(this.getId(), target);
    }

    @Override
    public MutableComponent getDescription(ItemStack stack, LootRarity rarity, float level) {
        GunEffectData data = this.values.get(rarity);
        if (data == null) return Component.empty();
        MobEffectInstance inst = data.build(this.effect, level);
        MutableComponent comp = Component.translatable("affix.tcc:gun_mob_effect.desc",
            toComponent(inst),
            Component.literal((int) (data.rate().get(level) * 100) + "%").withStyle(ChatFormatting.GOLD));
        if (this.stackOnReapply) {
            comp = comp.append(" ").append(Component.translatable("affix.apotheosis.stacking"));
        }
        return comp;
    }

    @Override
    public Component getAugmentingText(ItemStack stack, LootRarity rarity, float level) {
        GunEffectData data = this.values.get(rarity);
        if (data == null) return Component.empty();
        MobEffectInstance min = data.build(this.effect, 0);
        MobEffectInstance max = data.build(this.effect, 1);
        MutableComponent comp = Component.translatable("affix.tcc:gun_mob_effect.desc",
            toComponent(max),
            Component.literal(
                (int) (data.rate().get(0) * 100) + "%-" + (int) (data.rate().get(1) * 100) + "%")
                .withStyle(ChatFormatting.GOLD));
        return comp;
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory cat, LootRarity rarity) {
        return (this.types.isEmpty() || this.types.contains(cat)) && this.values.containsKey(rarity);
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }

    public static Component toComponent(MobEffectInstance inst) {
        MutableComponent comp = Component.translatable(inst.getDescriptionId());
        MobEffect effect = inst.getEffect();
        if (inst.getAmplifier() > 0) {
            comp = Component.translatable("potion.withAmplifier", comp,
                Component.translatable("potion.potency." + inst.getAmplifier()));
        }
        if (!effect.isInstantenous() && inst.getDuration() > 20) {
            comp = Component.translatable("potion.withDuration", comp,
                MobEffectUtil.formatDuration(inst, 1));
        }
        return comp.withStyle(effect.getCategory().getTooltipFormatting());
    }

    /**
     * 稀有度对应效果数据：duration, amplifier, rate（触发概率）
     */
    public record GunEffectData(StepFunction duration, StepFunction amplifier, StepFunction rate, int cooldown) {

        private static final Codec<GunEffectData> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                StepFunction.CODEC.fieldOf("duration").forGetter(GunEffectData::duration),
                StepFunction.CODEC.fieldOf("amplifier").forGetter(GunEffectData::amplifier),
                StepFunction.CODEC.fieldOf("rate").forGetter(GunEffectData::rate),
                PlaceboCodecs.nullableField(Codec.INT, "cooldown", -1).forGetter(GunEffectData::cooldown))
            .apply(inst, GunEffectData::new));

        public MobEffectInstance build(MobEffect effect, float level) {
            return new MobEffectInstance(effect, this.duration.getInt(level), this.amplifier.getInt(level));
        }
    }
}
