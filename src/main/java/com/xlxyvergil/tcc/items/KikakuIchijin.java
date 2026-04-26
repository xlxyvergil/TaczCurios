package com.xlxyvergil.tcc.items;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

/**
 * 掎角一阵 - 裂隙级饰品
 * 效果：造成伤害时，以64格范围内最近的玩家或女仆为祭品，
 * 用祭品总血量的20%作为倍率乘以当前伤害，然后破坏6*6范围内的方块，
 * 最后扣除祭品全部血量
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KikakuIchijin extends ItemBaseCurio {

    public KikakuIchijin(Properties properties) {
        super(properties);
    }

    /**
     * 检查是否可以装备到指定插槽
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().equals("tcc_slot");
    }

    /**
     * 当物品在Curios插槽中时被右键点击
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    /**
     * 添加物品的悬浮提示信息（鼠标悬停时显示）
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加物品描述
        tooltip.add(Component.translatable("item.tcc.kikaku_ichijin.desc")
            .withStyle(net.minecraft.ChatFormatting.GRAY));

        // 添加空行分隔
        tooltip.add(Component.literal(""));

        // 添加装备效果
        tooltip.add(Component.translatable("item.tcc.kikaku_ichijin.effect")
            .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));

        // 添加饰品槽位信息
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tcc.tooltip.slot"));

        // 添加稀有度提示
        tooltip.add(Component.translatable("tcc.tooltip.rarity.mythic"));
    }

    /**
     * 监听伤害事件
     * 当装备此饰品的玩家或女仆造成伤害时触发
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 获取伤害来源
        Entity sourceEntity = event.getSource().getEntity();
        if (!(sourceEntity instanceof LivingEntity attacker)) {
            return;
        }

        // 检查攻击者是否装备了掎角一阵
        boolean hasKikakuIchijin = CuriosApi.getCuriosInventory(attacker)
            .map(handler -> {
                var stacksHandler = handler.getCurios().get("tcc_slot");
                if (stacksHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                        if (stack.getItem() instanceof KikakuIchijin) {
                            return true;
                        }
                    }
                }
                return false;
            })
            .orElse(false);

        if (!hasKikakuIchijin) {
            return;
        }

        // 只在服务端执行
        if (!(attacker.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // 寻找祭品（64格范围内最近的非自身玩家或女仆）
        LivingEntity sacrifice = findSacrifice(attacker, serverLevel);

        // 计算伤害倍率（祭品总血量 × 配置倍率）
        float sacrificeMaxHealth = sacrifice.getMaxHealth();
        float healthMultiplier = TaczCuriosConfig.COMMON.kikakuIchijinHealthMultiplier.get().floatValue();
        float damageMultiplier = sacrificeMaxHealth * healthMultiplier;

        // 获取当前伤害并乘以倍率
        float originalDamage = event.getAmount();
        float newDamage = originalDamage * damageMultiplier;
        event.setAmount(newDamage);

        // 破坏目标周围6*6范围内的方块
        LivingEntity victim = event.getEntity();
        destroyBlocksAroundVictim(serverLevel, victim);

        // 对祭品造成100%生命值的伤害（让不死图腾有机会生效）
        // 使用伤害源让不死图腾可以触发
        sacrifice.hurt(sacrifice.damageSources().magic(), sacrificeMaxHealth);

        // 广播消息给所有玩家
        if (sacrifice == attacker) {
            // 祭品是自己
            serverLevel.getServer().getPlayerList().broadcastSystemMessage(Component.literal(attacker.getName().getString() + ": Stella"), false);
        } else {
            // 祭品是其他实体
            String sacrificeName;
            if (sacrifice instanceof EntityMaid maid) {
                // 如果是女仆，使用 getDisplayName()
                sacrificeName = maid.getDisplayName().getString();
            } else {
                sacrificeName = sacrifice.getName().getString();
            }
            // 广播消息
            serverLevel.getServer().getPlayerList().broadcastSystemMessage(
                Component.literal(attacker.getName().getString() + ": " + sacrificeName + " 这是必要的牺牲，你明白吧"), false);
        }
    }

    /**
     * 寻找祭品
     * 64格范围内最近的非自身玩家或女仆，如果没有则选择自己
     */
    private static LivingEntity findSacrifice(LivingEntity attacker, ServerLevel level) {
        AABB searchBox = attacker.getBoundingBox().inflate(64.0);

        // 搜索玩家
        List<Player> nearbyPlayers = level.getEntitiesOfClass(
            Player.class,
            searchBox,
            player -> player != attacker && player.isAlive()
        );

        // 合并列表并按距离排序
        List<LivingEntity> candidates = new java.util.ArrayList<>();
        candidates.addAll(nearbyPlayers);

        // 搜索女仆（如果安装了车万女仆模组）
        if (ModList.get().isLoaded("touhou_little_maid")) {
            List<EntityMaid> nearbyMaids = level.getEntitiesOfClass(
                EntityMaid.class,
                searchBox,
                maid -> maid != attacker && maid.isAlive()
            );
            candidates.addAll(nearbyMaids);
        }

        if (candidates.isEmpty()) {
            // 没有其他候选者，选择自己
            return attacker;
        }

        // 按距离排序，返回最近的
        return candidates.stream()
            .min(Comparator.comparingDouble(candidate -> candidate.distanceToSqr(attacker)))
            .orElse(attacker);
    }

    /**
     * 破坏目标周围球形范围内的方块（变成掉落物）
     * 半径6格的球形范围，根据配置决定是否破坏不可破坏方块
     */
    private static void destroyBlocksAroundVictim(ServerLevel level, LivingEntity victim) {
        BlockPos center = victim.blockPosition();
        int radius = 6; // 球形半径6格
        double radiusSq = radius * radius; // 半径平方，用于距离计算
        boolean destroyUnbreakable = TaczCuriosConfig.COMMON.kikakuIchijinDestroyUnbreakableBlocks.get();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    // 检查是否在球形范围内（距离中心点不超过半径）
                    if (x * x + y * y + z * z > radiusSq) {
                        continue;
                    }

                    BlockPos pos = center.offset(x, y, z);
                    BlockState blockState = level.getBlockState(pos);

                    // 不破坏空气
                    if (blockState.isAir()) {
                        continue;
                    }

                    // 根据配置决定是否跳过不可破坏方块
                    if (!destroyUnbreakable && blockState.getDestroySpeed(level, pos) < 0) {
                        continue;
                    }

                    // 破坏方块并掉落
                    Block.dropResources(blockState, level, pos, level.getBlockEntity(pos), victim, ItemStack.EMPTY);
                    level.removeBlock(pos, false);
                }
            }
        }
    }
}
