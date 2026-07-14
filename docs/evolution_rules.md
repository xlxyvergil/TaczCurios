# TaczCurios 规则系统说明

TaczCurios 通过两套 JSON 配置驱动所有饰品进化、属性成长与发放逻辑：

| 配置文件 | 运行时路径 | 默认模板 |
|---|---|---|
| `evolution_rules.json` | `config/tcc/evolution_rules.json` | [evolution_rules.json](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/resources/tcc_defaults/evolution_rules.json) |
| `achievement_definitions.json` | `config/tcc/achievement_definitions.json` | [achievement_definitions.json](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/resources/tcc_defaults/achievement_definitions.json) |

## 架构概览

### 职责分离

| 功能 | 配置文件 | 说明 |
|---|---|---|
| **ATTRIBUTE**（属性成长） | `evolution_rules.json` | 击杀实体 → 写入进度 NBT → 饰品读取为属性加成 |
| **GRANT**（发放物品） | `achievement_definitions.json` | 满足条件 → 发放物品奖励 |
| **EVOLVE**（进化替换） | `achievement_definitions.json` | 满足条件 → 将饰品替换为进化形态 |

### 触发器与事件处理器

| 触发器 | 事件处理器 | 来源 |
|---|---|---|
| `gun_headshot_kill` | [GunHeadshotEventHandler](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/GunHeadshotEventHandler.java) | TACZ 爆头击杀 |
| `gun_kill` | [GunKillEventHandler](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/GunKillEventHandler.java) | TACZ 枪械击杀 |
| `living_death` | [LivingDeathEventHandler](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/LivingDeathEventHandler.java) | 通用死亡事件 |
| `bleeding_settlement` | HeavenFireSettlementHandler | 天火流血结算事件 |
| `stat_polling` | [StatPollingEventHandler](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/StatPollingEventHandler.java) | 统计数据轮询（每 3 tick） |
| `biome_visit` | [StatPollingEventHandler](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/StatPollingEventHandler.java) | 群系访问检测（每 20 tick） |

- `gun_kill` 和 `living_death` 会同时触发 **achievement_definitions** 的 GRANT/EVOLVE 奖励 和 **evolution_rules** 的 ATTRIBUTE 规则。
- `stat_polling` 和 `biome_visit` 也会同时触发 **achievement_definitions** 的 GRANT/EVOLVE 奖励 和 **evolution_rules** 的 ATTRIBUTE 规则。
- `gun_headshot_kill` 和 `bleeding_settlement` 仅触发 **achievement_definitions**。

---

# evolution_rules.json — ATTRIBUTE 属性成长规则

## 顶层结构

文件根对象必须包含 `rules`。

### 对象形式（推荐，ruleId 作为 key）

```json
{
  "rules": {
    "tcc_summer_beach_gain_imaginary_resistance": {
      "enabled": true,
      "trigger": "gun_kill",
      "type": "attribute",
      "item": "tcc:summer_beach"
    }
  }
}
```

### 数组形式（每个元素必须包含 `ruleId` 字段）

```json
{
  "rules": [
    {
      "ruleId": "tcc:example_rule",
      "enabled": true,
      "trigger": "gun_kill",
      "type": "attribute"
    }
  ]
}
```

## Rule（规则对象）字段

| 字段 | 类型 | 必需 | 默认值 | 说明 |
|---|---|---|---|---|
| `enabled` | boolean | 否 | `true` | 是否启用 |
| `trigger` | string | 否 | — | 触发器：`gun_kill` / `living_death` / `stat_polling` / `biome_visit` |
| `type` | string | 否 | `"evolve"` | 规则类型，当前仅支持 `"attribute"` |
| `playerKilled` | boolean | 否 | `false` | `false`=玩家击杀；`true`=玩家死亡（仅 `living_death` 生效） |
| `damageSourceTags` | string[] | 否 | `[]` | 伤害类型 Tag 限制（仅 `living_death` 触发时生效） |
| `item` | string | **是** | — | 需要追踪的饰品 itemId |
| `requirements` | object | 否 | — | 额外条件（见下文） |
| `kills` | KillGain[] | 否 | — | 击杀增量表（顶层字段，仅 `gun_kill` / `living_death` 生效） |
| `progress` | object | **是** | — | 进度写入配置（见下文） |
| `stat` | string | 否 | — | 统计项 ResourceLocation，如 `"minecraft:mob_kills"`（仅 `stat_polling` 触发） |
| `statThreshold` | int | 否 | `0` | 统计值阈值。同时作为步进间隔：`availableSteps = currentStat / statThreshold`（仅 `stat_polling` 触发） |
| `biome` | string | 否 | — | 群系 ID 或 `#` 前缀的 tag（仅 `biome_visit` 触发） |
| `value` | double | 否 | `1.0` | 条件满足时写入进度的增量。`stat_polling`：每步（`statThreshold` 间隔）的增量；`biome_visit`：单次增量 |

