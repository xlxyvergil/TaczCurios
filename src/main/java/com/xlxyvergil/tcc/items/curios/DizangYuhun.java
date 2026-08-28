package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 旭光系列·神之键线（tcc_tdk）：地藏御魂。
 * <p>
 * 攻击必定持久削减目标当前护甲 / 韧性 5%（改 data，不恢复）。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DizangYuhun extends BaseCurioItem {

    private static final UUID ARMOR_STRIP_UUID = UUID.fromString("1b28a6a4-747b-40aa-887d-2492faca2b99");
    private static final UUID TOUGHNESS_STRIP_UUID = UUID.fromString("3ad28e89-46d1-4dfd-a3b0-e669ae311df2");

    /** 每次攻击削减百分比 */
    private static double stripPercent() {
        return TaczCuriosConfig.COMMON.dizangYuhunStripPercent.get();
    }

    public DizangYuhun(Properties properties) {
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
                stack -> stack.getItem() instanceof DizangYuhun).isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel)) {
            return;
        }
        LivingEntity attacker = resolveAttacker(event);
        if (attacker == null) {
            return;
        }
        ItemStack equipped = CurioSearchHelper.findFirstEquippedStack(attacker,
                stack -> stack.getItem() instanceof DizangYuhun);
        if (equipped.isEmpty()) {
            return;
        }
        if (!((DizangYuhun) equipped.getItem()).matchesRestriction(attacker)) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target.isDeadOrDying()) {
            return;
        }
        // 按当前实际值百分比削减，持久累加，不随时间恢复（归 0 后削 0）
        double stripArmor = Math.round(target.getAttributeValue(Attributes.ARMOR) * stripPercent() * 100.0) / 100.0;
        double stripToughness = Math.round(target.getAttributeValue(Attributes.ARMOR_TOUGHNESS) * stripPercent() * 100.0) / 100.0;
        if (stripArmor > 0) {
            AttributeHelper.applyStackingModifier(target, Attributes.ARMOR,
                    -stripArmor, ARMOR_STRIP_UUID,
                    "tcc.dawn_key.armor_strip", AttributeModifier.Operation.ADDITION);
        }
        if (stripToughness > 0) {
            AttributeHelper.applyStackingModifier(target, Attributes.ARMOR_TOUGHNESS,
                    -stripToughness, TOUGHNESS_STRIP_UUID,
                    "tcc.dawn_key.toughness_strip", AttributeModifier.Operation.ADDITION);
        }
    }

    /** 解析伤害事件中的攻击者（近战直接命中） */
    private static LivingEntity resolveAttacker(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity living) {
            return living;
        }
        if (event.getSource().getDirectEntity() instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.dawn.key_effect",
                String.format("%.0f", stripPercent() * 100))
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
