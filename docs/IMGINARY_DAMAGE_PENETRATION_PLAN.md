# 虚数伤害绝对穿透方案

## 目标

虚数伤害只受自身代码控制（虚数抗性、虚数侵染、攻击者加成），不受原版/其他 mod 的任何减伤影响。

同时解决 `setHealth()` 直接扣血导致实体死亡时无掉落物的问题。

---

## Minecraft 1.20.1 伤害流水线

```
1. isInvulnerableTo() 检查          → BYPASSES_INVULNERABILITY
2. LivingAttackEvent               → 可被 cancel
3. 伤害冷却(hurtResistantTime)      → BYPASSES_COOLDOWN
4. getDamageAfterArmorAbsorb()     → BYPASSES_ARMOR
5. getDamageAfterMagicAbsorb()     → BYPASSES_ENCHANTMENTS
6. 抗性提升效果                      → BYPASSES_RESISTANCE
7. LivingHurtEvent                  → event.setAmount() 修改最终伤害
8. actuallyHurt() 内部:
   a. 伤害吸收                       → BYPASSES_EFFECTS
   b. LivingDamageEvent             → ForgeHooks.onLivingDamage()
   c. setHealth(getHealth() - amount)
   d. LivingDeathEvent (若死亡)
   e. 死亡掉落
```

---

## 当前项目的虚数伤害配置

### DamageType JSON (`data/tcc/damage_type/imaginary_damage.json`)

```json
{
  "exhaustion": 0.1,
  "message_id": "tcc:imaginary_damage",
  "scaling": "never"
}
```

### 已有 bypass 标签注册

| 标签文件 | 作用 |
|----------|------|
| `data/minecraft/tags/damage_type/bypasses_armor.json` | 绕过护甲 |
| `data/minecraft/tags/damage_type/bypasses_resistance.json` | 绕过抗性提升 |
| `data/minecraft/tags/damage_type/bypasses_enchantments.json` | 绕过保护附魔 |
| `data/minecraft/tags/damage_type/bypasses_effects.json` | 绕过伤害吸收 |
| `data/minecraft/tags/damage_type/bypasses_invulnerability.json` | 绕过无敌状态 |

### 缺失的标签

- **`bypasses_cooldown`** — 绕过错伤帧（hurtResistantTime），TACZ 多弹丸枪械必需

---

## 三个参考 Mod 的减伤/限制伤害方式

### GoetyRevelation

**减伤词条 (`LivingHurtEvent`)**：
- Halo 饰品：乘算减伤（地狱 75%、主世界 50%），特定伤害类型直接设为 0（火焰、魔法、爆炸等）
- Broken Halo：根据维度 0.7x/0.85x 减伤

**Boss 伤害上限**：
- Apollyon 通过 `@ModifyVariable` 在 `actuallyHurt` 处限制每击最大伤害
- `@Inject(method = {"hurt"})` 取消攻击（下界无敌帧期间）
- `@Redirect` 劫持 `ForgeHooks.onLivingDamage()` 的返回值，加上限

**FeDamage 穿甲方案**：
- Mixin 劫持 `DamageSource.is(TagKey)`，让 FeDamage 对所有 bypass 标签返回 `true`
- Mixin `hurt()` 的 `@ModifyVariable` 在 `HEAD` 处将 DamageSource 替换为 FeDamage

### Cataclysm（new1.20.1）

- Boss 在 `hurt()` 中检查 `BYPASSES_INVULNERABILITY` 标签来放行

### L2Hostility

**减伤词条**：
- `AdaptingTrait`：同类型伤害累计减免，检查 `BYPASSES_INVULNERABILITY` / `BYPASSES_EFFECTS` 绕过
- `ArenaTrait`：`LivingAttackEvent` 中 cancel + `DamageModifier.nonlinearFinal` 归零，检查 `BYPASSES_INVULNERABILITY`

**放大**：
- 使用 L2DamageTracker / `AttackListener`，通过 `DamageModifier.multTotal()` 增伤

---

## 三大方案对比

