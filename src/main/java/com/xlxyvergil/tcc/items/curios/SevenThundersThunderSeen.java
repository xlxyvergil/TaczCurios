package com.xlxyvergil.tcc.items.curios;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SevenThundersThunderSeen extends BaseCurioItem {

    private static final UUID HEADSHOT_MULTIPLIER_UUID = UUID.fromString("de0a7b0e-ec6f-45e5-8e3a-7f2d8f159f15");
    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("e6e6a5a6-5b3b-4d79-8dbd-9b9c31a6f0f4");
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("0f7f3eaa-8db2-4f8c-9f51-f06c9c0b0f17");

    private static final String PROC_KEY = "tcc_seven_thunders_thunder_seen_proc";
    private static final String PROC_USED_KEY = "tcc_seven_thunders_thunder_seen_proc_used";

    public SevenThundersThunderSeen(Properties properties) {
        super(properties);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        if (slotContext.entity() instanceof Player player) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.getBoolean("IsBound")) {
                tag.putBoolean("IsBound", true);
                tag.putString("BoundPlayer", player.getStringUUID());
                tag.putString("BoundPlayerName", player.getGameProfile().getName());
            }
        }
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingSniper(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER,
                TaczCuriosConfig.COMMON.sevenThundersThunderSeenHeadshotMultiplier.get(), HEADSHOT_MULTIPLIER_UUID,
                "tcc.seven_thunders_thunder_seen.headshot_multiplier", AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritChance.get(), CRIT_CHANCE_UUID,
                "tcc.seven_thunders_thunder_seen.crit_chance", AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritDamage.get(), CRIT_DAMAGE_UUID,
                "tcc.seven_thunders_thunder_seen.crit_damage", AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER, HEADSHOT_MULTIPLIER_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerUUID = tag.getString("BoundPlayer");
            if (slotContext.entity() instanceof Player player) {
                return player.getStringUUID().equals(boundPlayerUUID);
            }
            return false;
        }
        return super.canEquip(slotContext, stack);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }

    public static boolean hasEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof SevenThundersThunderSeen).isEmpty();
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingSniper(attacker)) return;

        // 伤害完全转为虚数伤害（狙击枪命中即转换，不限制爆头）
        event.setDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
        event.setDamageSource(GunDamageSourcePart.ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));

        // 爆头时概率触发额外虚数伤害
        if (event.isHeadShot()
            && attacker.getRandom().nextFloat() < TaczCuriosConfig.COMMON.sevenThundersThunderSeenProcChance.get().floatValue()
            && event.getBullet() != null) {
            event.getBullet().getPersistentData().putBoolean(PROC_KEY, true);
            event.getBullet().getPersistentData().putBoolean(PROC_USED_KEY, false);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide || target.isDeadOrDying()) return;

        DamageSource source = event.getSource();
        if (!source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) return;
        if (!(source.getEntity() instanceof LivingEntity attacker)) return;
        if (!hasEquipped(attacker)) return;
        if (!GunTypeChecker.isHoldingSniper(attacker)) return;

        Entity bullet = source.getDirectEntity();
        if (bullet == null) return;

        var data = bullet.getPersistentData();
        if (!data.getBoolean(PROC_KEY) || data.getBoolean(PROC_USED_KEY)) return;

        float extra = (float) ((float) Math.round(target.getMaxHealth() * TaczCuriosConfig.COMMON.sevenThundersThunderSeenExtraHpDamage.get() * 100.0) / 100.0);
        if (extra > 0) {
            target.setHealth(Math.max(0, target.getHealth() - extra));
        }
        data.putBoolean(PROC_USED_KEY, true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        String gunTypes = GunTypeChecker.formatGunTypes(List.of("sniper"));
        tooltip.add(Component.translatable("tcc.tooltip.restricted_gun_types", gunTypes));

        String sttsHeadshotStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenHeadshotMultiplier.get() * 100);
        String sttsCritChanceStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritChance.get() * 100);
        String sttsCritDamageStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritDamage.get() * 100);
        String sttsProcStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenProcChance.get() * 100);
        String sttsExtraHpStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenExtraHpDamage.get() * 100);
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.sevenThundersThunderSeenHeadshotMultiplier.get() * 100, "%.0f", Component.translatable(AttributeHelper.HEADSHOT_MULTIPLIER.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritChance.get() * 100, "%.0f", Component.translatable(AttributeHelper.CRIT_CHANCE.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritDamage.get() * 100, "%.0f", Component.translatable(AttributeHelper.CRIT_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.tcc.seven_thunders_thunder_seen.special",
                sttsHeadshotStr, sttsCritChanceStr, sttsCritDamageStr, sttsProcStr, sttsExtraHpStr)
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("tcc.tooltip.rarity.rare"));

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.bound", boundPlayerName)
                .withStyle(ChatFormatting.RED));
        }
    }
}
