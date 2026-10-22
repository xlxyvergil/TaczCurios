package com.xlxyvergil.tcc.items;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public abstract class BaseCurioItem extends Item implements ICurioItem {

    /** 饰品互斥表。构造期读不到配置，所以首次使用时构建，配置载入/重载后重建。 */
    private static volatile Map<String, Set<String>> conflictMap;

    /** 按配置构建互斥表；配置还没载入时返回 null，调用方不要缓存这个结果。 */
    private static Map<String, Set<String>> buildConflictsFromConfig() {
        if (!TaczCuriosConfig.COMMON_SPEC.isLoaded()) {
            return null;
        }
        List<? extends String> conflictGroups = TaczCuriosConfig.COMMON.curioConflicts.get();

        Map<String, Set<String>> map = new HashMap<>();
        for (String group : conflictGroups) {
            String[] items = group.split(",");
            Set<String> groupSet = new HashSet<>();
            for (String item : items) {
                groupSet.add(item.trim());
            }

            for (String itemName : groupSet) {
                // 同一组的成员互相冲突（含自身），与旧实现语义一致
                map.computeIfAbsent(itemName, k -> new HashSet<>()).addAll(groupSet);
            }
        }
        return map;
    }

    /** 取互斥表；配置还没加载时返回空表且不缓存，等配置加载事件再重建。 */
    private static Map<String, Set<String>> conflicts() {
        Map<String, Set<String>> map = conflictMap;
        if (map != null) {
            return map;
        }
        map = buildConflictsFromConfig();
        if (map == null) {
            return Collections.emptyMap();
        }
        conflictMap = map;
        return map;
    }

    /** 配置加载/重载（含客户端接收服务端配置）后调用，重建互斥表。 */
    public static synchronized void reloadConflicts() {
        Map<String, Set<String>> map = buildConflictsFromConfig();
        if (map != null) {
            conflictMap = map;
        }
    }

    public BaseCurioItem(Properties properties) {
        super(properties);
    }

    
    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, boolean recentlyHit, ItemStack stack) {
        return DropRule.DEFAULT;
    }

    
    @Override
    public boolean canBeHurtBy(ItemStack stack, DamageSource source) {
        return false;
    }

    
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
    }

    
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        applyDeclaredEffects(entity, stack);
        
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
    }

    
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        removeModifiers(entity);
        
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        String slotId = slotContext.identifier();
        LivingEntity entity = (LivingEntity) slotContext.entity();
        if (!isAllowedInSlot(slotId, stack)) {
            return false;
        }
        if (isSameItemAlreadyWorn(entity, slotContext, stack)) {
            return false;
        }
        String currentRegName = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        Set<String> conflictNames = conflicts().getOrDefault(currentRegName, Collections.emptySet());

        if (!conflictNames.isEmpty()) {
            Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(entity);
            Boolean hasConflict = curiosInventory.map(inv -> {
                var handlerOpt = inv.getStacksHandler(slotId);
                if (handlerOpt.isPresent()) {
                    var handler = handlerOpt.orElse(null);
                    for (int i = 0; i < handler.getSlots(); i++) {
                        if (i == slotContext.index()) continue;
                        ItemStack slotStack = handler.getStacks().getStackInSlot(i);
                        if (!slotStack.isEmpty()) {
                            String slotRegName = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(slotStack.getItem()).toString();
                            if (conflictNames.contains(slotRegName)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }).orElse(false);

            if (hasConflict) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    /**
     * 属性行由各饰品在 {@code appendHoverText} 里手写（统一走 {@code formatModifierTooltip}），
     * 因此这里把 Curios 官方属性段整段关掉：
     * <ul>
     *   <li>官方段只能追加在 tooltip 末尾，位置不可控；</li>
     *   <li>全属性加成类饰品会把每个属性各展开一行，长达上百行；</li>
     *   <li>官方段按「佩戴时当前是否生效」求值，没拿枪时属性行会整段消失。</li>
     * </ul>
     * 属性面板里的来源图标不受影响（那边用的是 {@code getAttributeModifiers}）。
     */
    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, Item.TooltipContext context,
                                               ItemStack stack) {
        return List.of();
    }

    /**
     * 本模组的饰品只认「自己声明过的栏位」，即 data/curios/tags/item/&lt;slot&gt;.json 里登记的物品标签。
     * <p>
     * 为什么需要这个额外判断：Curios 的 {@code isStackValid} 里写死了
     * {@code id.equals("curio") || slots.contains(id) || slots.contains("curio")}，
     * 也就是**通用「curio」栏位接受任何饰品**（客户端/服务端都不例外）。
     * 本模组的栏位是分开设计的（Tacz饰品栏 / 逐火之蛾 / 神之键 / 空白之键），
     * 因此在这里把不属于该饰品的栏位（含通用「curio」）一律拒绝。
     */
    protected static boolean isAllowedInSlot(String identifier, ItemStack stack) {
        if (identifier == null || identifier.equals(CURIO_GENERIC_SLOT)) {
            return false;
        }
        return stack.is(slotTag(identifier));
    }

    private static TagKey<Item> slotTag(String identifier) {
        return SLOT_TAGS.computeIfAbsent(identifier,
            id -> ItemTags.create(ResourceLocation.fromNamespaceAndPath(CuriosApi.MODID, id)));
    }

    /** Curios 的通用栏位标识；该栏位按 Curios 设计接受一切饰品，本模组不使用。 */
    private static final String CURIO_GENERIC_SLOT = "curio";

    /** 栏位 → 物品标签缓存（Curios 同款：curios:&lt;slot&gt;）。 */
    private static final Map<String, TagKey<Item>> SLOT_TAGS = new ConcurrentHashMap<>();

    /**
     * 同一件饰品不允许同时占两个槽位。
     * <p>
     * 饰品的修饰符 ID 是按物品固定的（客户端来源图标也按物品匹配），装两件会互相覆盖、
     * 面板上只剩一个来源图标，因此直接禁止。
     */
    private static boolean isSameItemAlreadyWorn(LivingEntity entity, SlotContext slotContext, ItemStack stack) {
        if (entity == null || stack.isEmpty()) {
            return false;
        }
        return CuriosApi.getCuriosInventory(entity).map(inv -> {
            for (Map.Entry<String, ICurioStacksHandler> entry : inv.getCurios().entrySet()) {
                ICurioStacksHandler handler = entry.getValue();
                for (int i = 0; i < handler.getSlots(); i++) {
                    if (i == slotContext.index() && entry.getKey().equals(slotContext.identifier())) {
                        continue;
                    }
                    ItemStack worn = handler.getStacks().getStackInSlot(i);
                    if (!worn.isEmpty() && worn.getItem() == stack.getItem()) {
                        return true;
                    }
                }
            }
            return false;
        }).orElse(false);
    }

    public final void refreshEffects(LivingEntity entity, ItemStack stack) {
        rebuildEffects(entity, stack);
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
    }

    /**
     * 重建属性效果（先移除再重新施加），但不触发 TACZ 缓存变更事件。
     * 供批量刷新入口统一调用，避免逐个饰品重复触发昂贵的缓存重算。
     */
    public final void rebuildEffects(LivingEntity entity, ItemStack stack) {
        removeModifiers(entity);
        applyDeclaredEffects(entity, stack);
    }

    /** 仅重算属性修饰符，不清理旧效果、不触发 TACZ 事件。 */
    public final void reapplyEffects(LivingEntity entity, ItemStack stack) {
        applyDeclaredEffects(entity, stack);
    }

    /**
     * 声明该饰品在当前条件下应当施加的属性修饰符（<strong>只计算、不写入实体</strong>）。
     * <p>
     * 由 declareModifiers 经 AttributeHelper.collectDeclarations 的收集模式汇总而来。
     * 同一份数据有两个用途：一是真正施加，
     * 二是经 {@code CurioAttributeModifierEvent} 交给 Curios 官方通道，
     * 属性面板与工具提示据此把修饰符映射回佩戴的饰品栈（显示来源图标 / 官方属性行）。
     */
    public final Multimap<Holder<Attribute>, AttributeModifier> declaredEffects(LivingEntity entity, ItemStack stack) {
        if (entity == null || stack.isEmpty()) {
            return LinkedHashMultimap.create();
        }
        // 面板与工具提示会在一帧内多次查询同一件饰品；按（实体 + tick）缓存，避免逐帧重跑全部声明。
        DeclarationCache cache = DECLARATION_CACHE.get();
        if (cache.entity != entity || cache.tick != entity.tickCount) {
            cache.entity = entity;
            cache.tick = entity.tickCount;
            cache.byStack.clear();
        }
        Multimap<Holder<Attribute>, AttributeModifier> cached = cache.byStack.get(stack);
        if (cached != null) {
            return cached;
        }
        Multimap<Holder<Attribute>, AttributeModifier> declared =
            AttributeHelper.collectDeclarations(() -> declareModifiers(entity, stack));
        cache.byStack.put(stack, declared);
        return declared;
    }

    /**
     * 施加 declaredEffects 的结果，再执行声明之外的副作用。
     * <p>
     * 逐个按 ID 比对后再写入：值与运算方式都没变就跳过，避免切枪/击杀结算这类高频刷新
     * 无谓地替属性实例打脏标记（丢脏即重算 + 额外同步包）。
     */
    public final void applyDeclaredEffects(LivingEntity entity, ItemStack stack) {
        if (entity == null) {
            return;
        }
        Multimap<Holder<Attribute>, AttributeModifier> declared = declaredEffects(entity, stack);
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : declared.entries()) {
            applyIfChanged(entity, entry.getKey(), entry.getValue());
        }
        applySideEffects(entity, stack);
    }

    /** 用 Curios 同款原语写入单个修饰符；值/方式未变化时不写。 */
    private static void applyIfChanged(LivingEntity entity, Holder<Attribute> attribute, AttributeModifier modifier) {
        AttributeInstance instance = entity.getAttributes().getInstance(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier existing = instance.getModifier(modifier.id());
        if (existing != null
            && existing.amount() == modifier.amount()
            && existing.operation() == modifier.operation()) {
            return;
        }
        instance.addTransientModifier(modifier);
    }

    /**
     * 声明之外的副作用（直接下发药水效果等）写在这里。
     * 只在真正施加时调用；收集模式下不会执行，因此面板逐帧刷新不会重复触发。
     */
    protected void applySideEffects(LivingEntity entity, ItemStack stack) {
    }

    /** declaredEffects 的每线程缓存。客户端与集成服务端可能共用静态字段，必须按线程隔离。 */
    private static final ThreadLocal<DeclarationCache> DECLARATION_CACHE =
        ThreadLocal.withInitial(DeclarationCache::new);

    private static final class DeclarationCache {
        private LivingEntity entity;
        private int tick = Integer.MIN_VALUE;
        private final Map<ItemStack, Multimap<Holder<Attribute>, AttributeModifier>> byStack = new IdentityHashMap<>();
    }

    /**
     * 该饰品的属性效果是否已改为“事件驱动”：不再在 curioTick 中每 tick 重算，
     * 而由切枪、装备变更等事件统一刷新。默认 false，保持每 tick 刷新的原有行为。
     */
    public boolean isEventDriven() {
        return false;
    }

    /**
     * 该饰品的效果是否依赖其它属性的当前值。依赖则需在装备变更、击杀结算等事件里重算。默认 false。
     */
    public boolean dependsOnOtherAttributes() {
        return false;
    }

    /**
     * 声明该饰品想要施加的属性修饰符：条件判断（手持武器类型等）与数值计算都写在这里，
     * 通过 {@code AttributeHelper.applyModifier} 等方法「登记」。<strong>禁止副作用</strong>
     * ——收集模式下本方法只登记不施加，真正的写入由 applyDeclaredEffects 统一完成，
     * 药水效果之类的副作用请放 applySideEffects。
     */
    protected abstract void declareModifiers(LivingEntity entity, ItemStack stack);

    protected abstract void removeModifiers(LivingEntity entity);

    
    public boolean matchesRestriction(LivingEntity entity) {
        List<String> restriction = getWeaponTypeRestriction();
        if (restriction == null || restriction.isEmpty()) {
            return true; 
        }
        if (restriction.size() == 1 && "melee".equals(restriction.get(0))) {
            return GunTypeChecker.isHoldingMeleeWeapon(entity);
        }
        
        if (restriction.equals(GunTypeChecker.ALL_GUN_TYPES_LIST)) {
            return GunTypeChecker.isHoldingAnyGun(entity);
        }
        
        return GunTypeChecker.isHoldingConfiguredGunTypes(entity, restriction);
    }

    /**
     * 与 matchesRestriction 相同的武器类型限制，但<strong>主手或副手任一满足</strong>即可。
     *
     * 用于气息遮蔽 / 生命恢复这类"佩戴者持枪就生效"的被动：副手拿着狙击枪时不该被判成不合格。
     * 护甲/移速这类属性修饰符仍走 matchesRestriction（只看主手），属性面板口径不变。
     */
    protected boolean matchesRestrictionEitherHand(LivingEntity entity) {
        List<String> restriction = getWeaponTypeRestriction();
        if (restriction == null || restriction.isEmpty()) {
            return true;
        }
        if (restriction.size() == 1 && "melee".equals(restriction.get(0))) {
            return GunTypeChecker.isHoldingMeleeWeaponEitherHand(entity);
        }
        if (restriction.equals(GunTypeChecker.ALL_GUN_TYPES_LIST)) {
            return GunTypeChecker.isHoldingValidGunTypeEitherHand(entity, GunTypeChecker.ALL_GUN_TYPES);
        }
        return GunTypeChecker.isHoldingConfiguredGunTypesEitherHand(entity, restriction);
    }

    
    protected static MutableComponent formatModifierTooltip(double value, String valueFormat, Component attrName) {
        // 1.21.1 起 Apothic Attributes 不再提供 attributeslib.modifier.* 翻译键，
        // 与 NeoForge / Apotheosis 1.21 保持一致改用 neoforge.modifier.plus / take。
        // 注意 neoforge.modifier.take 的文案为 "%s %s"（不含负号），故数值需保留自身符号。
        String formatted = String.format(valueFormat, value);
        String key = value >= 0 ? "neoforge.modifier.plus" : "neoforge.modifier.take";
        return Component.translatable(key, formatted, attrName);
    }

    
    @Nullable
    public List<String> getWeaponTypeRestriction() {
        return null;
    }
}
