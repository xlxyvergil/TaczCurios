package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.ImaginaryConversionHelper;
import net.minecraft.ChatFormatting;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import net.minecraft.resources.ResourceLocation;
@EventBusSubscriber(modid = TaczCurios.MODID)
public class ShijieFanyan extends BoundCurioItem {
    private static final ResourceLocation CRIT_CHANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "shijie_fanyan_d94e7f8a_1a4d");
    private static final ResourceLocation CRIT_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "shijie_fanyan_e05f8a9b_2b5e");
    private static final ResourceLocation LUCK_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "shijie_fanyan_c3d4e5f6_9003");

    public ShijieFanyan(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 ID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(CRIT_CHANCE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(CRIT_DAMAGE_ID, stack.getItem());
        AttributeHelper.registerSourceItem(LUCK_ID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.LUCK,
                TaczCuriosConfig.COMMON.shijieFanyanLuck.get(), LUCK_ID, AttributeModifier.Operation.ADD_VALUE);

            int luck = (int) livingEntity.getAttributeValue(AttributeHelper.LUCK);
            double critChance = Math.round(luck * TaczCuriosConfig.COMMON.shijieFanyanCritChancePerLuck.get() * 10000.0) / 10000.0;
            double critDamage = Math.round(luck * TaczCuriosConfig.COMMON.shijieFanyanCritDamagePerLuck.get() * 10000.0) / 10000.0;

            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE,
                critChance, CRIT_CHANCE_ID, AttributeModifier.Operation.ADD_VALUE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE,
                critDamage, CRIT_DAMAGE_ID, AttributeModifier.Operation.ADD_VALUE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.LUCK, LUCK_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_ID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_ID);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity(), stack);
    }

    public static boolean hasEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof ShijieFanyan).isEmpty();
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingAnyGun(attacker)) return;

        ImaginaryConversionHelper.convertToImaginary(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingAnyGun(attacker)) return;

        if (event.getBullet() == null) return;
        if (!event.getBullet().getPersistentData().getBoolean(ImaginaryConversionHelper.INFECTION_KEY)) return;

        Entity hurtEntity = event.getHurtEntity();
        if (!(hurtEntity instanceof LivingEntity targetLiving)) return;
        if (targetLiving.isDeadOrDying()) return;

        ImaginaryConversionHelper.applyInfection(event, attacker, false);

        int luck = (int) attacker.getAttributeValue(AttributeHelper.LUCK);
        double collapseChance = Math.round((TaczCuriosConfig.COMMON.shijieFanyanCollapseBaseChance.get()
            + (luck / 10.0) * TaczCuriosConfig.COMMON.shijieFanyanCollapsePerLuck.get()) * 10000.0) / 10000.0;
        if (attacker.getRandom().nextDouble() < collapseChance) {
            TccAttributeEvents.applyCollapse(targetLiving, attacker);
        }
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("smg");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);

        tooltip.add(Component.literal(""));

        int luck = TaczCuriosConfig.COMMON.shijieFanyanLuck.get();
        double critChance = 0;
        double critDamage = 0;
        double collapseChance = 0;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null) {
                int actualLuck = (int) wearer.getAttributeValue(AttributeHelper.LUCK);
                critChance = actualLuck * TaczCuriosConfig.COMMON.shijieFanyanCritChancePerLuck.get();
                critDamage = actualLuck * TaczCuriosConfig.COMMON.shijieFanyanCritDamagePerLuck.get();
                collapseChance = (TaczCuriosConfig.COMMON.shijieFanyanCollapseBaseChance.get()
                    + (actualLuck / 10.0) * TaczCuriosConfig.COMMON.shijieFanyanCollapsePerLuck.get()) * 100;
            }
        }
        String sfCollapseStr = String.format("%.1f", collapseChance);
        tooltip.add(formatModifierTooltip(luck, "%.0f", Component.translatable(AttributeHelper.LUCK.value().getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(formatModifierTooltip(critChance, "%.2f", Component.translatable(AttributeHelper.CRIT_CHANCE.value().getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(formatModifierTooltip(critDamage, "%.2f", Component.translatable(AttributeHelper.CRIT_DAMAGE.value().getDescriptionId()))
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.tcc.shijie_fanyan.special",
                sfCollapseStr)
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tcc.tooltip.gun_to_imaginary")
            .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable("tcc.tooltip.affected_by_luck")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }
}
