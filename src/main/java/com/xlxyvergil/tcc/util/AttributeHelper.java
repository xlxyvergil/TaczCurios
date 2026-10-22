package com.xlxyvergil.tcc.util;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.fusedcritical.attribute.FusedAttributes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayDeque;
import java.util.Collection;

public class AttributeHelper {

    /**
     * 声明收集栈：栈非空时处于「只登记不施加」模式。饰品的 {@code declareModifiers} 在这层里运行，
     * 由 collectDeclarations 收集成一份修饰符表交给 Curios 官方通道
     * （CurioAttributeModifierEvent → getAttributeModifiers），属性面板因此能按 Curios 的规则
     * 把修饰符映射回佩戴的饰品栈显示来源图标。
     * <p>
     * 用栈而不是单个变量，是为了容忍嵌套调用（声明期间又触发了别的饰品计算）。
     */
    private static final ThreadLocal<ArrayDeque<Multimap<Holder<Attribute>, AttributeModifier>>> DECLARATION_SINK =
        ThreadLocal.withInitial(ArrayDeque::new);

    /** 当前线程是否处于声明收集模式。声明代码里禁止做收集以外的副作用。 */
    public static boolean isCollecting() {
        return !DECLARATION_SINK.get().isEmpty();
    }

    public static Multimap<Holder<Attribute>, AttributeModifier> collectDeclarations(Runnable declarations) {
        ArrayDeque<Multimap<Holder<Attribute>, AttributeModifier>> stack = DECLARATION_SINK.get();
        Multimap<Holder<Attribute>, AttributeModifier> sink = LinkedHashMultimap.create();
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
    private static boolean collect(Holder<Attribute> attribute, AttributeModifier modifier) {
        ArrayDeque<Multimap<Holder<Attribute>, AttributeModifier>> stack = DECLARATION_SINK.get();
        if (stack.isEmpty() || attribute == null) {
            return false;
        }
        stack.peek().put(attribute, modifier);
        return true;
    }


    public static final Holder<Attribute> ATTACK_DAMAGE = Attributes.ATTACK_DAMAGE;

    public static final Holder<Attribute> MAX_HEALTH = Attributes.MAX_HEALTH;

    public static final Holder<Attribute> ARMOR = Attributes.ARMOR;

    public static final Holder<Attribute> MOVEMENT_SPEED = Attributes.MOVEMENT_SPEED;

    public static final Holder<Attribute> LUCK = Attributes.LUCK;


    public static final Holder<Attribute> ENTITY_REACH = Attributes.ENTITY_INTERACTION_RANGE;

    public static final Holder<Attribute> BLOCK_REACH = Attributes.BLOCK_INTERACTION_RANGE;


    public static final Holder<Attribute> CRIT_CHANCE = FusedAttributes.CRIT_CHANCE;

    public static final Holder<Attribute> CRIT_DAMAGE = FusedAttributes.CRIT_DAMAGE;

    public static final Holder<Attribute> LIFE_STEAL = TccAttributes.LIFE_STEAL;

    public static final Holder<Attribute> OVERHEAL = TccAttributes.OVERHEAL;


    public static final Holder<Attribute> BULLET_GUNDAMAGE = EntityAttributeRegistry.BULLET_GUNDAMAGE;

    public static final Holder<Attribute> ADS_TIME = EntityAttributeRegistry.ADS_TIME;

    public static final Holder<Attribute> AMMO_SPEED = EntityAttributeRegistry.AMMO_SPEED;

    public static final Holder<Attribute> ARMOR_IGNORE = EntityAttributeRegistry.ARMOR_IGNORE;

    public static final Holder<Attribute> EFFECTIVE_RANGE = EntityAttributeRegistry.EFFECTIVE_RANGE;

    public static final Holder<Attribute> EXPLOSION_RADIUS = EntityAttributeRegistry.EXPLOSION_RADIUS;

    public static final Holder<Attribute> EXPLOSION_DAMAGE = EntityAttributeRegistry.EXPLOSION_DAMAGE;

    public static final Holder<Attribute> EXPLOSION_KNOCKBACK = EntityAttributeRegistry.EXPLOSION_KNOCKBACK;

    public static final Holder<Attribute> EXPLOSION_DESTROY_BLOCK = EntityAttributeRegistry.EXPLOSION_DESTROY_BLOCK;

    public static final Holder<Attribute> EXPLOSION_DELAY = EntityAttributeRegistry.EXPLOSION_DELAY;

    public static final Holder<Attribute> EXPLOSION_ENABLED = EntityAttributeRegistry.EXPLOSION_ENABLED;

    public static final Holder<Attribute> MOVE_SPEED = EntityAttributeRegistry.MOVE_SPEED;

    public static final Holder<Attribute> HEADSHOT_MULTIPLIER = EntityAttributeRegistry.HEADSHOT_MULTIPLIER;

    public static final Holder<Attribute> IGNITE = EntityAttributeRegistry.IGNITE;

    public static final Holder<Attribute> INACCURACY = EntityAttributeRegistry.INACCURACY;

    public static final Holder<Attribute> INACCURACY_STAND = EntityAttributeRegistry.INACCURACY_STAND;

    public static final Holder<Attribute> INACCURACY_MOVE = EntityAttributeRegistry.INACCURACY_MOVE;

    public static final Holder<Attribute> INACCURACY_SNEAK = EntityAttributeRegistry.INACCURACY_SNEAK;

    public static final Holder<Attribute> INACCURACY_LIE = EntityAttributeRegistry.INACCURACY_LIE;

    public static final Holder<Attribute> INACCURACY_AIM = EntityAttributeRegistry.INACCURACY_AIM;

    public static final Holder<Attribute> KNOCKBACK = EntityAttributeRegistry.KNOCKBACK;

    public static final Holder<Attribute> PIERCE = EntityAttributeRegistry.PIERCE;

    public static final Holder<Attribute> RECOIL = EntityAttributeRegistry.RECOIL;

    public static final Holder<Attribute> RECOIL_PITCH = EntityAttributeRegistry.RECOIL_PITCH;

    public static final Holder<Attribute> RECOIL_YAW = EntityAttributeRegistry.RECOIL_YAW;

    public static final Holder<Attribute> ROUNDS_PER_MINUTE = EntityAttributeRegistry.ROUNDS_PER_MINUTE;

    public static final Holder<Attribute> SILENCE = EntityAttributeRegistry.SILENCE;

    public static final Holder<Attribute> WEIGHT = EntityAttributeRegistry.WEIGHT;

    public static final Holder<Attribute> BULLET_COUNT = EntityAttributeRegistry.BULLET_COUNT;

    public static final Holder<Attribute> MAGAZINE_CAPACITY = EntityAttributeRegistry.MAGAZINE_CAPACITY;

    public static final Holder<Attribute> RELOAD_TIME = EntityAttributeRegistry.RELOAD_TIME;

    public static final Holder<Attribute> MELEE_DAMAGE = EntityAttributeRegistry.MELEE_DAMAGE;

    public static final Holder<Attribute> MELEE_DISTANCE = EntityAttributeRegistry.MELEE_DISTANCE;


    public static final Holder<Attribute> HEAT_MAX = EntityAttributeRegistry.HEAT_MAX;

    public static final Holder<Attribute> HEAT_COOLING = EntityAttributeRegistry.HEAT_COOLING;

    public static final Holder<Attribute> BULLET_GUNDAMAGE_PISTOL = EntityAttributeRegistry.BULLET_GUNDAMAGE_PISTOL;
    public static final Holder<Attribute> BULLET_GUNDAMAGE_RIFLE = EntityAttributeRegistry.BULLET_GUNDAMAGE_RIFLE;
    public static final Holder<Attribute> BULLET_GUNDAMAGE_SHOTGUN = EntityAttributeRegistry.BULLET_GUNDAMAGE_SHOTGUN;
    public static final Holder<Attribute> BULLET_GUNDAMAGE_SNIPER = EntityAttributeRegistry.BULLET_GUNDAMAGE_SNIPER;
    public static final Holder<Attribute> BULLET_GUNDAMAGE_SMG = EntityAttributeRegistry.BULLET_GUNDAMAGE_SMG;
    public static final Holder<Attribute> BULLET_GUNDAMAGE_LMG = EntityAttributeRegistry.BULLET_GUNDAMAGE_LMG;
    public static final Holder<Attribute> BULLET_GUNDAMAGE_LAUNCHER = EntityAttributeRegistry.BULLET_GUNDAMAGE_LAUNCHER;

    public static Holder<Attribute> resolveAttribute(String attributeId) {
        if (attributeId == null || attributeId.isBlank()) {
            return null;
        }
        ResourceLocation loc = ResourceLocation.tryParse(attributeId);
        if (loc == null) return null;
        return BuiltInRegistries.ATTRIBUTE.getHolder(loc).orElse(null);
    }

    public static AttributeInstance getInstance(LivingEntity entity, Holder<Attribute> attribute) {
        return entity.getAttributes().getInstance(attribute);
    }

    public static void applyModifier(LivingEntity entity, Holder<Attribute> attribute, double value, ResourceLocation id, AttributeModifier.Operation operation) {
        // 收集模式：只登记声明，是否真正写到实体由调用方统一交给 Curios 官方通道。
        // 这里不做幂等短路——面板/工具提示依赖完整声明，缺项会直接丢来源图标。
        AttributeModifier modifier = new AttributeModifier(id, value, operation);
        if (collect(attribute, modifier)) {
            return;
        }

        AttributeInstance instance = getInstance(entity, attribute);

        if (instance != null) {
            AttributeModifier existing = instance.getModifier(id);
            // 幂等：数值与运算方式均未变化时直接跳过，避免反复移除/重加触发属性重算与客户端同步。
            if (existing != null && existing.amount() == value && existing.operation() == operation) {
                return;
            }
            instance.removeModifier(id);
            instance.addPermanentModifier(modifier);
        }
    }

    public static void removeModifier(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id) {
        // 收集模式下「移除」没有意义：整份声明会被整体替换。
        if (isCollecting()) {
            return;
        }
        AttributeInstance instance = getInstance(entity, attribute);

        if (instance != null && instance.getModifier(id) != null) {
            instance.removeModifier(id);
        }
    }


    public static void applyStackingModifier(LivingEntity entity, Holder<Attribute> attribute, double delta,
                                             ResourceLocation id, AttributeModifier.Operation operation) {
        AttributeInstance instance = getInstance(entity, attribute);
        if (instance == null) {
            return;
        }
        double old = 0.0;
        AttributeModifier oldModifier = instance.getModifier(id);
        if (oldModifier != null) {
            old = oldModifier.amount();
        }
        double newValue = old + delta;
        // 收集模式：产出「叠加后的目标值」，与真正施加时的口径一致（施加前旧值就是当前实例值）。
        if (newValue != 0.0 && collect(attribute, new AttributeModifier(id, newValue, operation))) {
            return;
        }
        // 幂等：叠加量为 0（结果未变）时无需重建修饰符。
        if (oldModifier != null && old == newValue) {
            return;
        }
        instance.removeModifier(id);
        if (newValue != 0.0) {
            instance.addPermanentModifier(new AttributeModifier(id, newValue, operation));
        }
    }


    public static void applyAllAttributesModifier(LivingEntity entity, ResourceLocation id,
                                                  double value, AttributeModifier.Operation operation) {
        Collection<? extends String> blacklist = TaczCuriosConfig.COMMON.attributeBonusBlacklist.get();
        applyAllAttributesModifier(entity, id, value, operation, blacklist);
    }


    public static void applyInfiniteAllAttributesModifier(LivingEntity entity, ResourceLocation id, double value) {
        Collection<? extends String> blacklist = TaczCuriosConfig.COMMON.attributeBonusBlacklist.get();
        applyAllAttributesModifier(entity, id, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, blacklist);
    }

    private static void applyAllAttributesModifier(LivingEntity entity, ResourceLocation id,
                                                   double value, AttributeModifier.Operation operation,
                                                   Collection<? extends String> blacklist) {
        for (Holder.Reference<Attribute> holder : BuiltInRegistries.ATTRIBUTE.holders().toList()) {
            if (blacklist != null && !blacklist.isEmpty()) {
                ResourceLocation key = holder.key().location();
                if (blacklist.contains(key.toString())) {
                    continue;
                }
            }
            // 收集模式：全属性声明不依赖实体已有实例，逐个登记后由 Curios 统一施加。
            if (isCollecting()) {
                if (value != 0.0) {
                    collect(holder, new AttributeModifier(id, value, operation));
                }
                continue;
            }
            AttributeInstance instance = entity.getAttributes().getInstance(holder);
            if (instance == null) {
                continue;
            }
            AttributeModifier existing = instance.getModifier(id);
            if (value == 0.0) {
                // 清零：仅在修饰符确实存在时才移除，避免无谓的属性重算。
                if (existing != null) {
                    instance.removeModifier(id);
                }
                continue;
            }
            // 幂等：数值与运算方式均未变化时跳过，避免遍历全属性表时反复移除/重加。
            if (existing != null && existing.amount() == value && existing.operation() == operation) {
                continue;
            }
            if (existing != null) {
                instance.removeModifier(id);
            }
            instance.addTransientModifier(new AttributeModifier(id, value, operation));
        }
    }
}
