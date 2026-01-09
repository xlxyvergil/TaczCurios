package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;



/**
 * 天火劫灭 - 踏上前来
 * 效果：玩家血量为100%，buff生效，提升1000%的bullet_gundamage，+10explosion_radius，提升1000%的explosion_damage，
 * 造成伤害后对玩家造成当前生命值100%的伤害，同时对玩家周围的其他玩家提供15秒的100%bullet_gundamage加成（加算）。
 */
@Mod.EventBusSubscriber(modid = "tcc")
public class HeavenFireApocalypse extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("8c87e97e-cc63-415f-b92d-6ac2e521b219");
    private static final UUID EXPLOSION_RADIUS_UUID = UUID.fromString("79f78f03-e9ba-4567-9ba9-75f729f6c3e8");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("3de85a73-816c-49c0-bc43-4c7dec18c951");
    private static final UUID EXPLOSION_ENABLED_UUID = UUID.fromString("d4e5f6a7-b8c9-0d1e-2a3b-4c5d6e7f8a9b");
    
    // 用于周围玩家的加成UUID
    private static final UUID NEARBY_GUN_DAMAGE_UUID = UUID.fromString("916209dd-ba04-45c9-9d6e-ccb29a7c6a0a");
    
    // 修饰符名称
    private static final String GUN_DAMAGE_NAME = "tcc.heaven_fire_apocalypse.gun_damage";
    private static final String EXPLOSION_RADIUS_NAME = "tcc.heaven_fire_apocalypse.explosion_radius";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.heaven_fire_apocalypse.explosion_damage";
    private static final String EXPLOSION_ENABLED_NAME = "tcc.heaven_fire_apocalypse.explosion_enabled";
    private static final String NEARBY_GUN_DAMAGE_NAME = "tcc.heaven_fire_apocalypse.nearby_gun_damage";
    
    // 用于追踪周围玩家加成效果的标记
    private static final String NEARBY_BUFF_DURATION_TAG = "HeavenFireApocalypse_NearbyBuff_Duration";
    
    public HeavenFireApocalypse(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给生物添加属性加成
        applyEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除生物的属性加成
        removeEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * 只与HeavenFireJudgment互斥，只能装备一个
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在指定的槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查是否已经装备了HeavenFireJudgment
        return !top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem() instanceof HeavenFireJudgment))
            .orElse(java.util.Optional.empty()).isPresent();
    }
    
    /**
     * 应用所有效果加成
     * 生物血量为100%时，buff生效，提升1000%的bullet_gundamage，+10explosion_radius，提升1000%的explosion_damage
     */
    public void applyEffects(LivingEntity livingEntity) {
        // 检查生物血量是否为100%（等于最大生命值）
        float healthPercentage = livingEntity.getHealth() / livingEntity.getMaxHealth();
        
        if (healthPercentage >= 1.0) {
            // 血量为100%时应用效果
            
            // 获取配置中的伤害加成、爆炸范围、爆炸伤害和爆炸启用属性值
            double damageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseDamageBoost.get();
            double explosionRadiusBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionRadius.get();
            double explosionDamageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionDamage.get();
            double explosionEnabled = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionEnabled.get();
            
            // 应用配置中的通用枪械伤害加成
            applyAttributeModifier(livingEntity, "taa", "bullet_gundamage", damageBoost, GUN_DAMAGE_UUID, GUN_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
            
            // 应用配置中的爆炸范围加成
            applyAttributeModifier(livingEntity, "taa", "explosion_radius", explosionRadiusBoost, EXPLOSION_RADIUS_UUID, EXPLOSION_RADIUS_NAME, AttributeModifier.Operation.ADDITION);
            
            // 应用配置中的爆炸伤害加成
            applyAttributeModifier(livingEntity, "taa", "explosion_damage", explosionDamageBoost, EXPLOSION_DAMAGE_UUID, EXPLOSION_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
            
            // 应用配置中的爆炸启用属性
            applyAttributeModifier(livingEntity, "taa", "explosion_enabled", explosionEnabled, EXPLOSION_ENABLED_UUID, EXPLOSION_ENABLED_NAME, AttributeModifier.Operation.ADDITION);
        } else {
            // 血量不为100%时移除效果
            removeEffects(livingEntity);
        }
    }
    
    /**
     * 通用的属性修饰符应用方法
     */
    private void applyAttributeModifier(LivingEntity livingEntity, String namespace, String attributeName, double multiplier, UUID uuid, String modifierName, AttributeModifier.Operation operation) {
        var attributes = livingEntity.getAttributes();
        var attribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation(namespace, attributeName)
            )
        );
        
        if (attribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            attribute.removeModifier(uuid);
            
            // 添加属性修饰符
            AttributeModifier modifier = new AttributeModifier(
                uuid,
                modifierName,
                multiplier,
                operation
            );
            attribute.addPermanentModifier(modifier);
        }
    }
    
    /**
     * 移除所有效果加成
     */
    public void removeEffects(LivingEntity livingEntity) {
        removeAttributeModifier(livingEntity, "taa", "bullet_gundamage", GUN_DAMAGE_UUID);
        removeAttributeModifier(livingEntity, "taa", "explosion_radius", EXPLOSION_RADIUS_UUID);
        removeAttributeModifier(livingEntity, "taa", "explosion_damage", EXPLOSION_DAMAGE_UUID);
        removeAttributeModifier(livingEntity, "taa", "explosion_enabled", EXPLOSION_ENABLED_UUID);
    }
    
    /**
     * 通用的属性修饰符移除方法
     */
    private void removeAttributeModifier(LivingEntity livingEntity, String namespace, String attributeName, UUID uuid) {
        var attributes = livingEntity.getAttributes();
        var attribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation(namespace, attributeName)
            )
        );
        
        if (attribute != null) {
            attribute.removeModifier(uuid);
        }
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        // 根据语言文件中的占位符顺序调整参数传递顺序：
        // %1$s - damageBoost (通用枪械伤害加成)
        // %2$s - explosionRadiusBoost (爆炸范围加成)
        // %3$s - explosionDamageBoost (爆炸伤害加成)
        // %4$s - healthCost (当前生命值扣除)
        // %5$s - nearbyPlayerRadius (周围玩家范围)
        // %6$s - nearbyPlayerDamageBoost (周围玩家伤害加成)
        // %7$s - nearbyPlayerDuration (持续时间)
        double damageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseDamageBoost.get() * 100;
        double explosionRadiusBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionRadius.get();
        double explosionDamageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseExplosionDamage.get() * 100;
        double healthCost = TaczCuriosConfig.COMMON.heavenFireApocalypseHealthCost.get() * 100;
        double nearbyPlayerRadius = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerRadius.get();
        double nearbyPlayerDamageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDamageBoost.get() * 100;
        int nearbyPlayerDuration = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDuration.get();
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse.effect", 
                String.format("%.0f", damageBoost), 
                String.format("%.0f", explosionRadiusBoost), 
                String.format("%.0f", explosionDamageBoost), 
                String.format("%.0f", healthCost),
                String.format("%.0f", nearbyPlayerRadius), 
                String.format("%.0f", nearbyPlayerDamageBoost),
                String.format("%d", nearbyPlayerDuration))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.mythic"));
    }
    
    /**
     * 监听伤害事件，处理伤害触发后的生命值扣除
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        
        // 检查伤害来源是否是生物
        if (source.getEntity() instanceof LivingEntity livingEntity) {
            // 检查生物是否装备了天火劫灭
            if (hasHeavenFireApocalypseEquipped(livingEntity)) {
                // 检查生物是否手持枪械
                ItemStack mainHandItem = livingEntity.getMainHandItem();
                com.tacz.guns.api.item.IGun iGun = com.tacz.guns.api.item.IGun.getIGunOrNull(mainHandItem);
                
                // 只有在生物手持枪械时才触发效果
                if (iGun != null) {
                    // 检查生物血量是否为100%
                    float healthPercentage = livingEntity.getHealth() / livingEntity.getMaxHealth();
                    if (healthPercentage < 1.0) {
                        return; // 血量不为100%时不生效
                    }
                    
                    // 造成伤害后对生物造成当前生命值100%的伤害
                    float currentHealth = livingEntity.getHealth();
                    float healthToDeduct = currentHealth * 1.0f;
                    
                    if (healthToDeduct > 0) {
                        livingEntity.hurt(livingEntity.damageSources().magic(), healthToDeduct);
                        
                        // 显示扣除生命值的提示（仅对玩家）
                        if (livingEntity instanceof Player player && net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
                            player.displayClientMessage(
                                net.minecraft.network.chat.Component.literal(
                                    "§4天火劫灭反噬 - 生命值-100%当前生命值"
                                ),
                                true
                            );
                        }
                    }
                    
                    // 获取配置中的影响范围
                    double nearbyPlayerRadius = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerRadius.get();
                    
                    // 对周围的其他玩家提供配置中持续时间和伤害加成的bullet_gundamage加成（加算）
                    List<Player> nearbyPlayers = livingEntity.level().getEntitiesOfClass(Player.class, livingEntity.getBoundingBox().inflate(nearbyPlayerRadius));

                    for (Player nearbyPlayer : nearbyPlayers) {
                        // 排除自己
                        if (nearbyPlayer == livingEntity) continue;

                        // 给周围玩家添加属性修饰符
                        var attributes = nearbyPlayer.getAttributes();
                        var gunDamageAttribute = attributes.getInstance(
                            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage")
                            )
                        );
                        
                        if (gunDamageAttribute != null) {
                            // 移除已存在的修饰符
                            gunDamageAttribute.removeModifier(NEARBY_GUN_DAMAGE_UUID);
                            
                            // 获取配置中的附近玩家伤害加成值
                            double nearbyPlayerDamageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDamageBoost.get();
                            int nearbyPlayerDuration = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDuration.get();
                            
                            // 添加配置中的伤害加成（加算）
                            AttributeModifier modifier = new AttributeModifier(
                                NEARBY_GUN_DAMAGE_UUID,
                                NEARBY_GUN_DAMAGE_NAME,
                                nearbyPlayerDamageBoost,
                                AttributeModifier.Operation.ADDITION
                            );
                            gunDamageAttribute.addPermanentModifier(modifier);
                            
                            // 设置持续时间标记（配置中的持续时间，转换为ticks）
                            net.minecraft.nbt.CompoundTag persistentData = nearbyPlayer.getPersistentData();
                            persistentData.putInt(NEARBY_BUFF_DURATION_TAG, nearbyPlayerDuration * 20);
                        }
                    }
                    
                    // 获取配置中的附近玩家伤害加成值和持续时间
                    double nearbyPlayerDamageBoost = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDamageBoost.get() * 100;
                    int nearbyPlayerDuration = TaczCuriosConfig.COMMON.heavenFireApocalypseNearbyPlayerDuration.get();
                    
                    // 显示提示信息（仅对玩家）
                    if (livingEntity instanceof Player player && net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
                        player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal(
                                "§6天火劫灭 - 为周围玩家提供" + nearbyPlayerDuration + "秒的" + String.format("%.0f", nearbyPlayerDamageBoost) + "%枪械伤害加成"
                            ),
                            true
                        );
                    }
                }
            }
        }
    }
    
    /**
     * 每tick检查并移除周围玩家的加成效果
     */
    public static void tickNearbyPlayerBuffs(LivingEntity livingEntity) {
        net.minecraft.nbt.CompoundTag persistentData = livingEntity.getPersistentData();
        if (persistentData.contains(NEARBY_BUFF_DURATION_TAG)) {
            int duration = persistentData.getInt(NEARBY_BUFF_DURATION_TAG);
            
            if (duration > 0) {
                // 减少持续时间
                persistentData.putInt(NEARBY_BUFF_DURATION_TAG, duration - 1);
            } else {
                // 移除加成效果
                var attributes = livingEntity.getAttributes();
                var gunDamageAttribute = attributes.getInstance(
                    net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                        new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage")
                    )
                );
                
                if (gunDamageAttribute != null) {
                    gunDamageAttribute.removeModifier(NEARBY_GUN_DAMAGE_UUID);
                }
                
                // 清除标记
                persistentData.remove(NEARBY_BUFF_DURATION_TAG);
            }
        }
    }
    
    /**
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效，检查血量条件
        applyEffects((LivingEntity) slotContext.entity());
    }
    
    /**
     * 检查生物是否装备了天火劫灭
     */
    private static boolean hasHeavenFireApocalypseEquipped(LivingEntity livingEntity) {
        // 使用Curios API检查生物是否装备了天火劫灭
        return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(livingEntity)
            .map(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof HeavenFireApocalypse))
            .orElse(java.util.Optional.empty()).isPresent();
    }
    
    /**
     * 当生物切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }
}