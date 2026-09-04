package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Kongmeng extends BoundCurioItem {
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("e6b2f5d1-8c4a-4e9b-a7d3-1f9e4c2b8d56");

    private static double specialFishChance() {
        return TaczCuriosConfig.COMMON.kongmengSpecialFishChance.get();
    }

    public Kongmeng(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(IMAGINARY_RESISTANCE_UUID, stack.getItem());
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Kongmeng);
        CompoundTag tag = equipped.getTag();
        double total = 1.0
                + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                total, IMAGINARY_RESISTANCE_UUID,
                "tcc.kongmeng.imaginary_resistance", AttributeModifier.Operation.ADDITION);
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(), IMAGINARY_RESISTANCE_UUID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("shotgun");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof Kongmeng).isEmpty();
    }

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(player,
                stack -> stack.getItem() instanceof Kongmeng);
        if (equipped.isEmpty()) {
            return;
        }
        if (player.getRandom().nextDouble() < specialFishChance()) {
            ItemStack special = player.getRandom().nextBoolean()
                    ? new ItemStack(Items.NETHER_STAR)
                    : new ItemStack(Items.DRAGON_EGG);
            player.addItem(special);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity player) || player.level().isClientSide) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(player,
                stack -> stack.getItem() instanceof Kongmeng);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((Kongmeng) equipped.getItem()).matchesRestriction(player)) {
            return;
        }
        if (player.getRandom().nextDouble() >= ImaginaryResistanceHelper.getResistanceProbability(player)) {
            return;
        }
        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (!stack.isEmpty()) {
                stack.grow(stack.getCount());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        appendImaginaryResistance(stack, tooltip);
        tooltip.add(Component.translatable("item.tcc.dream.curio_effect_fish")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.dream.curio_effect_loot")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        appendBoundPlayer(stack, tooltip);
    }
}
