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



public class MergedRifling extends TccCurioItem {
    private static final UUID[] DAMAGE_UUIDS = {
        UUID.fromString("f36f64c9-c3ec-4faf-b233-1d3ae64ef940"),
        UUID.fromString("32254b9b-364b-44de-bbf2-352df3726ac5"),
        UUID.fromString("adfae406-517c-442b-99cb-1708ec1f1f63"),
        UUID.fromString("f1f1f906-2111-425c-bb8c-be24a54a1f95"),
        UUID.fromString("39f3a9fd-562e-48bf-b26f-fbe3d106e7e8")
    };
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("6967f153-c8f1-4f6c-9752-bd2f5e5253c2");
    
    private static final String[] DAMAGE_NAMES = {
        "tcc.merged_rifling.rifle_damage",
        "tcc.merged_rifling.sniper_damage",
        "tcc.merged_rifling.smg_damage",
        "tcc.merged_rifling.lmg_damage",
        "tcc.merged_rifling.launcher_damage"
    };
    private static final String MOVEMENT_SPEED_NAME = "tcc.merged_rifling.movement_speed";
    
    public MergedRifling(Properties properties) {
        super(properties);
    }
    
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(MOVEMENT_SPEED_UUID, stack.getItem());
        for (UUID uuid : DAMAGE_UUIDS) {
            AttributeHelper.registerSourceItem(uuid, stack.getItem());
        }
        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.mergedRiflingDamageBoost.get());
        double speedBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.mergedRiflingMovementSpeedBoost.get());
        
        // 枪械伤害加成只在主手持有对应武器类型时生效；切换武器时由 GunSwitchEventHandler 触发 refreshEffects 重新评估
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_RIFLE, damageBoost, DAMAGE_UUIDS[0], DAMAGE_NAMES[0], AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SNIPER, damageBoost, DAMAGE_UUIDS[1], DAMAGE_NAMES[1], AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SMG, damageBoost, DAMAGE_UUIDS[2], DAMAGE_NAMES[2], AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LMG, damageBoost, DAMAGE_UUIDS[3], DAMAGE_NAMES[3], AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, damageBoost, DAMAGE_UUIDS[4], DAMAGE_NAMES[4], AttributeModifier.Operation.ADDITION);

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.MOVE_SPEED, speedBoost, MOVEMENT_SPEED_UUID, MOVEMENT_SPEED_NAME, AttributeModifier.Operation.ADDITION);
        }
    }
    
    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_RIFLE, DAMAGE_UUIDS[0]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SNIPER, DAMAGE_UUIDS[1]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_SMG, DAMAGE_UUIDS[2]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LMG, DAMAGE_UUIDS[3]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER, DAMAGE_UUIDS[4]);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.MOVE_SPEED, MOVEMENT_SPEED_UUID);
    }
    

    @Override
    public java.util.List<String> getWeaponTypeRestriction() {
        return java.util.List.of("rifle", "sniper", "smg", "mg", "rpg");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        double damageBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.mergedRiflingDamageBoost.get() ) * 100;
        double speedBoost = FusionData.from(stack).getActualValue(TaczCuriosConfig.COMMON.mergedRiflingMovementSpeedBoost.get() ) * 100;
        tooltip.add(Component.translatable("item.tcc.merged_rifling.effect", 
                String.format("%+.0f", damageBoost), String.format("%+.0f", speedBoost))
            .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(""));

    }
    
}