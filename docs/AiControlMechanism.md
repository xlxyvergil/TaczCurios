# AI 控制机制技术文档

## 概述

本文档描述了基于 Minecraft 原生 API 的实体 AI 控制机制的实现方案。核心功能为：佩戴特定饰品的实体攻击时，停止目标实体 AI，持续指定时间后自动恢复。

---

## 一、核心机制

### 1.1 AI 控制原理

Minecraft 的 `Mob` 类提供了原生的 AI 控制 API：

| 方法 | 作用 |
|------|------|
| `setNoAi(boolean)` | 设置实体是否停止 AI |
| `isNoAi()` | 获取当前 AI 状态 |

当 `setNoAi(true)` 被调用时，实体将：
- 停止移动（`mob.setNoAi(true)` 会阻止所有 AI Goal 执行）
- 停止攻击（攻击目标选择和攻击行为均被禁用）
- 停止寻路（不再寻找路径到达目标位置）
- 停止击退（不会被攻击击退）
- 停止游泳（不会尝试游泳保持漂浮）
- 停止看向目标（不会转向攻击目标）
- 保持当前状态直到 AI 恢复

### 1.2 实现架构

采用事件驱动 + 计时恢复的模式：

```
攻击事件触发
        ↓
检查攻击者是否佩戴特定饰品
        ↓
设置目标实体 setNoAi(true)
        ↓
记录恢复时间戳到 PersistentData
        ↓
LivingTickEvent 中检查并恢复 AI
```

---

## 二、实现步骤

### 步骤 1：创建 AI 控制饰品类

```java
package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.items.ItemBaseCurio;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Properties;

public class AiControlAmulet extends ItemBaseCurio {

    public AiControlAmulet(Properties properties) {
        super(properties);
    }

    public static boolean hasEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof AiControlAmulet).isEmpty();
    }
}
```

### 步骤 2：创建 AI 控制工具类

```java
package com.xlxyvergil.tcc.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class AiControlHelper {

    private static final String AI_DISABLED_KEY = "TCC_AiDisabled";
    private static final String AI_DISABLE_END_TIME_KEY = "TCC_AiDisableEndTime";

    public static void disableAi(Mob mob, int durationTicks) {
        if (mob.level().isClientSide) return;
        if (mob.isDeadOrDying()) return;
        
        mob.setNoAi(true);
        
        CompoundTag tag = mob.getPersistentData();
        tag.putBoolean(AI_DISABLED_KEY, true);
        tag.putLong(AI_DISABLE_END_TIME_KEY, mob.level().getGameTime() + durationTicks);
    }

    public static void tickAiControl(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        if (!(entity instanceof Mob mob)) return;
        if (mob.isDeadOrDying()) return;

        CompoundTag tag = mob.getPersistentData();
        if (!tag.getBoolean(AI_DISABLED_KEY)) return;

        long endTime = tag.getLong(AI_DISABLE_END_TIME_KEY);
        if (entity.level().getGameTime() >= endTime) {
            mob.setNoAi(false);
            tag.putBoolean(AI_DISABLED_KEY, false);
        }
    }

    public static boolean isAiDisabled(LivingEntity entity) {
        if (!(entity instanceof Mob)) return false;
        return entity.getPersistentData().getBoolean(AI_DISABLED_KEY);
    }
}
```

### 步骤 3：创建攻击事件处理器（核心）

```java
package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.curios.AiControlAmulet;
import com.xlxyvergil.tcc.util.AiControlHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class AiControlEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        
        if (!(target instanceof Mob mob)) return;
        if (mob.level().isClientSide) return;
        if (mob.isDeadOrDying()) return;
        if (AiControlHelper.isAiDisabled(target)) return;

        LivingEntity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingAttacker)) return;

        if (!AiControlAmulet.hasEquipped(livingAttacker)) return;

        int durationTicks = TaczCuriosConfig.COMMON.aiDisableDuration.get();
        AiControlHelper.disableAi(mob, durationTicks);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingTickEvent event) {
        AiControlHelper.tickAiControl(event.getEntity());
    }
}
```

