package com.xlxyvergil.tcc.util;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.fusedcritical.attribute.FusedAttributes;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.UUID;

public class AttributeHelper {

    /**
     * 声明收集栈：栈非空时处于「只登记不施加」模式。饰品的 {@code declareModifiers} 在这层里运行，
     * 由 {@link #collectDeclarations} 收集成一份修饰符表交给 Curios 官方通道（CurioAttributeModifierEvent
     * → getAttributeModifiers），属性面板因此能按 Curios 的规则把修饰符映射回佩戴的饰品栈显示来源图标。
     * <p>
     * 用栈而不是单个变量，是为了容忍嵌套调用（声明期间又触发了别的饰品计算）。
     */
    private static final ThreadLocal<ArrayDeque<Multimap<Attribute, AttributeModifier>>> DECLARATION_SINK =
        ThreadLocal.withInitial(ArrayDeque::new);

    /** 当前线程是否处于声明收集模式。声明代码里禁止做收集以外的副作用。 */
    public static boolean isCollecting() {
        return !DECLARATION_SINK.get().isEmpty();
    }

    public static Multimap<Attribute, AttributeModifier> collectDeclarations(Runnable declarations) {
        ArrayDeque<Multimap<Attribute, AttributeModifier>> stack = DECLARATION_SINK.get();
        Multimap<Attribute, AttributeModifier> sink = LinkedHashMultimap.create();
        stack.push(sink);
        try {
            declarations.run();
        } finally {
            stack.pop();
            if (stack.isEmpty()) {
                DECLARATION_SINK.remove();
            }
        }
        return sink;
    }