> **注意**：
> - `stat_polling` 的 ATTRIBUTE 规则采用 **step-based accumulative** 模式：`statThreshold` 作为步进间隔（如 48000 tick = 2游戏天），`value` 为每步增量。通过 `StatEvoSteps_<ruleId>` 追踪已应用的步数，每规则独立 `capCounterKey` 控制单规则上限。阈值高则为一次性触发，阈值低则为累积式触发。
> - `biome_visit` 的 ATTRIBUTE 规则是**一次性**触发（通过 NBT `StatEvoApplied_<ruleId>` 标记防止重复）。
> - `gun_kill` / `living_death` 的 ATTRIBUTE 规则是**累积式**触发（每次击杀增加 `kills[].value` 进度）。

> **注意**：`type` 的合法值为 `"attribute"` / `"gain_attribute"` / `"modifier"`（不区分大小写）。
> `"evolve"` 和 `"grant"` 在 evolution_rules.json 中保留支持但不推荐使用——它们已迁移至 achievement_definitions.json。

### requirements（额外条件对象）

所有字段均可选，多个条件为 AND 关系：

| 字段 | 类型 | 说明 |
|---|---|---|
| `requiredEffects` | string[] | 玩家必须拥有的药水效果 id 列表 |
| `holdingGunTypes` | string[] | 玩家手持枪械类型限制（如 `"sniper"`、`"pistol"`） |
| `minDistance` | double | 击杀时的最小距离（格），不填则无限制 |

```json
"requirements": {
  "requiredEffects": ["minecraft:invisibility"],
  "holdingGunTypes": ["pistol"],
  "minDistance": 100.0
}
```

- `requiredEffects`：通过伤害前 Buff 快照缓存判断（解决攻击破隐类效果在击杀时已消失的问题），代码位置：[EffectCacheHelper](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/util/EffectCacheHelper.java)
- `holdingGunTypes`：`gun_kill` 触发时用 GunId 匹配枪械类型；`living_death` 触发时检查玩家当前手持物品是否为配置的枪械类型
- `minDistance`：使用 `player.distanceToSqr(killed)` 判定

### kills — 击杀增量表（顶层字段）

定义"击杀什么实体、增加多少进度"，位于规则顶层（非 requirements 内）。

```json
"kills": [
  {
    "entity": {
      "key": "minecraft:wither",
      "nbt": ["apoth.boss=true"],
      "name": "神化"
    },
    "value": 1.0
  }
]
```

| 字段 | 类型 | 说明 |
|---|---|---|
| `entity.key` | string | 实体类型 id（必填，如 `"minecraft:wither"`） |
| `entity.nbt` | string[] | NBT 条件列表（可选，格式 `key=value`，全部命中才算匹配） |
| `entity.name` | string | UI 后缀名称（填写了 nbt 则必须填写，否则该条目被忽略） |
| `value` | double | 每次击杀增加多少进度值 |

### progress — 进度写入配置

```json
"progress": {
  "attribute": "tcc:imaginary_damage_resistance",
  "operation": "ADDITION",
  "cap": 20.0,
  "nbtKey": "Progress_imaginary_damage_resistance",
  "capCounterKey": "CapCounter_imaginary_damage_resistance"
}
```