### 步骤 4：注册饰品

在 `TccItems.java` 中注册：

```java
public static final RegistryObject<Item> AI_CONTROL_AMULET = ITEMS.register("ai_control_amulet",
    () -> new AiControlAmulet(new Item.Properties().stacksTo(1)));
```

---

## 三、完整代码示例

### AiControlAmulet.java

```java
package com.xlxyvergil.tcc.items.curios;

import com.xlxyvergil.tcc.items.ItemBaseCurio;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Properties;

public class AiControlAmulet extends ItemBaseCurio {

    public AiControlAmulet(Properties properties) {
        super(properties);
    }

    public static boolean hasEquipped(LivingEntity entity) {
        return !CurioSearchHelper.findFirstEquippedStack(entity,
            stack -> stack.getItem() instanceof AiControlAmulet).isEmpty();
    }
}
```

### AiControlHelper.java

```java
package com.xlxyvergil.tcc.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class AiControlHelper {

    private static final String AI_DISABLED_KEY = "TCC_AiDisabled";
    private static final String AI_DISABLE_END_TIME_KEY = "TCC_AiDisableEndTime";

    public static void disableAi(Mob mob, int durationTicks) {
        if (mob.level().isClientSide) return;
        if (mob.isDeadOrDying()) return;
        
        mob.setNoAi(true);
        
        CompoundTag tag = mob.getPersistentData();
        tag.putBoolean(AI_DISABLED_KEY, true);
        tag.putLong(AI_DISABLE_END_TIME_KEY, mob.level().getGameTime() + durationTicks);
    }

    public static void tickAiControl(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        if (!(entity instanceof Mob mob)) return;
        if (mob.isDeadOrDying()) return;

        CompoundTag tag = mob.getPersistentData();
        if (!tag.getBoolean(AI_DISABLED_KEY)) return;

        long endTime = tag.getLong(AI_DISABLE_END_TIME_KEY);
        if (entity.level().getGameTime() >= endTime) {
            mob.setNoAi(false);
            tag.putBoolean(AI_DISABLED_KEY, false);
        }
    }

    public static boolean isAiDisabled(LivingEntity entity) {
        if (!(entity instanceof Mob)) return false;
        return entity.getPersistentData().getBoolean(AI_DISABLED_KEY);
    }
}
```

### AiControlEventHandler.java

```java
package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.items.curios.AiControlAmulet;
import com.xlxyvergil.tcc.util.AiControlHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class AiControlEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        
        if (!(target instanceof Mob mob)) return;
        if (mob.level().isClientSide) return;
        if (mob.isDeadOrDying()) return;
        if (AiControlHelper.isAiDisabled(target)) return;

        LivingEntity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingAttacker)) return;

        if (!AiControlAmulet.hasEquipped(livingAttacker)) return;

        int durationTicks = TaczCuriosConfig.COMMON.aiDisableDuration.get();
        AiControlHelper.disableAi(mob, durationTicks);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingTickEvent event) {
        AiControlHelper.tickAiControl(event.getEntity());
    }
}
```

---

## 四、流程图

```
攻击事件触发 (LivingHurtEvent)
        ↓
┌───────────────────────────────┐
│ 检查目标是否为 Mob            │
│ 检查是否已处于 AI 禁用状态     │
│ 检查目标是否死亡               │
└───────────────────┬───────────┘
                    ↓
┌───────────────────────────────┐
│ 获取攻击者并检查是否佩戴饰品    │
│ AiControlAmulet.hasEquipped() │
└───────────────────┬───────────┘
                    ↓
┌───────────────────────────────┐
│ 调用 setNoAi(true) 停止 AI     │
│ 记录恢复时间戳到 PersistentData│
└───────────────────┬───────────┘
                    ↓
         每 Tick 检查 (LivingTickEvent)
                    ↓
┌───────────────────────────────┐
│ 当前游戏时间 >= 恢复时间戳?     │
└───────────────────┬───────────┘
              是 ↓        ↓ 否
         ┌───────────┐  ┌───────────┐
         │setNoAi(false)│ │ 保持状态   │
         │ 恢复 AI     │ │           │
         └───────────┘  └───────────┘
```

