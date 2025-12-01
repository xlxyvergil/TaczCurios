package com.xlxyvergil.tcc.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class GunTypeChecker {
    // 定义支持的枪械类型集合
    public static final Set<String> DMG_BOOST_GUN_TYPES = Set.of("rifle", "sniper", "smg", "mg", "rpg");
    public static final Set<String> SHOTGUN_GUN_TYPES = Set.of("shotgun");
    public static final Set<String> PISTOL_GUN_TYPES = Set.of("pistol");
    public static final Set<String> SNIPER_GUN_TYPES = Set.of("sniper");
    
    /**
     * 检查玩家是否持有指定类型的枪械
     * @param player 玩家实体
     * @param validTypes 有效的枪械类型集合
     * @return 如果玩家持有指定类型的枪械返回true，否则返回false
     */
    public static boolean isHoldingValidGunType(Player player, Set<String> validTypes) {
        ItemStack mainHandItem = player.getMainHandItem();
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
     * 检查玩家是否持有支持伤害加成的枪械类型（步枪、狙击枪、冲锋枪、机枪、发射器）
     * @param player 玩家实体
     * @return 如果玩家持有支持伤害加成的枪械类型返回true，否则返回false
     */
    public static boolean isHoldingDmgBoostGunType(Player player) {
        return isHoldingValidGunType(player, DMG_BOOST_GUN_TYPES);
    }
    
    /**
     * 检查玩家是否持有霰弹枪
     * @param player 玩家实体
     * @return 如果玩家持有霰弹枪返回true，否则返回false
     */
    public static boolean isHoldingShotgun(Player player) {
        return isHoldingValidGunType(player, SHOTGUN_GUN_TYPES);
    }
    
    /**taa:magazine_capacity
     * 检查玩家是否持有手枪
     * @param player 玩家实体
     * @return 如果玩家持有手枪返回true，否则返回false
     */
    public static boolean isHoldingPistol(Player player) {
        return isHoldingValidGunType(player, PISTOL_GUN_TYPES);
    }
    
    /**
     * 检查玩家是否持有狙击枪
     * @param player 玩家实体
     * @return 如果玩家持有狙击枪返回true，否则返回false
     */
    public static boolean isHoldingSniper(Player player) {
        return isHoldingValidGunType(player, SNIPER_GUN_TYPES);
    }
    
    /**
     * 检查玩家手中枪械的当前弹匣是否满弹药
     * @param player 玩家实体
     * @return 如果玩家持有枪械且弹匣满弹药返回true，否则返回false
     */
    public static boolean isHoldingGunWithFullMagazine(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(mainHandItem);
        
        if (iGun != null) {
            // 获取当前弹药数
            int currentAmmo = iGun.getCurrentAmmoCount(mainHandItem);
            // 获取最大弹药数
            ResourceLocation gunId = iGun.getGunId(mainHandItem);
            return TimelessAPI.getCommonGunIndex(gunId)
                .map(index -> {
                    int maxAmmo = index.getGunData().getAmmoAmount();
                    // 考虑扩容弹匣
                    ItemStack extendedMag = iGun.getAttachment(mainHandItem, com.tacz.guns.api.item.attachment.AttachmentType.EXTENDED_MAG);
                    if (!extendedMag.isEmpty()) {
                        // 通过AttachmentDataUtils获取考虑扩容弹匣后的最大弹药数
                        maxAmmo = com.tacz.guns.util.AttachmentDataUtils.getAmmoCountWithAttachment(mainHandItem, index.getGunData());
                    }
                    return currentAmmo >= maxAmmo;
                })
                .orElse(false);
        }
        
        return false;
    }
}