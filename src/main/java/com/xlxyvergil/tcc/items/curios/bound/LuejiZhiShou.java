package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.compat.lootr.LootrCompat;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.List;

@EventBusSubscriber(modid = TaczCurios.MODID)
public class LuejiZhiShou extends BoundCurioItem {
    private static final ResourceLocation IMAGINARY_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "lueji_zhi_shou_e6a2b8d4_46b7");

    private static double specialFishChance() {
        return TaczCuriosConfig.COMMON.luejiZhiShouSpecialFishChance.get();
    }

    public LuejiZhiShou(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    protected void declareModifiers(LivingEntity livingEntity, ItemStack stack) {
        // 虚数抗性不受武器类型限制，装备即生效（与渡鸦/小岛一致：基础值 1 + 共享进度）。
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof LuejiZhiShou);
        CompoundTag equippedTag = ItemNbtHelper.getTag(equipped.isEmpty() ? stack : equipped);
        double totalResistance = ImaginaryResistanceHelper.calculateTotalResistance(1, equippedTag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
                totalResistance, IMAGINARY_RESISTANCE_ID, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
                IMAGINARY_RESISTANCE_ID);
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("shotgun");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof LuejiZhiShou).isEmpty();
    }

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(player,
                stack -> stack.getItem() instanceof LuejiZhiShou);
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
                stack -> stack.getItem() instanceof LuejiZhiShou);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((LuejiZhiShou) equipped.getItem()).matchesRestriction(player)) {
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal(""));

        appendImaginaryResistance(stack, tooltip);
        tooltip.add(Component.translatable("item.tcc.dream.curio_effect_fish")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.dream.curio_effect_loot")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.dream.fall_immunity")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.dream.fishing_speed")
                .withStyle(ChatFormatting.GOLD));
        if (LootrCompat.isLoaded()) {
                                                tooltip.add(Component.translatable("item.tcc.dream.lootr_highlight.line1")
                    .withStyle(ChatFormatting.GREEN));
                        tooltip.add(Component.translatable("item.tcc.dream.lootr_highlight.line2")
                    .withStyle(ChatFormatting.GREEN));
                        tooltip.add(Component.translatable("item.tcc.dream.lootr_highlight.line3")
                    .withStyle(ChatFormatting.GREEN));
                        tooltip.add(Component.translatable("item.tcc.dream.lootr_highlight.line4")
                    .withStyle(ChatFormatting.GREEN));
                                }
                                tooltip.add(Component.translatable("item.tcc.dream.spawner_highlight.line1")
                .withStyle(ChatFormatting.GREEN));
                tooltip.add(Component.translatable("item.tcc.dream.spawner_highlight.line2")
                .withStyle(ChatFormatting.GREEN));
                tooltip.add(Component.translatable("item.tcc.dream.spawner_highlight.line3")
                .withStyle(ChatFormatting.GREEN));
                        tooltip.add(Component.translatable("tcc.tooltip.affected_by_imaginary_resistance")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        appendBoundPlayer(stack, tooltip);
        tooltip.add(Component.literal(""));
    }
}
