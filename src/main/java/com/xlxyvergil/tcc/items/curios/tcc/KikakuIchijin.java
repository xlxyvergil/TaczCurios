package com.xlxyvergil.tcc.items.curios.tcc;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.TccCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

/**
 * 掎角一阵 - 裂隙级饰品：造成伤害时以64格内最近的玩家或女仆为祭品，
 * 以祭品血量倍率放大伤害并破坏其周围6*6方块，最后扣除祭品全部血量
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KikakuIchijin extends TccCurioItem {

    public KikakuIchijin(Properties properties) {
        super(properties);
    }

    /**
     * 掎角一阵没有属性效果，只有事件触发逻辑
     */
    @Override
    protected void applyEffects(LivingEntity livingEntity, ItemStack stack) {
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        tooltip.add(Component.translatable("item.tcc.kikaku_ichijin.effect")
            .withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.literal(""));

    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel serverLevel)) {
            return;
        }

        LivingEntity attacker = resolveAttacker(event);
        if (attacker == null) return;

        boolean hasKikaku = !CurioSearchHelper.findFirstEquippedStack(attacker,
            stack -> stack.getItem() instanceof KikakuIchijin).isEmpty();
        if (!hasKikaku) return;

        // 寻找祭品（根据装备者类型决定搜索逻辑）
        LivingEntity sacrifice = findSacrifice(attacker, serverLevel);

        float healthMultiplier = TaczCuriosConfig.COMMON.kikakuIchijinHealthMultiplier.get().floatValue();
        float damageMultiplier = sacrifice.getMaxHealth() * healthMultiplier;
        event.setAmount(event.getAmount() * damageMultiplier);

        destroyBlocksAroundVictim(serverLevel, event.getEntity());

        // 击杀祭品——多重方式依次执行确保死亡（应对不同实体的死亡保护机制）
        sacrifice.dead = true;
        sacrifice.die(sacrifice.damageSources().genericKill());
        sacrifice.kill();

        boolean isSelfSacrifice = (sacrifice == attacker);
        if (isSelfSacrifice) {
            serverLevel.getServer().getPlayerList().broadcastSystemMessage(
                Component.translatable("message.tcc.kikaku_ichijin.self_sacrifice", attacker.getName()), false);
        } else {
            Component sacrificeName = sacrifice instanceof EntityMaid maid
                ? maid.getDisplayName()
                : sacrifice.getName();
            serverLevel.getServer().getPlayerList().broadcastSystemMessage(
                Component.translatable("message.tcc.kikaku_ichijin.sacrifice", attacker.getName(), sacrificeName), false);
        }
    }

    /**
     * 解析伤害事件的真正攻击者（支持弹射物、法术、驯服生物等间接来源）
     */
    private static LivingEntity resolveAttacker(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        LivingEntity attacker = resolveFromEntity(source.getEntity());
        if (attacker != null) return attacker;
        return resolveFromEntity(source.getDirectEntity());
    }

    private static LivingEntity resolveFromEntity(Entity entity) {
        if (entity == null) return null;
        if (entity instanceof LivingEntity living) return living;
        if (entity instanceof Projectile proj) {
            if (proj.getOwner() instanceof LivingEntity owner) return owner;
            return null;
        }
        if (entity instanceof OwnableEntity ownable) {
            Entity owner = ownable.getOwner();
            if (owner instanceof LivingEntity living) return living;
        }
        return null;
    }

    /**
     * 寻找祭品：女仆装备者献祭最近玩家，玩家装备者优先女仆再玩家，其他装备者献祭自己
     */
    private static LivingEntity findSacrifice(LivingEntity attacker, ServerLevel level) {
        AABB searchBox = attacker.getBoundingBox().inflate(64.0);

        if (attacker instanceof EntityMaid) {
            // 女仆装备者：献祭最近的玩家
            List<Player> nearbyPlayers = level.getEntitiesOfClass(
                Player.class, searchBox,
                player -> player != attacker && player.isAlive()
            );
            if (!nearbyPlayers.isEmpty()) {
                return nearbyPlayers.stream()
                    .min(Comparator.comparingDouble(p -> p.distanceToSqr(attacker)))
                    .get();
            }
            return attacker;
        }

        if (attacker instanceof Player) {
            // 玩家装备者：优先女仆，其次玩家
            if (ModList.get().isLoaded("touhou_little_maid")) {
                List<EntityMaid> nearbyMaids = level.getEntitiesOfClass(
                    EntityMaid.class, searchBox,
                    maid -> maid != attacker && maid.isAlive()
                );
                if (!nearbyMaids.isEmpty()) {
                    return nearbyMaids.stream()
                        .min(Comparator.comparingDouble(m -> m.distanceToSqr(attacker)))
                        .get();
                }
            }
            List<Player> nearbyPlayers = level.getEntitiesOfClass(
                Player.class, searchBox,
                player -> player != attacker && player.isAlive()
            );
            if (!nearbyPlayers.isEmpty()) {
                return nearbyPlayers.stream()
                    .min(Comparator.comparingDouble(p -> p.distanceToSqr(attacker)))
                    .get();
            }
            return attacker;
        }

        // 既不是玩家也不是女仆（如亚波伦）→ 献祭自己
        return attacker;
    }

    /**
     * 破坏目标周围6格球形范围内的方块（根据配置决定是否破坏不可破坏与普通方块）
     */
    private static void destroyBlocksAroundVictim(ServerLevel level, LivingEntity victim) {
        BlockPos center = victim.blockPosition();
        int radius = 6;
        double radiusSq = radius * radius;
        boolean destroyUnbreakable = TaczCuriosConfig.COMMON.kikakuIchijinDestroyUnbreakableBlocks.get();
        boolean destroyNormal = TaczCuriosConfig.COMMON.kikakuIchijinDestroyNormalBlocks.get();

        if (!destroyUnbreakable && !destroyNormal) {
            return;
        }

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radiusSq) {
                        continue;
                    }

                    BlockPos pos = center.offset(x, y, z);
                    BlockState blockState = level.getBlockState(pos);

                    if (blockState.isAir()) {
                        continue;
                    }

                    float destroySpeed = blockState.getDestroySpeed(level, pos);

                    boolean isUnbreakable = destroySpeed < 0;

                    if (isUnbreakable && !destroyUnbreakable) {
                        continue;
                    }
                    if (!isUnbreakable && !destroyNormal) {
                        continue;
                    }

                    Block.dropResources(blockState, level, pos, level.getBlockEntity(pos), victim, ItemStack.EMPTY);
                    level.removeBlock(pos, false);
                }
            }
        }
    }
}
