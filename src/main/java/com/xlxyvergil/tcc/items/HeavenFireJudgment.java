package com.xlxyvergil.tcc.items;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.util.TacDamageHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
 * 天火圣裁 - 我将发动一次牛逼的攻击
 * 效果：玩家血量高于40%时，提升玩家325%的bullet_gundamage，造成伤害后玩家立即扣除30%血量，
 * 然后每秒消耗最大HP的5%，持续5秒。玩家血量低于40%时，此饰品的全部效果都不生效。
 */
@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeavenFireJudgment extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("daa1ac19-3221-43ba-b951-788015e19255");
    
    // 修饰符名称
    private static final String GUN_DAMAGE_NAME = "tcc.heaven_fire_judgment.gun_damage";
    
    // 添加一个用于追踪持续伤害效果的标记
    private static final String DAMAGE_TAG = "HeavenFireJudgment_Damage";
    
    // 添加一个用于追踪持续伤害开始时间的标记
    private static final String DAMAGE_START_TIME_TAG = "HeavenFireJudgment_DamageStartTime";
    
    public HeavenFireJudgment(Properties properties) {
        super(properties);
    }
    
    /**
     * 当饰品被装备时调用
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        
        // 给实体添加枪械伤害属性加成
        applyGunDamageBonus((LivingEntity) slotContext.entity());
    }
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        // 移除实体的枪械伤害属性加成
        removeGunDamageBonus((LivingEntity) slotContext.entity());
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
        return !CuriosApi.getCuriosInventory(slotContext.entity())
            .map(inv -> inv.findFirstCurio(
                itemStack -> itemStack.getItem() instanceof HeavenFireApocalypse))
            .orElse(java.util.Optional.empty()).isPresent();
    }
    
    /**
     * 应用枪械伤害加成
     * 实体血量高于40%时，提升实体325%的bullet_gundamage
     */
    private void applyGunDamageBonus(LivingEntity livingEntity) {
        // 检查实体血量是否高于40%
        float healthPercentage = livingEntity.getHealth() / livingEntity.getMaxHealth();
        if (healthPercentage <= 0.4) {
            // 血量低于40%时不生效，移除加成
            removeGunDamageBonus(livingEntity);
            return;
        }
        
        // 获取配置中的伤害加成值
        double damageMultiplier = TaczCuriosConfig.COMMON.heavenFireJudgmentDamageBoost.get();
        
        // 使用TaczAttributeAdd中的通用枪械伤害属性
        var attributes = livingEntity.getAttributes();
        var gunDamageAttribute = attributes.getInstance(
            ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "bullet_gundamage")
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
    private void removeGunDamageBonus(LivingEntity livingEntity) {
        var attributes = livingEntity.getAttributes();
        var gunDamageAttribute = attributes.getInstance(
            ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("taa", "bullet_gundamage")
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
                String.format("%+.0f", damageBoost), 
                String.format("%+.0f", healthCost), 
                String.format("%+.0f", healthDrain), 
                String.format("%d", drainDuration)));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.legendary"));
    }
    
    /**
     * 监听 TACZ 枪械伤害事件，处理伤害触发后的生命值扣除
     */
    @SubscribeEvent
    public static void onGunHurt(EntityHurtByGunEvent.Post event) {
        // 使用工具类检查并获取攻击者
        LivingEntity attacker = TacDamageHelper.getAttacker(event);
        if (attacker == null) {
            return;
        }
        
        // 检查攻击者是否装备了天火圣裁
        if (!hasHeavenFireJudgmentEquipped(attacker)) {
            return;
        }
        
        // 只在服务端执行
        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }
        
        // 检查攻击者是否手持枪械
        ItemStack mainHandItem = attacker.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHandItem);
        
        // 只有在攻击者手持枪械时才触发效果
        if (iGun != null) {
            // 获取配置中的生命值扣除比例和持续时间
            double healthCost = TaczCuriosConfig.COMMON.heavenFireJudgmentHealthCost.get();
            int drainDuration = TaczCuriosConfig.COMMON.heavenFireJudgmentDrainDuration.get();

            // 立即扣除配置的生命值比例（healthCost为负值，取反得到正值）
            float healthToDeduct = attacker.getHealth() * (float)(-healthCost);
            if (healthToDeduct > 0) {
                // 使用虚空伤害源，绕过护甲、魔法减免和附魔减伤
                attacker.hurt(TccDamageSources.voidDamage(attacker), healthToDeduct);
            }
            
            // 设置持续伤害效果：等待2秒后开始，每2秒消耗最大生命值的配置比例，持续配置的秒数
            CompoundTag persistentData = attacker.getPersistentData();
            persistentData.putInt(DAMAGE_TAG, drainDuration);
            persistentData.putLong(DAMAGE_START_TIME_TAG, attacker.level().getGameTime());
        }
    }
    
    /**
     * 每tick检查并应用持续伤害效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity livingEntity = (LivingEntity) slotContext.entity();
            
        CompoundTag persistentData = livingEntity.getPersistentData();
        int duration = persistentData.getInt(DAMAGE_TAG);
            
        if (duration > 0) {
            // 检查是否已经过了2秒延迟
            long currentTime = livingEntity.level().getGameTime();
            long startTime = persistentData.getLong(DAMAGE_START_TIME_TAG);
            long elapsedTime = currentTime - startTime;
            
            // 等待2秒（40 ticks）后开始持续伤害，每2秒（40 ticks）触发一次
            if (elapsedTime >= 40 && (elapsedTime - 40) % 40 == 0) {
                // 获取配置中的生命值消耗比例（负值）
                double healthDrain = TaczCuriosConfig.COMMON.heavenFireJudgmentHealthDrain.get();
                    
                // 计算要扣除的血量
                float maxHealth = livingEntity.getMaxHealth();
                float currentHealth = livingEntity.getHealth();
                float healthToDeduct = (float) (maxHealth * (-healthDrain));  // 取反得到正值
                    
                // 确保不会扣到0以下
                if (healthToDeduct > 0 && currentHealth > healthToDeduct) {
                    // 使用虚空伤害源，绕过护甲、魔法减免和附魔减伤
                    livingEntity.hurt(TccDamageSources.voidDamage(livingEntity), healthToDeduct);
                        
                    // 减少持续时间
                    persistentData.putInt(DAMAGE_TAG, duration - 2);
                    
                    // 如果持续时间<=0，清除效果
                    if (duration - 2 <= 0) {
                        persistentData.remove(DAMAGE_TAG);
                        persistentData.remove(DAMAGE_START_TIME_TAG);
                    }
                } else {
                    // 如果会造成血量低于0，则清除效果
                    persistentData.remove(DAMAGE_TAG);
                    persistentData.remove(DAMAGE_START_TIME_TAG);
                }
            }
        } else {
            // 确俚属性加成持续生效
            applyGunDamageBonus(livingEntity);
        }
    }
    
    /**
     * 检查实体是否装备了天火圣裁
     */
    private static boolean hasHeavenFireJudgmentEquipped(LivingEntity livingEntity) {
        // 使用Curios API检查实体是否装备了天火圣裁
        return CuriosApi.getCuriosInventory(livingEntity)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_slot");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof HeavenFireJudgment) {
                            return true;
                        }
                    }
                }
                return false;
            })
            .orElse(false);
    }
    
    /**
     * 当实体切换武器时应用效果
     */
    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyGunDamageBonus(livingEntity);
    }
}