| | 方案一：DamageSource 覆写 + 事件写回 | 方案二：Mixin 新 hurt 方法 | 方案三：setHealth + 手动战利品 |
|---|---|---|---|
| **复杂度** | 中 | 中高 | 低 |
| **需要新增类** | `ImaginaryDamageSource` | Mixin 类 | 无（在现有逻辑上加） |
| **需要 Mixin** | 否 | 是 | 否 |
| **原版减伤穿透** | `is()` → 100% | 绕过 hurt() 入口 → 100% | setHealth → 100% |
| **其他 mod LivingHurtEvent 减伤** | LOWEST 写回覆盖 | 绕过 → 100% | setHealth → 100% |
| **Boss hurt() 覆写** | `is()` + 标签穿透 | 绕过 → 100% | 绕过（setHealth 不进 hurt）|
| **Boss actuallyHurt 硬上限** | LivingDamageEvent 写回（大部分） | 同样绕不过 | 绕过 → 100% |
| **Boss 完全覆写 actuallyHurt 且不调 super** | 无法穿透 | 无法穿透 | 穿透 → 100% |
| **死亡掉落** | 走原生流程，天然正常 | 需手动触发 LivingDeathEvent | 需读战利品表手动生成 |
| **兼容性** | 高（标准事件流程） | 中（跳过标准流程） | 高（Minecraft 原版 API） |
| **维护成本** | 低 | 中 | 低 |
| **防双倍掉落** | 天然不发生 | 需处理 | 需拦截 LivingDropsEvent |

---

## 方案一：扩展 DamageSource 覆盖 is(TagKey) + 事件写回

### 核心思路

1. 创建 `ImaginaryDamageSource extends DamageSource`，覆写 `is(TagKey)` 让所有 bypass 标签返回 `true`
2. `LivingHurtEvent` HIGHEST 捕获原始伤害 → LOWEST 写回（仅叠加虚数抗性/侵染/攻击者加成）
3. `LivingDamageEvent` LOWEST 写回（应对 Boss actuallyHurt 硬上限）
4. 所有 `setHealth()` 替换为 `hurt()`

### 第一步：补 `bypasses_cooldown` 标签

**新建文件**：`src/main/resources/data/minecraft/tags/damage_type/bypasses_cooldown.json`

```json
{
  "replace": false,
  "values": [
    "tcc:imaginary_damage"
  ]
}
```

### 第二步：新建 `ImaginaryDamageSource`

**新建文件**：`src/main/java/com/xlxyvergil/tcc/core/ImaginaryDamageSource.java`

```java
package com.xlxyvergil.tcc.core;

import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

/**
 * 虚数伤害源 — 通过覆写 is(TagKey) 实现绝对穿透
 *
 * 对所有 bypass 系列标签返回 true，确保原版护甲/保护/抗性/吸收/无敌/冷却帧全部跳过。
 * 其他 mod 通过检查标签判断是否减伤的，也会跳过。
 *
 * 留下的唯一可控点：
 * - 虚数抗性（IMAGINARY_DAMAGE_RESISTANCE）— 在 LivingHurtEvent LOWEST 计算
 * - 虚数侵染（IMAGINARY_INFECTION）— 在 LivingHurtEvent LOWEST 计算
 */
public class ImaginaryDamageSource extends DamageSource {

    public ImaginaryDamageSource(Holder<DamageType> type,
                                  @Nullable Entity directEntity,
                                  @Nullable Entity causingEntity) {
        super(type, directEntity, causingEntity);
    }

    @Override
    public boolean is(TagKey<DamageType> tagKey) {
        // 对所有 bypass 标签返回 true
        if (tagKey == DamageTypeTags.BYPASSES_ARMOR
            || tagKey == DamageTypeTags.BYPASSES_RESISTANCE
            || tagKey == DamageTypeTags.BYPASSES_ENCHANTMENTS
            || tagKey == DamageTypeTags.BYPASSES_EFFECTS
            || tagKey == DamageTypeTags.BYPASSES_INVULNERABILITY
            || tagKey == DamageTypeTags.BYPASSES_COOLDOWN) {
            return true;
        }
        return super.is(tagKey);
    }
}
```

### 第三步：改 `TccDamageSources` 工厂方法

