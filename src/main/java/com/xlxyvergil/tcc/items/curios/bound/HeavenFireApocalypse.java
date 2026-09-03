package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.ImaginaryConversionHelper;
import com.xlxyvergil.tcc.util.TacDamageHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.ChatFormatting;
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



@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeavenFireApocalypse extends BoundCurioItem {
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("8c87e97e-cc63-415f-b92d-6ac2e521b219");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("3de85a73-816c-49c0-bc43-4c7dec18c951");
    
    private static final String GUN_DAMAGE_NAME = "tcc.heaven_fire_apocalypse.gun_damage";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.heaven_fire_apocalypse.explosion_damage";
    
    
    public HeavenFireApocalypse(Properties properties) {
        super(properties);
    }
    
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        LivingEntity entity = (LivingEntity) slotContext.entity();
        applyEffects(entity, stack);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double damageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseDamageBoost.get();
            double explosionDamageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionDamage.get();

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageBoost, GUN_DAMAGE_UUID, GUN_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, explosionDamageBoost, EXPLOSION_DAMAGE_UUID, EXPLOSION_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GUN_DAMAGE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.EXPLOSION_DAMAGE, EXPLOSION_DAMAGE_UUID);
    }
    
    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("pistol");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        
        tooltip.add(Component.literal(""));

        // 参数顺序需与语言文件占位符一一对应（枪伤/爆伤/扣血/范围/加成/时长）
        double damageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseDamageBoost.get() * 100;
        double explosionDamageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionDamage.get() * 100;
        double healthCost = TaczCuriosConfig.COMMON.heavenFireApocalypseHealthCost.get() * 100;
        double nearbyPlayerRadius = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerRadius.get();
        int nearbyPlayerDamageBoost = (int)(TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDamageBoost.get() * 100);
        int potionAmplifier = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerPotionAmplifier.get();
        int totalNearbyPlayerDamageBoost = nearbyPlayerDamageBoost * (potionAmplifier + 1);
        int nearbyPlayerDuration = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDuration.get();
        tooltip.add(formatModifierTooltip(damageBoost, "%.0f%%", Component.translatable(AttributeHelper.BULLET_GUNDAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(formatModifierTooltip(explosionDamageBoost, "%.0f%%", Component.translatable(AttributeHelper.EXPLOSION_DAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.gun_to_imaginary")
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse.special",
                String.format("%.0f", healthCost),
                String.format("%.0f", nearbyPlayerRadius), 
                String.format("%+d", totalNearbyPlayerDamageBoost),
                String.format("%d", nearbyPlayerDuration))
            .withStyle(ChatFormatting.RED));
        
        // 伤害转换信息由客户端 TaczCuriosClientTooltip 通过 ItemTooltipEvent 动态追加

        appendBoundPlayer(stack, tooltip);
    }
    
    /** 将 TACZ 枪械伤害转换为虚数伤害（Pre 事件） */
    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasHeavenFireApocalypseEquipped(attacker)) {
            return;
        }
        
        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, List.of("pistol"))) return;

        ImaginaryConversionHelper.convertToImaginary(event);
    }
    
    /** 扣血并为周围玩家提供加成（Post 事件） */
    @SubscribeEvent
    public static void onGunHurt(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = TacDamageHelper.getAttacker(event);
        if (attacker == null) {
            return;
        }

        if (!hasHeavenFireApocalypseEquipped(attacker)) {
            return;
        }
        
        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, List.of("pistol"))) return;

        float healthPercentage = attacker.getHealth() / attacker.getMaxHealth();
        if (healthPercentage < 1.0) {
            return;  // 血量不满，不触发扣血
        }
        
        // 造成伤害后直接设置玩家生命值（使用setHealth，不触发不死图腾）
        double healthCostConfig = TaczCuriosConfig.COMMON.heavenFireApocalypseHealthCost.get();
        
        // 检查是否装备了梵天百兽，如果是则减少扣血比例
        if (BrahmaBeasts.hasBrahmaBeastsEquipped(attacker)) {
            double reduction = TaczCuriosConfig.COMMON.brahmaBeastsHealthCostReduction.get();
            healthCostConfig += reduction;
        }

        // 限制扣血比例：最高99%（至少保留1%血量）
        double clampedHealthCost = Math.min(-healthCostConfig, 0.99);

        double remainingHealthRatio = 1.0 - clampedHealthCost;
        float newHealth = (float) ((float) Math.round(attacker.getMaxHealth() * remainingHealthRatio * 10000.0) / 10000.0);
        
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
    public static boolean hasHeavenFireApocalypseEquipped(LivingEntity livingEntity) {
        return !findEquippedStack(livingEntity).isEmpty();
    }
    
    /** 从天火饰品槽位中查找已装备的天火劫灭实例 */
    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof HeavenFireApocalypse);
    }
    
    /** 血量变化回调（由 HeavenFireHealthListener 调用） */
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
            instance.applyEffects(entity, equippedStack);
            AttachmentPropertyManager.postChangeEvent(entity, mainHandItem);
        } else {
            // 非满血时移除属性
            instance.removeEffects(entity);
            AttachmentPropertyManager.postChangeEvent(entity, mainHandItem);
        }
    }
}
