package com.xlxyvergil.tcc.items;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.TacDamageHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 天火劫灭·无烬终焉 - 裂隙级，天火劫灭的进化版本
 * 取消满血检测和扣血限制，属性效果完全常驻，伤害100%转为虚数
 */
@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeavenFireApocalypseEndless extends BaseCurioItem {
    
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("8c87e97e-cc63-415f-b92d-6ac2e521b219");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("3de85a73-816c-49c0-bc43-4c7dec18c951");
    
    private static final String GUN_DAMAGE_NAME = "tcc.heaven_fire_apocalypse_endless.gun_damage";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.heaven_fire_apocalypse_endless.explosion_damage";
    
    public HeavenFireApocalypseEndless(Properties properties) {
        super(properties.stacksTo(1).fireResistant());
    }
    
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        LivingEntity entity = (LivingEntity) slotContext.entity();
        applyEffects(entity);
    }
    
    @Override
    protected boolean isBoundItem() {
        return true;
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (!GunTypeChecker.isHoldingConfiguredGunTypes(livingEntity, TaczCuriosConfig.COMMON.endlessGunTypes.get())) return;
        
        // 使用可配置的数值
        double damageBoost = TaczCuriosConfig.COMMON.endlessDamageBoost.get();
        double explosionDamageBoost = TaczCuriosConfig.COMMON.endlessExplosionDamage.get();
        
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageBoost, GUN_DAMAGE_UUID, GUN_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        // 无烬终焉：移除爆炸范围和爆炸启用
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, explosionDamageBoost, EXPLOSION_DAMAGE_UUID, EXPLOSION_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GUN_DAMAGE_UUID);
        // 无烬终焉：不移除爆炸范围和爆炸启用（因为未添加）
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, EXPLOSION_DAMAGE_UUID);
    }
    
    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        tooltip.add(Component.literal(""));
        
        // 限定枪械类型
        String gunTypes = GunTypeChecker.formatGunTypes(TaczCuriosConfig.COMMON.endlessGunTypes.get());
        tooltip.add(Component.translatable("tcc.tooltip.restricted_gun_types", gunTypes));
        
        // 使用无烬终焉自身的配置值（damageBoost和explosionDamage），无爆炸范围
        double damageBoost = TaczCuriosConfig.COMMON.endlessDamageBoost.get() * 100;
        double explosionDamageBoost = TaczCuriosConfig.COMMON.endlessExplosionDamage.get() * 100;
        double nearbyPlayerRadius = TaczCuriosConfig.COMMON.endlessNearbyPlayerRadius.get();
        double perLevelBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDamageBoost.get() * 100;
        int potionAmplifier = TaczCuriosConfig.COMMON.endlessNearbyPlayerPotionAmplifier.get();
        int totalNearbyPlayerDamageBoost = (int)(perLevelBoost * (potionAmplifier + 1));
        int nearbyPlayerDuration = TaczCuriosConfig.COMMON.endlessNearbyPlayerDuration.get();
        
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse_endless.effect", 
                String.format("%+.0f", damageBoost), 
                "0", 
                String.format("%+.0f", explosionDamageBoost),
                String.format("%+.0f", nearbyPlayerRadius), 
                String.format("%+d", totalNearbyPlayerDamageBoost),
                String.format("%d", nearbyPlayerDuration)));
        
        // 伤害转换信息由客户端 TaczCuriosClientTooltip 通过 ItemTooltipEvent 动态追加
        
        // 虚数侵染上限 + 虚数崩解
        int infectionMax = TaczCuriosConfig.COMMON.endlessImaginaryInfectionMaxLevel.get();
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse_endless.inflection_max",
                String.format("%d", infectionMax)));
        double collapsePct = TaczCuriosConfig.COMMON.collapsePercentPerLevel.get() * 100;
        double debuffPct = TaczCuriosConfig.COMMON.collapsePercentPerDebuff.get() * 100;
        int maxDebuff = TaczCuriosConfig.COMMON.collapseMaxDebuffCount.get();
        double maxCollapsePct = collapsePct * infectionMax * (1 + maxDebuff * debuffPct / 100);
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse_endless.collapse_info",
                String.format("%.1f", collapsePct), String.format("%.0f", debuffPct), String.valueOf(maxDebuff),
                String.format("%.1f", maxCollapsePct))
            .withStyle(ChatFormatting.DARK_PURPLE));
        
        tooltip.add(Component.literal(""));
        
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rift"));
        
        // 添加获取方式
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse_endless.how_to_obtain")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
    
    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasHeavenFireApocalypseEndlessEquipped(attacker)) {
            return;
        }
        
        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, TaczCuriosConfig.COMMON.endlessGunTypes.get())) return;

        // 100%转换为虚数伤害
        event.setDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
        event.setDamageSource(GunDamageSourcePart.ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
    }
    
    @SubscribeEvent
    public static void onGunHurt(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = TacDamageHelper.getAttacker(event);
        if (attacker == null) {
            return;
        }
        
        if (!hasHeavenFireApocalypseEndlessEquipped(attacker)) {
            return;
        }
        
        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, TaczCuriosConfig.COMMON.endlessGunTypes.get())) return;

        // 无烬终焉：不扣血，直接施加BUFF给周围玩家
        double nearbyPlayerRadius = TaczCuriosConfig.COMMON.endlessNearbyPlayerRadius.get();
        List<Player> nearbyPlayers = attacker.level().getEntitiesOfClass(Player.class, attacker.getBoundingBox().inflate(nearbyPlayerRadius));
        int nearbyPlayerDuration = TaczCuriosConfig.COMMON.endlessNearbyPlayerDuration.get();

        for (Player nearbyPlayer : nearbyPlayers) {
            int potionAmplifier = TaczCuriosConfig.COMMON.endlessNearbyPlayerPotionAmplifier.get();
            nearbyPlayer.addEffect(new MobEffectInstance(
                TccMobEffects.HEAVEN_FIRE_APOCALYPSE_BUFF.get(),
                nearbyPlayerDuration * 20,
                potionAmplifier,
                false, false, true));
        }
    }
    
    public static boolean hasHeavenFireApocalypseEndlessEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof HeavenFireApocalypseEndless).isEmpty();
    }
    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