**修改文件**：`src/main/java/com/xlxyvergil/tcc/core/TccDamageSources.java`

```java
public static ImaginaryDamageSource imaginaryDamage(Level level, Entity attacker) {
    return new ImaginaryDamageSource(
        level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
            .getHolderOrThrow(IMAGINARY_DAMAGE),
        attacker, attacker);
}

public static ImaginaryDamageSource imaginaryDamage(Level level, Entity bullet, Entity attacker) {
    return new ImaginaryDamageSource(
        level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
            .getHolderOrThrow(IMAGINARY_DAMAGE),
        bullet, attacker);
}
```

### 第四步：改 `TccAttributeEvents`

**修改文件**：`src/main/java/com/xlxyvergil/tcc/event/TccAttributeEvents.java`

新增 `ThreadLocal` 字段存储意图伤害：

```java
// 存储本次 hurt() 调用中意图造成的伤害值
// HIGHEST 捕获 → LOWEST 写回
private static final ThreadLocal<Float> INTENDED_DAMAGE = new ThreadLocal<>();
```

**新方法 ①**：HIGHEST 捕获原始伤害

```java
@SubscribeEvent(priority = EventPriority.HIGHEST)
public static void captureImaginaryDamage(LivingHurtEvent event) {
    if (event.getAmount() <= 0) return;
    if (!event.getSource().is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) return;
    INTENDED_DAMAGE.set(event.getAmount());
}
```

**修改方法 ②**：改写现有 `imaginaryDamageOnAttack`（保持 `@SubscribeEvent(priority = EventPriority.LOWEST)`），从 `INTENDED_DAMAGE` 读取意图值，叠加虚数抗性/侵染/攻击者加成后写回。

**新方法 ③**：`LivingDamageEvent` LOWEST 应对 Boss 硬上限

```java
@SubscribeEvent(priority = EventPriority.LOWEST)
public static void enforceImaginaryDamage(LivingDamageEvent event) {
    if (!(event.getSource() instanceof ImaginaryDamageSource)) return;
    Float intended = INTENDED_DAMAGE.get();
    if (intended == null) return;
    // ... 计算 expectedDamage（同 imaginaryDamageOnAttack 公式）
    if (event.getAmount() < expectedDamage) {
        event.setAmount(expectedDamage);
    }
    INTENDED_DAMAGE.remove(); // 清理
}
```

### 第五步：替换所有 `setHealth()` 为 `hurt()`

涉及文件：
- `JudgementKey.java` — 删除 `useSetHealth` 分支，统一 `hurt()`
- `SevenThundersThunderSeen.java` — `setHealth` → `hurt()`
- 全局搜索其他 `setHealth` 扣血

### 方案一效果矩阵

| 减伤/限制来源 | 穿透方式 |
|---------------|----------|
| 原版护甲/保护/抗性/吸收/无敌 | `is()` = true |
| 伤害冷却帧 | bypasses_cooldown 标签 |
| 其他 mod LivingHurtEvent（检查 bypass 标签） | `is()` = true → 跳过 |
| 其他 mod LivingHurtEvent（不检查标签） | LOWEST 写回覆盖 |
| Boss hurt() 覆写 | `is()` + 标签穿透 |
| Boss actuallyHurt Mixin 硬上限 | LivingDamageEvent 写回 |
| Boss 完全覆写 actuallyHurt 且不调 super | 无法穿透 |

---

## 方案二：Mixin 新 hurt 方法

### 核心思路

在 `LivingEntity` 上 Mixin 一个全新的 `tccImaginaryHurt(DamageSource, float)` 方法，绕过 `hurt()` 入口的所有逻辑（`LivingAttackEvent`、`LivingHurtEvent` 等），直接调用 `actuallyHurt()`。

### 实现

```java
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Unique
    public boolean tccImaginaryHurt(DamageSource source, float amount) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.isInvulnerableTo(source)) return false;
        if (self.level().isClientSide) return false;
        if (self.isDeadOrDying()) return false;
        
        // 直接走 actuallyHurt，跳过 hurt() 的所有中间层
        ((AccessorLivingEntity) self).invokeActuallyHurt(source, amount);
        return true;
    }
}
```

