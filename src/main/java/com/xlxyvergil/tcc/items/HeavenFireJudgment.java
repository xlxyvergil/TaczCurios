package com.xlxyvergil.tcc.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
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
 * 效果：以当前玩家生命值的50%作为倍率提升通用伤害，造成伤害后扣除玩家当前生命值的50%
 */
@Mod.EventBusSubscriber(modid = "tcc")
public class HeavenFireJudgment extends ItemBaseCurio {
    
    // 属性修饰符UUID - 用于唯一标识这些修饰符
    private static final UUID GUN_DAMAGE_UUID = UUID.fromString("456789ab-1234-1234-1234-123456789abc");
    
    // 修饰符名称
    private static final String GUN_DAMAGE_NAME = "tcc.heaven_fire_judgment.gun_damage";
    
    // 伤害标记，用于跟踪由该饰品发动的攻击
    private static final String DAMAGE_MARKER = "tcc.heaven_fire_judgment";
    
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
        if (slotContext.getWearer() instanceof Player player) {
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
        if (slotContext.getWearer() instanceof Player player) {
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
        if (!slotContext.getIdentifier().equals("tcc_slot")) {
            return false;
        }
        
        // 检查是否已经装备了HeavenFireApocalypse
        return !top.theillusivec4.curios.api.CuriosApi.getCuriosHelper().findFirstCurio(slotContext.getWearer(), 
            itemStack -> itemStack.getItem() instanceof HeavenFireApocalypse).isPresent();
    }
    
    /**
     * 应用枪械伤害加成
     * 以玩家当前生命值的50%作为倍率提升通用伤害
     */
    private void applyGunDamageBonus(Player player) {
        // 获取玩家当前生命值
        float currentHealth = player.getHealth();
        
        // 计算伤害倍率（当前生命值的50%）
        double damageMultiplier = currentHealth * 0.5;
        
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
            
            // 添加动态计算的伤害加成倍率
            AttributeModifier modifier = new AttributeModifier(
                GUN_DAMAGE_UUID,
                GUN_DAMAGE_NAME,
                damageMultiplier,
                AttributeModifier.Operation.MULTIPLY_TOTAL
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
     * 当玩家持有时，每tick更新效果
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 确保效果持续生效，动态更新基于生命值的伤害加成
        if (slotContext.getWearer() instanceof Player player) {
            applyGunDamageBonus(player);
        }
    }
    
    /**
     * 当物品被装备时，显示提示信息
     */
    @Override
    public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        if (slotContext.getWearer() instanceof Player player) {
            float currentHealth = player.getHealth();
            double damageMultiplier = currentHealth * 0.5;
            
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                    String.format("§6天火圣裁已装备 - 枪械伤害倍率+%.1f (基于%.1f生命值)", 
                        damageMultiplier, currentHealth)
                ),
                true
            );
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
        tooltip.add(Component.translatable("item.tcc.heaven_fire_judgment.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        
        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7装备槽位：§aTCC饰品栏")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // 添加稀有度提示
        tooltip.add(Component.literal("§7稀有度：§6稀有")
            .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
    
    /**
     * 监听伤害事件，处理伤害触发后的生命值扣除
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        
        // 检查伤害来源是否是玩家
        if (source.getEntity() instanceof Player player) {
            // 检查玩家是否装备了天火圣裁
            if (hasHeavenFireJudgmentEquipped(player)) {
                // 造成伤害后扣除玩家当前生命值的50%
                float currentHealth = player.getHealth();
                float healthToDeduct = currentHealth * 0.5f;
                
                if (healthToDeduct > 0) {
                    player.hurt(player.damageSources().magic(), healthToDeduct);
                    
                    // 显示扣除生命值的提示
                    player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal(
                            String.format("§4天火圣裁反噬 - 生命值-%.1f", healthToDeduct)
                        ),
                        true
                    );
                }
            }
        }
    }
    
    /**
     * 检查玩家是否装备了天火圣裁
     */
    private static boolean hasHeavenFireJudgmentEquipped(Player player) {
        // 使用Curios API检查玩家是否装备了天火圣裁
        return top.theillusivec4.curios.api.CuriosApi.getCuriosHelper().findFirstCurio(player, 
            stack -> stack.getItem() instanceof HeavenFireJudgment).isPresent();
    }
}