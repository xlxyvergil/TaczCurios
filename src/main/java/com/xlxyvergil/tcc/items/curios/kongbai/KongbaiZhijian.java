package com.xlxyvergil.tcc.items.curios.kongbai;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.BoundCurioItem;
import com.xlxyvergil.tcc.link.AttributeLinkData;
import com.xlxyvergil.tcc.link.AttributeLinkRegistry;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.client.TaczCuriosClientTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 空白之键：由数据包驱动的联动饰品，占用独立槽位 {@code tcc_kb}。
 * <p>
 * 安装时读取一次数据包中固定槽位（slot）的 1 号槽位，命中规则后把规则记入饰品自身，
 * 之后持续按该规则读取实体属性总值 × 比例，作用于 TAA 通用枪伤。
 * 固定槽位没有命中规则的饰品时不允许安装；可自由拆下（不消耗材料），
 * 但装备仍绑定玩家（绑定与死亡不掉落逻辑由 {@link BoundCurioItem} 提供）。
 * 无数据包配置时无任何效果。
 */
public class KongbaiZhijian extends BoundCurioItem {

    /** 属性修饰符 UUID，单条规则故固定一个。 */
    private static final UUID LINK_UUID = UUID.fromString("8b3f2c14-6d5a-4e79-9c1b-2f4a6d8e0b31");

    private static final String LINK_NAME = "tcc.kongbai_zhijian.link";

    private static final String SELF_ID = TaczCurios.MODID + ":kongbai_zhijian";

    /** 已确定规则在饰品 NBT 中的键。 */
    private static final String RULE_TAG = "TccKongbaiRule";

    /** 每隔多少 tick 重新按规则结算一次数值。 */
    private static final int UPDATE_INTERVAL = 5;

    public KongbaiZhijian(Properties properties) {
        super(properties);
    }

    /**
     * 仅当固定槽位 1 号槽位存在命中规则的饰品时才允许安装。
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (!super.canEquip(slotContext, stack)) {
            return false;
        }
        return resolveSlotRule(slotContext.entity()) != null;
    }

    /**
     * 可自由拆下，不消耗任何材料；装备仍绑定玩家（由 {@link BoundCurioItem} 处理）。
     */
    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    /**
     * Tooltip：显示已绑定的饰品、联动规则（来源属性 × 比例）与当前动态加成。
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));

        AttributeLinkData link = readRule(stack);
        if (link == null) {
            tooltip.add(Component.translatable("item.tcc.kongbai_zhijian.unbound")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        // 已绑定饰品
        tooltip.add(Component.translatable("item.tcc.kongbai_zhijian.bound_curio", describeItemName(link.curio()))
                .withStyle(ChatFormatting.GRAY));

        // 联动规则：来源属性总值 × 比例
        tooltip.add(Component.translatable("item.tcc.kongbai_zhijian.link_rule",
                        describeAttributeName(link.attribute()), formatRatio(link.ratio()))
                .withStyle(ChatFormatting.GRAY));

        // 当前动态加成：与神之键一致，实时读取佩戴者源属性总值 × 比例后用 formatModifierTooltip 显示
        double bonus = 0;
        boolean equipped = false;
        if (level != null && level.isClientSide()) {
            LivingEntity wearer = TaczCuriosClientTooltip.resolveWearer(stack);
            if (wearer != null && isEquipped(wearer)) {
                Attribute source = AttributeHelper.resolveAttribute(link.attribute());
                if (source != null) {
                    bonus = wearer.getAttributeValue(source) * link.ratio();
                    equipped = true;
                }
            }
        }
        if (equipped) {
            boolean addition = link.operation() == AttributeModifier.Operation.ADDITION;
            tooltip.add(formatModifierTooltip(addition ? bonus : bonus * 100,
                            addition ? "%.2f" : "%.0f%%",
                            Component.translatable(AttributeHelper.BULLET_GUNDAMAGE.getDescriptionId()))
                    .withStyle(ChatFormatting.BLUE));
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
        AttributeLinkData link = resolveSlotRule(entity);
        if (link == null) {
            AttributeHelper.removeModifier(entity, AttributeHelper.BULLET_GUNDAMAGE, LINK_UUID);
            return;
        }
        writeRule(stack, link);
        AttributeHelper.registerSourceItem(LINK_UUID, stack.getItem());
        applyValue(entity, link);
    }

    @Override
    protected void removeEffects(LivingEntity entity) {
        AttributeHelper.removeModifier(entity, AttributeHelper.BULLET_GUNDAMAGE, LINK_UUID);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        if (entity.tickCount % UPDATE_INTERVAL != 0) {
            return;
        }
        AttributeLinkData link = readRule(stack);
        if (link == null) {
            AttributeHelper.removeModifier(entity, AttributeHelper.BULLET_GUNDAMAGE, LINK_UUID);
            return;
        }
        applyValue(entity, link);
    }

    /**
     * 按已确定的规则读取实体属性总值并写入通用枪伤；数值未变化时不重复写入。
     */
    private void applyValue(LivingEntity entity, AttributeLinkData link) {
        Attribute source = AttributeHelper.resolveAttribute(link.attribute());
        if (source == null || isGunDamageAttribute(source)) {
            AttributeHelper.removeModifier(entity, AttributeHelper.BULLET_GUNDAMAGE, LINK_UUID);
            return;
        }

        double value = entity.getAttributeValue(source) * link.ratio();
        if (value == 0) {
            AttributeHelper.removeModifier(entity, AttributeHelper.BULLET_GUNDAMAGE, LINK_UUID);
            return;
        }

        AttributeInstance instance = AttributeHelper.getInstance(entity, AttributeHelper.BULLET_GUNDAMAGE);
        if (instance != null) {
            AttributeModifier existing = instance.getModifier(LINK_UUID);
            if (existing != null && existing.getAmount() == value && existing.getOperation() == link.operation()) {
                return;
            }
        }

        AttributeHelper.applyModifier(entity, AttributeHelper.BULLET_GUNDAMAGE, value, LINK_UUID, LINK_NAME, link.operation());
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
    }