    /** 收集模式下登记一个修饰符并返回 true；非收集模式返回 false，交给调用方真正写入实体。 */
    private static boolean collect(Attribute attribute, AttributeModifier modifier) {
        ArrayDeque<Multimap<Attribute, AttributeModifier>> stack = DECLARATION_SINK.get();
        if (stack.isEmpty() || attribute == null) {
            return false;
        }
        stack.peek().put(attribute, modifier);
        return true;
    }

    
    public static final Attribute ATTACK_DAMAGE = Attributes.ATTACK_DAMAGE;
    
    
    public static final Attribute MAX_HEALTH = Attributes.MAX_HEALTH;
    
    
    public static final Attribute ARMOR = Attributes.ARMOR;
    
    
    public static final Attribute MOVEMENT_SPEED = Attributes.MOVEMENT_SPEED;
    
    
    public static final Attribute LUCK = Attributes.LUCK;
    
    
    
    
    public static final Attribute ENTITY_REACH = ForgeMod.ENTITY_REACH.get();
    
    
    public static final Attribute BLOCK_REACH = ForgeMod.BLOCK_REACH.get();
    
    
    
    
    public static final Attribute CRIT_CHANCE = FusedAttributes.CRIT_CHANCE.get();
    
    
    public static final Attribute CRIT_DAMAGE = FusedAttributes.CRIT_DAMAGE.get();
    
    
    public static final Attribute LIFE_STEAL = TccAttributes.LIFE_STEAL.get();
    
    
    public static final Attribute OVERHEAL = TccAttributes.OVERHEAL.get();
    
    
    
    
    public static final Attribute BULLET_GUNDAMAGE = EntityAttributeRegistry.BULLET_GUNDAMAGE.get();
    
    
    public static final Attribute ADS_TIME = EntityAttributeRegistry.ADS_TIME.get();
    
    
    public static final Attribute AMMO_SPEED = EntityAttributeRegistry.AMMO_SPEED.get();
    
    
    public static final Attribute ARMOR_IGNORE = EntityAttributeRegistry.ARMOR_IGNORE.get();
    
    
    public static final Attribute EFFECTIVE_RANGE = EntityAttributeRegistry.EFFECTIVE_RANGE.get();
    
    
    public static final Attribute EXPLOSION_RADIUS = EntityAttributeRegistry.EXPLOSION_RADIUS.get();
    
    
    public static final Attribute EXPLOSION_DAMAGE = EntityAttributeRegistry.EXPLOSION_DAMAGE.get();
    
    
    public static final Attribute EXPLOSION_KNOCKBACK = EntityAttributeRegistry.EXPLOSION_KNOCKBACK.get();
    
    
    public static final Attribute EXPLOSION_DESTROY_BLOCK = EntityAttributeRegistry.EXPLOSION_DESTROY_BLOCK.get();
    
    
    public static final Attribute EXPLOSION_DELAY = EntityAttributeRegistry.EXPLOSION_DELAY.get();
    
    
    public static final Attribute EXPLOSION_ENABLED = EntityAttributeRegistry.EXPLOSION_ENABLED.get();
    
    
    public static final Attribute MOVE_SPEED = EntityAttributeRegistry.MOVE_SPEED.get();
    
    
    public static final Attribute HEADSHOT_MULTIPLIER = EntityAttributeRegistry.HEADSHOT_MULTIPLIER.get();
    
    
    public static final Attribute IGNITE = EntityAttributeRegistry.IGNITE.get();
    
    
    public static final Attribute INACCURACY = EntityAttributeRegistry.INACCURACY.get();
    
    
    public static final Attribute INACCURACY_STAND = EntityAttributeRegistry.INACCURACY_STAND.get();
    
    
    public static final Attribute INACCURACY_MOVE = EntityAttributeRegistry.INACCURACY_MOVE.get();
    
    
    public static final Attribute INACCURACY_SNEAK = EntityAttributeRegistry.INACCURACY_SNEAK.get();
    
    
    public static final Attribute INACCURACY_LIE = EntityAttributeRegistry.INACCURACY_LIE.get();
    
    
    public static final Attribute INACCURACY_AIM = EntityAttributeRegistry.INACCURACY_AIM.get();
    
    
    public static final Attribute KNOCKBACK = EntityAttributeRegistry.KNOCKBACK.get();
    
    
    public static final Attribute PIERCE = EntityAttributeRegistry.PIERCE.get();
    
    
    public static final Attribute RECOIL = EntityAttributeRegistry.RECOIL.get();
    
    
    public static final Attribute RECOIL_PITCH = EntityAttributeRegistry.RECOIL_PITCH.get();
    
    
    public static final Attribute RECOIL_YAW = EntityAttributeRegistry.RECOIL_YAW.get();
    
    
    public static final Attribute ROUNDS_PER_MINUTE = EntityAttributeRegistry.ROUNDS_PER_MINUTE.get();
    
    
    public static final Attribute SILENCE = EntityAttributeRegistry.SILENCE.get();
    
    
    public static final Attribute WEIGHT = EntityAttributeRegistry.WEIGHT.get();
    
    
    public static final Attribute BULLET_COUNT = EntityAttributeRegistry.BULLET_COUNT.get();
    
    
    public static final Attribute MAGAZINE_CAPACITY = EntityAttributeRegistry.MAGAZINE_CAPACITY.get();
    
    
    public static final Attribute RELOAD_TIME = EntityAttributeRegistry.RELOAD_TIME.get();
    
    
    public static final Attribute MELEE_DAMAGE = EntityAttributeRegistry.MELEE_DAMAGE.get();
    
    
    public static final Attribute MELEE_DISTANCE = EntityAttributeRegistry.MELEE_DISTANCE.get();
    
    
    
    
    public static final Attribute HEAT_MAX = EntityAttributeRegistry.HEAT_MAX.get();
    
    
    public static final Attribute HEAT_COOLING = EntityAttributeRegistry.HEAT_COOLING.get();
    
    
    public static final Attribute BULLET_GUNDAMAGE_PISTOL = EntityAttributeRegistry.BULLET_GUNDAMAGE_PISTOL.get();
    public static final Attribute BULLET_GUNDAMAGE_RIFLE = EntityAttributeRegistry.BULLET_GUNDAMAGE_RIFLE.get();
    public static final Attribute BULLET_GUNDAMAGE_SHOTGUN = EntityAttributeRegistry.BULLET_GUNDAMAGE_SHOTGUN.get();
    public static final Attribute BULLET_GUNDAMAGE_SNIPER = EntityAttributeRegistry.BULLET_GUNDAMAGE_SNIPER.get();
    public static final Attribute BULLET_GUNDAMAGE_SMG = EntityAttributeRegistry.BULLET_GUNDAMAGE_SMG.get();
    public static final Attribute BULLET_GUNDAMAGE_LMG = EntityAttributeRegistry.BULLET_GUNDAMAGE_LMG.get();
    public static final Attribute BULLET_GUNDAMAGE_LAUNCHER = EntityAttributeRegistry.BULLET_GUNDAMAGE_LAUNCHER.get();

    public static Attribute resolveAttribute(String attributeId) {
        if (attributeId == null || attributeId.isBlank()) {
            return null;
        }
        ResourceLocation loc = ResourceLocation.tryParse(attributeId);
        if (loc == null) return null;
        return ForgeRegistries.ATTRIBUTES.getValue(loc);
    }
    
    public static AttributeInstance getInstance(LivingEntity entity, Attribute attribute) {
        return entity.getAttributes().getInstance(attribute);
    }
    