---

## 五、关键技术点

### 5.1 Curio 装备检查

通过 `CurioSearchHelper.findFirstEquippedStack()` 检查攻击者是否佩戴了 `AiControlAmulet` 饰品，只有佩戴饰品的实体攻击时才会触发 AI 停止效果。

### 5.2 PersistentData 存储

使用 `getPersistentData()` 存储状态，确保：
- 跨存档保存
- 实体卸载/重新加载后状态不丢失
- 无需额外的 Capability 系统

### 5.3 游戏时间计时

使用 `level.getGameTime()` 作为时间戳，单位为 tick（20 tick = 1 秒）。

### 5.4 状态保护

在攻击事件中检查 `isAiDisabled()`，避免重复设置导致计时被重置。

### 5.5 死亡状态检查

在 `disableAi()` 和 `tickAiControl()` 中检查 `isDeadOrDying()`，避免：
- 对已死亡实体设置 NoAI
- 实体死亡后仍尝试恢复 AI（虽然实体移除后数据自然消失，但可避免不必要的操作）
- 复活机制（如 Totem of Undying）导致残留标志问题

### 5.6 服务端执行

所有 AI 控制逻辑仅在服务端执行，避免客户端同步问题。

---

## 六、配置化扩展

在 `TaczCuriosConfig.java` 的 `Common` 类中添加：

```java
public final ForgeConfigSpec.IntValue aiDisableDuration;

// 在构造函数中：
builder.comment("AI 控制配置").push("ai_control");
aiDisableDuration = builder
    .comment("攻击触发 AI 停止的持续时间 (tick, 默认: 400 = 20秒)")
    .defineInRange("duration", 400, 1, 12000);
builder.pop();
```

---

## 七、NoAI 副作用说明

`setNoAi(true)` 会产生以下效果：

| 行为 | 状态 | 说明 |
|------|------|------|
| 移动 | 禁用 | 实体不会主动移动 |
| 攻击 | 禁用 | 实体不会攻击目标 |
| 寻路 | 禁用 | 实体不会寻找路径 |
| 击退 | 禁用 | 实体不会被攻击击退 |
| 游泳 | 禁用 | 实体不会尝试游泳保持漂浮 |
| 看向目标 | 禁用 | 实体不会转向攻击目标 |
| AI Goal 执行 | 全部禁用 | 所有 AI Goal 均不执行 |

---

## 八、模组兼容性注意事项

### 8.1 兼容性说明

| 模组类型 | 兼容性 | 说明 |
|----------|--------|------|
| 使用标准 Mob AI 的模组 | 兼容 | 大多数模组使用标准 Mob AI，可正常被 `setNoAi()` 控制 |
| 使用自定义 AI 管理器的模组 | 可能不兼容 | 部分模组通过 Mixin 或自定义 AI 管理器绕过 NoAI 标志 |
| 使用 Behavior Tree 的模组 | 可能不兼容 | 部分模组使用 Behavior Tree 替代传统 Goal 系统 |

### 8.2 不兼容时的解决方案

如果遇到不兼容的模组实体，可考虑以下方案：

1. **Mixin 拦截**：通过 Mixin 拦截目标模组的 AI 更新方法
2. **事件监听**：监听目标模组的自定义事件或使用 `EntityTickEvent` 强制设置状态
3. **Capabilities**：检查目标模组是否提供了 Capability 接口来控制 AI

---

## 九、性能考虑

- `LivingHurtEvent` 和 `LivingTickEvent` 都是高频事件，需确保逻辑简洁
- `isAiDisabled()` 使用 `PersistentData` 读取，效率较高
- `AiControlAmulet.hasEquipped()` 使用 `CurioSearchHelper`，复杂度取决于 Curios 槽位数量
- 建议在配置中设置合理的持续时间，避免过长时间的 AI 禁用导致实体堆积
