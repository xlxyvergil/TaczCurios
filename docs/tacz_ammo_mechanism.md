# TACZ 弹药恢复方案（纯事件监听，无需 Mixin）

## 概述

通过监听 `LivingTickEvent`，定期调用 TACZ 的 `IGun` API 直接补充枪械弹药。整个方案**不依赖任何 Mixin**，只需要 Forge 事件总线和 TACZ 公开 API。

## 原理

TACZ 的 `IGun` 接口暴露了完整的弹匣操作能力：

- `getCurrentAmmoCount(ItemStack)` — 获取弹匣当前弹药数
- `setCurrentAmmoCount(ItemStack, int)` — 设置弹匣弹药数
- `hasBulletInBarrel(ItemStack)` — 膛内是否有弹
- `setBulletInBarrel(ItemStack, boolean)` — 设置膛内是否有弹

搭配 `AttachmentDataUtils.getAmmoCountWithAttachment()` 获取含配件的最大弹容量，即可实现完整的弹药补充逻辑。

## 完整实现

```java
// TaczAmmoRecovery.java

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = YourMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TaczAmmoRecovery {

    // -- 配置常量 --
    private static final int RECOVERY_INTERVAL_SECONDS = 5;   // 恢复间隔（秒）
    private static final float RECOVERY_PERCENT = 0.10f;       // 每次恢复百分比

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        // 检查是否装备了特定饰品
        if (!hasCurio(entity)) return;

        // 按配置间隔触发
        if (entity.level().getGameTime() % (20 * RECOVERY_INTERVAL_SECONDS) != 0) return;

        // 主手
        ItemStack mainHand = entity.getMainHandItem();
        if (IGun.getIGunOrNull(mainHand) != null) {
            reloadGunByPercent(mainHand, RECOVERY_PERCENT);
        }
        // 副手
        ItemStack offHand = entity.getOffhandItem();
        if (IGun.getIGunOrNull(offHand) != null) {
            reloadGunByPercent(offHand, RECOVERY_PERCENT);
        }
    }

    // -- 百分比恢复（推荐） --
    // 按弹匣总容量的百分比计算恢复量，至少恢复 1 发
    public static void reloadGunByPercent(ItemStack gunStack, float percent) {
        IGun iGun = IGun.getIGunOrNull(gunStack);
        if (iGun == null) return;

        int maxAmmo = AttachmentDataUtils.getAmmoCountWithAttachment(
            gunStack,
            TimelessAPI.getCommonGunIndex(iGun.getGunId(gunStack)).get().getGunData()
        );
        int amount = Math.max(1, (int) (maxAmmo * percent));
        reloadGun(gunStack, amount);
    }

    // -- 固定数值恢复 --
    public static void reloadGunByFixed(ItemStack gunStack, int amount) {
        reloadGun(gunStack, amount);
    }

    // -- 核心补充逻辑 --
    private static void reloadGun(ItemStack gunStack, int fillAmount) {
        IGun iGun = IGun.getIGunOrNull(gunStack);
        if (iGun == null) return;

        int currentAmmo = iGun.getCurrentAmmoCount(gunStack);
        int maxAmmo = AttachmentDataUtils.getAmmoCountWithAttachment(
            gunStack,
            TimelessAPI.getCommonGunIndex(iGun.getGunId(gunStack)).get().getGunData()
        );

        // 弹药已满，跳过
        if (currentAmmo >= maxAmmo) return;

        // 闭膛待击（Closed Bolt）：如果膛内无弹，先补一发进膛
        Bolt bolt = TimelessAPI.getCommonGunIndex(iGun.getGunId(gunStack))
            .get().getGunData().getBolt();
        if (bolt != Bolt.OPEN_BOLT && !iGun.hasBulletInBarrel(gunStack)) {
            iGun.setBulletInBarrel(gunStack, true);
            fillAmount -= 1;
        }

        int newAmmo = Math.min(maxAmmo, currentAmmo + fillAmount);
        iGun.setCurrentAmmoCount(gunStack, newAmmo);
    }

    // -- 装备检查（Curios 示例） --
    private static boolean hasCurio(LivingEntity entity) {
        return !CuriosApi.getCuriosInventory(entity)
            .map(inv -> inv.findFirstCurio(YourCurioItem.get()))
            .orElse(Optional.empty())
            .isEmpty();
    }
}
```

## 调用示例

```java
// 百分比恢复：每次恢复弹匣容量的 10%
reloadGunByPercent(gunStack, 0.10f);
// 30 发弹匣 → 恢复 3 发
// 5 发弹匣  → 恢复 1 发（保底）

// 固定数值恢复：每次恢复 3 发
reloadGunByFixed(gunStack, 3);
```

## 弹药满了的行为

`currentAmmo >= maxAmmo` 时直接 `return`，定时器照常运行但不做任何操作，无副作用。

## 参考项目

- **MineFargo**：`TaczTickEvent` + `Tacz_WTC_Util` — 定时恢复弹药
  - `Event/Tacz/TaczTickEvent.java`
  - `Util/Tacz/Tacz_WTC_Util.java`

- **TACZ 源码**：弹药 API 核心
  - `api/entity/IGunOperator.java` — 射击者接口
  - `api/item/IGun.java` — 枪械物品接口（弹药读写）
  - `util/AttachmentDataUtils.java` — 配件扩容计算
  - `resource/pojo/data/gun/Bolt.java` — 枪机类型枚举
