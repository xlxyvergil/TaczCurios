package com.xlxyvergil.tcc.items;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.TacDamageHelper;
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
import top.theillusivec4.curios.api.CuriosApi;
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
    private static final UUID EXPLOSION_RADIUS_UUID = UUID.fromString("79f78f03-e9ba-4567-9ba9-75f729f6c3e8");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("3de85a73-816c-49c0-bc43-4c7dec18c951");
    private static final UUID EXPLOSION_ENABLED_UUID = UUID.fromString("d4e5f6a7-b8c9-0d1e-2a3b-4c5d6e7f8a9b");
    
    private static final String GUN_DAMAGE_NAME = "tcc.heaven_fire_apocalypse_endless.gun_damage";
    private static final String EXPLOSION_RADIUS_NAME = "tcc.heaven_fire_apocalypse_endless.explosion_radius";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.heaven_fire_apocalypse_endless.explosion_damage";
    private static final String EXPLOSION_ENABLED_NAME = "tcc.heaven_fire_apocalypse_endless.explosion_enabled";
    
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
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        LivingEntity entity = (LivingEntity) slotContext.entity();
        removeEffects(entity);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (!GunTypeChecker.isHoldingConfiguredGunTypes(livingEntity, TaczCuriosConfig.COMMON.heavenFireApocalypseGunTypes.get())) return;
        
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
    public boolean canUnequip(SlotContext context, ItemStack stack) {
        if (context.entity() instanceof Player player && player.isCreative())
            return super.canUnequip(context, stack);
        return false;
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        tooltip.add(Component.literal(""));
        
        // 使用与天火劫灭相同的参数格式，但不显示扣血信息
        double damageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseDamageBoost.get() * 100;
        double explosionRadiusBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionRadius.get();
        double explosionDamageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionDamage.get() * 100;
        double nearbyPlayerRadius = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerRadius.get();
        int nearbyPlayerDamageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerPotionAmplifier.get() + 1;
        int nearbyPlayerDuration = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDuration.get();
        
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse_endless.effect", 
                String.format("%+.0f", damageBoost), 
                String.format("%+.0f", explosionRadiusBoost), 
                String.format("%+.0f", explosionDamageBoost),
                String.format("%+.0f", nearbyPlayerRadius), 
                String.format("%+d", nearbyPlayerDamageBoost),
                String.format("%d", nearbyPlayerDuration)));
        
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        tooltip.add(Component.translatable("tcc.tooltip.rarity.rift"));
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

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, TaczCuriosConfig.COMMON.heavenFireApocalypseGunTypes.get())) return;

        // 100%转换为虚数伤害
        event.setDamageSource(com.tacz.guns.api.event.common.GunDamageSourcePart.NON_ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker));
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

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, TaczCuriosConfig.COMMON.heavenFireApocalypseGunTypes.get())) return;

        // 无烬终焉：不扣血，直接施加BUFF给周围玩家
        double nearbyPlayerRadius = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerRadius.get();
        List<Player> nearbyPlayers = attacker.level().getEntitiesOfClass(Player.class, attacker.getBoundingBox().inflate(nearbyPlayerRadius));
        int nearbyPlayerDuration = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDuration.get();

        for (Player nearbyPlayer : nearbyPlayers) {
            nearbyPlayer.addEffect(new MobEffectInstance(
                TccMobEffects.HEAVEN_FIRE_APOCALYPSE_BUFF.get(),
                nearbyPlayerDuration * 20,
                0,
                false, false, true));
        }
    }
    
    public static boolean hasHeavenFireApocalypseEndlessEquipped(LivingEntity livingEntity) {
        return CuriosApi.getCuriosInventory(livingEntity)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_slot");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof HeavenFireApocalypseEndless) {
                            return true;
                        }
                    }
                }
                return false;
            })
            .orElse(false);
    }
    
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}
