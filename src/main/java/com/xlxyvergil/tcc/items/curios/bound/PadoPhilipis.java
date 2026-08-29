package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PadoPhilipis extends BoundCurioItem {
    /** 钓鱼特殊战利品概率 */
    private static double specialFishChance() {
        return TaczCuriosConfig.COMMON.padoPhilipisSpecialFishChance.get();
    }

    public PadoPhilipis(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("shotgun");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof PadoPhilipis).isEmpty();
    }

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(player,
                stack -> stack.getItem() instanceof PadoPhilipis);
        if (equipped.isEmpty()) {
            return;
        }
        // 0.01% 概率获得下界之星 / 龙蛋
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
        if (!(attacker instanceof Player player) || player.level().isClientSide) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(player,
                stack -> stack.getItem() instanceof PadoPhilipis);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((PadoPhilipis) equipped.getItem()).matchesRestriction(player)) {
            return;
        }
        // 击杀战利品翻倍：概率 = 施加者虚数抗性
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
