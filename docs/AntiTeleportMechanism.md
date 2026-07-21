# 范围禁止瞬移机制技术文档

## 概述

本文档详细分析了 Minecraft 及相关模组中实体的瞬移实现机制，并提供了一套完整的"范围禁止瞬移"饰品效果的实现方案。

---

## 一、瞬移机制分析

### 1.1 实体瞬移方法分类

根据对多个模组的源码分析，实体瞬移主要分为以下几类：

| 类型 | 瞬移方法 | 触发事件 | 适用实体 |
|------|---------|---------|---------|
| **标准瞬移** | `teleportTo()` / `randomTeleport()` | `EntityTeleportEvent.EnderEntity` | 末影人、Wight、阿波伦 |
| **潜影贝瞬移** | `teleportSomewhere()` | 无 | 潜影贝 |
| **自定义瞬移** | 直接 `setPos()` / `move()` | 可能触发 `EntityTeleportEvent` | 部分自定义实体 |

### 1.2 末影人瞬移机制

Minecraft 原版末影人使用 `teleportTo()` 方法进行瞬移，该方法会触发 `EntityTeleportEvent.EnderEntity` 事件。

### 1.3 Wight 瞬移机制

Goety 模组中的 Wight 有三个瞬移方法：

- `teleport()` - 随机瞬移（32格范围）
- `teleportNearTo(Entity)` - 向目标附近瞬移（从目标背后16格处出现）
- `teleport(double, double, double)` - 核心实现，调用 `ForgeEventFactory.onEnderTeleport()` 触发事件

**触发场景**：
- 受伤时（躲避机制）
- 隐身结束后
- 尖叫结束后

**冷却机制**：瞬移后有 2 秒冷却（`teleportCool = 40 tick`）

### 1.4 阿波伦瞬移机制

GoetyRevelation 模组中的阿波伦使用以下瞬移方式：

- `teleport()` - 阿波伦专属瞬移方法
- `teleportTo()` - 重写的通用瞬移方法

**特殊逻辑**：
- Smite 状态下瞬移被禁用或距离减半
- Doom 状态下强制停止射击
- 高空限制：Y >= 99.5 时强制瞬移到 Y >= 129

### 1.5 潜影贝瞬移机制

潜影贝使用私有方法 `teleportSomewhere()` 进行瞬移，**不触发任何 Forge 事件**，必须通过 Mixin 拦截。

### 1.6 Enigmatic-Legacy EnderSlayer 机制

EnderSlayer 通过以下方式阻止瞬移：

1. 伤害触发时设置 `ELTeleportBlock` 标记
2. 监听 `EntityTeleportEvent.EnderEntity` 拦截末影人瞬移
3. Mixin 拦截 `teleportSomewhere()` 阻止潜影贝瞬移
4. `LivingTickEvent` 中自动递减冷却时间

---

## 二、范围禁止瞬移方案

### 2.1 核心设计

**架构关系**：事件处理器是核心，主动监听瞬移事件并查询饰品状态；饰品类只是被动提供查询接口。

```
事件处理器 (核心) ← 查询 ← 饰品类 (被动接口)
```

**工作流程**：
1. 实体尝试瞬移 → 触发 Forge 事件 / 调用瞬移方法
2. 事件处理器 / Mixin 拦截器收到事件 → 查询瞬移实体周围是否有佩戴饰品的实体
3. 饰品类提供 `hasEquipped()` 方法 → 返回是否佩戴了禁止瞬移的饰品
4. 如果存在佩戴饰品的实体 → 取消瞬移

### 2.2 实现步骤

#### 步骤 1：创建饰品类（被动接口）

```java
package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.items.ItemBaseCurio;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Properties;

public class AntiTeleportAmulet extends ItemBaseCurio {

    public AntiTeleportAmulet(Properties properties) {
        super(properties);
    }

    public static boolean hasEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity, 
            stack -> stack.getItem() instanceof AntiTeleportAmulet).isEmpty();
    }
}
```

#### 步骤 2：添加配置参数

在 `TaczCuriosConfig.java` 的 `Common` 类中添加：

