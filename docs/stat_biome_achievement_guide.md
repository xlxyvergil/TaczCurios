# TaczCurios 统计数据 & 群系访问成就配置文档

## 概述

支持两种新的 achievement trigger 类型：

| trigger | 说明 | 轮询间隔 |
|---|---|---|
| `stat_polling` | 读取 MC 统计数据（支持 MC 内置 + 其他 mod 注册的 stat） | 每 3 tick |
| `biome_visit` | 检测玩家当前所处的群系 | 每 20 tick（1 秒） |

---

## stat_polling — 统计数据成就

### JSON 格式

```json
{
  "achievements": {
    "<achievement_id>": {
      "enabled": true,
      "display": {
        "title": { "zh_cn": "<中文标题>", "en_us": "<English Title>" },
        "description": { "zh_cn": "<中文描述>（%d/%d）", "en_us": "<English description> (%d/%d)" },
        "icon": "<item_id>",
        "frame": "task|goal|challenge",
        "hidden": false
      },
      "parent": "<parent_achievement_id>",
      "trigger": "stat_polling",
      "criteria_count": <阈值>,
      "conditions": {
        "stat": "<stat_resource_location>",
        "statThreshold": <阈值>,
        "equippedCurios": ["<curio_id>"],
        "attributes": [
          { "attribute": "<attribute_id>", "comparator": "gt|gte|lt|lte|eq|ne", "value": <number> }
        ]
      },
      "prerequisites": ["<prereq_id>"],
      "reward": {
        "type": "grant|evolve",
        "item": "<item_id>",
        "overflowMode": "INVENTORY_THEN_DROP|SKIP|DROP|REPLACE_FIRST",
        "bindToPlayer": false
      }
    }
  }
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|---|---|---|
| `trigger` | string | 固定为 `"stat_polling"` |
| `criteria_count` | int | 和 `statThreshold` 保持一致 |
| `conditions.stat` | string | 统计项的完整 ResourceLocation，如 `"minecraft:mob_kills"` |
| `conditions.statThreshold` | int | 触发阈值，达到此数值时完成成就 |
| `conditions.equippedCurios` | string[] | 可选，需要装备的饰品列表 |
| `conditions.attributes` | object[] | 可选，需要满足的属性条件 |

### 进度模型说明

当 `criteria_count > 1` 时，成就使用**逐步推进**模式，每 1 个统计点推进 1 步。例如 `criteria_count: 100`，玩家有 35 击杀时显示 35/100。

当 `criteria_count == 1` 时，成就是**一次性完成**模式，统计值达到 `statThreshold` 后一次完成。

---

### MC 内置可用的 stat（部分列表）

#### 战斗类

| stat | 说明 |
|---|---|
| `minecraft:mob_kills` | 击杀生物总数 |
| `minecraft:player_kills` | 击杀玩家数 |
| `minecraft:damage_dealt` | 造成伤害总值 |
| `minecraft:damage_taken` | 受到伤害总值 |
| `minecraft:damage_blocked_by_shield` | 盾牌格挡伤害 |
| `minecraft:deaths` | 死亡次数 |
| `minecraft:target_hit` | 命中靶子次数 |

#### 移动类

| stat | 说明 |
|---|---|
| `minecraft:walk_one_cm` | 行走距离（cm） |
| `minecraft:sprint_one_cm` | 疾跑距离 |
| `minecraft:crouch_one_cm` | 潜行距离 |
| `minecraft:swim_one_cm` | 游泳距离 |
| `minecraft:fly_one_cm` | 飞行距离 |
| `minecraft:fall_one_cm` | 坠落距离 |
| `minecraft:climb_one_cm` | 攀爬距离 |
| `minecraft:aviate_one_cm` | 鞘翅飞行距离 |
| `minecraft:boat_one_cm` | 乘船距离 |
| `minecraft:horse_one_cm` | 骑马距离 |
| `minecraft:minecart_one_cm` | 矿车距离 |
| `minecraft:pig_one_cm` | 骑猪距离 |
| `minecraft:strider_one_cm` | 炽足兽距离 |
| `minecraft:jump` | 跳跃次数 |

#### 交互类

| stat | 说明 |
|---|---|
| `minecraft:interact_with_crafting_table` | 使用工作台 |
| `minecraft:interact_with_furnace` | 使用熔炉 |
| `minecraft:interact_with_blast_furnace` | 使用高炉 |
| `minecraft:interact_with_smoker` | 使用烟熏炉 |
| `minecraft:interact_with_anvil` | 使用铁砧 |
| `minecraft:interact_with_beacon` | 使用信标 |
| `minecraft:interact_with_brewingstand` | 使用酿造台 |
| `minecraft:interact_with_loom` | 使用织布机 |
| `minecraft:interact_with_grindstone` | 使用砂轮 |
| `minecraft:interact_with_stonecutter` | 使用切石机 |
| `minecraft:interact_with_cartography_table` | 使用制图台 |
| `minecraft:interact_with_smithing_table` | 使用锻造台 |
| `minecraft:interact_with_lectern` | 使用讲台 |
| `minecraft:interact_with_campfire` | 使用营火 |
| `minecraft:open_chest` | 打开箱子 |
| `minecraft:open_enderchest` | 打开末影箱 |
| `minecraft:open_shulker_box` | 打开潜影盒 |
| `minecraft:open_barrel` | 打开木桶 |
| `minecraft:trigger_trapped_chest` | 触发陷阱箱 |
| `minecraft:sleep_in_bed` | 在床上睡觉 |
| `minecraft:bell_ring` | 敲钟 |
| `minecraft:raid_win` | 赢得袭击 |
| `minecraft:noteblock_played` | 演奏音符盒 |
| `minecraft:noteblock_tuned` | 调音音符盒 |
| `minecraft:record_played` | 播放唱片 |
| `minecraft:talked_to_villager` | 与村民交谈 |
| `minecraft:traded_with_villager` | 与村民交易 |
| `minecraft:inspect_dispenser` | 查看发射器 |
| `minecraft:inspect_dropper` | 查看投掷器 |
| `minecraft:inspect_hopper` | 查看漏斗 |
| `minecraft:flower_potted` | 种花入盆 |
| `minecraft:clean_armor` | 清洗盔甲 |
| `minecraft:clean_banner` | 清洗旗帜 |
| `minecraft:clean_shulker_box` | 清洗潜影盒 |
| `minecraft:cauldron_filled` | 装满炼药锅 |
| `minecraft:cauldron_used` | 使用炼药锅 |

#### 物品/生存类

| stat | 说明 |
|---|---|
| `minecraft:fish_caught` | 钓鱼捕获数 |
| `minecraft:animals_bred` | 繁殖动物数 |
| `minecraft:enchant_item` | 附魔次数 |
| `minecraft:drop` | 丢弃物品次数 |
| `minecraft:play_time` | 游戏时间（tick，20 tick = 1秒） |
| `minecraft:sneak_time` | 潜行时间（tick） |
| `minecraft:time_since_rest` | 距上次睡觉的时间（tick） |
| `minecraft:leave_game` | 退出游戏次数 |
| `minecraft:cake_slices_eaten` | 吃掉蛋糕片数 |
| `minecraft:honey_bottles_drunk` | 饮用蜂蜜瓶 |

#### 其他 Mod 的 stat

只要知道 stat 的完整 ResourceLocation，就能查询。例如某个 mod 注册了 `somemod:bullets_fired`：

```json
"conditions": {
  "stat": "somemod:bullets_fired",
  "statThreshold": 1000
}
```

### 配置示例

```json
"tcc:kill_100_mobs": {
  "enabled": true,
  "display": {
    "title": { "zh_cn": "百人斩", "en_us": "Centurion" },
    "description": { "zh_cn": "击杀100个生物（%d/%d）", "en_us": "Kill 100 mobs (%d/%d)" },
    "icon": "minecraft:diamond_sword",
    "frame": "goal",
    "hidden": false
  },
  "parent": "tcc:tcc_root",
  "trigger": "stat_polling",
  "criteria_count": 100,
  "conditions": {
    "stat": "minecraft:mob_kills",
    "statThreshold": 100
  },
  "prerequisites": [],
  "reward": {
    "type": "grant",
    "item": "tcc:killer_ring",
    "overflowMode": "INVENTORY_THEN_DROP",
    "bindToPlayer": false
  }
},

