package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.ImaginaryConversionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
@EventBusSubscriber(modid = TaczCurios.MODID)
public class JudgementKey extends BoundCurioItem {
    private static final ResourceLocation CRIT_CHANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "judgement_key_f13a5b08_3bdf");
    private static final ResourceLocation CRIT_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "judgement_key_2a1e47bd_b47c");

    private static final String PROC_KEY = "tcc_judgement_key_set_proc";
    private static final String PROC_DAMAGE_KEY = "tcc_judgement_key_set_damage";
    private static final String PROC_DAMAGE_AFTER_HEADSHOT_KEY = "tcc_judgement_key_set_damage_after_headshot";

    public JudgementKey(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(CRIT_CHANCE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(CRIT_DAMAGE_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                TaczCuriosConfig.COMMON.judgementKeyCritChance.get(), CRIT_CHANCE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                TaczCuriosConfig.COMMON.judgementKeyCritDamage.get(), CRIT_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_ID);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    public static boolean hasEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof JudgementKey).isEmpty();
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingSniper(attacker)) return;

        ImaginaryConversionHelper.convertToImaginary(event);

        if (!event.isHeadShot()) return;

        if (event.getBullet() != null) {
            event.getBullet().getPersistentData().putBoolean(PROC_KEY, true);
            float damage = (float) GunTypeChecker.getMainHandGunDamage(attacker, GunTypeChecker.SNIPER_GUN_TYPES);
            event.getBullet().getPersistentData().putFloat(PROC_DAMAGE_KEY, damage);
            float damageAfterHeadshot = damage * event.getHeadshotMultiplier();
            event.getBullet().getPersistentData().putFloat(PROC_DAMAGE_AFTER_HEADSHOT_KEY, damageAfterHeadshot);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingSniper(attacker)) return;

        Entity bullet = event.getBullet();
        if (bullet == null) return;

        var data = bullet.getPersistentData();
        if (!data.getBoolean(PROC_KEY)) return;

        float damageAfterHeadshot = data.getFloat(PROC_DAMAGE_AFTER_HEADSHOT_KEY);

        Entity hurtEntity = event.getHurtEntity();
        if (!(hurtEntity instanceof LivingEntity targetLiving)) return;
        if (targetLiving.isDeadOrDying()) return;

        double setHealthProc = TaczCuriosConfig.COMMON.judgementProcChance.get();
        if (attacker.getRandom().nextDouble() < setHealthProc && damageAfterHeadshot > 0) {
            double directPercent = TaczCuriosConfig.COMMON.judgementDirectDamagePercent.get();
            float directDamage = (float) (damageAfterHeadshot * directPercent);
            TccAttributeEvents.applyImaginaryDamage(targetLiving, TccDamageSources.imaginaryDamage(targetLiving.level(), attacker), directDamage);
        }

        double collapseProc = TaczCuriosConfig.COMMON.judgementCollapseProcChance.get();
        if (attacker.getRandom().nextDouble() < collapseProc) {
            TccAttributeEvents.applyCollapse(targetLiving, attacker);
        }
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

        double critChance = TaczCuriosConfig.COMMON.judgementKeyCritChance.get() * 100;
        double critDamage = TaczCuriosConfig.COMMON.judgementKeyCritDamage.get() * 100;

        tooltip.add(formatModifierTooltip(critChance, "%.0f%%", Component.translatable(AttributeHelper.CRIT_CHANCE.value().getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(formatModifierTooltip(critDamage, "%.0f%%", Component.translatable(AttributeHelper.CRIT_DAMAGE.value().getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.gun_to_imaginary")
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.judgement_key.special")
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.literal(""));

        appendAlwaysImaginaryCollapse(tooltip);
        appendBoundPlayer(stack, tooltip);
    }
}
