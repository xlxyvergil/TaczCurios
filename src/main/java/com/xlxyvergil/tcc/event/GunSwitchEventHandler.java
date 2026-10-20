package com.xlxyvergil.tcc.event;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.util.CurioEffectRefresher;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 切枪驱动：主手/副手的枪械<strong>类型</strong>发生变化时重建饰品属性效果。
 *
 * 直接用 {@link LivingEquipmentChangeEvent}——Forge 在 LivingEntity 的每 tick 装备差分里
 * （detectEquipmentUpdates 遍历全部 EquipmentSlot，含 MAINHAND / OFFHAND）就会发这个事件，
 * 主手换枪、副手换枪、快捷栏切换、背包拖拽都能覆盖到，不需要自己比对手部状态。
 *
 * 过滤条件只看枪械类型（sniper / rifle / smg ...）：饰品属性限制判的就是类型，
 * 所以同一把枪的弹匣数、NBT 变化，以及"狙击枪换成另一把狙击枪"都不必重算。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GunSwitchEventHandler {

    /** 非枪械物品的类型占位符。 */
    private static final String NO_GUN_TYPE = "-";

    private GunSwitchEventHandler() {}

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        EquipmentSlot slot = event.getSlot();
        if (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity == null) {
            return;
        }
        // 类型没变（同一把枪的 NBT 变化、或同类型换枪）→ 限制判定的结果也不会变，不必重算
        if (gunType(event.getFrom()).equals(gunType(event.getTo()))) {
            return;
        }
        // 切枪后统一重建所有饰品效果，并按当前手持枪械类型确保属性仅在对应枪械下生效
        CurioEffectRefresher.refreshAll(entity);
    }

    /** 手持物的枪械类型；不是枪械（或查不到枪械数据）时返回 {@link #NO_GUN_TYPE}。 */
    private static String gunType(ItemStack stack) {
        IGun gun = IGun.getIGunOrNull(stack);
        if (gun == null) {
            return NO_GUN_TYPE;
        }
        return TimelessAPI.getCommonGunIndex(gun.getGunId(stack))
                .map(CommonGunIndex::getType)
                .orElse(NO_GUN_TYPE);
    }
}
