package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.event.RavenKeyAimHandler;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SevenThundersThunderSeen extends BoundCurioItem {
    private static final UUID HEADSHOT_MULTIPLIER_UUID = UUID.fromString("de0a7b0e-ec6f-45e5-8e3a-7f2d8f159f15");
    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("e6e6a5a6-5b3b-4d79-8dbd-9b9c31a6f0f4");
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("0f7f3eaa-8db2-4f8c-9f51-f06c9c0b0f17");

    public SevenThundersThunderSeen(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(HEADSHOT_MULTIPLIER_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(CRIT_CHANCE_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(CRIT_DAMAGE_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
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
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean isEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof SevenThundersThunderSeen).isEmpty();
    }

    @SubscribeEvent
    public static void onGunHurt(EntityHurtByGunEvent.Post event) {
        handleHit(event.getAttacker(), event.getHurtEntity(), event.isHeadShot());
    }

    /** 致死命中只发 EntityKillByGunEvent（与 Post 互斥），同样需要触发范围溅射。 */
    @SubscribeEvent
    public static void onGunKill(EntityKillByGunEvent event) {
        handleHit(event.getAttacker(), event.getKilledEntity(), event.isHeadShot());
    }

    private static void handleHit(LivingEntity attacker, Entity hurtEntity, boolean headShot) {
        // 爆头判定直接取事件信息，不再依赖 Pre 写入的子弹 NBT
        if (!headShot) return;
        if (attacker == null || !isEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingSniper(attacker)) return;
        if (!(hurtEntity instanceof LivingEntity target)) return;
        if (attacker.getRandom().nextFloat() >= TaczCuriosConfig.COMMON.sevenThundersThunderSeenProcChance.get().floatValue()) return;

        float extra = (float) (Math.round(target.getMaxHealth() * TaczCuriosConfig.COMMON.sevenThundersThunderSeenExtraHpDamage.get() * 10000.0) / 10000.0);
        if (extra > 0) {
            // 附加魔法伤害改为以受击者为中心的范围溅射，并叠加开镜蓄力增幅
            double amp = RavenKeyAimHandler.getAmp(attacker);
            float splash = (float) (extra * (1.0 + amp));
            RavenKeyAimHandler.applySplashMagic(attacker, target, splash,
                TaczCuriosConfig.COMMON.sevenThundersThunderSeenSplashRadius.get());
        }
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("sniper");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        String sttsHeadshotStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenHeadshotMultiplier.get() * 100);
        String sttsCritChanceStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritChance.get() * 100);
        String sttsCritDamageStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritDamage.get() * 100);
        String sttsProcStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenProcChance.get() * 100);
        String sttsExtraHpStr = String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenExtraHpDamage.get() * 100);
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.sevenThundersThunderSeenHeadshotMultiplier.get() * 100, "%.0f%%", Component.translatable(AttributeHelper.HEADSHOT_MULTIPLIER.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritChance.get() * 100, "%.0f%%", Component.translatable(AttributeHelper.CRIT_CHANCE.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(formatModifierTooltip(TaczCuriosConfig.COMMON.sevenThundersThunderSeenCritDamage.get() * 100, "%.0f%%", Component.translatable(AttributeHelper.CRIT_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.tcc.seven_thunders_thunder_seen.special",
                sttsHeadshotStr, sttsCritChanceStr, sttsCritDamageStr, sttsProcStr, sttsExtraHpStr)
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("tcc.tooltip.raven_splash",
                String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenSplashRadius.get()))
            .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("tcc.tooltip.raven_aim_amp",
                String.format("%.1f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenAimTimeToMax.get()),
                String.format("%.0f", TaczCuriosConfig.COMMON.sevenThundersThunderSeenAimMaxAmp.get() * 100))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

        appendBoundPlayer(stack, tooltip);
    }
}
