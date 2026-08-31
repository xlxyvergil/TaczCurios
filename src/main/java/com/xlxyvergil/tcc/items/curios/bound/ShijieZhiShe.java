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
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShijieZhiShe extends BoundCurioItem {
    /** NBT 击杀计数前缀（后面接实体注册 ID，同类型去重） */
    public static final String KILL_KEY_PREFIX = "tcc_kill_count_";

    private static final UUID ALL_ATTRIBUTES_UUID = UUID.fromString("a3560a57-f063-4a1a-bf55-b6349d0d7918");

    /** 每种实体类型的全属性加成 */
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
        return List.of("mg");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof ShijieZhiShe).isEmpty();
    }

    /** 已累计的实体类型种数 */
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
        if (matchesRestriction(livingEntity)) {
            ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(livingEntity,
                    s -> s.getItem() instanceof ShijieZhiShe);
            int types = getKilledTypeCount(equipped.isEmpty() ? stack : equipped);
            double bonus = types * perTypeBonus();
            AttributeHelper.applyAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_UUID,
                    "tcc.infinite.all_attributes", bonus, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.applyAllAttributesModifier(livingEntity, ALL_ATTRIBUTES_UUID,
                "tcc.infinite.all_attributes", 0, AttributeModifier.Operation.MULTIPLY_BASE);
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
        // 归一化击杀归属：玩家击杀记玩家；女仆击杀归主人，使女仆造成的击杀同样累加到主人佩戴的「无限」饰品上。
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
