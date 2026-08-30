package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.api.items.IBindable;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.items.materials.CollapseCrystal;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import java.util.List;

/**
 * 3rd / tdk 饰品基类，负责绑定玩家信息、显示当前虚数抗性（含进化规则累积值）。
 * 绑定饰品在 tcc_3rd / tcc_tdk 槽位中无法直接卸下，须创造模式或消耗背包中的崩坏结晶才能取下。
 */
public abstract class BoundCurioItem extends BaseCurioItem implements IBindable {
    /** 需要使用崩坏结晶才能卸下的 Curios 槽位标识 */
    private static final String[] BOUND_SLOT_IDS = {"tcc_3rd", "tcc_tdk"};

    public BoundCurioItem(Properties properties) {
        super(properties);
    }

    /** 3rd / tdk 系列饰品默认均为绑定物品。 */
    protected boolean isBoundItem() {
        return true;
    }

    /**
     * 3rd / tdk 绑定饰品统一死亡不掉落（ALWAYS_KEEP）。
     * 死亡时物品保留在饰品槽位中：玩家直接保留；女仆死亡则交由墓碑机制提取。
     * 需要在基类统一处理，各子类无需再覆写 getDropRule。
     */
    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    /** 公开判断当前饰品是否为绑定物品（需要崩坏结晶才能卸下），用于客户端 tooltip 展示。 */
    public boolean requiresCollapseCrystal() {
        return isBoundItem();
    }

    /** 判断当前槽位是否属于需要崩坏结晶才能卸下的绑定槽位。 */
    private boolean isBoundSlot(SlotContext context) {
        String slotId = context.identifier();
        for (String boundSlot : BOUND_SLOT_IDS) {
            if (boundSlot.equals(slotId)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCollapseCrystal(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isConsumableCrystal(stack)) {
                return true;
            }
        }
        return false;
    }

    /** 消耗玩家背包中的一个崩坏结晶（仅消耗干净的、未记录合成进度的结晶）。 */
    private void consumeCollapseCrystal(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isConsumableCrystal(stack)) {
                stack.shrink(1);
                return;
            }
        }
    }

    /**
     * 判断崩坏结晶是否为可消耗的「干净」水晶：参与过合成记录的水晶会在 NBT 写入记录条目，只用于合成，
     * 不能作为卸下消耗；无 NBT 条目者才可被消耗。
     */
    private static boolean isConsumableCrystal(ItemStack stack) {
        if (!(stack.getItem() instanceof CollapseCrystal)) return false;
        CompoundTag tag = stack.getTag();
        return tag == null || tag.isEmpty();
    }

    /**
     * 绑定饰品是否可以卸下：创造模式始终允许；绑定槽位 + （主人）背包有崩坏结晶允许；其他情况禁止。
     * 佩戴者为玩家本人或其女仆时同样约束（女仆归一化为其主人玩家，结晶从主人背包消耗）。
     */
    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        Player player = isBoundItem() ? MaidCompat.resolveOwnerPlayer(slotContext.entity()) : null;
        if (player == null) {
            return true;
        }
        // 佩戴实体死亡（如女仆死亡生成墓碑）时允许取出，否则绑定饰品会因 ALWAYS_KEEP 且无法
        // 提取而随实体一并消失。此时不再校验崩坏结晶。
        if (slotContext.entity() == null || slotContext.entity().isDeadOrDying()) {
            return true;
        }
        if (player.isCreative()) {
            return true;
        }
        if (isBoundSlot(slotContext) && hasCollapseCrystal(player)) {
            return true;
        }
        return false;
    }

    /** 绑定饰品在绑定槽位中非创造模式卸下时，自动消耗（主人背包中的）一个崩坏结晶。 */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        // 佩戴实体死亡时（如女仆死亡转入墓碑）不消耗崩坏结晶，避免主人被无形扣掉结晶。
        boolean deadOrDying = slotContext.entity() == null || slotContext.entity().isDeadOrDying();
        Player player = isBoundItem() ? MaidCompat.resolveOwnerPlayer(slotContext.entity()) : null;
        if (player != null && !player.isCreative() && isBoundSlot(slotContext) && !deadOrDying) {
            consumeCollapseCrystal(player);
        }
        super.onUnequip(slotContext, newStack, stack);
    }

    /** 绑定物品在首次装备时记录归属玩家，之后仅归属玩家可装备与卸下。 */
    private void bindIfNeeded(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) {
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getBoolean("IsBound")) {
            return;
        }
        tag.putBoolean("IsBound", true);
        tag.putString("BoundPlayer", player.getStringUUID());
        tag.putString("BoundPlayerName", player.getGameProfile().getName());
    }

    /**
     * 绑定物品允许归属玩家本人或其女仆装备；尚未绑定时允许任意实体装备。
     * 仅在能明确解析出归属玩家时才强制校验；暂时解析不到主人（如女仆经魂符放出、
     * 实体 NBT 恢复槽位阶段 owner 尚未写入）时放行，避免绑定饰品被误判为无效栈而丢弃。
     */
    private boolean canEquipOwner(SlotContext slotContext, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.getBoolean("IsBound")) {
            return true;
        }
        String bound = tag.getString("BoundPlayer");
        // 玩家本人，或该玩家的女仆（resolveOwnerPlayer 会把女仆归一化为其主人）。
        Player boundOwner = MaidCompat.resolveOwnerPlayer(slotContext.entity());
        if (boundOwner == null) {
            return true;
        }
        return boundOwner.getStringUUID().equals(bound);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        bindIfNeeded(slotContext, stack);
        ensureCapCounters(stack);
        super.onEquip(slotContext, prevStack, stack);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return canEquipOwner(slotContext, stack) && super.canEquip(slotContext, stack);
    }

    /** 初始化进化规则中声明的累积计数键，保证进度值可被后续读取。 */
    private static void ensureCapCounters(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        ResourceLocation key = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key == null) {
            return;
        }
        String itemId = key.toString();
        CompoundTag tag = stack.getOrCreateTag();
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.ATTRIBUTE, itemId)) {
            EvolutionRegistry.Progress progress = rule.progress;
            if (progress == null || progress.capCounterKey == null || progress.capCounterKey.isBlank()) {
                continue;
            }
            if (!tag.contains(progress.capCounterKey)) {
                tag.putDouble(progress.capCounterKey, 0.0);
            }
        }
    }

    /**
     * 显示当前虚数抗性（含进化规则累积值），与旧系列 tooltip 风格一致。
     */
    protected void appendImaginaryResistance(ItemStack stack, List<Component> tooltip) {
        CompoundTag tag = stack.getTag();
        double total = 1.0 + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        tooltip.add(Component.literal(""));
        tooltip.add(formatModifierTooltip(total, "%.0f",
                Component.translatable(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
    }

    /**
     * 显示绑定玩家信息（红色），仅对已绑定饰品展示。
     */
    protected void appendBoundPlayer(ItemStack stack, List<Component> tooltip) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.bound", boundPlayerName)
                    .withStyle(ChatFormatting.RED));
        }
    }
}