"tcc:play_1_hour": {
  "enabled": true,
  "display": {
    "title": { "zh_cn": "初来乍到", "en_us": "Fresh Start" },
    "description": { "zh_cn": "累计游玩1小时", "en_us": "Play for 1 hour" },
    "icon": "minecraft:clock",
    "frame": "task",
    "hidden": false
  },
  "parent": "tcc:tcc_root",
  "trigger": "stat_polling",
  "criteria_count": 1,
  "conditions": {
    "stat": "minecraft:play_time",
    "statThreshold": 72000
  },
  "prerequisites": [],
  "reward": {
    "type": "grant",
    "item": "tcc:starter_amulet"
  }
},

"tcc:trade_200_times": {
  "enabled": true,
  "display": {
    "title": { "zh_cn": "商业大亨", "en_us": "Tycoon" },
    "description": { "zh_cn": "与村民交易200次（%d/%d）", "en_us": "Trade with villagers 200 times (%d/%d)" },
    "icon": "minecraft:emerald",
    "frame": "goal",
    "hidden": false
  },
  "parent": "tcc:tcc_root",
  "trigger": "stat_polling",
  "criteria_count": 200,
  "conditions": {
    "stat": "minecraft:traded_with_villager",
    "statThreshold": 200
  },
  "prerequisites": [],
  "reward": {
    "type": "grant",
    "item": "tcc:merchant_charm",
    "overflowMode": "INVENTORY_THEN_DROP",
    "bindToPlayer": false
  }
},

