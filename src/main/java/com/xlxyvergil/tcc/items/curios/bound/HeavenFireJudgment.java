package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.evolution.AchievementDefinitions;
import com.xlxyvergil.tcc.evolution.RuleAdvancementMapping;
import com.xlxyvergil.tcc.event.HeavenFireBleedingSettlementEvent;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.TacDamageHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
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



@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeavenFireJudgment extends BoundCurioItem {
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("daa1ac19-3221-43ba-b951-788015e19255");
    
    private static final String GUN_DAMAGE_NAME = "tcc.heaven_fire_judgment.gun_damage";
    
    public HeavenFireJudgment(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        if (matchesRestriction(livingEntity)) {
            double damageMultiplier = TaczCuriosConfig.COMMON.heavenFireJudgmentDamageBoost.get();
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, damageMultiplier, GUN_DAMAGE_UUID, GUN_DAMAGE_NAME, AttributeModifier.Operation.MULTIPLY_BASE);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE, GUN_DAMAGE_UUID);
    }
    

    
    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("pistol");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        

        
        tooltip.add(Component.literal(""));
        
        double damageBoost = TaczCuriosConfig.COMMON.heavenFireJudgmentDamageBoost.get() * 100;
        double healthCost = TaczCuriosConfig.COMMON.heavenFireJudgmentHealthCost.get() * 100;
        tooltip.add(formatModifierTooltip(damageBoost, "%.0f%%", Component.translatable(AttributeHelper.BULLET_GUNDAMAGE.getDescriptionId()))
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.tcc.heaven_fire_judgment.special",
                String.format("%+.0f", healthCost))
            .withStyle(ChatFormatting.WHITE));
        
        tooltip.add(Component.literal(""));
        
        
        appendBoundPlayer(stack, tooltip);
    }
    
    /** 扣血并施加天火流血标记（Post 事件） */
    @SubscribeEvent
    public static void onGunHurt(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = TacDamageHelper.getAttacker(event);
        if (attacker == null) {
            return;
        }

        if (!hasHeavenFireJudgmentEquipped(attacker)) {
            return;
        }

        if (!(attacker.level() instanceof ServerLevel)) {
            return;
        }

        if (!GunTypeChecker.isHoldingConfiguredGunTypes(attacker, List.of("pistol"))) {
            return;
        }

        float healthPercentage = attacker.getHealth() / attacker.getMaxHealth();
        if (healthPercentage <= 0.4) {
            return;  // 血量不满足条件，不触发扣血
        }

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
     * 由 achievement_definitions.json (tcc:judgment_to_apocalypse) 驱动。
     */
    @SubscribeEvent
    public static void onBleedingSettlement(HeavenFireBleedingSettlementEvent event) {
        LivingEntity entity = event.getEntity();
        if (event.isDead()) return;
        if (!(entity instanceof ServerPlayer serverPlayer)) return;
        if (!hasHeavenFireJudgmentEquipped(entity)) return;

        AchievementDefinitions.AchievementDef def =
                AchievementDefinitions.get("tcc:judgment_to_apocalypse").orElse(null);
        if (def == null) return;

        if (RuleAdvancementMapping.isAdvancementDone(serverPlayer, def.id())) return;

        if (!RuleAdvancementMapping.arePrerequisitesMet(serverPlayer, def)) return;

        if (def.conditions() != null && def.conditions().attributes() != null) {
            double resistance = entity.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            for (AchievementDefinitions.AttributeCondition ac : def.conditions().attributes()) {
                if ("tcc:imaginary_damage_resistance".equals(ac.attribute())) {
                    boolean ok = switch (ac.comparator()) {
                        case "lt" -> resistance < ac.value();
                        case "lte" -> resistance <= ac.value();
                        case "eq" -> Double.compare(resistance, ac.value()) == 0;
                        case "ne" -> Double.compare(resistance, ac.value()) != 0;
                        case "gt" -> resistance > ac.value();
                        default -> resistance >= ac.value();
                    };
                    if (!ok) return;
                    break;
                }
            }
        }

        // 授予成就
        RuleAdvancementMapping.awardNextCriterion(
                serverPlayer, def.id(), def.targetCount());
    }
    
    public static boolean hasHeavenFireJudgmentEquipped(LivingEntity livingEntity) {
        return !findEquippedStack(livingEntity).isEmpty();
    }
    
    /** 从天火饰品槽位中查找已装备的天火圣裁实例 */
    private static ItemStack findEquippedStack(LivingEntity livingEntity) {
        return CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof HeavenFireJudgment);
    }
    
    /** 血量变化回调（由 HeavenFireHealthListener 调用） */
    public static void onHealthChanged(LivingEntity entity) {
        ItemStack equippedStack = findEquippedStack(entity);
        if (equippedStack.isEmpty()) {
            return;
        }
        
        float healthPercentage = entity.getHealth() / entity.getMaxHealth();
        ItemStack mainHandItem = entity.getMainHandItem();
        HeavenFireJudgment instance = (HeavenFireJudgment) equippedStack.getItem();
        
        if (healthPercentage > 0.4) {
            // 血量 > 40% 时恢复属性
            instance.applyEffects(entity, equippedStack);
            AttachmentPropertyManager.postChangeEvent(entity, mainHandItem);
        } else {
            // 血量 <= 40% 时移除属性
            instance.removeEffects(entity);
            AttachmentPropertyManager.postChangeEvent(entity, mainHandItem);
        }
    }
}
