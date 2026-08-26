package com.xlxyvergil.tcc.items.curios;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.event.TccAttributeEvents;
import com.xlxyvergil.tcc.util.AiStopHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 浮生系列·神之键线（tcc_tdk）：凡尘难渡。
 * <p>
 * 攻击 15% 概率使目标停止 AI（定身）5 秒。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FanchenNandu extends BaseCurioItem {

    /** 定身时长（tick） */
    private static int stopDuration() {
        return TaczCuriosConfig.COMMON.fanchenNanduStopDurationSeconds.get() * 20;
    }

    /** 定身概率 */
    private static double stopChance() {
        return TaczCuriosConfig.COMMON.fanchenNanduStopChance.get();
    }

    /** 攻击附加护甲值虚数伤害比例 */
    private static double armorImaginaryScale() {
        return TaczCuriosConfig.COMMON.fanchenNanduArmorImaginaryScale.get();
    }

    public FanchenNandu(Properties properties) {
        super(properties);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
        if (slotContext.entity() instanceof Player player) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.getBoolean("IsBound")) {
                tag.putBoolean("IsBound", true);
                tag.putString("BoundPlayer", player.getStringUUID());
                tag.putString("BoundPlayerName", player.getGameProfile().getName());
            }
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerUUID = tag.getString("BoundPlayer");
            if (slotContext.entity() instanceof Player player) {
                return player.getStringUUID().equals(boundPlayerUUID);
            }
            return false;
        }
        return super.canEquip(slotContext, stack);
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
        return List.of("melee");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof FanchenNandu).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGunHurtPost(EntityHurtByGunEvent.Post event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !(attacker.level() instanceof ServerLevel)) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(attacker,
                stack -> stack.getItem() instanceof FanchenNandu);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((FanchenNandu) equipped.getItem()).matchesRestriction(attacker)) {
            return;
        }
        Entity hurt = event.getHurtEntity();
        if (hurt instanceof LivingEntity target && !target.isDeadOrDying()) {
            if (attacker.getRandom().nextDouble() < stopChance()) {
                AiStopHelper.apply(target, stopDuration());
            }
            // 攻击时附加（护甲值 × 比例）的虚数伤害
            double armor = attacker.getAttributeValue(Attributes.ARMOR);
            float imaginary = (float) (armor * armorImaginaryScale());
            TccAttributeEvents.applyImaginaryDamage(target,
                    TccDamageSources.imaginaryDamage(target.level(), attacker), imaginary);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.transient.key_effect",
                String.format("%.0f", stopChance() * 100))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.transient.key_effect_armor_imaginary",
                String.format("%.0f", armorImaginaryScale() * 100))
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
