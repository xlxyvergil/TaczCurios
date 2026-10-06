package com.xlxyvergil.tcc.items.curios.kongbai;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.link.AttributeLinkData;
import com.xlxyvergil.tcc.link.AttributeLinkRegistry;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 空白之键：由数据包驱动的联动饰品，占用独立槽位 {@code tcc_kb}。
 * <p>
 * 每条规则自带「槽位 - 饰品id - 源属性 - 目标属性 - 倍率 - uuid」。
 * 命中规则由数据包全局表（{@link AttributeLinkRegistry}）与佩戴者当前槽位内容实时解析，
 * 并作为快照持久化到空白之键自身的 NBT（{@link #RULE_TAG}，另附规则表版本号）：
 * 快照让「卸下规则饰品」后仍能精确清除已写入的 uuid；数据包 reload 后由 {@link #refreshAll()}
 * 或兜底 tick 发现版本不符，用新规则表重算并覆盖快照。
 * <p>
 * 检测到命中规则时，读取实体源属性总值 × 倍率，以该规则独立的 uuid 作用到其目标属性；
 * 源属性变化由「装备变更 / 饰品槽变动」事件实时触发重算，另以 {@link #UPDATE_INTERVAL}
 * 的低频 tick 兜底，因此多条规则可同时生效且互不冲突。
 * 固定槽位没有命中规则时不允许安装；可自由拆下（不消耗材料），
 * 但装备仍绑定玩家（绑定与死亡不掉落逻辑由 {@link BoundCurioItem} 提供）。
 * 无数据包配置时无任何效果。
 */
public class KongbaiZhijian extends BoundCurioItem {

    private static final String LINK_NAME = "tcc.kongbai_zhijian.link";

    private static final String SELF_ID = TaczCurios.MODID + ":kongbai_zhijian";

    /**
     * 兜底重算周期（tick）：属性变化已由装备/饰品变动事件实时驱动，
     * 这里仅按 10 秒的低频兜底覆盖没有事件可监听的变化（如其它来源的属性修饰符）。
     */
    private static final int UPDATE_INTERVAL = 200;

    /** 命中的规则快照存放于空白之键自身 NBT 的该键下（compound 列表）。 */
    private static final String RULE_TAG = "TccKongbaiRule";

    /** 快照中记录的规则表版本号，用于判断数据包 reload 后是否需要重新解析。 */
    private static final String RULE_VERSION = "TccKongbaiRuleVersion";

    private static final String KEY_SLOT = "slot";
    private static final String KEY_CURIO = "curio";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_TARGET = "target";
    private static final String KEY_RATIO = "ratio";
    private static final String KEY_OPERATION = "operation";
    private static final String KEY_UUID = "uuid";

    public KongbaiZhijian(Properties properties) {
        super(properties);
    }

    /**
     * 仅玩家可用，且至少命中一条规则时才允许安装。
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player)) {
            return false;
        }
        if (!super.canEquip(slotContext, stack)) {
            return false;
        }
        return !resolveRules(slotContext.entity()).isEmpty();
    }

    /**
     * 可自由拆下，不消耗任何材料；装备仍绑定玩家（由 {@link BoundCurioItem} 处理）。
     */
    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    /**
     * Tooltip：按佩戴者当前槽位实时解析命中的规则，逐条显示（来源饰品、源属性 × 倍率 → 目标属性）
     * 与当前动态加成；未佩戴时不展示具体规则（规则取决于佩戴者其它槽位内容）。
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        LivingEntity wearer = null;
        if (level != null && level.isClientSide()) {
            LivingEntity candidate = TaczCuriosClientTooltip.resolveWearer(stack);
            if (candidate != null && isEquipped(candidate)) {
                wearer = candidate;
            }
        }

        if (wearer == null) {
            tooltip.add(Component.translatable("item.tcc.kongbai_zhijian.unworn")
                    .withStyle(ChatFormatting.DARK_GRAY));
        } else {
            List<AttributeLinkData> rules = resolveRules(wearer);
            if (rules.isEmpty()) {
                tooltip.add(Component.translatable("item.tcc.kongbai_zhijian.unbound")
                        .withStyle(ChatFormatting.DARK_GRAY));
            } else {
                for (AttributeLinkData rule : rules) {
                    tooltip.add(Component.translatable("item.tcc.kongbai_zhijian.bound_curio", describeItemName(rule.curio()))
                            .withStyle(ChatFormatting.GRAY));

                    tooltip.add(Component.translatable("item.tcc.kongbai_zhijian.link_rule",
                                    describeAttributeName(rule.source()), formatRatio(rule.ratio()), describeAttributeName(rule.target()))
                            .withStyle(ChatFormatting.GRAY));

                    Attribute source = AttributeHelper.resolveAttribute(rule.source());
                    if (source != null) {
                        double bonus = wearer.getAttributeValue(source) * rule.ratio();
                        boolean addition = rule.operation() == AttributeModifier.Operation.ADDITION;
                        tooltip.add(formatModifierTooltip(addition ? bonus : bonus * 100,
                                        addition ? "%.2f" : "%.0f%%",
                                        describeAttributeName(rule.target()))
                                .withStyle(ChatFormatting.BLUE));
                    }
                }
            }
        }

        tooltip.add(Component.literal(""));
        appendBoundPlayer(stack, tooltip);
    }

    private static boolean isEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
                s -> s.getItem() instanceof KongbaiZhijian).isEmpty();
    }

    private Component describeItemName(String curioId) {
        ResourceLocation loc = ResourceLocation.tryParse(curioId);
        Item item = loc == null ? null : ForgeRegistries.ITEMS.getValue(loc);
        return item == null ? Component.literal(curioId) : new ItemStack(item).getHoverName();
    }

    private Component describeAttributeName(String attributeId) {
        Attribute attribute = AttributeHelper.resolveAttribute(attributeId);
        return attribute == null
                ? Component.literal(attributeId)
                : Component.translatable(attribute.getDescriptionId());
    }

    private String formatRatio(double ratio) {
        double percent = ratio * 100.0;
        return percent == Math.floor(percent)
                ? String.format("%.0f%%", percent)
                : String.format("%.1f%%", percent);
    }

    @Override
    protected void applyEffects(LivingEntity entity, ItemStack stack) {
        if (entity == null) {
            return;
        }
        // 先按物品上的旧快照清除上一轮写入的 uuid，再解析并覆盖快照，
        // 这样数据包热改/热删 uuid 后也不会残留旧修饰符
        clearModifiers(entity, readRules(stack));
        List<AttributeLinkData> rules = resolveRules(entity);
        writeRules(stack, rules);
        for (AttributeLinkData rule : rules) {
            AttributeHelper.registerSourceItem(rule.uuid(), stack.getItem());
            applyValue(entity, rule);
        }
    }

    @Override
    protected void removeEffects(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        // 不携带 stack 时的兜底清除：按当前规则表逐条移除（uuid 已不在表中者由 applyEffects 处理）
        clearModifiers(entity, null);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity != null) {
            // 卸下时 stack（及其 NBT 快照）仍可用，据此精确清除本轮命中的 uuid
            clearModifiers(entity, readRules(stack));
        }
        super.onUnequip(slotContext, newStack, stack);
    }

    /**
     * 兜底：属性变化主要由事件实时驱动，这里仅每 {@link #UPDATE_INTERVAL} tick 结算一次，
     * 覆盖没有事件可监听的属性来源；同时校验快照版本，发现数据包已 reload 就重新解析。
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        if (entity.tickCount % UPDATE_INTERVAL != 0) {
            return;
        }
        if (!hasSnapshot(stack) || isSnapshotStale(stack)) {
            // 快照缺失（首次 tick、世界重载等）或规则表已变化：完整重算并覆盖快照
            refreshEffects(entity, stack);
            return;
        }
        for (AttributeLinkData rule : readRules(stack)) {
            applyValue(entity, rule);
        }
    }

    /**
     * 数据包 reload 后用新规则表对在线玩家即时重算一次：先按物品 NBT 快照清除旧 uuid，
     * 再按新规则解析并覆盖快照，从而不残留旧数据。需在服务端主线程调用。
     * <p>
     * 非玩家实体（女仆等）没有遍历入口，靠自身兜底 tick 发现快照版本过期后自动重算。
     */
    public static void refreshAll() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ItemStack stack = CurioSearchHelper.findFirstEquippedStack(player,
                    s -> s.getItem() instanceof KongbaiZhijian);
            if (stack.getItem() instanceof KongbaiZhijian item) {
                item.refreshEffects(player, stack);
            }
        }
    }

    /** 把当前命中的规则写入物品 NBT 并记录规则表版本；无命中时写入空列表（保留快照标记）。 */
    private static void writeRules(ItemStack stack, List<AttributeLinkData> rules) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        ListTag list = new ListTag();
        for (AttributeLinkData rule : rules) {
            CompoundTag entry = new CompoundTag();
            entry.putString(KEY_SLOT, rule.slot());
            entry.putString(KEY_CURIO, rule.curio());
            entry.putString(KEY_SOURCE, rule.source());
            entry.putString(KEY_TARGET, rule.target());
            entry.putDouble(KEY_RATIO, rule.ratio());
            entry.putString(KEY_OPERATION, rule.operation().name());
            entry.putUUID(KEY_UUID, rule.uuid());
            list.add(entry);
        }
        CompoundTag tag = stack.getOrCreateTag();
        tag.put(RULE_TAG, list);
        tag.putInt(RULE_VERSION, AttributeLinkRegistry.getVersion());
    }

    /** 读取物品 NBT 中记录的命中规则；无快照时返回空列表。 */
    private static List<AttributeLinkData> readRules(ItemStack stack) {
        CompoundTag tag = stack == null ? null : stack.getTag();
        if (tag == null || !tag.contains(RULE_TAG, Tag.TAG_LIST)) {
            return List.of();
        }
        ListTag list = tag.getList(RULE_TAG, Tag.TAG_COMPOUND);
        List<AttributeLinkData> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            AttributeLinkData data = readRule(list.getCompound(i));
            if (data != null) {
                result.add(data);
            }
        }
        return result;
    }

    @Nullable
    private static AttributeLinkData readRule(CompoundTag entry) {
        try {
            return new AttributeLinkData(
                    entry.getString(KEY_SLOT),
                    entry.getString(KEY_CURIO),
                    entry.getString(KEY_SOURCE),
                    entry.getString(KEY_TARGET),
                    entry.getDouble(KEY_RATIO),
                    parseOperation(entry.getString(KEY_OPERATION)),
                    entry.getUUID(KEY_UUID));
        } catch (Exception e) {
            // 快照损坏（字段缺失等）时忽略该条，避免影响其它规则
            return null;
        }
    }

    private static AttributeModifier.Operation parseOperation(String name) {
        for (AttributeModifier.Operation op : AttributeModifier.Operation.values()) {
            if (op.name().equals(name)) {
                return op;
            }
        }
        return AttributeModifier.Operation.MULTIPLY_BASE;
    }

    /** 是否已写入过快照（空列表也算，用于区分「未初始化」与「已解析但无命中」）。 */
    private static boolean hasSnapshot(ItemStack stack) {
        CompoundTag tag = stack == null ? null : stack.getTag();
        return tag != null && tag.contains(RULE_TAG, Tag.TAG_LIST);
    }

    /** 快照记录的规则表版本与当前是否不一致（数据包 reload 后即为过期）。 */
    private static boolean isSnapshotStale(ItemStack stack) {
        CompoundTag tag = stack == null ? null : stack.getTag();
        return tag == null || tag.getInt(RULE_VERSION) != AttributeLinkRegistry.getVersion();
    }

    /**
     * 按单条规则读取实体源属性总值并写入其目标属性；数值未变化时不重复写入。
     */
    private static void applyValue(LivingEntity entity, AttributeLinkData rule) {
        Attribute target = AttributeHelper.resolveAttribute(rule.target());
        if (target == null) {
            return;
        }
        Attribute source = AttributeHelper.resolveAttribute(rule.source());
        // 源属性与目标属性相同会自我叠加，跳过以避免递归放大
        if (source == null || source == target) {
            AttributeHelper.removeModifier(entity, target, rule.uuid());
            return;
        }

        double value = entity.getAttributeValue(source) * rule.ratio();
        if (value == 0) {
            AttributeHelper.removeModifier(entity, target, rule.uuid());
            return;
        }

        AttributeInstance instance = AttributeHelper.getInstance(entity, target);
        if (instance != null) {
            AttributeModifier existing = instance.getModifier(rule.uuid());
            if (existing != null && existing.getAmount() == value && existing.getOperation() == rule.operation()) {
                return;
            }
        }

        AttributeHelper.applyModifier(entity, target, value, rule.uuid(), LINK_NAME, rule.operation());
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
    }

    /**
     * 遍历规则表中所有槽位，收集命中「槽位内存在对应饰品」的全部规则。
     */
    private static List<AttributeLinkData> resolveRules(LivingEntity entity) {
        if (entity == null) {
            return List.of();
        }
        ICuriosItemHandler inventory = CuriosApi.getCuriosInventory(entity).resolve().orElse(null);
        if (inventory == null) {
            return List.of();
        }

        List<AttributeLinkData> result = new ArrayList<>();
        for (var entry : AttributeLinkRegistry.getLinksBySlot().entrySet()) {
            List<AttributeLinkData> links = entry.getValue();
            if (links.isEmpty()) {
                continue;
            }
            ICurioStacksHandler handler = inventory.getStacksHandler(entry.getKey()).orElse(null);
            if (handler == null || handler.getSlots() <= 0) {
                continue;
            }

            Set<String> presentIds = new HashSet<>();
            for (int i = 0; i < handler.getStacks().getSlots(); i++) {
                ItemStack slotStack = handler.getStacks().getStackInSlot(i);
                if (slotStack.isEmpty()) {
                    continue;
                }
                ResourceLocation key = ForgeRegistries.ITEMS.getKey(slotStack.getItem());
                if (key != null) {
                    presentIds.add(key.toString());
                }
            }
            if (presentIds.isEmpty()) {
                continue;
            }

            for (AttributeLinkData link : links) {
                if (!SELF_ID.equals(link.curio()) && presentIds.contains(link.curio())) {
                    result.add(link);
                }
            }
        }
        return result;
    }

    /**
     * 清除 entity 上由「传入的快照规则 ∪ 当前数据包规则表全部规则」对应的修饰符。
     * <p>
     * 并集保证：规则被数据包热删后，其 uuid 只存在于物品快照中也能被移除。
     *
     * @param cached 上一轮写入快照的规则，可为 {@code null}
     */
    private static void clearModifiers(LivingEntity entity, @Nullable List<AttributeLinkData> cached) {
        if (entity == null) {
            return;
        }
        Set<UUID> removed = new HashSet<>();
        if (cached != null) {
            for (AttributeLinkData rule : cached) {
                if (!removed.add(rule.uuid())) {
                    continue;
                }
                Attribute target = AttributeHelper.resolveAttribute(rule.target());
                if (target != null) {
                    AttributeHelper.removeModifier(entity, target, rule.uuid());
                }
            }
        }
        for (List<AttributeLinkData> links : AttributeLinkRegistry.getLinksBySlot().values()) {
            for (AttributeLinkData rule : links) {
                if (!removed.add(rule.uuid())) {
                    continue;
                }
                Attribute target = AttributeHelper.resolveAttribute(rule.target());
                if (target != null) {
                    AttributeHelper.removeModifier(entity, target, rule.uuid());
                }
            }
        }
    }
}
