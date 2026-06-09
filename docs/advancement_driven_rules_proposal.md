# 进化/发放规则 → 成就系统改造方案

## 现状

### 规则类型

| 类型 | 触发方式 | 是否一次性的 | 示例 |
|------|---------|-------------|------|
| GRANT | 击杀达标 → 发放物品 | 是 (`oncePerPlayer`) | 七雷、希奥拉 |
| EVOLVE | 条件满足 → 替换饰品 | 是 | 七雷→雷鸣见、希奥拉→渡鸦 |
| ATTRIBUTE | 击杀 → 累加属性 | 否（有上限） | 夏日沙滩叠虚数抗性 |

### 当前问题

1. **发放防重复依赖 persistentData**：不可靠，可能丢失
2. **进度追踪不直观**：玩家不知道自己杀了多少、还差多少（除已做的两个成就外）
3. **两套系统并存**：evolution_rules.json + 成就 JSON，逻辑重复

## 方案：全部转为成就驱动

### 核心思路

> **一个规则 = 一个成就。成就完成 = 效果触发。**

- 进度条由 Minecraft 原生 Advancement 系统提供
- 防重复由成就的 `isDone()` 保证（Minecraft 持久化，比 persistentData 更可靠）
- 玩家看到完整的成就树，解锁路径一目了然

### 规则类型对照

| 规则类型 | 成就类型 | 触发效果 |
|---------|---------|---------|
| GRANT（有计数器） | 多步进度成就 | 完成最后一格时发放物品 |
| GRANT（无计数器） | 单步成就 | 条件满足时立即完成并发物品 |
| EVOLVE | 单步成就 | 条件满足时立即进化饰品 |
| ATTRIBUTE | **不改造** | 保持现有逻辑，因为是渐进叠加非一次性 |

### 成就 → 规则映射表

每个成就的 JSON 中加一个自定义字段 `"tcc_rule"`，值为对应的规则 ID：

```
tcc:tcc_root (根)
  ├─ tcc:my_island          → tcc_grant_seven_thunders      发放七雷
  │    └─ tcc:xiora_mission → tcc_grant_xiora               发放希奥拉
  ├─ tcc:thunder_seen       → tcc_evolve_thunder_seen       七雷进化为雷鸣见
  ├─ tcc:xiora_to_raven     → tcc_xiora_to_tcc_raven        希奥拉进化为渡鸦
  ├─ tcc:raven_to_island    → tcc_raven_to_island_boom_raven 渡鸦进化为小岛爆爆鸦
  ├─ ...（夏日沙滩线）
  └─ ...（天火圣裁线）
```

### 方案一：利用 damageSourceTags 作为元数据通道（纯 JSON，零代码改动）

**核心技巧**：既然 `damageSourceTags` 本身就是成就的自定义字段，而 `tcc_rule` 不具备 JSON 通用性，我们就复用已有的 `damageSourceTags` 字段作为进化/发放规则 ID。

```json
{
  "display": {
    "icon": { "item": "tcc:raven" },
    "title": { "translate": "advancement.tcc.xiora_to_raven.title" },
    "description": { "translate": "advancement.tcc.xiora_to_raven.description" },
    "frame": "goal",
    "show_toast": true,
    "announce_to_chat": true,
    "hidden": true
  },
  "parent": "tcc:xiora_mission",
  "criteria": {
    "done": {
      "trigger": "minecraft:impossible"
    }
  },
  "damageSourceTags": ["tcc_xiora_to_tcc_raven"]
}
```

**注意**：Forge 成就 JSON 解析器不认 `damageSourceTags`，但 Minecraft 会忽略未知字段（JSON 默认行为）。需要验证 1.20.1 是否严格拒绝未知字段。

### 方案二：用 `criteria` 的 `impossible` trigger（当前做法，已验证可行）

保持当前的 `minecraft:impossible`，代码中手动 award criterion。

```json
{
  "parent": "tcc:xiora_mission",
  "criteria": {
    "done": { "trigger": "minecraft:impossible" }
  }
}
```

代码中条件满足时：
```java
Advancement adv = server.getAdvancements().getAdvancement(advId);
if (adv != null) {
    serverPlayer.getAdvancements().award(adv, "done");
}
```

**推荐方案二**，因为已有两个成就（my_island、xiora_mission）跑通了全链路。

### 代码改造点

#### 1. 新增映射类 `RuleAdvancementMapping`

```java
/**
 * 规则 ID → 成就 ID 映射。
 * 当一个成就完成（isDone() == true），对应的规则视为"已执行"。
 */
public final class RuleAdvancementMapping {
    private static final Map<String, ResourceLocation> GRANT_RULES = Map.ofEntries(
        Map.entry("tcc_grant_seven_thunders",   new ResourceLocation("tcc", "my_island")),
        Map.entry("tcc_grant_xiora",            new ResourceLocation("tcc", "xiora_mission")),
        // 未来扩展...
        Map.entry("tcc_player_death_grant_tcc_summer_beach", new ResourceLocation("tcc", "summer_beach_grant"))
    );

    private static final Map<String, ResourceLocation> EVOLVE_RULES = Map.ofEntries(
        Map.entry("tcc_grant_seven_thunders_thunder_seen", new ResourceLocation("tcc", "thunder_seen")),
        Map.entry("tcc_xiora_to_tcc_raven",                new ResourceLocation("tcc", "xiora_to_raven")),
        Map.entry("tcc_raven_to_tcc_island_boom_raven",    new ResourceLocation("tcc", "raven_to_island")),
        // 未来扩展...
        Map.entry("tcc_heaven_fire_judgment_to_tcc_heaven_fire_apocalypse", new ResourceLocation("tcc", "judgment_to_apocalypse"))
    );

    public static ResourceLocation getAdvancement(String ruleId) {
        ResourceLocation adv = GRANT_RULES.get(ruleId);
        if (adv != null) return adv;
        return EVOLVE_RULES.get(ruleId);
    }

    public static boolean isAdvancementDone(Player player, ResourceLocation advId) {
        if (!(player instanceof ServerPlayer sp)) return false;
        Advancement adv = sp.server.getAdvancements().getAdvancement(advId);
        if (adv == null) return false;
        return sp.getAdvancements().getOrStartProgress(adv).isDone();
    }
}
```