    /**
     * 读取固定槽位 1 号槽位的饰品，并按 id 直接查表得到规则；未命中返回 null。
     */
    private AttributeLinkData resolveSlotRule(LivingEntity entity) {
        if (entity == null) {
            return null;
        }
        String slot = AttributeLinkRegistry.getSlot();
        if (slot == null) {
            return null;
        }

        ICurioStacksHandler handler = CuriosApi.getCuriosInventory(entity)
                .resolve()
                .flatMap(inv -> inv.getStacksHandler(slot))
                .orElse(null);
        if (handler == null || handler.getSlots() <= 0) {
            return null;
        }

        ItemStack target = handler.getStacks().getStackInSlot(0);
        if (target.isEmpty()) {
            return null;
        }
        String targetId = ForgeRegistries.ITEMS.getKey(target.getItem()).toString();
        if (SELF_ID.equals(targetId)) {
            return null;
        }
        return AttributeLinkRegistry.getLink(targetId);
    }

    private void writeRule(ItemStack stack, AttributeLinkData link) {
        CompoundTag rule = new CompoundTag();
        rule.putString("curio", link.curio());
        rule.putString("attribute", link.attribute());
        rule.putDouble("ratio", link.ratio());
        rule.putString("operation", link.operation().name());
        stack.getOrCreateTag().put(RULE_TAG, rule);
    }

    private AttributeLinkData readRule(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(RULE_TAG, CompoundTag.TAG_COMPOUND)) {
            return null;
        }
        CompoundTag rule = tag.getCompound(RULE_TAG);
        String curio = rule.getString("curio");
        String attribute = rule.getString("attribute");
        if (curio.isEmpty() || attribute.isEmpty()) {
            return null;
        }
        AttributeModifier.Operation operation = AttributeModifier.Operation.MULTIPLY_BASE;
        try {
            operation = AttributeModifier.Operation.valueOf(rule.getString("operation"));
        } catch (IllegalArgumentException ignored) {
        }
        return new AttributeLinkData(curio, attribute, rule.getDouble("ratio"), operation);
    }

    /**
     * TAA 枪伤系属性不可作为来源，避免自乘递归。
     */
    private boolean isGunDamageAttribute(Attribute attribute) {
        return attribute == AttributeHelper.BULLET_GUNDAMAGE
                || attribute == AttributeHelper.BULLET_GUNDAMAGE_PISTOL
                || attribute == AttributeHelper.BULLET_GUNDAMAGE_RIFLE
                || attribute == AttributeHelper.BULLET_GUNDAMAGE_SHOTGUN
                || attribute == AttributeHelper.BULLET_GUNDAMAGE_SNIPER
                || attribute == AttributeHelper.BULLET_GUNDAMAGE_SMG
                || attribute == AttributeHelper.BULLET_GUNDAMAGE_LMG
                || attribute == AttributeHelper.BULLET_GUNDAMAGE_LAUNCHER;
    }
}
