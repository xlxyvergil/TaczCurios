# TACZ 弹药恢复方案

## 概述

多个饰品通过 `curioTick()` 内置周期恢复，统一调用 `AmmoRegenHelper.regenAmmo()` 补充手持枪械弹药。无需额外的 Forge 事件监听或 Mixin。

## 核心组件

### AmmoRegenHelper（统一恢复工具）

[AmmoRegenHelper](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/util/AmmoRegenHelper.java) 是所有饰品的弹药恢复入口，确保：

1. **弹匣容量来源一致**：从 TaczAttributeAdd 玩家缓存 `ExtendedGunProperties.MAGAZINE_CAPACITY` 获取经全部计算（含 `magazine_capacity` modifier）后的容量，而非 `AttachmentDataUtils`
2. **写入方式统一**：使用 `IGun.setCurrentAmmoCount()` 写入弹药
3. **闭膛待击处理**：闭膛枪械膛内无弹时先补膛内子弹

```java
// 接口签名
public static void regenAmmo(Player player, ItemStack held, IGun iGun, double regenPercent)
```

| 参数 | 说明 |
|---|---|
| `player` | 持枪玩家 |
| `held` | 手持枪械 ItemStack |
| `iGun` | `IGun.getIGunOrNull(held)` 的返回值 |
| `regenPercent` | 恢复比例（0.0 ~ 1.0），实际恢复量 = `Math.max(1, Math.round(弹匣容量 * regenPercent))` |

### 实现流程

1. 从 `IGunOperator.fromLivingEntity(player).getCacheProperty().getCache(ExtendedGunProperties.MAGAZINE_CAPACITY)` 获取经 TaczAttributeAdd 计算后的弹匣容量
2. 当前弹药 ≥ 容量则跳过
3. 计算恢复量 `= Math.max(1, Math.round(容量 * regenPercent))`
4. 闭膛待击（非 OPEN_BOLT）：膛内无弹时先补一发进膛
5. `iGun.setCurrentAmmoCount(held, Math.min(current + amount, max))`

---

## 饰品实现

每个需要弹药恢复的饰品在 `curioTick()` 中内联调用 `AmmoRegenHelper.regenAmmo()`。

### 重型武器类（rpg / mg）

这些饰品限定手持 `rpg` 或 `mg` 类枪械时生效，使用 `GunTypeChecker.isHoldingHeavyWeapon()` 判定。

| 饰品 | 恢复比例 | 默认值 | 配置路径 |
|---|---|---|---|
| 虚空万藏 | `xukongWancangAmmoRegenPercent` | 0.05 (5%) | `tcc.xukongWancang.ammoRegenPercent` |
| 启示之键 | `qishiZhijianAmmoRegenPercent` | 0.1 (10%) | `tcc.qishiZhijian.ammoRegenPercent` |
| 虚空万藏·终焉 | `xukongWancangYZTHAmmoRegenPercent` | 0.2 (20%) | `tcc.xukongWancangYZTH.ammoRegenPercent` |

```java
// 示例：XukongWancang.curioTick()
@Override
public void curioTick(SlotContext slotContext, ItemStack stack) {
    LivingEntity entity = slotContext.entity();
    if (!(entity instanceof Player player)) return;
    if (player.level().isClientSide()) return;

    // 每秒恢复一次
    if (player.tickCount % 20 != 0) return;
    if (!GunTypeChecker.isHoldingHeavyWeapon(player)) return;

    ItemStack held = player.getMainHandItem();
    IGun iGun = IGun.getIGunOrNull(held);
    if (iGun == null) return;

    AmmoRegenHelper.regenAmmo(player, held, iGun,
        (double) TaczCuriosConfig.COMMON.xukongWancangAmmoRegenPercent.get());
}
```

### 步枪类（rifle）

这些饰品限定手持 `rifle` 类枪械时生效，使用 `GunTypeChecker.isHoldingRifle()` 判定。

