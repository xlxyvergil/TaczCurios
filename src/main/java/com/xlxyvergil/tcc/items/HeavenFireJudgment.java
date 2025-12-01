package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
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
 * 天火圣裁 - 我将发动一次牛逼的攻击
 * 效果：玩家血量高于40%时，提升玩家325%的bullet_gundamage，造成伤害后玩家立即扣除30%血量，
 * 然后每秒消耗最大HP的5%，持续5秒。玩家血量低于40%时，此饰品的全部效果都不生效。
 */
@Mod.EventBusSubscriber(modid = "tcc")
public class HeavenFireJudgment extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("456789ab-1234-1234-1234-123456789abc");
    
    // 修饰符名称
    private static final String GUN_DAMAGE_NAME = "tcc.heaven_fire_judgment.gun_damage";
    
    // 添加一个用于追踪持续伤害效果的标记
    private static final String DAMAGE_TAG = "HeavenFireJudgment_Damage";
    
    public HeavenFireJudgment(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给玩家添加枪械伤害属性加成
        if (slotContext.entity() instanceof Player player) {
            applyGunDamageBonus(player);
        }
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除玩家的枪械伤害属性加成
        if (slotContext.entity() instanceof Player player) {
            removeGunDamageBonus(player);
        }
    }
    
    /**
     * 检查是否可以装备到指定插槽
     * 与HeavenFireApocalypse互斥，只能装备一个
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // 检查是否装备在指定的槽位
        if (!slotContext.identifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查是否已经装备了HeavenFireApocalypse
        return !top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem() instanceof HeavenFireApocalypse))
            .orElse(java.util.Optional.empty()).isPresent();
    }
    
    /**
     * 应用枪械伤害加成
     * 玩家血量高于40%时，提升玩家325%的bullet_gundamage
     */
    private void applyGunDamageBonus(Player player) {
        // 检查玩家血量是否高于40%
        float healthPercentage = player.getHealth() / player.getMaxHealth();
        if (healthPercentage <= 0.4) {
            // 血量低于40%时不生效，移除加成
            removeGunDamageBonus(player);
            return;
        }
        
        // 获取配置中的伤害加成值
        double damageMultiplier = TaczCuriosConfig.COMMON.heavenFireJudgmentDamageBoost.get();
        
        // 使用TaczAttributeAdd中的通用枪械伤害属性
        var attributes = player.getAttributes();
        var gunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage")
            )
        );
        
        if (gunDamageAttribute != null) {
            // 检查是否已经存在相同的修饰符，如果存在则移除
            gunDamageAttribute.removeModifier(GUN_DAMAGE_UUID);
            
            // 添加配置中的伤害加成
            AttributeModifier modifier = new AttributeModifier(
                GUN_DAMAGE_UUID,
                GUN_DAMAGE_NAME,
                damageMultiplier,
                AttributeModifier.Operation.MULTIPLY_BASE
            );
            gunDamageAttribute.addPermanentModifier(modifier);
        }
    }
    
    /**
     * 移除枪械伤害加成
     */
    private void removeGunDamageBonus(Player player) {
        var attributes = player.getAttributes();
        var gunDamageAttribute = attributes.getInstance(
            net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES.getValue(
                new net.minecraft.resources.ResourceLocation("taa", "bullet_gundamage")
            )
        );
        
        if (gunDamageAttribute != null) {
            // 移除之前添加的修饰符
            gunDamageAttribute.removeModifier(GUN_DAMAGE_UUID);
        }
    }
    
    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.heaven_fire_judgment.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加空行分隔
        tooltip.add(Component.literal(""));
        
        // 添加装备效果
        // 根据语言文件中的占位符顺序传递参数：
        // %1$s - damageBoost (通用枪械伤害加成)
        // %2$s - healthCost (当前生命值扣除)
        // %3$s - healthDrain (每秒消耗最大生命值)
        // %4$s - drainDuration (持续时间)
        double damageBoost = TaczCuriosConfig.COMMON.heavenFireJudgmentDamageBoost.get() * 100;
        double healthCost = TaczCuriosConfig.COMMON.heavenFireJudgmentHealthCost.get() * 100;
        double healthDrain = TaczCuriosConfig.COMMON.heavenFireJudgmentHealthDrain.get() * 100;
        int drainDuration = TaczCuriosConfig.COMMON.heavenFireJudgmentDrainDuration.get();
        tooltip.add(Component.translatable("item.tcc.heaven_fire_judgment.effect", 
                String.format("%.0f", damageBoost), 
                String.format("%.0f", healthCost), 
                String.format("%.0f", healthDrain), 
                String.format("%d", drainDuration))
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.legendary"));
    }
    
    /**
     * 监听伤害事件，处理伤害触发后的生命值扣除
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        
        // 检查伤害来源是否是玩家
        if (source.getEntity() instanceof Player player) {
            // 检查玩家是否装备了天火圣裁
            if (hasHeavenFireJudgmentEquipped(player)) {
                // 检查玩家是否手持枪械
                ItemStack mainHandItem = player.getMainHandItem();
                com.tacz.guns.api.item.IGun iGun = com.tacz.guns.api.item.IGun.getIGunOrNull(mainHandItem);
                
                // 只有在玩家手持枪械时才触发效果
                if (iGun != null) {
                    // 检查玩家血量是否高于40%
                    float healthPercentage = player.getHealth() / player.getMaxHealth();
                    if (healthPercentage <= 0.4) {
                        return; // 血量低于或等于40%时不生效
                    }

                    // 获取配置中的生命值扣除比例和持续时间
                    double healthCost = TaczCuriosConfig.COMMON.heavenFireJudgmentHealthCost.get();
                    double healthDrain = TaczCuriosConfig.COMMON.heavenFireJudgmentHealthDrain.get();
                    int drainDuration = TaczCuriosConfig.COMMON.heavenFireJudgmentDrainDuration.get();
                    
                    // 计算扣除配置的生命值比例后的血量百分比
                    float healthAfterDeduction = (player.getHealth() - player.getHealth() * (float)healthCost) / player.getMaxHealth();
                    if (healthAfterDeduction <= 0.4) {
                        return; // 扣除配置的生命值比例后如果低于或等于40%，则不触发效果
                    }

                    // 立即扣除配置的生命值比例
                    float healthToDeduct = player.getHealth() * (float)healthCost;
                    if (healthToDeduct > 0) {
                        player.hurt(player.damageSources().magic(), healthToDeduct);
                    }
                    
                    // 设置持续伤害效果：每秒消耗最大生命值的配置比例，持续配置的秒数
                    // 这里我们通过给玩家添加一个NBT标签来跟踪效果
                    net.minecraft.nbt.CompoundTag persistentData = player.getPersistentData();
                    persistentData.putInt(DAMAGE_TAG, drainDuration); // 持续配置的秒数
                }
            }
        }
    }
    
    /**
     * 每tick检查并应用持续伤害效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            // 检查玩家血量是否高于40%
            float healthPercentage = player.getHealth() / player.getMaxHealth();
            if (healthPercentage <= 0.4) {
                // 血量低于40%时移除加成并清除持续伤害效果
                removeGunDamageBonus(player);
                player.getPersistentData().remove(DAMAGE_TAG);
                return;
            }
            
            net.minecraft.nbt.CompoundTag persistentData = player.getPersistentData();
            int duration = persistentData.getInt(DAMAGE_TAG);
            
            if (duration > 0) {
                // 每秒触发一次伤害（20 ticks = 1秒）
                if (player.tickCount % 20 == 0) {
                    // 获取配置中的生命值消耗比例
                    double healthDrain = TaczCuriosConfig.COMMON.heavenFireJudgmentHealthDrain.get();
                    
                    // 检查扣除配置的生命值消耗比例后是否会低于40%
                    float maxHealth = player.getMaxHealth();
                    float currentHealth = player.getHealth();
                    float healthToDeduct = (float) (maxHealth * healthDrain);
                    float healthAfterDeduction = (currentHealth - healthToDeduct) / maxHealth;
                    
                    if (healthAfterDeduction > 0.4) {
                        // 只有扣除后血量仍高于40%才造成伤害
                        if (healthToDeduct > 0) {
                            player.hurt(player.damageSources().magic(), healthToDeduct);
                        }
                        
                        // 减少持续时间
                        persistentData.putInt(DAMAGE_TAG, duration - 1);
                    } else {
                        // 如果会造成血量低于40%，则清除效果
                        persistentData.remove(DAMAGE_TAG);
                    }
                }
            } else {
                // 确保属性加成持续生效
                applyGunDamageBonus(player);
            }
        }
    }
    
    /**
     * 检查玩家是否装备了天火圣裁
     */
    private static boolean hasHeavenFireJudgmentEquipped(Player player) {
        // 使用Curios API检查玩家是否装备了天火圣裁
        return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player)
            .map(inv -> inv.findFirstCurio(stack -> stack.getItem() instanceof HeavenFireJudgment))
            .orElse(java.util.Optional.empty()).isPresent();
    }
    
    /**
     * 当玩家切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(Player player) {
        applyGunDamageBonus(player);
    }
}