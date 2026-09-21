package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.ImaginaryConversionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
@EventBusSubscriber(modid = TaczCurios.MODID)
public class SevenThundersThunderSeen extends BoundCurioItem {
    private static final ResourceLocation HEADSHOT_MULTIPLIER_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "seven_thunders_thunder_seen_de0a7b0e_9f15");
    private static final ResourceLocation CRIT_CHANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "seven_thunders_thunder_seen_e6e6a5a6_f0f4");
    private static final ResourceLocation CRIT_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "seven_thunders_thunder_seen_0f7f3eaa_0f17");

    private static final String PROC_KEY = "tcc_seven_thunders_thunder_seen_proc";
    private static final String PROC_USED_KEY = "tcc_seven_thunders_thunder_seen_proc_used";

    public SevenThundersThunderSeen(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(HEADSHOT_MULTIPLIER_ID, stack.getItem());
        AttributeHelper.registerSourceItem(CRIT_CHANCE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(CRIT_DAMAGE_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER,
                TaczCuriosConfig.COMMON.sevenThundersThunderSeenHeadshotMultiplier.get(), HEADSHOT_MULTIPLIER_ID, AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritChance.get(), CRIT_CHANCE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritDamage.get(), CRIT_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER, HEADSHOT_MULTIPLIER_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_ID);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
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

        ImaginaryConversionHelper.convertToImaginary(event);

        if (event.isHeadShot()
            && attacker.getRandom().nextFloat() < TaczCuriosConfig.COMMON.sevenThundersThunderSeenProcChance.get().floatValue()
            && event.getBullet() != null) {
            event.getBullet().getPersistentData().putBoolean(PROC_KEY, true);
            event.getBullet().getPersistentData().putBoolean(PROC_USED_KEY, false);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
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

        float extra = (float) ((float) Math.round(target.getMaxHealth() * TaczCuriosConfig.COMMON.sevenThundersThunderSeenExtraHpDamage.get() * 10000.0) / 10000.0);
        if (extra > 0) {
            target.setHealth(Math.max(0, target.getHealth() - extra));
        }
        data.putBoolean(PROC_USED_KEY, true);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("sniper");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        String sttsHeadshotStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenHeadshotMultiplier.get() * 100);
        String sttsCritChanceStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritChance.get() * 100);
        String sttsCritDamageStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritDamage.get() * 100);
        String sttsProcStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenProcChance.get() * 100);
        String sttsExtraHpStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenExtraHpDamage.get() * 100);
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.sevenThundersThunderSeenHeadshotMultiplier.get() * 100, "%.0f%%", Component.translatable(AttributeHelper.HEADSHOT_MULTIPLIER.value().getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritChance.get() * 100, "%.0f%%", Component.translatable(AttributeHelper.CRIT_CHANCE.value().getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritDamage.get() * 100, "%.0f%%", Component.translatable(AttributeHelper.CRIT_DAMAGE.value().getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.tcc.seven_thunders_thunder_seen.special",
                sttsHeadshotStr, sttsCritChanceStr, sttsCritDamageStr, sttsProcStr, sttsExtraHpStr)
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

        appendBoundPlayer(stack, tooltip);
    }
}