"tcc:walk_100km": {
  "enabled": true,
  "display": {
    "title": { "zh_cn": "行者", "en_us": "Traveler" },
    "description": { "zh_cn": "累计行走100公里", "en_us": "Walk 100km total" },
    "icon": "minecraft:leather_boots",
    "frame": "goal",
    "hidden": false
  },
  "parent": "tcc:tcc_root",
  "trigger": "stat_polling",
  "criteria_count": 1,
  "conditions": {
    "stat": "minecraft:walk_one_cm",
    "statThreshold": 10000000
  },
  "prerequisites": [],
  "reward": {
    "type": "grant",
    "item": "tcc:walker_boots"
  }
}
```

---

## biome_visit — 群系访问成就

### JSON 格式

```json
{
  "achievements": {
    "<achievement_id>": {
      "enabled": true,
      "display": {
        "title": { "zh_cn": "<中文标题>", "en_us": "<English Title>" },
        "description": { "zh_cn": "<中文描述>", "en_us": "<English description>" },
        "icon": "<item_id>",
        "frame": "task|goal|challenge",
        "hidden": false
      },
      "parent": "<parent_achievement_id>",
      "trigger": "biome_visit",
      "criteria_count": 1,
      "conditions": {
        "biome": "<biome_id_or_tag>",
        "equippedCurios": ["<curio_id>"],
        "attributes": [
          { "attribute": "<attribute_id>", "comparator": "gt|gte|lt|lte|eq|ne", "value": <number> }
        ]
      },
      "prerequisites": ["<prereq_id>"],
      "reward": {
        "type": "grant|evolve",
        "item": "<item_id>",
        "overflowMode": "INVENTORY_THEN_DROP|SKIP|DROP|REPLACE_FIRST",
        "bindToPlayer": false
      }
    }
  }
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|---|---|---|
| `trigger` | string | 固定为 `"biome_visit"` |
| `criteria_count` | int | 固定为 `1`（站入群系即完成） |
| `conditions.biome` | string | 群系 ID，或 `#` 前缀的群系 tag |
| `conditions.equippedCurios` | string[] | 可选 |
| `conditions.attributes` | object[] | 可选 |

### biome 字段支持三种写法

```json
// 1. 单个群系
"biome": "minecraft:desert"

// 2. 群系 tag（以 # 开头）
"biome": "#minecraft:is_forest"

// 3. 其他 mod 的群系
"biome": "biomesoplenty:redwood_forest"
```

### MC 默认可用的 biome tag（部分）

| tag | 说明 |
|---|---|
| `#minecraft:is_forest` | 所有森林群系 |
| `#minecraft:is_ocean` | 所有海洋群系 |
| `#minecraft:is_mountain` | 所有山地群系 |
| `#minecraft:is_river` | 所有河流群系 |
| `#minecraft:is_beach` | 所有沙滩群系 |
| `#minecraft:is_badlands` | 恶地群系 |
| `#minecraft:is_jungle` | 丛林群系 |
| `#minecraft:is_taiga` | 针叶林群系 |
| `#minecraft:is_nether` | 下界群系 |
| `#minecraft:is_end` | 末地群系 |
| `#minecraft:is_deep_ocean` | 深海群系 |
| `#minecraft:is_savanna` | 热带草原 |
| `#minecraft:is_hill` | 丘陵群系 |

### 配置示例

```json
"tcc:visit_desert": {
  "enabled": true,
  "display": {
    "title": { "zh_cn": "沙漠旅人", "en_us": "Desert Traveler" },
    "description": { "zh_cn": "进入沙漠群系", "en_us": "Enter a Desert biome" },
    "icon": "minecraft:sand",
    "frame": "task",
    "hidden": false
  },
  "parent": "tcc:tcc_root",
  "trigger": "biome_visit",
  "criteria_count": 1,
  "conditions": {
    "biome": "minecraft:desert"
  },
  "prerequisites": [],
  "reward": {
    "type": "grant",
    "item": "tcc:desert_scarab",
    "overflowMode": "INVENTORY_THEN_DROP"
  }
},

"tcc:enter_nether": {
  "enabled": true,
  "display": {
    "title": { "zh_cn": "地狱行者", "en_us": "Hell Walker" },
    "description": { "zh_cn": "进入下界", "en_us": "Enter the Nether" },
    "icon": "minecraft:obsidian",
    "frame": "task",
    "hidden": false
  },
  "parent": "tcc:tcc_root",
  "trigger": "biome_visit",
  "criteria_count": 1,
  "conditions": {
    "biome": "#minecraft:is_nether"
  },
  "prerequisites": [],
  "reward": {
    "type": "grant",
    "item": "tcc:nether_charm"
  }
},

"tcc:visit_warped_forest": {
  "enabled": true,
  "display": {
    "title": { "zh_cn": "诡异森林", "en_us": "Warped Forest" },
    "description": { "zh_cn": "进入诡异森林群系", "en_us": "Enter a Warped Forest biome" },
    "icon": "minecraft:warped_fungus",
    "frame": "task",
    "hidden": false
  },
  "parent": "tcc:enter_nether",
  "trigger": "biome_visit",
  "criteria_count": 1,
  "conditions": {
    "biome": "minecraft:warped_forest",
    "equippedCurios": ["tcc:nether_charm"]
  },
  "prerequisites": ["tcc:enter_nether"],
  "reward": {
    "type": "evolve",
    "item": "tcc:nether_charm",
    "to": "tcc:warped_charm"
  }
},

"tcc:visit_any_forest": {
  "enabled": true,
  "display": {
    "title": { "zh_cn": "森林漫步", "en_us": "Forest Walker" },
    "description": { "zh_cn": "进入任意森林群系", "en_us": "Enter any forest biome" },
    "icon": "minecraft:oak_sapling",
    "frame": "task",
    "hidden": false
  },
  "parent": "tcc:tcc_root",
  "trigger": "biome_visit",
  "criteria_count": 1,
  "conditions": {
    "biome": "#minecraft:is_forest"
  },
  "prerequisites": [],
  "reward": {
    "type": "grant",
    "item": "tcc:forest_ring"
  }
}
```

---

## 可组合性

`stat_polling` 和 `biome_visit` 成就都可以和现有的条件字段组合使用：

- `equippedCurios`：需要装备指定饰品
- `attributes`：需要满足属性条件（如虚数抗性 > 60）

```json
"conditions": {
  "stat": "minecraft:mob_kills",
  "statThreshold": 1000,
  "equippedCurios": ["tcc:killer_ring"],
  "attributes": [
    { "attribute": "tcc:imaginary_damage_resistance", "comparator": "gte", "value": 40.0 }
  ]
}
```

**注意**：`kill` 条件（entity/NBT/value）仅适用于 `gun_kill` / `gun_headshot_kill` trigger，在 `stat_polling` 和 `biome_visit` 中不生效。
