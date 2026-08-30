package com.xlxyvergil.tcc.items.curios.bound;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WangshiDeSheying extends BoundCurioItem {
    /** 移除正面 buff 的概率 */
    private static double removeChance() {
        return TaczCuriosConfig.COMMON.wangshiDeSheyingRemoveChance.get();
    }

    public WangshiDeSheying(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isBoundItem() {
        return true;
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
    }

    @Override
    public List<String> getWeaponTypeRestriction() {
        return List.of("mg");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof WangshiDeSheying).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !(attacker.level() instanceof ServerLevel)) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(attacker,
                stack -> stack.getItem() instanceof WangshiDeSheying);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((WangshiDeSheying) equipped.getItem()).matchesRestriction(attacker)) {
            return;
        }
        Entity hurt = event.getHurtEntity();
        if (hurt instanceof LivingEntity target && !target.isDeadOrDying()) {
            if (attacker.getRandom().nextDouble() < removeChance()) {
                List<MobEffect> beneficials = new ArrayList<>();
                for (MobEffectInstance instance : target.getActiveEffects()) {
                    if (instance.getEffect().isBeneficial()) {
                        beneficials.add(instance.getEffect());
                    }
                }
                if (!beneficials.isEmpty()) {
                    MobEffect toRemove = beneficials.get(attacker.getRandom().nextInt(beneficials.size()));
                    target.removeEffect(toRemove);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.infinite.key_effect",
                String.format("%.0f", removeChance() * 100))
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
