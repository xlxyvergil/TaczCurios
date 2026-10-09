package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.ImaginaryInfectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class YuhunShixian extends BoundCurioItem {
    private static final UUID ARMOR_STRIP_UUID = UUID.fromString("388a60d4-6ec8-455a-ac26-8619f5c59c86");
    private static final UUID TOUGHNESS_STRIP_UUID = UUID.fromString("91f79641-decf-4cc9-af18-958b9f3a7f45");

    public YuhunShixian(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ARMOR_STRIP_UUID, stack.getItem());
        AttributeHelper.registerSourceItem(TOUGHNESS_STRIP_UUID, stack.getItem());
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("melee");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof YuhunShixian).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        // 仅响应攻击者发起的近战攻击本身，不响应虚数伤害（近战附加子伤害、崩解 DoT）
        DamageSource source = event.getSource();
        if (!source.is(DamageTypes.PLAYER_ATTACK) && !source.is(DamageTypes.MOB_ATTACK)) return;
        if (!(event.getEntity().level() instanceof ServerLevel)) {
            return;
        }
        LivingEntity attacker = resolveAttacker(event);
        if (attacker == null) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(attacker,
                stack -> stack.getItem() instanceof YuhunShixian);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((YuhunShixian) equipped.getItem()).matchesRestriction(attacker)) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target.isDeadOrDying()) {
            return;
        }
        double pct = ImaginaryResistanceHelper.getResistanceValue(attacker) / 100.0;
        // 削甲/削韧最多降至 0：削减量不超过属性当前值
        double armor = target.getAttributeValue(Attributes.ARMOR);
        double toughness = target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        double stripArmor = Math.min(Math.round(armor * pct * 100.0) / 100.0, Math.max(0.0, armor));
        double stripToughness = Math.min(Math.round(toughness * pct * 100.0) / 100.0, Math.max(0.0, toughness));
        if (stripArmor > 0) {
            AttributeHelper.applyStackingModifier(target, Attributes.ARMOR,
                    -stripArmor, ARMOR_STRIP_UUID,
                    "tcc.dawn_key.armor_strip", AttributeModifier.Operation.ADDITION);
        }
        if (stripToughness > 0) {
            AttributeHelper.applyStackingModifier(target, Attributes.ARMOR_TOUGHNESS,
                    -stripToughness, TOUGHNESS_STRIP_UUID,
                    "tcc.dawn_key.toughness_strip", AttributeModifier.Operation.ADDITION);
        }
        // 攻击命中时施加虚数侵染（崩解已不再由近战神之键施加）
        TccAttributeEvents.applyInfection(target, attacker, ImaginaryInfectionHelper.resolveMaxLevel(attacker));
    }

    private static LivingEntity resolveAttacker(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity living) {
            return living;
        }
        if (event.getSource().getDirectEntity() instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.dawn.key_effect_resistance")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance").withStyle(ChatFormatting.LIGHT_PURPLE));

        appendBoundPlayer(stack, tooltip);
    }
}
