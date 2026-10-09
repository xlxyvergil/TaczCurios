package com.xlxyvergil.tcc.items;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import javax.annotation.Nullable;
import java.util.*;


public abstract class BaseCurioItem extends Item implements ICurioItem, Vanishable {
    
    private static final Map<String, Set<String>> CONFLICT_MAP = new HashMap<>();

    static {
        loadConflictsFromConfig();
    }

    private static void loadConflictsFromConfig() {
        List<? extends String> conflictGroups = TaczCuriosConfig.COMMON.curioConflicts.get();

        for (String group : conflictGroups) {
            String[] items = group.split(",");
            Set<String> groupSet = new HashSet<>();
            for (String item : items) {
                groupSet.add(item.trim());
            }

            
            for (String itemName : groupSet) {
                Set<String> conflicts = CONFLICT_MAP.computeIfAbsent(itemName, k -> new HashSet<>());
                conflicts.addAll(groupSet);
                conflicts.add(itemName);
            }
        }
    }

    public BaseCurioItem(Properties properties) {
        super(properties);
    }


    
    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.DEFAULT;
    }

    
    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return false;
    }

    
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
    }

    
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        applyEffects(entity, stack);
        
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
    }

    
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        removeEffects(entity);
        
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        String slotId = slotContext.identifier();
        LivingEntity entity = (LivingEntity) slotContext.entity();
        String currentRegName = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
        Set<String> conflictNames = CONFLICT_MAP.getOrDefault(currentRegName, new HashSet<>());

        if (!conflictNames.isEmpty()) {
            LazyOptional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(entity);
            Boolean hasConflict = curiosInventory.map(inv -> {
                var handlerOpt = inv.getStacksHandler(slotId);
                if (handlerOpt.isPresent()) {
                    var handler = handlerOpt.orElse(null);
                    for (int i = 0; i < handler.getSlots(); i++) {
                        if (i == slotContext.index()) continue;
                        ItemStack slotStack = handler.getStacks().getStackInSlot(i);
                        if (!slotStack.isEmpty()) {
                            String slotRegName = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(slotStack.getItem()).toString();
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

    public final void refreshEffects(LivingEntity entity, ItemStack stack) {
        rebuildEffects(entity, stack);
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
    }

    /**
     * 重建属性效果（先移除再重新施加），但不触发 TACZ 缓存变更事件。
     * 供批量刷新入口统一调用，避免逐个饰品重复触发昂贵的缓存重算。
     */
    public final void rebuildEffects(LivingEntity entity, ItemStack stack) {
        removeEffects(entity);
        applyEffects(entity, stack);
    }

    /** 仅重算属性修饰符，不清理旧效果、不触发 TACZ 事件。 */
    public final void reapplyEffects(LivingEntity entity, ItemStack stack) {
        applyEffects(entity, stack);
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

    protected abstract void applyEffects(LivingEntity entity, ItemStack stack);

    protected abstract void removeEffects(LivingEntity entity);

    
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

    
    protected static MutableComponent formatModifierTooltip(double value, String valueFormat, Component attrName) {
        String formatted = String.format(valueFormat, value >= 0 ? value : -value);
        String key = value >= 0 ? "attributeslib.modifier.plus" : "attributeslib.modifier.take";
        return Component.translatable(key, formatted, attrName);
    }

    
    @Nullable
    public List<String> getWeaponTypeRestriction() {
        return null;
    }
}
