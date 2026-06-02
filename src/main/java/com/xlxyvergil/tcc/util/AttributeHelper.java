package com.xlxyvergil.tcc.util;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

/**
 * 属性工具类 - 提供统一的属性访问方式
 */
public class AttributeHelper {
    
    // ========== 原版属性 ==========
    
    /** 攻击伤害 */
    public static final Attribute ATTACK_DAMAGE = Attributes.ATTACK_DAMAGE;
    
    /** 最大生命值 */
    public static final Attribute MAX_HEALTH = Attributes.MAX_HEALTH;
    
    /** 护甲值 */
    public static final Attribute ARMOR = Attributes.ARMOR;
    
    /** 移动速度 */
    public static final Attribute MOVEMENT_SPEED = Attributes.MOVEMENT_SPEED;
    
    // ========== Forge 属性 ==========
    
    /** 实体交互范围（攻击范围） */
    public static final Attribute ENTITY_REACH = ForgeMod.ENTITY_REACH.get();
    
    /** 方块交互范围 */
    public static final Attribute BLOCK_REACH = ForgeMod.BLOCK_REACH.get();
    
    // ========== Apothic-Attributes 暴击属性 ==========
    
    /** 暴击几率 (PercentBasedAttribute, base=0.05, range=[0,10]) */
    public static final Attribute CRIT_CHANCE = ALObjects.Attributes.CRIT_CHANCE.get();
    
    /** 暴击伤害 (PercentBasedAttribute, base=1.5, range=[1,100]) */
    public static final Attribute CRIT_DAMAGE = ALObjects.Attributes.CRIT_DAMAGE.get();
    
    // ========== TAA 枪械属性 ==========
    
    /** 通用枪械伤害 */
    public static final Attribute BULLET_GUNDAMAGE = EntityAttributeRegistry.BULLET_GUNDAMAGE.get();
    
    /** 瞄准时间 */
    public static final Attribute ADS_TIME = EntityAttributeRegistry.ADS_TIME.get();
    
    /** 弹药速度 */
    public static final Attribute AMMO_SPEED = EntityAttributeRegistry.AMMO_SPEED.get();
    
    /** 护甲穿透 */
    public static final Attribute ARMOR_IGNORE = EntityAttributeRegistry.ARMOR_IGNORE.get();
    
    /** 有效射程 */
    public static final Attribute EFFECTIVE_RANGE = EntityAttributeRegistry.EFFECTIVE_RANGE.get();
    
    /** 爆炸半径 */
    public static final Attribute EXPLOSION_RADIUS = EntityAttributeRegistry.EXPLOSION_RADIUS.get();
    
    /** 爆炸伤害 */
    public static final Attribute EXPLOSION_DAMAGE = EntityAttributeRegistry.EXPLOSION_DAMAGE.get();
    
    /** 爆炸击退 */
    public static final Attribute EXPLOSION_KNOCKBACK = EntityAttributeRegistry.EXPLOSION_KNOCKBACK.get();
    
    /** 爆炸破坏方块 */
    public static final Attribute EXPLOSION_DESTROY_BLOCK = EntityAttributeRegistry.EXPLOSION_DESTROY_BLOCK.get();
    
    /** 爆炸延迟 */
    public static final Attribute EXPLOSION_DELAY = EntityAttributeRegistry.EXPLOSION_DELAY.get();
    
    /** 爆炸开启 */
    public static final Attribute EXPLOSION_ENABLED = EntityAttributeRegistry.EXPLOSION_ENABLED.get();
    
    /** 移动速度 */
    public static final Attribute MOVE_SPEED = EntityAttributeRegistry.MOVE_SPEED.get();
    
    /** 爆头倍率 */
    public static final Attribute HEADSHOT_MULTIPLIER = EntityAttributeRegistry.HEADSHOT_MULTIPLIER.get();
    
    /** 点燃效果 */
    public static final Attribute IGNITE = EntityAttributeRegistry.IGNITE.get();
    
    /** 准确度 */
    public static final Attribute INACCURACY = EntityAttributeRegistry.INACCURACY.get();
    
