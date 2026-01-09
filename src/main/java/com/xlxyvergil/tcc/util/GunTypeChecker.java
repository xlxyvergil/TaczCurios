package com.xlxyvergil.tcc.util;

import java.util.Set;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class GunTypeChecker {
    // 定义支持的枪械类型集合
    public static final Set<String> DMG_BOOST_GUN_TYPES = Set.of("rifle", "sniper", "smg", "mg", "rpg");
    public static final Set<String> SHOTGUN_GUN_TYPES = Set.of("shotgun");
    public static final Set<String> PISTOL_GUN_TYPES = Set.of("pistol");
    public static final Set<String> SNIPER_GUN_TYPES = Set.of("sniper");
    
    /**
     * 检查生物是否持有指定类型的枪械
     * @param livingEntity 生物实体
     * @param validTypes 有效的枪械类型集合
     * @return 如果生物持有指定类型的枪械返回true，否则返回false
     */
    public static boolean isHoldingValidGunType(LivingEntity livingEntity, Set<String> validTypes) {
        ItemStack mainHandItem = livingEntity.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHandItem);
        
        if (iGun != null) {
            ResourceLocation gunId = iGun.getGunId(mainHandItem);
            return TimelessAPI.getCommonGunIndex(gunId)
                .map(CommonGunIndex::getType)
                .map(validTypes::contains)
                .orElse(false);
        }
        
        return false;
    }
    
    /**
     * 检查生物是否持有支持伤害加成的枪械类型（步枪、狙击枪、冲锋枪、机枪、发射器）
     * @param livingEntity 生物实体
     * @return 如果生物持有支持伤害加成的枪械类型返回true，否则返回false
     */
    public static boolean isHoldingDmgBoostGunType(LivingEntity livingEntity) {
        return isHoldingValidGunType(livingEntity, DMG_BOOST_GUN_TYPES);
    }
    
    /**
     * 检查生物是否持有霰弹枪
     * @param livingEntity 生物实体
     * @return 如果生物持有霰弹枪返回true，否则返回false
     */
    public static boolean isHoldingShotgun(LivingEntity livingEntity) {
        return isHoldingValidGunType(livingEntity, SHOTGUN_GUN_TYPES);
    }
    
    /**taa:magazine_capacity
     * 检查生物是否持有手枪
     * @param livingEntity 生物实体
     * @return 如果生物持有手枪返回true，否则返回false
     */
    public static boolean isHoldingPistol(LivingEntity livingEntity) {
        return isHoldingValidGunType(livingEntity, PISTOL_GUN_TYPES);
    }
    
    /**
     * 检查生物是否持有狙击枪
     * @param livingEntity 生物实体
     * @return 如果生物持有狙击枪返回true，否则返回false
     */
    public static boolean isHoldingSniper(LivingEntity livingEntity) {
        return isHoldingValidGunType(livingEntity, SNIPER_GUN_TYPES);
    }
    
    /**
     * 检查生物手中枪械的当前弹匣是否满弹药
     * @param livingEntity 生物实体
     * @return 如果生物持有枪械且弹匣满弹药返回true，否则返回false
     */
    public static boolean isHoldingGunWithFullMagazine(LivingEntity livingEntity) {
        ItemStack mainHandItem = livingEntity.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHandItem);
        
        if (iGun == null) {
            return false;
        }
        
        ResourceLocation gunId = iGun.getGunId(mainHandItem);
        return TimelessAPI.getCommonGunIndex(gunId)
            .map(index -> {
                // 基础弹药数（包含枪管中的子弹）
                int barrelBulletAmount = (iGun.hasBulletInBarrel(mainHandItem) && index.getGunData().getBolt() != com.tacz.guns.resource.pojo.data.gun.Bolt.OPEN_BOLT) ? 1 : 0;
                int ammoAmount = index.getGunData().getAmmoAmount() + barrelBulletAmount;
                
                // 获取修改后的最大弹药数（与GunPropertyDiagramsMixin相同的逻辑）
                int maxAmmoCount = ammoAmount; // 默认值
                
                // 检查是否为背包供弹模式，如果是则不修改
                boolean isUsingInventoryAsMagazine = index.getGunData().getReloadData() != null && 
                    index.getGunData().getReloadData().getType() == com.tacz.guns.resource.pojo.data.gun.FeedType.INVENTORY;
                    
                if (!isUsingInventoryAsMagazine) {
                    // 首先尝试从ShooterContext获取缓存数据（最高优先级）
                    LivingEntity shooter = ShooterContext.getShooter();
                    if (shooter != null) {
                        IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
                        if (operator != null) {
                            AttachmentCacheProperty cache = operator.getCacheProperty();
                            if (cache != null) {
                                Integer modifiedAmmoCount = cache.getCache(AmmoCountModifier.ID);
                                if (modifiedAmmoCount != null) {
                                    maxAmmoCount = modifiedAmmoCount; // 使用缓存值并加上枪管中的子弹
                                }
                            }
                        }
                    }
                    
                    // 如果ShooterContext中没有，尝试从生物获取缓存数据（备选方案）
                    if (maxAmmoCount == ammoAmount) { // 只有在还没有修改值时才尝试
                        IGunOperator operator = IGunOperator.fromLivingEntity(livingEntity);
                        if (operator != null) {
                            AttachmentCacheProperty cache = operator.getCacheProperty();
                            if (cache != null) {
                                Integer modifiedAmmoCount = cache.getCache(AmmoCountModifier.ID);
                                if (modifiedAmmoCount != null) {
                                    maxAmmoCount = modifiedAmmoCount; // 使用缓存值并加上枪管中的子弹
                                }
                            }
                        }
                    }
                }
                
                // 获取当前弹药数
                int currentAmmo = iGun.getCurrentAmmoCount(mainHandItem);
                
                // 确保弹药数不为负数且为有效值
                if (currentAmmo < 0 || maxAmmoCount <= 0) {
                    return false;
                }
                
                return currentAmmo == maxAmmoCount;
            })
            .orElse(false);
    }
}