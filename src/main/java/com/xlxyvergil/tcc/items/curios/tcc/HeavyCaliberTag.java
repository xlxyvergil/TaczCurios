package com.xlxyvergil.tcc.items.curios.tcc;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.FusionData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class HeavyCaliberTag extends TccCurioItem {
    // 属性修饰符UUID - 用于唯一标识这些修饰
    private static final UUID[] DAMAGE_UUIDS = {
        UUID.fromString("0de3ed5d-9cb1-4c22-8bd1-c9b68ac13e9f"),
        UUID.fromString("86c52112-49e1-4d80-84b1-5a327ffbc971"),
        UUID.fromString("216b141e-17b3-44f0-a03c-ddfc5758a15e"),
        UUID.fromString("7df0af83-2c3e-4680-b17f-4c37dc55dea8"),
        UUID.fromString("006a5e24-258e-487f-9301-dfb07c08caa3")
    };
    
    // 修饰符名
    private static final String[] DAMAGE_NAMES = {
        "tcc.heavy_caliber.rifle_damage",
        "tcc.heavy_caliber.sniper_damage",
        "tcc.heavy_caliber.smg_damage",
        "tcc.heavy_caliber.lmg_damage",
        "tcc.heavy_caliber.launcher_damage"
    };
    
    private static final UUID INACCURACY_UUID = UUID.fromString("4a8f7c31-22d9-4b74-8c5d-1a9e6f0b3d2e");
    private static final String INACCURACY_NAME = "tcc.heavy_caliber.inaccuracy";
    
    public HeavyCaliberTag(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(INACCURACY_UUID, stack.getItem());
        for (UUID uuid : DAMAGE_UUIDS) {
            AttributeHelper.registerSourceItem(uuid, stack.getItem());
        }
        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.heavyCaliberTagDamageBoost.get());
        double inaccuracyBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.heavyCaliberTagInaccuracyBoost.get());

        // 枪械伤害加成只在主手持有对应武器类型时生效；切换武器时由 GunSwitchEventHandler 触发 refreshEffects 重新评估
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_RIFLE, damageBoost, DAMAGE_UUIDS[0], DAMAGE_NAMES[0], AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SNIPER, damageBoost, DAMAGE_UUIDS[1], DAMAGE_NAMES[1], AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SMG, damageBoost, DAMAGE_UUIDS[2], DAMAGE_NAMES[2], AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LMG, damageBoost, DAMAGE_UUIDS[3], DAMAGE_NAMES[3], AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, damageBoost, DAMAGE_UUIDS[4], DAMAGE_NAMES[4], AttributeModifier.Operation.ADDITION);

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.INACCURACY, inaccuracyBoost, INACCURACY_UUID, INACCURACY_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_RIFLE, DAMAGE_UUIDS[0]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SNIPER, DAMAGE_UUIDS[1]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SMG, DAMAGE_UUIDS[2]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LMG, DAMAGE_UUIDS[3]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, DAMAGE_UUIDS[4]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.INACCURACY, INACCURACY_UUID);
    }
    

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.heavyCaliberTagDamageBoost.get() ) * 100;
        double inaccuracyBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.heavyCaliberTagInaccuracyBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.heavy_caliber_tag.effect", 
                String.format("%+.0f", damageBoost), String.format("%+.0f", inaccuracyBoost))
            .withStyle(ChatFormatting.GOLD));

        tooltip.add(Component.literal(""));

    }

}