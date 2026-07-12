package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.api.items.IBindable;
import com.xlxyvergil.tcc.items.materials.CollapseCrystal;

import com.xlxyvergil.tcc.items.materials.CollapseCrystal;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * Curios 饰品基类，提供通用的饰品行为和绑定卸载机制。
 * <p>
 * 绑定物品（{@link #isBoundItem()} 返回 {@code true}）在 tcc_3rd 或 tcc_tdk
 * 槽位中将无法直接卸下，只有创造模式或消耗背包中的崩坏结晶才能取下。
 */
public class ItemBaseCurio extends Item implements ICurioItem, IBindable, Vanishable {

    /** 需要使用崩坏结晶才能卸下的 Curios 槽位标识 */
    private static final String[] BOUND_SLOT_IDS = {"tcc_3rd", "tcc_tdk"};

    public ItemBaseCurio(Properties properties) {
        super(properties);
    }

    /**
     * 检查玩家背包中是否携带崩坏结晶。
     *
     * @param player 玩家
     * @return 背包中存在崩坏结晶时返回 {@code true}
     */
    protected boolean hasCollapseCrystal(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof CollapseCrystal) {
                return true;
            }
        }
        return false;
    }

    /**
     * 消耗玩家背包中的一个崩坏结晶。
     *
     * @param player 玩家
     */
    protected void consumeCollapseCrystal(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof CollapseCrystal) {
                stack.shrink(1);
                return;
            }
        }
    }

    /**
     * 判断当前饰品是否为绑定物品（不可直接卸下）。
     * <p>
     * 子类覆写此方法返回 {@code true} 即可启用绑定机制：
     * 在 tcc_3rd / tcc_tdk 槽位中只有创造模式或消耗崩坏结晶才能取下。
     *
     * @return 默认为 {@code false}，绑定饰品类需覆写为 {@code true}
     */
    protected boolean isBoundItem() {
        return false;
    }

    /**
     * 判断当前槽位是否属于需要崩坏结晶才能卸下的绑定槽位。
     *
     * @param context 槽位上下文
     * @return 如果是 tcc_3rd 或 tcc_tdk 槽位则返回 {@code true}
     */
    protected boolean isBoundSlot(SlotContext context) {
        String slotId = context.identifier();
        for (String boundSlot : BOUND_SLOT_IDS) {
            if (boundSlot.equals(slotId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    /**
     * 判断饰品是否可以卸下。
     * <p>
     * 对于绑定物品（{@link #isBoundItem()} 返回 {@code true}）：
     * <ul>
     *   <li>创造模式 — 始终允许卸下</li>
     *   <li>绑定槽位 + 背包有崩坏结晶 — 允许卸下</li>
     *   <li>其他情况 — 禁止卸下</li>
     * </ul>
     * 对于非绑定物品，始终允许卸下。
     */
    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        if (isBoundItem() && slotContext.entity() instanceof Player player) {
            if (player.isCreative()) {
                return true;
            }
            if (isBoundSlot(slotContext) && hasCollapseCrystal(player)) {
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.DEFAULT;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
    }

    /**
     * 饰品卸下后调用。
     * <p>
     * 对于绑定物品在绑定槽位中非创造模式卸下时，自动消耗玩家背包中的一个崩坏结晶。
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (isBoundItem() && slotContext.entity() instanceof Player player && !player.isCreative()) {
            if (isBoundSlot(slotContext)) {
                consumeCollapseCrystal(player);
            }
        }
    }

    /** 应用切枪效果，由子类覆写实现具体逻辑 */
    public void applyGunSwitchEffect(LivingEntity entity) {
    }
}