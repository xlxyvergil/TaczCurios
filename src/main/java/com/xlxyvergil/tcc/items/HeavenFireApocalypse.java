package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("56789abc-1234-1234-1234-123456789abc");
    private static final UUID EXPLOSION_RADIUS_UUID = UUID.fromString("56789abc-1234-1234-1234-123456789abd");
    private static final UUID EXPLOSION_DAMAGE_UUID = UUID.fromString("56789abc-1234-1234-1234-123456789abe");
    
    // 用于周围玩家的加成UUID
    private static final UUID NEARBY_GUN_DAMAGE_UUID = UUID.fromString("56789abc-1234-1234-1234-123456789abf");
    
    // 修饰符名称
    private static final String GUN_DAMAGE_NAME = "tcc.heaven_fire_apocalypse.gun_damage";
    private static final String EXPLOSION_RADIUS_NAME = "tcc.heaven_fire_apocalypse.explosion_radius";
    private static final String EXPLOSION_DAMAGE_NAME = "tcc.heaven_fire_apocalypse.explosion_damage";
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
        
        // 给玩家添加属性加成
        if (slotContext.entity() instanceof Player player) {
            applyEffects(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的属性加成
        if (slotContext.entity() instanceof Player player) {
            removeEffects(player);
        }
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
     * 玩家血量为100%时，buff生效，提升1000%的bullet_gundamage，+10explosion_radius，提升1000%的explosion_damage
     */
    public void applyEffects(Player player) {
        // 检查玩家血量是否为100%（等于最大生命值）
        float healthPercentage = player.getHealth() / player.getMaxHealth();
        
        if (healthPercentage >= 1.0) {
            // 血量为100%时应用效果
            
            // 应用1000%通用枪械伤害加成
            applyAttributeModifier(player, "taa", "bullet_gundamage", 10.0, GUN_DAMAGE_UUID, GUN_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
            
            // 固定应用10点爆炸范围加成
            applyAttributeModifier(player, "taa", "explosion_radius", 10.0, EXPLOSION_RADIUS_UUID, EXPLOSION_RADIUS_NAME, AttributeModifier.Operation.ADDITION);
            
            // 应用1000%爆炸伤害加成
            applyAttributeModifier(player, "taa", "explosion_damage", 10.0, EXPLOSION_DAMAGE_UUID, EXPLOSION_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            // 血量不为100%时移除效果
            removeEffects(player);
        }
    }
    
    /**
     * 通用的属性修饰符应用方法
     */
    private void applyAttributeModifier(Player player, String namespace, String attributeName, double multiplier, UUID uuid, String modifierName, AttributeModifier.Operation operation) {
        var attributes = player.getAttributes();
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
    public void removeEffects(Player player) {
        removeAttributeModifier(player, "taa", "bullet_gundamage", GUN_DAMAGE_UUID);
        removeAttributeModifier(player, "taa", "explosion_radius", EXPLOSION_RADIUS_UUID);
        removeAttributeModifier(player, "taa", "explosion_damage", EXPLOSION_DAMAGE_UUID);
    }
    
    /**
     * 通用的属性修饰符移除方法
     */
    private void removeAttributeModifier(Player player, String namespace, String attributeName, UUID uuid) {
        var attributes = player.getAttributes();
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
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§d裂隙")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
    
    /**
     * 监听伤害事件，处理伤害触发后的生命值扣除
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        
        // 检查伤害来源是否是玩家
        if (source.getEntity() instanceof Player player) {
            // 检查玩家是否装备了天火劫灭
            if (hasHeavenFireApocalypseEquipped(player)) {
                // 检查玩家血量是否为100%
                float healthPercentage = player.getHealth() / player.getMaxHealth();
                if (healthPercentage < 1.0) {
                    return; // 血量不为100%时不生效
                }
                
                // 造成伤害后对玩家造成当前生命值100%的伤害
                float currentHealth = player.getHealth();
                float healthToDeduct = currentHealth * 1.0f;
                
                if (healthToDeduct > 0) {
                    player.hurt(player.damageSources().magic(), healthToDeduct);
                    
                    // 显示扣除生命值的提示
                    if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
                        player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal(
                                "§4天火劫灭反噬 - 生命值-100%当前生命值"
                            ),
                            true
                        );
                    }
                }
                
                // 对玩家周围的其他玩家提供15秒的100%bullet_gundamage加成（加算）
                List<Player> nearbyPlayers = player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(32.0D));

                for (Player nearbyPlayer : nearbyPlayers) {
                    // 排除自己
                    if (nearbyPlayer == player) continue;

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
                        
                        // 添加100%的伤害加成（加算）
                        AttributeModifier modifier = new AttributeModifier(
                            NEARBY_GUN_DAMAGE_UUID,
                            NEARBY_GUN_DAMAGE_NAME,
                            1.0,
                            AttributeModifier.Operation.ADDITION
                        );
                        gunDamageAttribute.addPermanentModifier(modifier);
                        
                        // 设置持续时间标记（15秒 = 300 ticks）
                        net.minecraft.nbt.CompoundTag persistentData = nearbyPlayer.getPersistentData();
                        persistentData.putInt(NEARBY_BUFF_DURATION_TAG, 300);
                    }
                }
                
                // 显示提示信息
                if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(
                            "§6天火劫灭 - 为周围玩家提供15秒的100%枪械伤害加成"
                        ),
                        true
                    );
                }
            }
        }
    }
    
    /**
     * 每tick检查并移除周围玩家的加成效果
     */
    public static void tickNearbyPlayerBuffs(Player player) {
        net.minecraft.nbt.CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains(NEARBY_BUFF_DURATION_TAG)) {
            int duration = persistentData.getInt(NEARBY_BUFF_DURATION_TAG);
            
            if (duration > 0) {
                // 减少持续时间
                persistentData.putInt(NEARBY_BUFF_DURATION_TAG, duration - 1);
            } else {
                // 移除加成效果
                var attributes = player.getAttributes();
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
        if (slotContext.entity() instanceof Player player) {
            applyEffects(player);
        }
    }
    
    /**
     * 检查玩家是否装备了天火劫灭
     */
    private static boolean hasHeavenFireApocalypseEquipped(Player player) {
        // 使用Curios API检查玩家是否装备了天火劫灭
        return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player)
            .map(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof HeavenFireApocalypse))
            .orElse(java.util.Optional.empty()).isPresent();
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(Player player) {
        applyEffects(player);
    }
}