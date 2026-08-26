package com.xlxyvergil.tcc.items.materials;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.FusionData;
import com.xlxyvergil.tcc.util.FusionUpgradeUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

 /**
 * 融合容器（Fusion Vessel）
 * <p>用于存储大量内融核心（CoreFusion）。
 * 交互方式：</p>
 * <ul>
 *   <li>主手右键 → 切换自动吸收模式（auto:true 时自动吸收玩家背包内的内融核心）</li>
 *   <li>右键空格子 → 取出一组（64个）内融核心，不足取全部剩余（auto 模式下禁止取出）</li>
 *   <li>右键内融核心格子 → 整组存入容器</li>
 *   <li>右键饰品格子 → 按分解公式消耗饰品，等量 CoreFusion 存入容器</li>
 * </ul>
 */
public class FusionVesselItem extends Item {

    private static final String TAG_COUNT = "tcc_fusion_count";
    private static final String TAG_AUTO = "tcc_fusion_auto";
    private static final int MAX_TAKE = 64;

    public FusionVesselItem(Properties properties) {
        super(properties);
    }

    private static final TagKey<Item> TCC_SLOT = TagKey.create(Registries.ITEM,
            new ResourceLocation("curios", "tcc_slot"));

    // ========== 容量 ==========

    /**
     * 获取融合容器容量上限（从 Config 读取）。
     */
    public static int getCapacity() {
        return TaczCuriosConfig.COMMON.fusionVesselCapacity.get();
    }

    // ========== NBT 读写 ==========

    public static int getFusionCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_COUNT)) {
            return 0;
        }
        return tag.getInt(TAG_COUNT);
    }

    public static void setFusionCount(ItemStack stack, int count) {
        stack.getOrCreateTag().putInt(TAG_COUNT, Math.max(0, Math.min(count, getCapacity())));
    }

    public static boolean isAutoMode(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_AUTO);
    }

    public static void setAutoMode(ItemStack stack, boolean auto) {
        if (auto) {
            stack.getOrCreateTag().putBoolean(TAG_AUTO, true);
        } else {
            stack.removeTagKey(TAG_AUTO);
        }
    }

    // ========== 耐久条（= 存储进度条） ==========

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getFusionCount(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int count = getFusionCount(stack);
        int capacity = getCapacity();
        if (capacity <= 0) return 0;
        return Math.round(count * 13.0f / capacity);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float progress = (float) getFusionCount(stack) / getCapacity();
        // 从红(0%) → 黄(50%) → 绿(100%)
        int r = Math.min(255, Math.round(255 * (2 - progress * 2)));
        int g = Math.min(255, Math.round(255 * progress * 2));
        return (r << 16) | (g << 8) | 0x44;
    }

    // ========== Tooltip ==========

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        int count = getFusionCount(stack);
        int capacity = getCapacity();
        tooltip.add(Component.translatable("item.tcc.fusion_vessel.count", count, capacity));
        if (isAutoMode(stack)) {
            tooltip.add(Component.translatable("item.tcc.fusion_vessel.auto_on").withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.translatable("item.tcc.fusion_vessel.auto_off").withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("item.tcc.fusion_vessel.deposit").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.tcc.fusion_vessel.deposit_hint").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.tcc.fusion_vessel.remove").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.tcc.fusion_vessel.upgrade").withStyle(ChatFormatting.GRAY));
    }

    // ========== 主手右键切换自动吸收模式 ==========

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(player.getItemInHand(hand));

        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            setAutoMode(stack, !isAutoMode(stack));
        }
        // 进入"使用中"状态：按住右键时客户端每 4 tick 会重触发 use，
        // 处于使用中可阻止重复触发，保证长按/点按都只切换一次。
        player.startUsingItem(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /**
     * 使用时长设为极大值，使长按期间一直处于"使用中"状态，从而防止 use 被反复调用。
     */
    @Override
    public int getUseDuration(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    /**
     * auto 模式下每 tick 自动从玩家自身背包吸收内融核心，直到容器存满。
     */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;
        if (!isAutoMode(stack)) return;

        int fusionCount = getFusionCount(stack);
        int freeCapacity = getCapacity() - fusionCount;
        if (freeCapacity <= 0) return;

        List<ItemStack> inventory = player.getInventory().items;
        for (int i = 0; i < inventory.size(); i++) {
            if (freeCapacity <= 0) break;

            ItemStack invStack = inventory.get(i);
            if (invStack.getItem() != TccItems.CORE_FUSION) continue;

            int takeCount = Math.min(invStack.getCount(), freeCapacity);
            if (takeCount <= 0) continue;

            ItemStack taken = player.getInventory().removeItem(i, takeCount);
            int takenCount = taken.getCount();
            if (takenCount <= 0) continue;

            fusionCount += takenCount;
            freeCapacity -= takenCount;
        }

        if (fusionCount > getFusionCount(stack)) {
            setFusionCount(stack, fusionCount);
        }
    }

    // ========== overrideStackedOnOther ==========

    @Override
    public boolean overrideStackedOnOther(ItemStack vessel, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) return false;

        ItemStack slotItem = slot.getItem();
        int fusionCount = getFusionCount(vessel);

        // 1. 空格子 → 取出内融核心（auto 模式下禁止取出）
        if (slotItem.isEmpty()) {
            if (isAutoMode(vessel)) return false;
            if (fusionCount <= 0) return false;

            // 最多取 64 个，不足则取全部
            int takeCount = Math.min(MAX_TAKE, fusionCount);
            ItemStack coreStack = new ItemStack(TccItems.CORE_FUSION, takeCount);
            slot.safeInsert(coreStack);

            // safeInsert 会就地扣减 coreStack，未放入的剩余数量留在 coreStack 中；
            // 实际取出数量 = 期望取出数 - 剩余数
            int taken = takeCount - coreStack.getCount();
            setFusionCount(vessel, fusionCount - taken);
            if (getFusionCount(vessel) <= 0) {
                vessel.removeTagKey(TAG_COUNT);
            }
            return true;
        }

        // 2. 内融核心格子 → 存入容器
        if (slotItem.getItem() == TccItems.CORE_FUSION) {
            int capacity = getCapacity();
            int freeCapacity = capacity - fusionCount;
            if (freeCapacity <= 0) return false;

            int slotCount = slotItem.getCount();
            int addCount = Math.min(slotCount, freeCapacity);
            setFusionCount(vessel, fusionCount + addCount);
            slot.safeTake(slotCount, addCount, player);
            return true;
        }

        // 3. 饰品格子 → 分解存入
        if (isDecomposable(slotItem)) {
            FusionData data = FusionData.from(slotItem);
            int level = data.level();
            int output = FusionUpgradeUtil.getDecompositionOutput(data.rarity(), level);
            if (output <= 0) return false;

            // 消耗 1 个饰品
            ItemStack taken = slot.safeTake(1, 1, player);
            if (taken.isEmpty()) return false;

            setFusionCount(vessel, fusionCount + output);
            return true;
        }

        return false;
    }

    /**
     * 判断物品是否可分解为 CoreFusion。
     * 必须是 tcc_slot 饰品且稀有度在升级系统范围内。
     */
    private static boolean isDecomposable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!stack.is(TCC_SLOT)) return false;
        return FusionData.from(stack).isUpgradeable();
    }

}