```java
public final ForgeConfigSpec.DoubleValue antiTeleportAmuletRange;
public final ForgeConfigSpec.BooleanValue antiTeleportAmuletBlockEnderEntity;
public final ForgeConfigSpec.BooleanValue antiTeleportAmuletBlockShulker;

// 在构造函数中：
builder.comment("范围瞬移封禁饰品配置").push("anti_teleport_amulet");
antiTeleportAmuletRange = builder
        .comment("封禁瞬移的有效范围 (方块, 默认: 16)")
        .defineInRange("range", 16.0, 1.0, 128.0);
antiTeleportAmuletBlockEnderEntity = builder
        .comment("是否阻止末影人/Wight/阿波伦等瞬移 (默认: true)")
        .define("blockEnderEntity", true);
antiTeleportAmuletBlockShulker = builder
        .comment("是否阻止潜影贝瞬移 (默认: true)")
        .define("blockShulker", true);
builder.pop();
```

#### 步骤 3：注册饰品

在 `TccItems.java` 中注册：

```java
public static final RegistryObject<Item> ANTI_TELEPORT_AMULET = ITEMS.register("anti_teleport_amulet", 
    () -> new AntiTeleportAmulet(new Item.Properties().stacksTo(1)));
```

#### 步骤 4：创建事件处理器（核心）

事件处理器是整个机制的核心，主动监听瞬移事件并查询饰品状态。

```java
package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.curios.AntiTeleportAmulet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class AntiTeleportEventHandler {

    private static boolean hasNearbyAntiTeleportEntity(Entity entity) {
        if (entity.level().isClientSide) return false;
        
        double range = TaczCuriosConfig.COMMON.antiTeleportAmuletRange.get();
        AABB searchBox = new AABB(
            entity.getX() - range,
            entity.getY() - range,
            entity.getZ() - range,
            entity.getX() + range,
            entity.getY() + range,
            entity.getZ() + range
        );
        
        List<LivingEntity> nearbyEntities = entity.level()
            .getEntitiesOfClass(LivingEntity.class, searchBox);
        
        return nearbyEntities.stream()
            .anyMatch(AntiTeleportAmulet::hasEquipped);
    }

    @SubscribeEvent
    public static void onEnderEntityTeleport(EntityTeleportEvent.EnderEntity event) {
        if (!TaczCuriosConfig.COMMON.antiTeleportAmuletBlockEnderEntity.get()) return;
        if (hasNearbyAntiTeleportEntity(event.getEntity())) {
            event.setCanceled(true);
        }
    }
}
```

#### 步骤 5：创建 Mixin 拦截潜影贝（核心补充）

对于潜影贝这种不触发标准 Forge 事件的实体，使用 Mixin 作为事件处理器的补充。

```java
package com.xlxyvergil.tcc.mixin;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.curios.AntiTeleportAmulet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Shulker.class)
public class MixinShulker {

    @Inject(method = "teleportSomewhere", at = @At("HEAD"), cancellable = true)
    private void onTeleportSomewhere(CallbackInfoReturnable<Boolean> info) {
        if (!TaczCuriosConfig.COMMON.antiTeleportAmuletBlockShulker.get()) return;
        if (AntiTeleportAmulet.hasNearbyAntiTeleportEntity((Entity) (Object) this)) {
            info.setReturnValue(false);
        }
    }
}
```

#### 步骤 6：添加 Mixin 配置

在 `tcc.mixins.json` 中添加：

```json
{
  "required": true,
  "package": "com.xlxyvergil.tcc.mixin",
  "compatibilityLevel": "JAVA_17",
  "mixins": [
    "MixinShulker"
  ],
  "client": [],
  "injectors": {
    "defaultRequire": 1
  }
}
```

---

## 三、完整流程图

