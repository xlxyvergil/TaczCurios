package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.materials.CollapseCrystal;
import com.xlxyvergil.tcc.util.EvolutionNbtKeys;
import com.xlxyvergil.tcc.util.ItemNbtHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

/**
 * 铁砧进化合成：已绑定的进阶饰品 + 1 个崩坏结晶 → 下一阶段饰品。
 * <p>
 * 服务端校验门槛：合成者已完成对应进化成就；饰品的绑定者与合成者一致。
 * 经验消耗 1 级、崩坏结晶 1 个。
 * 原「达成成就自动原地进化」已停用（见 AchievementRewards）。
 * <p>
 * 仅在服务端产出：铁砧结果槽由服务端同步给客户端，客户端无需另做校验。
 */
@EventBusSubscriber(modid = TaczCurios.MODID)
public final class AnvilEvolutionHandler {

    private AnvilEvolutionHandler() {}

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.isEmpty() || right.isEmpty()) return;

        // 右槽必须是未附加数据的崩坏结晶（带数据的用于真我/黑渊白花收集）
        if (!isPlainCollapseCrystal(right)) return;

        // 左槽饰品必须已绑定，且绑定者为合成者本人
        CompoundTag tag = ItemNbtHelper.getTag(left);
        if (!tag.getBoolean("IsBound")) return;
        if (!player.getStringUUID().equals(tag.getString("BoundPlayer"))) return;

        String fromId = itemId(left);
        if (fromId == null) return;

        String toId = resolveEvolutionTarget(fromId, player);
        if (toId == null) return;

        Item toItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(toId));
        if (toItem == null) return;

        ItemStack result = new ItemStack(toItem);
        // 复制原饰品数据（保留绑定与进度），仅清除击杀计数并重置进度计数器
        CompoundTag resultTag = tag.copy();
        resultTag.remove(EvolutionNbtKeys.KILL_COUNTS);
        ItemNbtHelper.setTag(result, resultTag);
        LivingDeathEventHandler.resetCapCountersForItem(toId, result);

        event.setOutput(result);
        event.setCost(1);         // 经验消耗：1 级
        event.setMaterialCost(1); // 消耗右槽 1 个崩坏结晶
    }

    /**
     * 在成就定义中查找该饰品对应的进化目标：命中「主进化 item→to」或「联动进化 linked.item→linked.to」，
     * 且该成就已完成时返回目标物品 id，否则返回 null。
     */
    private static String resolveEvolutionTarget(String fromId, ServerPlayer player) {
        for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.all()) {
            AchievementDefinitions.Reward reward = def.reward();
            if (reward == null || !reward.isEvolve() || reward.to() == null) continue;

            boolean isMain = fromId.equals(reward.item());
            boolean isLinked = false;
            if (!isMain && reward.linkedEvolves() != null) {
                for (AchievementDefinitions.LinkedEvolveRef linked : reward.linkedEvolves()) {
                    if (linked.item() != null && fromId.equals(linked.item())) {
                        isLinked = true;
                        break;
                    }
                }
            }
            if (!isMain && !isLinked) continue;
            if (!RuleAdvancementMapping.isAdvancementDone(player, def.id())) continue;

            if (isMain) return reward.to();
            for (AchievementDefinitions.LinkedEvolveRef linked : reward.linkedEvolves()) {
                if (linked.item() != null && fromId.equals(linked.item()) && linked.to() != null) {
                    return linked.to();
                }
            }
        }
        return null;
    }

    /** 未附加数据的崩坏结晶才能作为进化材料。 */
    private static boolean isPlainCollapseCrystal(ItemStack stack) {
        if (!(stack.getItem() instanceof CollapseCrystal)) return false;
        return ItemNbtHelper.getTag(stack).isEmpty();
    }

    private static String itemId(ItemStack stack) {
        if (stack.isEmpty()) return null;
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return key != null ? key.toString() : null;
    }
}