需要通过 `@Accessor` 暴露 `actuallyHurt` 方法：

```java
@Mixin(LivingEntity.class)
public interface AccessorLivingEntity {
    @Invoker("actuallyHurt")
    void invokeActuallyHurt(DamageSource source, float amount);
}
```

### 效果

| 减伤/限制来源 | 穿透方式 |
|---------------|----------|
| 原版护甲/保护/抗性/吸收/无敌 | 绕过 hurt() 入口 |
| 伤害冷却帧 | 绕过 hurt() 入口 |
| 其他 mod 的所有 LivingHurtEvent | 绕过 |
| Boss hurt() 覆写 | 绕过 |
| Boss actuallyHurt Mixin 硬上限 | **绕不过**（调的同一个 actuallyHurt） |
| Boss 完全覆写 actuallyHurt 且不调 super | 无法穿透 |

**额外问题**：
- `LivingDamageEvent` 在 `actuallyHurt()` 内部由 `ForgeHooks.onLivingDamage()` 触发，Mixin 新方法仍会触发
- 需要手动触发 `LivingDeathEvent` 和死亡掉落

### 结论

方案二比方案一的唯一优势是绕过其他 mod 的 `LivingHurtEvent`。但方案一的 LOWEST 写回已覆盖此场景。**方案二还损失了 `LivingHurtEvent` 这个入口**（你需要在这里计算虚数抗性/侵染/攻击者加成），反而更不方便。

---

## 方案三：setHealth + 手动读取战利品表生成掉落物

### 核心思路

保留 `setHealth()` 直接扣血（天然穿透 100% 所有减伤），在 `setHealth` 导致实体死亡时，手动读取战利品表生成掉落物。同时拦截 `LivingDropsEvent` 防止双倍掉落。

### 实现

#### 3.1 手动战利品生成工具方法

**新建文件**：`src/main/java/com/xlxyvergil/tcc/util/ManualLootHelper.java`

```java
package com.xlxyvergil.tcc.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

/**
 * 手动战利品生成工具 — 用于 setHealth 死亡后补充掉落物
 */
public class ManualLootHelper {

    public static final String MANUAL_DROPS_KEY = "tcc_manual_drops";

    /**
     * 为被 setHealth 杀死的实体手动生成掉落物
     *
     * @param target   死亡实体
     * @param source   伤害来源（用于战利品表条件判断）
     * @param attacker 攻击者（用于战利品表 player/last_damage_player 条件）
     */
    public static void generateLoot(LivingEntity target, DamageSource source, LivingEntity attacker) {
        if (!(target.level() instanceof ServerLevel serverLevel)) return;

        ResourceLocation lootTableId = target.getLootTable();
        LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(lootTableId);

        LootParams.Builder builder = new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.THIS_ENTITY, target)
            .withParameter(LootContextParams.ORIGIN, target.position())
            .withParameter(LootContextParams.DAMAGE_SOURCE, source)
            .withOptionalParameter(LootContextParams.KILLER_ENTITY, attacker)
            .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, attacker)
            .withLuck(attacker instanceof Player p ? p.getLuck() : 0);

        LootParams params = builder.create(LootContextParamSets.ENTITY);
        List<ItemStack> drops = lootTable.getRandomItems(params);

        for (ItemStack stack : drops) {
            target.spawnAtLocation(stack);
        }

        // 标记：已手动处理掉落物
        target.getPersistentData().putBoolean(MANUAL_DROPS_KEY, true);
    }
}
```

#### 3.2 拦截 `LivingDropsEvent` 防止双倍掉落

**新建或在已有事件类中添加**：

```java
/**
 * 防止 setHealth 手动掉落和原生掉落重复
 */
@SubscribeEvent
public static void onLivingDrops(LivingDropsEvent event) {
    if (event.getEntity().getPersistentData().getBoolean(ManualLootHelper.MANUAL_DROPS_KEY)) {
        event.setCanceled(true);
        event.getEntity().getPersistentData().remove(ManualLootHelper.MANUAL_DROPS_KEY);
    }
}
```