    public static void applyModifier(LivingEntity entity, Attribute attribute, double value, UUID uuid, String name, AttributeModifier.Operation operation) {
        AttributeInstance instance = getInstance(entity, attribute);
        
        // 收集模式：只登记声明，是否真正写到实体由调用方统一交给 Curios 官方通道。
        // 这里不做幂等短路——面板/工具提示依赖完整声明，缺项会直接丢来源图标。
        AttributeModifier modifier = new AttributeModifier(uuid, name, value, operation);
        if (collect(attribute, modifier)) {
            return;
        }

        if (instance != null) {
            AttributeModifier existing = instance.getModifier(uuid);
            // 幂等：数值与运算方式均未变化时直接跳过，避免反复移除/重加触发属性重算与客户端同步。
            if (existing != null && existing.getAmount() == value && existing.getOperation() == operation) {
                return;
            }
            instance.removeModifier(uuid);
            instance.addPermanentModifier(modifier);
        }
    }
    
    public static void removeModifier(LivingEntity entity, Attribute attribute, UUID uuid) {
        // 收集模式下「移除」没有意义：整份声明会被整体替换。
        if (isCollecting()) {
            return;
        }
        AttributeInstance instance = getInstance(entity, attribute);
        
        if (instance != null && instance.getModifier(uuid) != null) {
            instance.removeModifier(uuid);
        }
    }

    
    public static void applyStackingModifier(LivingEntity entity, Attribute attribute, double delta,
                                             UUID uuid, String name, AttributeModifier.Operation operation) {
        AttributeInstance instance = getInstance(entity, attribute);
        if (instance == null) {
            return;
        }
        double old = 0.0;
        AttributeModifier oldModifier = instance.getModifier(uuid);
        if (oldModifier != null) {
            old = oldModifier.getAmount();
        }
        double newValue = old + delta;
        // 收集模式：产出「叠加后的目标值」，与真正施加时的口径一致（施加前旧值就是当前实例值）。
        if (newValue != 0.0 && collect(attribute, new AttributeModifier(uuid, name, newValue, operation))) {
            return;
        }
        // 幂等：叠加量为 0（结果未变）时无需重建修饰符。
        if (oldModifier != null && old == newValue) {
            return;
        }
        instance.removeModifier(uuid);
        if (newValue != 0.0) {
            instance.addPermanentModifier(new AttributeModifier(uuid, name, newValue, operation));
        }
    }

    
    public static void applyAllAttributesModifier(LivingEntity entity, UUID uuid, String name,
                                                  double value, AttributeModifier.Operation operation) {
        Collection<? extends String> blacklist = TaczCuriosConfig.COMMON.attributeBonusBlacklist.get();
        applyAllAttributesModifier(entity, uuid, name, value, operation, blacklist);
    }

    
    public static void applyInfiniteAllAttributesModifier(LivingEntity entity, UUID uuid, String name, double value) {
        Collection<? extends String> blacklist = TaczCuriosConfig.COMMON.attributeBonusBlacklist.get();
        applyAllAttributesModifier(entity, uuid, name, value, AttributeModifier.Operation.MULTIPLY_BASE, blacklist);
    }

    private static void applyAllAttributesModifier(LivingEntity entity, UUID uuid, String name,
                                                   double value, AttributeModifier.Operation operation,
                                                   Collection<? extends String> blacklist) {
        for (Attribute attribute : ForgeRegistries.ATTRIBUTES.getValues()) {
            if (blacklist != null && !blacklist.isEmpty()) {
                ResourceLocation key = ForgeRegistries.ATTRIBUTES.getKey(attribute);
                if (key != null && blacklist.contains(key.toString())) {
                    continue;
                }
            }
            // 收集模式：全属性声明不依赖实体已有实例，逐个登记后由 Curios 统一施加。
            if (isCollecting()) {
                if (value != 0.0) {
                    collect(attribute, new AttributeModifier(uuid, name, value, operation));
                }
                continue;
            }
            AttributeInstance instance = entity.getAttributes().getInstance(attribute);
            if (instance == null) {
                continue;
            }
            AttributeModifier existing = instance.getModifier(uuid);
            if (value == 0.0) {
                // 清零：仅在修饰符确实存在时才移除，避免无谓的属性重算。
                if (existing != null) {
                    instance.removeModifier(uuid);
                }
                continue;
            }
            // 幂等：数值与运算方式均未变化时跳过，避免遍历全属性表时反复移除/重加。
            if (existing != null && existing.getAmount() == value && existing.getOperation() == operation) {
                continue;
            }
            if (existing != null) {
                instance.removeModifier(uuid);
            }
            instance.addTransientModifier(new AttributeModifier(uuid, name, value, operation));
        }
    }
}
