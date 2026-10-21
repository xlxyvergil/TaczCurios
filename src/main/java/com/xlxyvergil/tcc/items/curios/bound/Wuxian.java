package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
@EventBusSubscriber(modid = TaczCurios.MODID)
public class Wuxian extends BoundCurioItem {
    public static final String KILL_KEY_PREFIX = "tcc_kill_count_";

    private static final ResourceLocation ALL_ATTRIBUTES_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "wuxian_0a604a5e_de85");
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "wuxian_5f8b3c2d_8c05");

    private static double perTypeBonus() {
        return TaczCuriosConfig.COMMON.wuxianPerTypeBonus.get();
    }

    public Wuxian(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("sniper");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof Wuxian).isEmpty();
    }

    public static int getKilledTypeCount(ItemStack stack) {
        CompoundTag tag = ItemNbtHelper.getTag(stack);
        if (tag == null) {
            return 0;
        }
        int count = 0;
        for (String key : tag.getAllKeys()) {
            if (key.startsWith(KILL_KEY_PREFIX)) {
                count++;
            }
        }
        return count;
    }

    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {

        // 虚数抗性不受武器类型限制，装备即生效（与渡鸦/梅比乌斯/噬界之蛇一致：基础值 1 + 共享进度）。
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Wuxian);
        CompoundTag equippedTag = ItemNbtHelper.getTag(equipped.isEmpty() ? stack : equipped);
        double totalResistance = ImaginaryResistanceHelper.calculateTotalResistance(1, equippedTag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
                totalResistance, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);

        if (matchesRestriction(livingEntity)) {
            int types = getKilledTypeCount(equipped.isEmpty() ? stack : equipped);
            double bonus = types * perTypeBonus();
            AttributeHelper.applyInfiniteAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_ID, bonus);
        } else {
            // 只清全属性加成，虚数抗性继续生效
            AttributeHelper.applyInfiniteAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_ID, 0);
        }
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.applyInfiniteAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_ID, 0);
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
                IMAGINARY_RESISTANCE_ID);
    }

    @Override
    public boolean isEventDriven() {
        return true;
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed.level().isClientSide) {
            return;
        }
        DamageSource source = event.getSource();
        if (source == null) {
            return;
        }
        Entity attacker = source.getEntity();
        if (!(attacker instanceof LivingEntity)) {
            return;
        }
        Player owner = MaidCompat.resolveOwnerPlayer(attacker);
        if (owner == null) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(owner,
                stack -> stack.getItem() instanceof Wuxian);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((Wuxian) equipped.getItem()).matchesRestriction(owner)) {
            return;
        }
        String encodeId = killed.getEncodeId();
        if (encodeId == null) {
            return;
        }
        if (getKilledTypeCount(equipped) >= TaczCuriosConfig.COMMON.sheshaLineKillTypeRecordLimit.get()) {
            return;
        }
        CompoundTag tag = ItemNbtHelper.getTag(equipped);
        String key = KILL_KEY_PREFIX + encodeId;
        if (!tag.contains(key)) {
            tag.putString(key, encodeId);
            ItemNbtHelper.setTag(equipped, tag);
            ((BoundCurioItem) equipped.getItem()).refreshEffects(owner, equipped);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);
        appendImaginaryResistance(stack, tooltip);
        tooltip.add(Component.translatable("item.tcc.infinite.curio_effect",
                String.format("%.1f", perTypeBonus() * 100))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.infinite.curio_effect_current",
                String.format("%.1f", getKilledTypeCount(stack) * perTypeBonus() * 100))
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