    /** 准确度-站立 */
    public static final Attribute INACCURACY_STAND = EntityAttributeRegistry.INACCURACY_STAND.get();
    
    /** 准确度-移动 */
    public static final Attribute INACCURACY_MOVE = EntityAttributeRegistry.INACCURACY_MOVE.get();
    
    /** 准确度-蹲下 */
    public static final Attribute INACCURACY_SNEAK = EntityAttributeRegistry.INACCURACY_SNEAK.get();
    
    /** 准确度-趴下 */
    public static final Attribute INACCURACY_LIE = EntityAttributeRegistry.INACCURACY_LIE.get();
    
    /** 准确度-瞄准 */
    public static final Attribute INACCURACY_AIM = EntityAttributeRegistry.INACCURACY_AIM.get();
    
    /** 击退 */
    public static final Attribute KNOCKBACK = EntityAttributeRegistry.KNOCKBACK.get();
    
    /** 穿透 */
    public static final Attribute PIERCE = EntityAttributeRegistry.PIERCE.get();
    
    /** 后坐力 */
    public static final Attribute RECOIL = EntityAttributeRegistry.RECOIL.get();
    
    /** 后坐力-垂直 */
    public static final Attribute RECOIL_PITCH = EntityAttributeRegistry.RECOIL_PITCH.get();
    
    /** 后坐力-水平 */
    public static final Attribute RECOIL_YAW = EntityAttributeRegistry.RECOIL_YAW.get();
    
    /** 射速 */
    public static final Attribute ROUNDS_PER_MINUTE = EntityAttributeRegistry.ROUNDS_PER_MINUTE.get();
    
    /** 消音效果 */
    public static final Attribute SILENCE = EntityAttributeRegistry.SILENCE.get();
    
    /** 重量 */
    public static final Attribute WEIGHT = EntityAttributeRegistry.WEIGHT.get();
    
    /** 弹头数量 */
    public static final Attribute BULLET_COUNT = EntityAttributeRegistry.BULLET_COUNT.get();
    
    /** 弹匣容量 */
    public static final Attribute MAGAZINE_CAPACITY = EntityAttributeRegistry.MAGAZINE_CAPACITY.get();
    
    /** 换弹时间 */
    public static final Attribute RELOAD_TIME = EntityAttributeRegistry.RELOAD_TIME.get();
    
    /** 近战伤害 */
    public static final Attribute MELEE_DAMAGE = EntityAttributeRegistry.MELEE_DAMAGE.get();
    
    /** 近战距离 */
    public static final Attribute MELEE_DISTANCE = EntityAttributeRegistry.MELEE_DISTANCE.get();
    
    // 具体枪械类型伤害
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
        try {
            return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeId));
        } catch (Exception ignored) {
            return null;
        }
    }
    
    /**
     * 获取属性实例的辅助方法
     * @param entity 实体
     * @param attribute 属性对象
     * @return 属性实例
     */
    public static AttributeInstance getInstance(LivingEntity entity, Attribute attribute) {
        return entity.getAttributes().getInstance(attribute);
    }
    
    /**
     * 应用属性修饰符
     * @param entity 实体
     * @param attribute 属性
     * @param value 修饰符值
     * @param uuid 修饰符UUID
     * @param name 修饰符名称
     * @param operation 操作类型
     */
    public static void applyModifier(LivingEntity entity, Attribute attribute, double value, UUID uuid, String name, AttributeModifier.Operation operation) {
        AttributeInstance instance = getInstance(entity, attribute);
        
        if (instance != null) {
            // 移除旧修饰符
            instance.removeModifier(uuid);
            
            // 添加新修饰符
            AttributeModifier modifier = new AttributeModifier(uuid, name, value, operation);
            instance.addPermanentModifier(modifier);
        }
    }
    
    /**
     * 移除属性修饰符
     * @param entity 实体
     * @param attribute 属性
     * @param uuid 修饰符UUID
     */
    public static void removeModifier(LivingEntity entity, Attribute attribute, UUID uuid) {
        AttributeInstance instance = getInstance(entity, attribute);
        
        if (instance != null) {
            instance.removeModifier(uuid);
        }
    }
}
