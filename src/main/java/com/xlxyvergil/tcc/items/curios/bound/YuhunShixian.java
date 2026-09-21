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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
@EventBusSubscriber(modid = TaczCurios.MODID)
public class YuhunShixian extends BoundCurioItem {
    private static final ResourceLocation ARMOR_STRIP_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "yuhun_shixian_388a60d4_9c86");
    private static final ResourceLocation TOUGHNESS_STRIP_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "yuhun_shixian_91f79641_7f45");

    public YuhunShixian(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ARMOR_STRIP_ID, stack.getItem());
        AttributeHelper.registerSourceItem(TOUGHNESS_STRIP_ID, stack.getItem());
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
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        // 仅响应真正的主动攻击（近战/枪械/虚数伤），过滤 FastHurt 再入产生的强制子伤害（残血 GENERIC_KILL 等），
        // 避免崩解/侵染/削甲特效被子伤害重复触发，造成递归施加
        if (!TccAttributeEvents.isActiveAttackSource(event.getSource())) return;
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
        double stripArmor = Math.round(target.getAttributeValue(Attributes.ARMOR) * pct * 100.0) / 100.0;
        double stripToughness = Math.round(target.getAttributeValue(Attributes.ARMOR_TOUGHNESS) * pct * 100.0) / 100.0;
        if (stripArmor > 0) {
            AttributeHelper.applyStackingModifier(target, Attributes.ARMOR,
                    -stripArmor, ARMOR_STRIP_ID, AttributeModifier.Operation.ADD_VALUE);
        }
        if (stripToughness > 0) {
            AttributeHelper.applyStackingModifier(target, Attributes.ARMOR_TOUGHNESS,
                    -stripToughness, TOUGHNESS_STRIP_ID, AttributeModifier.Operation.ADD_VALUE);
        }
        // 攻击命中时同时施加虚数侵染
        TccAttributeEvents.applyInfection(target, attacker, ImaginaryInfectionHelper.resolveMaxLevel(attacker));
        // 先施侵染，再施加剧增崩解，确保崩解结算时目标带侵染
        TccAttributeEvents.applyCollapse(target, attacker);
    }

    private static LivingEntity resolveAttacker(LivingIncomingDamageEvent event) {
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.dawn.key_effect_resistance")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance").withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendAlwaysImaginaryCollapse(tooltip);
        appendBoundPlayer(stack, tooltip);
    }
}