#### 3.3 调用方式

在 `JudgementKey`、`SevenThundersThunderSeen` 等使用 `setHealth` 的地方：

```java
// setHealth 扣血
targetLiving.setHealth(Math.max(0, targetLiving.getHealth() - directDamage));

// 如果实体死亡，手动处理掉落
if (targetLiving.isDeadOrDying()) {
    ManualLootHelper.generateLoot(
        targetLiving,
        TccDamageSources.imaginaryDamage(targetLiving.level(), attacker),
        attacker
    );
}
```

### 方案三效果矩阵

| 减伤/限制来源 | 穿透方式 |
|---------------|----------|
| 原版所有减伤 | setHealth → 100% |
| 其他 mod LivingHurtEvent | setHealth → 100% |
| Boss hurt() 覆写 | setHealth → 100% |
| Boss actuallyHurt 硬上限 | setHealth → 100% |
| Boss 完全覆写 actuallyHurt 且不调 super | setHealth → 100% |
| 死亡掉落 | 手动读战利品表 + 拦截 LivingDropsEvent |

### 方案三注意事项

1. **`getLootTable()` 返回值**：某些 Boss 可能返回 `null` 或在特定条件下才返回非空值。对于返回 `null` 的实体，手动生成不会产生任何掉落物（与原版行为一致）。
2. **战利品上下文**：`LootParams` 需要尽可能完整地填充参数，某些战利品表的 condition（如 `killed_by_player`、`random_chance_with_looting`）依赖 `KILLER_ENTITY` 和 luck 值。
3. **`LivingDropsEvent` 拦截时机**：必须在原生 `LivingDropsEvent` 触发之前设置 `MANUAL_DROPS_KEY`。`setHealth` → 死亡 → `LivingDeathEvent` → `LivingDropsEvent` 都在同一 tick 内同步发生，所以 `setHealth` 之后立即设置标记是安全的。
4. **`die()` 方法**：`setHealth(0)` 不会立即触发 `die()`，需要确认死亡后手动调用 `target.die(source)` 或不调用（让下一 tick 自然死亡）。建议手动调用以确保 `LivingDeathEvent` 触发。

---

## 推荐方案：方案一 + 方案三混合

### 策略

- **主流程**：使用方案一（`ImaginaryDamageSource` + 事件写回），走 `hurt()` 标准流水线
  - 覆盖 95% 场景，掉落天然正常，兼容性最好
- **极端场景**：对于确实需要绝对穿透（包括 Boss actuallyHurt 硬上限）的地方，保留 `setHealth` + 方案三的手动战利品

### 具体安排

| 代码位置 | 使用方案 | 原因 |
|----------|----------|------|
| JudgementKey 判决直伤 | 方案一（hurt） | 通常场景 |
| JudgementKey 配置 `useSetHealth=true` | 方案三（setHealth + 手动战利品） | 极端穿透需求，保留配置开关 |
| SevenThundersThunderSeen 20% 扣血 | 方案一（hurt） | 通常场景 |
| 其他所有 hurt 调用 | 方案一（hurt） | 默认行为 |

### 改动文件清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `data/minecraft/tags/damage_type/bypasses_cooldown.json` | **新建** | 补 bypasses_cooldown 标签 |
| `core/ImaginaryDamageSource.java` | **新建** | DamageSource 子类，覆写 is() |
| `core/TccDamageSources.java` | **修改** | 工厂方法返回 ImaginaryDamageSource |
| `event/TccAttributeEvents.java` | **修改** | HIGHEST 捕获 + 改写 LOWEST + LivingDamageEvent |
| `util/ManualLootHelper.java` | **新建** | setHealth 死亡后手动战利品生成 |
| `items/JudgementKey.java` | **修改** | setHealth 分支加手动战利品 |
| `items/SevenThundersThunderSeen.java` | **修改** | setHealth → hurt() |
| 全局其他 setHealth 扣血 | **修改** | setHealth → hurt() |
| 某事件类加 `onLivingDrops` | **修改** | 拦截防双倍掉落 |
| `config/TaczCuriosConfig.java` | **修改** | 保留 useSetHealth 配置项 |
