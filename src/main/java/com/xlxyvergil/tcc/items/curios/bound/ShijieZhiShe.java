package com.xlxyvergil.tcc.items.curios.bound;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShijieZhiShe extends BoundCurioItem {
    public static final String KILL_KEY_PREFIX = "tcc_kill_count_";

    private static final UUID ALL_ATTRIBUTES_UUID = UUID.fromString("a3560a57-f063-4a1a-bf55-b6349d0d7918");

    private static double perTypeBonus() {
        return TaczCuriosConfig.COMMON.shijieZhiShePerTypeBonus.get();
    }

    public ShijieZhiShe(Properties properties) {
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
                stack -> stack.getItem() instanceof ShijieZhiShe).isEmpty();
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
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
        // 登记该饰品施加的修饰符 UUID → 来源饰品，供客户端属性面板显示来源图标。
        AttributeHelper.registerSourceItem(ALL_ATTRIBUTES_UUID, stack.getItem());
        if (matchesRestriction(livingEntity)) {
            ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                    s -> s.getItem() instanceof ShijieZhiShe);
            int types = getKilledTypeCount(equipped.isEmpty() ? stack : equipped);
            double bonus = types * perTypeBonus();
            AttributeHelper.applyInfiniteAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_UUID,
                    "tcc.infinite.all_attributes", bonus);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.applyInfiniteAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_UUID,
                "tcc.infinite.all_attributes", 0);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        applyEffects(slotContext.entity(), stack);
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
                stack -> stack.getItem() instanceof ShijieZhiShe);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((ShijieZhiShe) equipped.getItem()).matchesRestriction(owner)) {
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