| 饰品 | 恢复公式 | 默认值 | 配置路径 |
|---|---|---|---|
| 万物休眠 | 固定比例 | 0.05 (5%) | `tcc.wanwuXiumian.ammoRegenPercent` |
| 停滞之键 | `basePercent + 虚数抗性 × resistanceScale` | base 0.1 + scale 0.005 | `tcc.tingzhiZhijian.ammoBasePercent` / `ammoResistanceScale` |
| 因果转轮 | `虚数抗性 × resistanceScale` | scale 0.01 | `tcc.yinguoZhuanlun.ammoResistanceScale` |

**停滞之键示例**（恢复比例与虚数抗性挂钩）：

```java
// TingzhiZhijian.curioTick()
double basePercent = TaczCuriosConfig.COMMON.tingzhiZhijianAmmoBasePercent.get();      // 0.1
double totalResistance = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
double resistanceScale = TaczCuriosConfig.COMMON.tingzhiZhijianAmmoResistanceScale.get(); // 0.005
double percent = Math.round((basePercent + totalResistance * resistanceScale) * 100.0) / 100.0;

AmmoRegenHelper.regenAmmo(player, held, iGun, percent);
```

**因果转轮示例**（纯虚数抗性缩放）：

```java
// YinguoZhuanlun.curioTick()
double totalResistance = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
double resistanceScale = TaczCuriosConfig.COMMON.yinguoZhuanlunAmmoResistanceScale.get();  // 0.01
double percent = Math.round(totalResistance * resistanceScale * 100.0) / 100.0;

AmmoRegenHelper.regenAmmo(player, held, iGun, percent);
```

---

## 枪械类型判定

`GunTypeChecker` 提供便捷方法判断手持枪械类型：

| 方法 | 对应枪械类型 | 使用饰品 |
|---|---|---|
| `isHoldingHeavyWeapon()` | `rpg`, `mg` | 虚空万藏、启示之键、虚空万藏·终焉 |
| `isHoldingRifle()` | `rifle` | 万物休眠、停滞之键、因果转轮 |

---

## 周期与触发

- 所有弹药恢复都通过 `curioTick()` 周期检查，频率：**每秒一次**（`player.tickCount % 20 == 0`）
- 仅检查**主手**持有的枪械
- 弹药满时 `AmmoRegenHelper.regenAmmo()` 直接跳过，无副作用

---

## 配置选项汇总

```properties
# 重型武器类
tcc.xukongWancang.ammoRegenPercent = 0.05     # 虚空万藏：每次恢复弹匣的 5%
tcc.qishiZhijian.ammoRegenPercent = 0.1        # 启示之键：10%
tcc.xukongWancangYZTH.ammoRegenPercent = 0.2   # 虚空万藏·终焉：20%

# 步枪类
tcc.wanwuXiumian.ammoRegenPercent = 0.05       # 万物休眠：5%

# 步枪类（虚数抗性缩放）
tcc.tingzhiZhijian.ammoBasePercent = 0.1       # 停滞之键：基础 10%
tcc.tingzhiZhijian.ammoResistanceScale = 0.005 # 停滞之键：每点虚数抗性增加 0.5%
tcc.yinguoZhuanlun.ammoResistanceScale = 0.01  # 因果转轮：每点虚数抗性 1%
```

---

## 关键依赖

| 坐标 | 用途 |
|---|---|
| `IGun` / `TimelessAPI` | TACZ 枪械接口与弹药读写 |
| `IGunOperator.getCacheProperty()` | TaczAttributeAdd 玩家属性缓存 |
| `ExtendedGunProperties.MAGAZINE_CAPACITY` | 经全部 modifier 计算后的弹匣容量 |
| `GunTypeChecker` | 枪械类型判定 |
| `TccAttributes.IMAGINARY_DAMAGE_RESISTANCE` | 虚数抗性属性（停滞之键/因果转轮缩放用） |
| `CuriosApi`（通过 `BaseCurioItem.curioTick`） | 饰品装备检测与周期回调 |