```
实体尝试瞬移
        ↓
┌───────────────────────────────────────┐
│ 事件处理器 / Mixin 拦截器（核心）       │
│ 收到瞬移事件 / 瞬移方法调用            │
└───────────────────────┬───────────────┘
                        ↓
┌───────────────────────────────────────┐
│ 查询瞬移实体周围 X 格内的所有实体       │
└───────────────────────┬───────────────┘
                        ↓
┌───────────────────────────────────────┐
│ 遍历实体，调用饰品类的 hasEquipped()   │
│ 饰品类（被动接口）返回是否佩戴饰品      │
└───────────────────────┬───────────────┘
                        ↓
┌───────────────────────────────────────┐
│ 是否存在佩戴禁止瞬移饰品的实体?         │
└───────────────────────┬───────────────┘
                   是 ↓        ↓ 否
              ┌─────────────┐  ┌─────────────────┐
              │ 取消瞬移     │  │ 瞬移正常进行     │
              │ event.set-  │  │                 │
              │ Canceled()  │  │                 │
              │ / 拦截返回  │  │                 │
              └─────────────┘  └─────────────────┘
```

### 架构关系图

```
┌─────────────────────────────────────────────────────────┐
│                     事件处理器（核心）                    │
│  AntiTeleportEventHandler.onEnderEntityTeleport()       │
│  MixinShulker.onTeleportSomewhere()                     │
└─────────────────────┬───────────────────────────────────┘
                      │ 查询
                      ↓
┌─────────────────────────────────────────────────────────┐
│                     饰品类（被动接口）                    │
│  AntiTeleportAmulet.hasEquipped(entity)                 │
│  → 返回 entity 是否佩戴了禁止瞬移的饰品                   │
└─────────────────────────────────────────────────────────┘
```

---

## 四、配置说明

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `anti_teleport_amulet.range` | 16 | 封禁瞬移的有效范围（方块） |
| `anti_teleport_amulet.blockEnderEntity` | true | 是否阻止末影人/Wight/阿波伦等使用标准瞬移方法的实体 |
| `anti_teleport_amulet.blockShulker` | true | 是否阻止潜影贝瞬移 |

---

## 五、关键技术点

### 5.1 职责划分

| 组件 | 角色 | 职责 |
|------|------|------|
| **事件处理器** | 核心 | 主动监听瞬移事件，执行范围检测，决定是否取消瞬移 |
| **Mixin 拦截器** | 核心补充 | 拦截不触发标准事件的实体（如潜影贝），复用事件处理器的检测逻辑 |
| **饰品类** | 被动接口 | 提供 `hasEquipped()` 方法，告诉事件处理器"这个实体是否佩戴了饰品" |

### 5.2 Forge 事件监听

通过监听 `EntityTeleportEvent.EnderEntity` 事件，可以拦截所有使用标准瞬移方法的实体（末影人、Wight、阿波伦等）。

### 5.3 Mixin 拦截

对于潜影贝等不触发标准 Forge 事件的实体，需要使用 Mixin 直接拦截其瞬移方法。

### 5.4 范围检测

使用 `AABB` 和 `getEntitiesOfClass()` 在瞬移实体周围查找佩戴饰品的实体。

### 5.5 配置化

通过 `ForgeConfigSpec` 让范围和封禁类型可配置，提高灵活性。

---

## 六、兼容性考虑

### 6.1 与其他模组的兼容性

- **Enigmatic-Legacy**：EnderSlayer 使用 `ELTeleportBlock` 标记，与本方案兼容
- **Goety/GoetyRevelation**：Wight 和阿波伦使用标准瞬移方法，可被本方案拦截
- **其他模组**：使用 `EntityTeleportEvent.EnderEntity` 事件的实体均可被拦截

### 6.2 性能考虑

- 范围检测使用 `AABB` 查询，复杂度为 O(n)，n 为范围内实体数量
- 建议将默认范围设置为合理值（16-32格），避免过大范围影响性能
- 仅在服务端执行检测，避免客户端不必要的计算

---

## 七、可选增强

1. **视觉效果**：瞬移被阻止时播放粒子效果或音效
2. **权限系统**：允许特定玩家/实体不受影响
3. **范围显示**：在客户端显示封禁范围的可视化效果
4. **方向限制**：只阻止瞬移到饰品佩戴者范围内，不阻止瞬移出范围
5. **冷却机制**：瞬移被阻止后添加短暂冷却，避免频繁尝试
