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
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import javax.annotation.Nullable;
import java.util.*;


public abstract class BaseCurioItem extends Item implements ICurioItem {

    /**
     * 冲突表延迟到首次使用时构建。@EventBusSubscriber 的类扫描发生在配置注册/加载之前，
     * 若在静态初始化块里读取配置会抛 "Cannot get config value before config is loaded"。
     */
    private static final class ConflictMapHolder {
        private static final Map<String, Set<String>> MAP = build();

        private static Map<String, Set<String>> build() {
            Map<String, Set<String>> map = new HashMap<>();
            List<? extends String> conflictGroups = TaczCuriosConfig.COMMON.curioConflicts.get();

            for (String group : conflictGroups) {
                String[] items = group.split(",");
                Set<String> groupSet = new HashSet<>();
                for (String item : items) {
                    groupSet.add(item.trim());
                }

                for (String itemName : groupSet) {
                    Set<String> conflicts = map.computeIfAbsent(itemName, k -> new HashSet<>());
                    conflicts.addAll(groupSet);
                    conflicts.add(itemName);
                }
            }
            return map;
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
        String currentRegName = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        Set<String> conflictNames = ConflictMapHolder.MAP.getOrDefault(currentRegName, new HashSet<>());

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

    public final void refreshEffects(LivingEntity entity, ItemStack stack) {
        removeEffects(entity);
        applyEffects(entity, stack);
        AttachmentPropertyManager.postChangeEvent(entity, entity.getMainHandItem());
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
        String sign = value >= 0 ? "+" : "-";
        return Component.literal(sign + formatted + " ").append(attrName);
    }

    
    @Nullable
    public List<String> getWeaponTypeRestriction() {
        return null;
    }
}