| 字段 | 类型 | 必需 | 默认值 | 说明 |
|---|---|---|---|---|
| `attribute` | string | **是** | — | 属性 id，用于标识此成长对应哪个属性 |
| `operation` | string | **是** | — | 属性修饰符运算（见下方别名表） |
| `cap` | double | 否 | `0` | 进度累计上限 |
| `nbtKey` | string | 否 | 自动推导 | 写入饰品 NBT 的 key，缺省为 `Progress_<attributeId>` |
| `capCounterKey` | string | 否 | 自动推导 | 写入饰品 NBT 的计数器 key，缺省为 `CapCounter_<attributeId>` |

**operation 别名**（解析代码：[EvolutionRegistry.parseOperation](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/EvolutionRegistry.java#L409-L417)）：

| 可接受的值 | 实际运算 |
|---|---|
| `ADDITION` / `ADD` / `ADD_VALUE` | `AttributeModifier.Operation.ADDITION`（加法） |
| `MULTIPLY_BASE` / `ADD_MULTIPLIED_BASE` | `AttributeModifier.Operation.MULTIPLY_BASE` |
| `MULTIPLY_TOTAL` / `ADD_MULTIPLIED_TOTAL` | `AttributeModifier.Operation.MULTIPLY_TOTAL` |

## 完整示例

```json
{
  "rules": {
    "tcc_summer_beach_gain_imaginary_resistance": {
      "enabled": true,
      "trigger": "gun_kill",
      "type": "attribute",
      "item": "tcc:summer_beach",
      "requirements": {
        "holdingGunTypes": ["pistol"]
      },
      "progress": {
        "attribute": "tcc:imaginary_damage_resistance",
        "operation": "ADDITION",
        "cap": 20.0
      },
      "kills": [
        {
          "entity": { "key": "minecraft:wither" },
          "value": 1.0
        }
      ]
    }
  }
}
```

### stat_polling 示例

**一次性触发**（阈值 100，达成即一次性写入）：

```json
{
  "rules": {
    "tcc_killer_ring_from_kills": {
      "enabled": true,
      "trigger": "stat_polling",
      "type": "attribute",
      "item": "tcc:killer_ring",
      "stat": "minecraft:mob_kills",
      "statThreshold": 100,
      "value": 5.0,
      "requirements": {
        "equippedCurios": ["tcc:tracker_necklace"]
      },
      "progress": {
        "attribute": "tcc:imaginary_damage_resistance",
        "operation": "ADDITION",
        "cap": 20.0
      }
    }
  }
}
```

**Step-based accumulative 继承链示例**（每 48000 tick = 2游戏天 +1 虚数抗性，上限各20，共享 nbtKey 实现继承）：

```json
{
  "rules": {
    "tcc_griseo_imaginary_from_days": {
      "enabled": true,
      "trigger": "stat_polling",
      "type": "attribute",
      "item": "tcc:griseo",
      "stat": "minecraft:play_time",
      "statThreshold": 48000,
      "value": 1.0,
      "progress": {
        "attribute": "tcc:imaginary_damage_resistance",
        "operation": "ADDITION",
        "cap": 20.0,
        "nbtKey": "Progress_tcc_imaginary_damage_resistance",
        "capCounterKey": "CapCounter_griseo_imaginary"
      }
    },
    "tcc_huishi_zhijuan_imaginary_from_days": {
      "enabled": true,
      "trigger": "stat_polling",
      "type": "attribute",
      "item": "tcc:huishi_zhijuan",
      "stat": "minecraft:play_time",
      "statThreshold": 48000,
      "value": 1.0,
      "progress": {
        "attribute": "tcc:imaginary_damage_resistance",
        "operation": "ADDITION",
        "cap": 20.0,
        "nbtKey": "Progress_tcc_imaginary_damage_resistance",
        "capCounterKey": "CapCounter_huishi_zhijuan_imaginary"
      }
    },
    "tcc_fanxing_imaginary_from_days": {
      "enabled": true,
      "trigger": "stat_polling",
      "type": "attribute",
      "item": "tcc:fanxing",
      "stat": "minecraft:play_time",
      "statThreshold": 48000,
      "value": 1.0,
      "progress": {
        "attribute": "tcc:imaginary_damage_resistance",
        "operation": "ADDITION",
        "cap": 20.0,
        "nbtKey": "Progress_tcc_imaginary_damage_resistance",
        "capCounterKey": "CapCounter_fanxing_imaginary"
      }
    }
  }
}
```

> **继承机制**：三件饰品共享同一个 `nbtKey`（`Progress_tcc_imaginary_damage_resistance`），但各有独立的 `capCounterKey`（`CapCounter_griseo_imaginary` / `CapCounter_huishi_zhijuan_imaginary` / `CapCounter_fanxing_imaginary`）。进化时 COPY_ALL 复制全部 NBT，后续规则通过独立 capCounterKey 继续累加。示例：griseo 基础21（来自 config）→ 40天到达 41 → 进化继承后 huishi_zhijuan 从 41 继续成长至 80天到达 61 → fanxing 同理至 120天到达 81。

### biome_visit 示例

```json
{
  "rules": {
    "tcc_nether_charm_from_biome": {
      "enabled": true,
      "trigger": "biome_visit",
      "type": "attribute",
      "item": "tcc:nether_charm",
      "biome": "#minecraft:is_nether",
      "value": 10.0,
      "progress": {
        "attribute": "tcc:imaginary_damage_resistance",
        "operation": "ADDITION",
        "cap": 30.0
      }
    }
  }
}
```

---

# achievement_definitions.json — 成就驱动的 GRANT / EVOLVE

## 顶层结构

```json
{
  "achievements": {
    "tcc:achievement_id": {
      "enabled": true,
      "display": { ... },
      "parent": "tcc:parent_id",
      "trigger": "gun_kill",
      "playerKilled": false,
      "criteria_count": 10,
      "conditions": { ... },
      "prerequisites": [ ... ],
      "reward": { ... }
    }
  }
}
```

### 顶层字段

| 字段 | 类型 | 必需 | 默认值 | 说明 |
|---|---|---|---|---|
| `enabled` | boolean | 否 | `true` | 是否启用 |
| `display` | object | 是 | — | 显示信息（标题、描述、图标等） |
| `parent` | string | 否 | — | 父成就在 advancement tree 中的 id |
| `trigger` | string | 是 | — | 触发器：`gun_headshot_kill` / `gun_kill` / `living_death` / `bleeding_settlement` / `stat_polling` / `biome_visit` / `auto` |
| `playerKilled` | boolean | 否 | `false` | `false`=玩家击杀；`true`=玩家死亡（`living_death` 专用） |
| `criteria_count` | int | 否 | `1` | 累积完成次数 |
| `conditions` | object | 否 | — | 触发条件（见下文） |
| `prerequisites` | string[] | 否 | `[]` | 前置成就 id 列表 |
| `reward` | object | 否 | — | 完成后发放的奖励（见下文） |

### display — 显示信息

```json
"display": {
  "title": {
    "zh_cn": "夜袭强敌",
    "en_us": "Night Raid"
  },
  "description": {
    "zh_cn": "持有涤罪七雷，隐身状态下狙击爆头击杀{entity}（%d/%d）",
    "en_us": "Land sniper headshot kills on {entity} while invisible and holding Seven Thunders (%d/%d)"
  },
  "icon": "tcc:xiora",
  "frame": "goal",
  "hidden": false
}
```

| 字段 | 类型 | 说明 |
|---|---|---|
| `title` | `{locale: string}` | 双语标题 |
| `description` | `{locale: string}` | 双语描述，支持 `%d` 格式化 + `{entity}` / `{killer}` 占位符 |
| `icon` | string | 图标 itemId |
| `frame` | string | `"task"` / `"goal"` / `"challenge"` |
| `hidden` | boolean | 是否隐藏 |

### conditions — 触发条件

所有字段均可选，多个条件为 AND 关系。

| 字段 | 类型 | 说明 |
|---|---|---|
| `kills` | KillCondition[] | 击杀实体条件：`entity`（实体 key / `"*"` 任意）、`nbt`（可选）、`value`（每次贡献的进度步数，默认 1）<br>适用于 `gun_kill` / `gun_headshot_kill` / `living_death` |
| `equippedCurios` | string[] | 玩家必须装备的饰品列表 |
| `requiredEffects` | string[] | 玩家必须拥有的药水效果列表（适用于 `gun_kill` / `gun_headshot_kill`） |
| `holdingGunTypes` | string[] | 手持枪械类型限制（适用于 `gun_kill` / `gun_headshot_kill`） |
| `minDistance` | double | 击杀最小距离（格） |
| `attributes` | AttributeCondition[] | 属性门槛（`attribute` + `comparator` + `value`） |
| `killer` | string | 凶手实体 key（仅 `playerKilled=true`、`living_death` 触发时生效） |
| `stat` | string | 统计项 ResourceLocation，如 `"minecraft:mob_kills"`（仅 `stat_polling` 触发） |
| `statThreshold` | int | 统计值阈值，达到后完成（仅 `stat_polling` 触发；默认 1） |
| `biome` | string | 群系 ID 或 `#` 前缀的群系 tag，如 `"minecraft:desert"` / `"#minecraft:is_forest"`（仅 `biome_visit` 触发） |
| `dimension` | string | 维度 ResourceLocation，如 `"minecraft:the_nether"` / `"minecraft:the_end"`（仅 `biome_visit` 触发；通过 `player.level().dimension()` 比较 `ResourceKey<Level>`） |

**comparator 支持**（用于 `attributes` 条件，对比玩家当前属性值与配置阈值）：

| comparator | 含义 | 示例 |
|---|---|---|
| `gt` | 大于 (greater than) | `"comparator": "gt", "value": 60.0` → 属性 > 60 |
| `gte` | 大于等于 (greater than or equal) | `"comparator": "gte", "value": 40.0` → 属性 ≥ 40 |
| `lt` | 小于 (less than) | `"comparator": "lt", "value": 20.0` → 属性 < 20 |
| `lte` | 小于等于 (less than or equal) | `"comparator": "lte", "value": 10.0` → 属性 ≤ 10 |
| `eq` | 等于 (equal) | `"comparator": "eq", "value": 0.0` → 属性 = 0 |
| `ne` | 不等于 (not equal) | `"comparator": "ne", "value": 0.0` → 属性 ≠ 0 |

示例：

```json
"conditions": {
  "equippedCurios": ["tcc:raven", "tcc:seven_thunders_thunder_seen"],
  "requiredEffects": ["minecraft:invisibility"],
  "holdingGunTypes": ["sniper"],
  "minDistance": 100.0,
  "kills": [
    { "entity": "minecraft:ender_dragon", "value": 1 }
  ],
  "attributes": [
    { "attribute": "tcc:imaginary_damage_resistance", "comparator": "gt", "value": 60.0 }
  ]
}
```

`stat_polling` 示例：

```json
"conditions": {
  "stat": "minecraft:mob_kills",
  "statThreshold": 100,
  "equippedCurios": ["tcc:killer_ring"],
  "attributes": [
    { "attribute": "tcc:imaginary_damage_resistance", "comparator": "gte", "value": 40.0 }
  ]
}
```

`biome_visit` 示例：

```json
"conditions": {
  "biome": "#minecraft:is_nether",
  "equippedCurios": ["tcc:nether_charm"]
}
```

### reward — 奖励

完成成就后执行的奖励，支持三种类型：

#### type = "grant"（发放物品）

```json
"reward": {
  "type": "grant",
  "item": "tcc:seven_thunders",
  "overflowMode": "INVENTORY_THEN_DROP",
  "bindToPlayer": false
}
```

| 字段 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `item` | string | — | 要发放的物品 id |
| `overflowMode` | string | `"INVENTORY_THEN_DROP"` | 满背包处理：`SKIP` / `INVENTORY_THEN_DROP` / `DROP` / `REPLACE_FIRST` |
| `bindToPlayer` | boolean | `false` | 是否绑定玩家（写入 BoundPlayer NBT） |

#### type = "evolve"（进化替换）

```json
"reward": {
  "type": "evolve",
  "item": "tcc:seven_thunders",
  "to": "tcc:seven_thunders_thunder_seen",
  "bindToPlayer": true,
  "linkedEvolves": [
    { "item": "tcc:seven_thunders_thunder_seen", "to": "tcc:judgement_key" }
  ],
  "autoAchievements": ["tcc:judgement_key"]
}
```

| 字段 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `item` | string | — | 要被替换的饰品 itemId |
| `to` | string | — | 进化目标 itemId |
| `bindToPlayer` | boolean | `false` | 是否绑定玩家 |
| `linkedEvolves` | object[] | `[]` | 联动进化：同时将其他饰品替换为目标（如主进化时联动进化另一件） |
| `autoAchievements` | string[] | `[]` | 自动完成的关联成就 id（如联动进化产物对应的成就） |

每个 `linkedEvolves` 条目包含：
| 字段 | 说明 |
|---|---|
| `item` | 要联动替换的饰品 itemId |
| `to` | 联动替换的目标 itemId |

#### 无 reward（auto 触发器）

```json
"reward": null
```

用于 `trigger: "auto"` 的成就——不自行触发，仅作为 `autoAchievements` 的目标被关联完成。

---

# EntityRef（实体条件对象）

用于 `evolution_rules.json` 的 `kills[].entity` 和 `achievement_definitions.json` 的 `conditions` 中的实体匹配。

```json
{
  "entity": {
    "key": "minecraft:wither",
    "nbt": ["apoth.boss=true", "apoth.rarity=uncommon"],
    "name": "神化"
  }
}
```

| 字段 | 类型 | 必需 | 说明 |
|---|---|---|---|
| `key` | string | **是** | 实体类型 id，`"*"` 表示任意实体 |
| `nbt` | string[] | 否 | NBT 条件（格式 `key=value`，全部命中 AND；`true`/`false` 按布尔比较） |
| `name` | string | 条件必需 | UI 后缀名称（填写了 `nbt` 就必须填写，否则该条目被忽略） |

> NBT 匹配目标：实体 `persistentData`（`LivingEntity#getPersistentData()`）

---

# 运行时行为

## 击杀触发流程

以 `gun_kill` 为例（[GunKillEventHandler](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/GunKillEventHandler.java)）：

1. TACZ 枪械击杀 → `EntityKillByGunEvent`
2. 遍历所有 `trigger=gun_kill` 的 **achievement_definitions**：
   - 检查 `enabled`、前置成就、是否已完成
   - `matchesKillConditions()` 检查 `equippedCurios` / `requiredEffects` / `holdingGunTypes` / `minDistance` / `kills` / `attributes`
   - 通过则 `awardSteps()` 计步，满 critera 则执行 reward（GRANT 发放 / EVOLVE 进化 / linkedEvolves 联动进化 / autoAchievements 自动完成）
3. 遍历所有 `trigger=gun_kill` 的 **evolution_rules** (ATTRIBUTE)：
   - 检查 `enabled`、`playerKilled`、`item` 是否装备
   - `passesExtraRequirements()` 检查 `requiredEffects` / `holdingGunTypes` / `minDistance`
   - 对匹配的 `kills[]` 条目，`incrementProgress()` 写入进度 NBT
   - 刷新饰品效果

## `auto` 触发器

`trigger: "auto"` 的成就不会被动触发。它们仅在被其他成就的 `autoAchievements` 引用时自动完成。

主要用于"获得联动进化产物"类的展示成就，例如：
- `tcc:raven_to_island` 进化完成后 `autoAchievements: ["tcc:judgement_key"]`
- `tcc:brahma_to_salvation` 进化完成后 `autoAchievements: ["tcc:heaven_fire_apocalypse_endless"]`

## `stat_polling` 轮询流程

`stat_polling` 通过玩家 tick 事件轮询 MC 统计数据（[StatPollingEventHandler](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/StatPollingEventHandler.java)），**每 3 tick** 检查一次：

**achievement_definitions 流程**：
1. 通过 `conditions.stat` 查找 `BuiltInRegistries.CUSTOM_STAT` 注册的统计项
2. 调用 `player.getStats().getValue()` 获取当前统计值
3. 与 `statThreshold` 比较：
   - `criteria_count == 1`：统计值 ≥ 阈值时一次性完成
   - `criteria_count > 1`：每 1 个统计点推进 1 步

**evolution_rules (ATTRIBUTE) 流程**：
1. 通过 `stat` / `statThreshold` 检查统计是否达标
2. 检查 `passesExtraRequirements`（`equippedCurios` 等）
3. 找到第一个匹配 `item` 的装备饰品
4. 检查 `capCounterKey` 是否已达本规则上限 `cap`
5. 计算可用步数：`availableSteps = currentStat / statThreshold`，扣除已应用的 `StatEvoSteps_<ruleId>`
6. 计算可添加步数（受 `capCounterKey` 剩余额度限制），写入 `value * stepsToAdd` 到 `nbtKey` 和 `capCounterKey`，更新步数追踪
7. 调用 `curio.refreshEffects(player)` 刷新效果

## `biome_visit` 检测流程

`biome_visit` 通过玩家 tick 事件检测当前所处群系（[StatPollingEventHandler](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/StatPollingEventHandler.java)），**每 20 tick（1 秒）** 检查一次：

**achievement_definitions 流程**：
1. 获取玩家当前位置的群系：`player.level().getBiome(player.blockPosition())`
2. 判断方式：单个群系精确匹配 / `#` tag 判断
3. 命中后立即完成成就

**evolution_rules (ATTRIBUTE) 流程**：
1. 通过 `biome` 检查群系是否匹配
2. 检查 `passesExtraRequirements`
3. 找到匹配的饰品
4. 检查 NBT `StatEvoApplied_<ruleId>` 防重复
5. 写入 `value` 到 `progress.nbtKey`，标记防重复
6. 刷新饰品效果

> `stat_polling` 和 `biome_visit` 也都支持 `equippedCurios`、`attributes` 和 `dimension` 条件组合，但不支持 `kills` 条件。

---

# KubeJS 脚本 API

安装 KubeJS 2001+ 后，`server_scripts` 中注入全局对象：

- `TccEvolution` → [EvolutionScriptApi](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/EvolutionScriptApi.java)

所有 API 方法都会忽略规则的 `enabled` 字段（`ignoreEnabled=true`），用于脚本侧手动触发。

## 触发 living_death 规则

```js
EntityEvents.death(event => {
  const killer = event.source.entity
  if (!killer || !killer.isPlayer()) return
  TccEvolution.triggerLivingDeath(killer, event.entity, killer, event.source, false)
})
```

玩家被杀时（用于 GRANT）：

```js
EntityEvents.death(event => {
  if (!event.entity.isPlayer()) return
  const victim = event.entity
  const killer = event.source.entity
  TccEvolution.triggerLivingDeath(victim, victim, killer, event.source, true)
})
```

## 触发单条 achievement 的 EVOLVE

```js
const ok = TccEvolution.tryEvolve(player, 'tcc:xiora_to_raven')
```

检查该成就的条件（装备/击杀/属性），满足则执行 award + 进化奖励。

## 触发单条 ATTRIBUTE 规则

```js
TccEvolution.applyAttribute(player, killed, damageSource, 'tcc_summer_beach_gain_imaginary_resistance')
```

如果 killed 命中该规则的 `kills[]`，就写入进度并返回 true。

## 触发单条 achievement 的 GRANT

```js
TccEvolution.tryGrant(victimPlayer, killerEntity, damageSource, 'tcc:summer_beach_grant')
```

## 完全自定义进化（不依赖 JSON）

```js
EntityEvents.death(event => {
  const killer = event.source.entity
  if (!killer || !killer.isPlayer()) return
  if (event.entity.type != 'minecraft:wither') return

  TccEvolution.evolveCurio(killer, 'tcc:summer_beach', 'tcc:brahma_beasts', ['KillCounts'])
})
```

- `evolveCurio(entity, fromItemId, toItemId, excludeNbtKeys[, postTaczChangeEvent])`
- `evolveCurioByUuid(entityUuid, fromItemId, toItemId, excludeNbtKeys[, postTaczChangeEvent])`

这两个方法只做 A→B 替换 + 全量 NBT 复制，不检查任何条件。

## enabled=false 的使用建议

- 想"只脚本触发"：设置 `enabled=false`，在 KubeJS 中调用对应 API
- 不推荐同时启用自动触发和脚本触发，容易重复结算
