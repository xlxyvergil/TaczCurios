package com.xlxyvergil.tcc.items;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccAttributes;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.HeavenFireBleedingSettlementEvent;
import com.xlxyvergil.tcc.registries.TaczItems;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.TacDamageHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;



/**
 * 天火圣裁 - 我将发动一次牛逼的攻击
 * 效果：玩家血量高于40%时，提升玩家325%的bullet_gundamage，造成伤害后玩家立即扣除30%血量，
 * 然后施加流血效果，每秒消耗最大HP的配置比例，持续配置的秒数。玩家血量低于40%时，此饰品的全部效果都不生效。
 */
@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeavenFireJudgment extends BaseCurioItem {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("daa1ac19-3221-43ba-b951-788015e19255");
    
    // 修饰符名称
    private static final String GUN_DAMAGE_NAME = "tcc.heaven_fire_judgment.gun_damage";
    
    public HeavenFireJudgment(Properties properties) {
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
    
    /**
     * 当饰品被卸下时调用
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
        
        LivingEntity entity = (LivingEntity) slotContext.entity();
        removeEffects(entity);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (!GunTypeChecker.isHoldingConfiguredGunTypes(livingEntity, TaczCuriosConfig.COMMON.heavenFireJudgmentGunTypes.get())) return;
        double damageMultiplier = TaczCuriosConfig.COMMON.heavenFireJudgmentDamageBoost.get();
        AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageMultiplier, GUN_DAMAGE_UUID, GUN_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GUN_DAMAGE_UUID);
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
        // %3$s - bleedingDamagePerLevel (每级流血伤害比例)
        // %4$s - bleedingDuration (流血持续时间)
        double damageBoost = TaczCuriosConfig.COMMON.heavenFireJudgmentDamageBoost.get() * 100;
        double healthCost = TaczCuriosConfig.COMMON.heavenFireJudgmentHealthCost.get() * 100;
        double bleedingDamagePerLevel = TaczCuriosConfig.COMMON.heavenFireBleedingDamagePerLevel.get() * 100;
        int bleedingDuration = TaczCuriosConfig.COMMON.heavenFireBleedingDuration.get();
        tooltip.add(Component.translatable("item.tcc.heaven_fire_judgment.effect", 
                String.format("%+.0f", damageBoost), 
                String.format("%+.0f", healthCost), 
                String.format("%+.0f", bleedingDamagePerLevel), 
                String.format("%d", bleedingDuration)));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));
        
        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.legendary"));
    }
    
    /**
     * 监听 TACZ 枪械伤害事件（Pre），将伤害转换为虚数伤害
     */
    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasHeavenFireJudgmentEquipped(attacker)) {
            return;
        }

        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, TaczCuriosConfig.COMMON.heavenFireJudgmentGunTypes.get())) return;

        // 转换为虚数伤害
        event.setDamageSource(com.tacz.guns.api.event.common.GunDamageSourcePart.NON_ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker));
    }
    
    /**
     * 监听 TACZ 枪械伤害事件（Post），处理扣血和持续伤害标记
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
        
        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, TaczCuriosConfig.COMMON.heavenFireJudgmentGunTypes.get())) {
            return;
        }

        // 检查血量是否大于40%
        float healthPercentage = attacker.getHealth() / attacker.getMaxHealth();
        if (healthPercentage <= 0.4) {
            return;  // 血量不满足条件，不触发扣血
        }
        
        // 获取配置中的生命值扣除比例
        double healthCost = TaczCuriosConfig.COMMON.heavenFireJudgmentHealthCost.get();

        // 立即扣除配置的生命值比例（使用setHealth直接设置，可触发不死图腾）
        float currentHealth = attacker.getHealth();
        float healthToDeduct = currentHealth * (float)(-healthCost);  // healthCost为负值，取反得到正值
        if (healthToDeduct > 0 && currentHealth > healthToDeduct) {
            attacker.setHealth(currentHealth - healthToDeduct);
        }
        
        // 施加天火流血效果（固定1级，不叠加）
        int bleedingDuration = TaczCuriosConfig.COMMON.heavenFireBleedingDuration.get();
        
        attacker.addEffect(new MobEffectInstance(
            TccMobEffects.HEAVEN_FIRE_BLEEDING.get(),
            bleedingDuration * 20,  // 转换为tick
            0,    // 固定0级(显示为1级)
            false,  // 不是药水
            false,  // 不显示粒子
            true    // 显示图标
        ));
    }
    
    /**
     * 监听天火流血结算事件：虚数抗性≥40且未死亡时，天火圣裁进化为天火劫灭。
     */
    @SubscribeEvent
    public static void onBleedingSettlement(HeavenFireBleedingSettlementEvent event) {
        LivingEntity entity = event.getEntity();
        if (event.isDead()) return;
        if (!hasHeavenFireJudgmentEquipped(entity)) return;

        // 虚数抗性 >= 40
        double resistance = entity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        if (resistance < 40.0) return;

        // 将天火圣裁替换为天火劫灭（参照 CuriosEventHandler 的 setStackInSlot + 手动生命周期）
        CuriosApi.getCuriosInventory(entity).ifPresent(handler -> {
            var stacksHandler = handler.getCurios().get("tcc_slot");
            if (stacksHandler == null) return;
            top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler stackHandler = stacksHandler.getStacks();
            for (int i = 0; i < stackHandler.getSlots(); i++) {
                ItemStack oldStack = stackHandler.getStackInSlot(i);
                if (oldStack.getItem() instanceof HeavenFireJudgment judgment) {
                    // 1. 拷贝NBT到新饰品
                    ItemStack newStack = new ItemStack(TaczItems.HEAVEN_FIRE_APOCALYPSE.get());
                    if (oldStack.hasTag()) {
                        newStack.setTag(oldStack.getTag().copy());
                    }
                    // 2. 构造SlotContext
                    boolean hasRenderer = stacksHandler.getRenders().size() > i && stacksHandler.getRenders().get(i);
                    SlotContext slotContext = new SlotContext("tcc_slot", entity, i, false, hasRenderer);
                    // 3. 手动卸下旧饰品（清理属性）
                    judgment.onUnequip(slotContext, ItemStack.EMPTY, oldStack);
                    // 4. 替换为天火劫灭
                    stackHandler.setStackInSlot(i, newStack);
                    // 5. 手动装备新饰品（应用属性）
                    if (newStack.getItem() instanceof HeavenFireApocalypse apocalypse) {
                        apocalypse.onEquip(slotContext, oldStack, newStack);
                    }
                    // 6. 更新TACZ缓存
                    AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
                    break;
                }
            }
        });
    }
    
    /**
     * 检查实体是否装备了天火圣裁
     */
    public static boolean hasHeavenFireJudgmentEquipped(LivingEntity livingEntity) {
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
        applyEffects(livingEntity);
    }
    
    /**
     * 血量变化回调 - 由 HeavenFireHealthListener 调用
     */
    public static void onHealthChanged(LivingEntity entity) {
        if (!hasHeavenFireJudgmentEquipped(entity)) {
            return;
        }
        
        float healthPercentage = entity.getHealth() / entity.getMaxHealth();
        ItemStack mainHandItem = entity.getMainHandItem();
        HeavenFireJudgment instance = new HeavenFireJudgment(new net.minecraft.world.item.Item.Properties());
        
        if (healthPercentage > 0.4) {
            // 血量 > 40% 时恢复属性
            instance.applyEffects(entity);
            AttachmentPropertyManager.postChangeEvent(entity, mainHandItem);
        } else {
            // 血量 <= 40% 时移除属性
            instance.removeEffects(entity);
            AttachmentPropertyManager.postChangeEvent(entity, mainHandItem);
        }
    }
}