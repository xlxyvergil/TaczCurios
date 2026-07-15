package com.xlxyvergil.tcc.items.curios;

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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.world.damagesource.DamageSource;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;



/**
 * 天火劫灭 - 踏上前来
 * 效果：玩家血量为100%，buff生效，提升1000%的bullet_gundamage，+10explosion_radius，提升1000%的explosion_damage，
 * 造成伤害后对玩家造成当前生命值100%的伤害（使用setHealth），同时对玩家周围的其他玩家提供15秒的100%bullet_gundamage加成（加算）。
 */
@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeavenFireApocalypse extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("8c87e97e-cc63-415f-b92d-6ac2e521b219");
    private static final UUID EXPLOSION_RADIUS_UUID = UUID.fromString("79f78f03-e9ba-4567-9ba9-75f729f6c3e8");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("3de85a73-816c-49c0-bc43-4c7dec18c951");
    
    private static final String GUN_DAMAGE_NAME = "tcc.heaven_fire_apocalypse.gun_damage";
    private static final String EXPLOSION_RADIUS_NAME = "tcc.heaven_fire_apocalypse.explosion_radius";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.heaven_fire_apocalypse.explosion_damage";
    
    
    public HeavenFireApocalypse(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
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
    
    /**
     * 应用所有效果加成
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (!GunTypeChecker.isHoldingConfiguredGunTypes(livingEntity, TaczCuriosConfig.COMMON.heavenFireApocalypseGunTypes.get())) return;
        double damageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseDamageBoost.get();
        double explosionRadiusBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionRadius.get();
        double explosionDamageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionDamage.get();
        
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageBoost, GUN_DAMAGE_UUID, GUN_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, explosionRadiusBoost, EXPLOSION_RADIUS_UUID, EXPLOSION_RADIUS_NAME, AttributeModifier.Operation.ADDITION);
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, explosionDamageBoost, EXPLOSION_DAMAGE_UUID, EXPLOSION_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GUN_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_RADIUS, EXPLOSION_RADIUS_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, EXPLOSION_DAMAGE_UUID);
    }
    
    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel,
            boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 限定枪械类型
        String gunTypes = GunTypeChecker.formatGunTypes(TaczCuriosConfig.COMMON.heavenFireApocalypseGunTypes.get());
        tooltip.add(Component.translatable("tcc.tooltip.restricted_gun_types", gunTypes));
        
        // 添加装备效果
        // 根据语言文件中的占位符顺序调整参数传递顺序：
        // %1$s - damageBoost (通用枪械伤害加成)
        // %2$s - explosionRadiusBoost (爆炸范围加成)
        // %3$s - explosionDamageBoost (爆炸伤害加成)
        // %4$s - healthCost (当前生命值扣除)
        // %5$s - nearbyPlayerRadius (周围玩家范围)
        // %6$s - nearbyPlayerDamageBoost (周围玩家伤害加成, 药水等级+1%)
        // %7$s - nearbyPlayerDuration (持续时间)
        double damageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseDamageBoost.get() * 100;
        double explosionRadiusBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionRadius.get();
        double explosionDamageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionDamage.get() * 100;
        double healthCost = TaczCuriosConfig.COMMON.heavenFireApocalypseHealthCost.get() * 100;
        double nearbyPlayerRadius = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerRadius.get();
        int nearbyPlayerDamageBoost = (int)(TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDamageBoost.get() * 100);
        int potionAmplifier = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerPotionAmplifier.get();
        int totalNearbyPlayerDamageBoost = nearbyPlayerDamageBoost * (potionAmplifier + 1);
        int nearbyPlayerDuration = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDuration.get();
        tooltip.add(formatModifierTooltip(damageBoost, "%.0f%%", Component.translatable(AttributeHelper.BULLET_GUNDAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(formatModifierTooltip(explosionRadiusBoost, "%.0f%%", Component.translatable(AttributeHelper.EXPLOSION_RADIUS.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(formatModifierTooltip(explosionDamageBoost, "%.0f%%", Component.translatable(AttributeHelper.EXPLOSION_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse.special",
                String.format("%.0f", healthCost),
                String.format("%.0f", nearbyPlayerRadius), 
                String.format("%+d", totalNearbyPlayerDamageBoost),
                String.format("%d", nearbyPlayerDuration))
            .withStyle(ChatFormatting.RED));
        
        // 伤害转换信息由客户端 TaczCuriosClientTooltip 通过 ItemTooltipEvent 动态追加
        
        // 虚数侵染上限 + 虚数崩解
        int infectionMax = TaczCuriosConfig.COMMON.apocalypseImaginaryInfectionMaxLevel.get();
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse.inflection_max",
                String.format("%d", infectionMax))
            .withStyle(ChatFormatting.RED));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rift"));
    }
    
    /**
     * 监听 TACZ 枪械伤害事件（Pre），将伤害转换为虚数伤害
     */
    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasHeavenFireApocalypseEquipped(attacker)) {
            return;
        }
        
        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, TaczCuriosConfig.COMMON.heavenFireApocalypseGunTypes.get())) return;

        event.setDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
        event.setDamageSource(GunDamageSourcePart.ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
    }
    
    /**
     * 监听 TACZ 枪械伤害事件（Post），处理扣血和周围玩家加成
     */
    @SubscribeEvent
    public static void onGunHurt(EntityHurtByGunEvent.Post event) {
        // 使用工具类检查并获取攻击者
        LivingEntity attacker = TacDamageHelper.getAttacker(event);
        if (attacker == null) {
            return;
        }
        
        // 检查攻击者是否装备了天火劫灭
        if (!hasHeavenFireApocalypseEquipped(attacker)) {
            return;
        }
        
        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, TaczCuriosConfig.COMMON.heavenFireApocalypseGunTypes.get())) return;

        float healthPercentage = attacker.getHealth() / attacker.getMaxHealth();
        if (healthPercentage < 1.0) {
            return;  // 血量不满，不触发扣血
        }
        
        // 造成伤害后直接设置玩家生命值（使用setHealth，不触发不死图腾）
        double healthCostConfig = TaczCuriosConfig.COMMON.heavenFireApocalypseHealthCost.get();
        
        // 检查是否装备了梵天百兽，如果是则减少扣血比例
        if (BrahmaBeasts.hasBrahmaBeastsEquipped(attacker)) {
            double reduction = TaczCuriosConfig.COMMON.brahmaBeastsHealthCostReduction.get();
            healthCostConfig += reduction;  // -1.0 + 0.6 = -0.4
        }
        
        // 限制扣血比例：最高99%（至少保留1%血量）
        double clampedHealthCost = Math.min(-healthCostConfig, 0.99);
        
        // 计算剩余血量比例
        double remainingHealthRatio = 1.0 - clampedHealthCost;
        float newHealth = (float) ((float) Math.round(attacker.getMaxHealth() * remainingHealthRatio * 100.0) / 100.0);
        
        // 确保至少有1点血量
        if (newHealth < 1.0f) {
            newHealth = 1.0f;
        }
        
        attacker.setHealth(newHealth);
        
        // 施加延迟标记效果，延迟后自动施加天火流血
        int delayDuration = TaczCuriosConfig.COMMON.heavenFireApocalypseDelayDuration.get();
        attacker.addEffect(new MobEffectInstance(
            TccMobEffects.HEAVEN_FIRE_APOCALYPSE_DELAY.get(),
            delayDuration * 20,
            0,
            false,  // 不是药水
            false,  // 不显示粒子
            true    // 显示图标
        ));
        
        // 获取配置中的影响范围
        double nearbyPlayerRadius = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerRadius.get();
        
        // 对周围的其他玩家提供配置中持续时间和伤害加成的bullet_gundamage加成（加算）
        List<Player> nearbyPlayers = attacker.level().getEntitiesOfClass(Player.class, attacker.getBoundingBox().inflate(nearbyPlayerRadius));

        int nearbyPlayerDuration = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDuration.get();

        for (Player nearbyPlayer : nearbyPlayers) {
            int potionAmplifier = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerPotionAmplifier.get();
            nearbyPlayer.addEffect(new MobEffectInstance(
                TccMobEffects.HEAVEN_FIRE_APOCALYPSE_BUFF.get(),
                nearbyPlayerDuration * 20,
                potionAmplifier,
                false, false, true));
        }
    }
    /**
     * 每tick检查并移除周围玩家的加成效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
    }

    /**
     * 检查生物是否装备了天火劫灭
     */
    public static boolean hasHeavenFireApocalypseEquipped(LivingEntity livingEntity) {
        return !findEquippedStack(livingEntity).isEmpty();
    }
    
    /**
     * 从天火饰品槽位中查找已装备的天火劫灭实例
     * @return 已装备的 ItemStack，未装备返回 ItemStack.EMPTY
     */
    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof HeavenFireApocalypse);
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
    
    /**
     * 血量变化回调 - 由 HeavenFireHealthListener 调用
     */
    public static void onHealthChanged(LivingEntity entity) {
        ItemStack equippedStack = findEquippedStack(entity);
        if (equippedStack.isEmpty()) {
            return;
        }
        
        float healthPercentage = entity.getHealth() / entity.getMaxHealth();
        ItemStack mainHandItem = entity.getMainHandItem();
        HeavenFireApocalypse instance = (HeavenFireApocalypse) equippedStack.getItem();
        
        if (healthPercentage >= 1.0) {
            // 满血时恢复属性
            instance.applyEffects(entity);
            AttachmentPropertyManager.postChangeEvent(entity, mainHandItem);
        } else {
            // 非满血时移除属性
            instance.removeEffects(entity);
            AttachmentPropertyManager.postChangeEvent(entity, mainHandItem);
        }
    }
}
