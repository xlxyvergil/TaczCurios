package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Mebius extends BoundCurioItem {
    public static final String KILL_KEY_PREFIX = "tcc_kill_count_";

    private static final UUID ALL_ATTRIBUTES_UUID = UUID.fromString("0b52d9a4-d644-4ad4-b96a-f686468ebb3a");
    private static final UUID IMAGINARY_RESISTANCE_UUID = UUID.fromString("7c1e4f2a-5b93-4d0e-9f14-2a6c8b3d71e5");

    private static double perTypeBonus() {
        return TaczCuriosConfig.COMMON.mebiusPerTypeBonus.get();
    }

    public Mebius(Properties properties) {
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
                stack -> stack.getItem() instanceof Mebius).isEmpty();
    }

    public static int getKilledTypeCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
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

        // 虚数抗性不受武器类型限制，装备即生效（与渡鸦/小岛一致：基础值 1 + 共享进度）。
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                s -> s.getItem() instanceof Mebius);
        CompoundTag equippedTag = (equipped.isEmpty() ? stack : equipped).getTag();
        double totalResistance = ImaginaryResistanceHelper.calculateTotalResistance(1, equippedTag);
        AttributeHelper.applyModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                totalResistance, IMAGINARY_RESISTANCE_UUID, "tcc.mebius.imaginary_resistance",
                AttributeModifier.Operation.ADDITION);

        if (matchesRestriction(livingEntity)) {
            int types = getKilledTypeCount(equipped.isEmpty() ? stack : equipped);
            double bonus = types * perTypeBonus();
            AttributeHelper.applyInfiniteAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_UUID,
                    "tcc.infinite.all_attributes", bonus);
        } else {
            // 只清全属性加成，虚数抗性继续生效
            AttributeHelper.applyInfiniteAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_UUID,
                    "tcc.infinite.all_attributes", 0);
        }
    }

    @Override
    protected void removeModifiers(LivingEntity livingEntity) {
        AttributeHelper.applyInfiniteAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_UUID,
                "tcc.infinite.all_attributes", 0);
        AttributeHelper.removeModifier(livingEntity, TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                IMAGINARY_RESISTANCE_UUID);
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
                stack -> stack.getItem() instanceof Mebius);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((Mebius) equipped.getItem()).matchesRestriction(owner)) {
            return;
        }
        String encodeId = killed.getEncodeId();
        if (encodeId == null) {
            return;
        }
        if (getKilledTypeCount(equipped) >= TaczCuriosConfig.COMMON.sheshaLineKillTypeRecordLimit.get()) {
            return;
        }
        CompoundTag tag = equipped.getOrCreateTag();
        String key = KILL_KEY_PREFIX + encodeId;
        if (!tag.contains(key)) {
            tag.putString(key, encodeId);
            ((BoundCurioItem) equipped.getItem()).refreshEffects(owner, equipped);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        appendImaginaryResistance(stack, tooltip);
        tooltip.add(Component.translatable("item.tcc.infinite.curio_effect",
                String.format("%.1f", perTypeBonus() * 100))
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.tcc.infinite.curio_effect_current",
                String.format("%.1f", getKilledTypeCount(stack) * perTypeBonus() * 100))
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);

        tooltip.add(Component.literal(""));
    }
}