#### 2. `passesOncePerPlayerTag` 改为查成就

```java
// 如果成就已完成 → 跳过（已发放/已进化）
ResourceLocation advId = RuleAdvancementMapping.getAdvancement(rule.ruleId);
if (advId != null && RuleAdvancementMapping.isAdvancementDone(player, advId)) {
    return false;
}
```

#### 3. 发放/进化成功后 → 完成成就

```java
// executeGrant 内
if (given && onceTag != null) {
    player.getPersistentData().putBoolean(onceTag, true);
    // 完成对应成就
    ResourceLocation advId = RuleAdvancementMapping.getAdvancement(rule.ruleId);
    if (advId != null && player instanceof ServerPlayer sp) {
        Advancement adv = sp.server.getAdvancements().getAdvancement(advId);
        if (adv != null) {
            sp.getAdvancements().award(adv, "done");
        }
    }
}
```

#### 4. EVOLVE 规则适配

当前 evolve 逻辑在 `GunKillEventHandler.tryEvolve` 中，进化为单一行为。改为：

```java
// 进化成功后
if (evolved) {
    // 完成成就
    ResourceLocation advId = RuleAdvancementMapping.getAdvancement(rule.ruleId);
    awardAdvancement(player, advId);
}
```

#### 5. 进度成就保持现状

七雷（30步）、希奥拉（10步）的多步成就逻辑不变，继续用 `applyAndCheckCounters` + `progressRuleAdvancement`。

### 成就 JSON 模板

**进度型（30步）：**
```json
{
  "display": {
    "icon": { "item": "tcc:seven_thunders" },
    "title": { "translate": "advancement.tcc.my_island.title" },
    "description": { "translate": "advancement.tcc.my_island.description" },
    "frame": "task"
  },
  "parent": "tcc:tcc_root",
  "criteria": {
    "step_1": { "trigger": "minecraft:impossible" },
    ...
    "step_30": { "trigger": "minecraft:impossible" }
  }
}
```

**单步型（进化）：**
```json
{
  "display": {
    "icon": { "item": "tcc:raven" },
    "title": { "translate": "advancement.tcc.xiora_to_raven.title" },
    "description": { "translate": "advancement.tcc.xiora_to_raven.description" },
    "frame": "goal",
    "show_toast": true,
    "announce_to_chat": true,
    "hidden": true
  },
  "parent": "tcc:xiora_mission",
  "criteria": {
    "done": { "trigger": "minecraft:impossible" }
  }
}
```

> 进化成就设 `"hidden": true`，避免成就列表剧透。只在触发时弹出 toast。

### 改造的规则范围

| 规则 ID | 类型 | 成就 | 进度步数 |
|---------|------|------|---------|
| `tcc_grant_seven_thunders` | GRANT | `my_island` | 30 |
| `tcc_grant_xiora` | GRANT | `xiora_mission` | 10 |
| `tcc_grant_seven_thunders_thunder_seen` | EVOLVE | `thunder_seen` | 1 |
| `tcc_xiora_to_tcc_raven` | EVOLVE | `xiora_to_raven` | 1 |
| `tcc_raven_to_tcc_island_boom_raven` | EVOLVE | `raven_to_island` | 1 |
| `tcc_raven_to_tcc_island_boom_raven` (linked) | AUTO | `judgement_key` | 1 |
| `tcc_heaven_fire_judgment_to_tcc_heaven_fire_apocalypse` | EVOLVE | `judgment_to_apocalypse` | 1 |
| `tcc_summer_beach_to_tcc_brahma_beasts` | EVOLVE | `summer_to_brahma` | 1 |
| `tcc_brahma_beasts_to_tcc_salvation` | EVOLVE | `brahma_to_salvation` | 1 |
| `tcc_brahma_beasts_to_tcc_salvation` (linked) | AUTO | `heaven_fire_apocalypse_endless` | 1 |
| `tcc_player_death_grant_tcc_summer_beach` | GRANT | `summer_beach_grant` | 1 |
| 所有 ATTRIBUTE 规则 | ATTRIBUTE | 不改造 | — |

### 不改的部分

- **ATTRIBUTE 规则**：渐进取值，非一次性触发，不适合成就
- **`tcc_root`**：根节点，`minecraft:tick` 自动触发，纯结构节点，不属于改造范围
- **扣除物品的逻辑**：已有 `piercingSetBonus`、EL 诅咒补偿等，不受影响

### 实施步骤

1. 创建所有成就 JSON（模板+硬编码规则ID作为注释）
2. 创建 `RuleAdvancementMapping` 映射类
3. 修改 `passesOncePerPlayerTag` 查成就
4. 修改 `executeGrant`/`tryEvolve` 成功后完成成就
5. 删除 `GrantHistoryData`（已完成）
6. 添加全套本地化
7. 测试：全链路（击杀→成就→发放→防重复）
