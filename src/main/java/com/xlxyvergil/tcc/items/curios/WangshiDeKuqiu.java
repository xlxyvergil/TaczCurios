package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.ImaginaryConversionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 戒律系列·神之键线（tcc_tdk）：往世的苦囚。
 * <p>
 * 佩戴时每 1 秒对 64 格内非玩家实体施加持续 15 秒的 3 级虚数侵染。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WangshiDeKuqiu extends BaseCurioItem {

    public WangshiDeKuqiu(Properties properties) {
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
        return List.of("shotgun");
    }

    public static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof WangshiDeKuqiu).isEmpty();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        Player player = slotContext.entity() instanceof Player p ? p : null;
        if (player == null || player.level().isClientSide) return;
        if (player.isDeadOrDying()) return;
        if (!matchesRestriction(player)) return;
        // 每 1 秒刷新一次范围内虚数侵染
        if (player.tickCount % 20 != 0) return;
        applyInfectionAura(player);
    }

    /** 每 1 秒：对光环半径内非玩家实体施加持续指定秒数的指定等级虚数侵染 */
    private void applyInfectionAura(Player player) {
        double radius = TaczCuriosConfig.COMMON.wangshiDeKuqiuAuraRadius.get();
        double radiusSq = radius * radius;
        int level = TaczCuriosConfig.COMMON.wangshiDeKuqiuInfectionLevel.get();
        int duration = TaczCuriosConfig.COMMON.wangshiDeKuqiuInfectionDurationSeconds.get();
        List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class,
                new AABB(player.blockPosition()).inflate(radius),
                e -> e != player && !(e instanceof Player) && e.isAlive()
                        && e.distanceToSqr(player) <= radiusSq);
        if (targets.isEmpty()) return;
        for (LivingEntity target : targets) {
            ImaginaryConversionHelper.applyInfection(target, player, level, duration);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.discipline.key_effect",
                TaczCuriosConfig.COMMON.wangshiDeKuqiuAuraRadius.get().intValue(),
                TaczCuriosConfig.COMMON.wangshiDeKuqiuInfectionLevel.get())
                .withStyle(ChatFormatting.GOLD));
        appendBoundPlayer(stack, tooltip);
    }
}
