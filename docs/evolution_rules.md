# evolution_rules.json 规则说明

该文件用于配置 Curios 饰品的进化（EVOLVE）、属性成长（ATTRIBUTE）与发放（GRANT）规则。

- 运行时路径：`config/tcc/evolution_rules.json`
- 默认模板来源：[evolution_rules.json](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/resources/tcc_defaults/evolution_rules.json)
- 解析与字段定义位置：[EvolutionRegistry](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/EvolutionRegistry.java)
- 通用触发器（死亡事件）处理位置：[LivingDeathEventHandler](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/LivingDeathEventHandler.java)

## 顶层结构

文件根对象必须包含 `rules`。

### rules 的两种写法

1) 推荐：对象形式（ruleId 作为 key）

```json
{
  "rules": {
    "tcc:example_rule": {
      "enabled": true,
      "trigger": "living_death",
      "type": "evolve"
    }
  }
}
```

2) 数组形式（每个元素必须包含 `ruleId` 字段）

```json
{
  "rules": [
    {
      "ruleId": "tcc:example_rule",
      "enabled": true,
      "trigger": "living_death",
      "type": "evolve"
    }
  ]
}
```

## Rule（规则对象）字段

### 通用字段（所有 type 均可出现）

- `enabled`：是否启用；缺省为 true
- `trigger`：触发器名字符串；目前通用规则触发器是 `living_death`
- `type`：规则类型（不区分大小写）
  - `"evolve"`（默认值）：进化替换
  - `"attribute"` / `"gain_attribute"` / `"modifier"`：属性成长
  - `"grant"` / `"obtain"`：发放
- `playerKilled`：用于区分触发分支；缺省为 false
  - false：玩家作为击杀者（玩家击杀某生物）
  - true：玩家作为死亡者（玩家被杀）
- `damageSourceTags`：伤害来源必须同时命中这些 DamageType Tag；缺省/空数组表示不限制
- `item`：需要追踪/处理的饰品 itemId（例如 `tcc:summer_beach`）
- `to`：EVOLVE 目标 itemId
- `excludeNbtKeys`：EVOLVE 时从 “全量 NBT 继承” 中排除的 key 列表
- `requirements`：条件集合（见下文）
- `requirementsRef`：联动进化列表（见下文）

### requirements（条件对象）

- `requirements.equippedCurios`：玩家必须同时装备的饰品 itemId 列表
- `requirements.kills`：击杀计数门槛（仅 EVOLVE 使用）
  - 每条包含：
    - `entity`：实体条件（EntityRef，见下文）
    - `count`：所需累计次数
- `requirements.attributes`：属性门槛列表
  - 每条包含：
    - `attribute`：属性 id（例如 `tcc:imaginary_resistance`）
    - `comparator`：比较符（缺省按 gte 处理）
      - `gt` / `>`：大于
      - `gte` / `>=`：大于等于
      - `lt` / `<`：小于
      - `lte` / `<=`：小于等于
      - `eq` / `==`：等于
      - `ne` / `!=`：不等于
    - `value`：阈值

### requirementsRef（联动进化）

当主 EVOLVE 进化成功后，会额外尝试把玩家身上所有满足 `item` 的物品进化为 `to`。

```json
{
  "requirementsRef": [
    { "item": "tcc:brahma_beasts", "to": "tcc:salvation" }
  ]
}
```

## EntityRef（实体条件对象）

EntityRef 用于描述 “目标实体类型 + NBT 条件 + UI 后缀名”。

```json
{
  "entity": {
    "key": "minecraft:wither",
    "nbt": ["apoth.boss=true", "apoth.rarity=uncommon"],
    "name": "神化"
  }
}
```

- `key`：实体类型 id（必填）
- `nbt`：NBT 条件列表（可选）
  - 多条默认全部满足（AND）
  - 条件语法：`<key>=<value>`
    - `<value>` 为 `true/false` 时按布尔比较
    - 否则按字符串全等比较
  - 匹配目标：实体 `persistentData`（`LivingEntity#getPersistentData()`）
- `name`：用于 UI 展示的后缀名（可选）
  - 只要填写了 `nbt`，就必须填写 `name`，否则该 EntityRef 会被判为无效并忽略

## type = EVOLVE（进化）

EVOLVE 会在玩家装备了 `item` 的情况下，持续累积击杀计数，并在满足条件时把该饰品替换为 `to`。

关键点：

- 击杀计数写入饰品 NBT：`KillCounts`（CompoundTag）
  - key 为 `entity.key` 或 `entity.key[nbt...]`（用于区分同实体不同 NBT 变体）
  - 代码位置：[LivingDeathEventHandler.incrementKillCount](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/LivingDeathEventHandler.java#L231-L241)
- 满足条件判断：`requirements.equippedCurios` + `requirements.kills` + `requirements.attributes`
  - 代码位置：[LivingDeathEvolutionRuleMatcher.matches](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/LivingDeathEvolutionRuleMatcher.java#L15-L44)
- 进化时默认全量继承 NBT（COPY_ALL），并应用 `excludeNbtKeys`

最小示例：

```json
{
  "enabled": true,
  "trigger": "living_death",
  "type": "evolve",
  "item": "tcc:summer_beach",
  "to": "tcc:brahma_beasts",
  "requirements": {
    "kills": [
      { "entity": { "key": "minecraft:wither" }, "count": 10 }
    ]
  }
}
```

## type = ATTRIBUTE（属性成长）

ATTRIBUTE 用于把 “击杀某实体 → 增加一段进度” 写入饰品 NBT，之后由饰品自身读取该进度并作为属性加成来源。

字段：

- `kills`：击杀增量表（每条包含 `entity` 与 `value`）
- `progress`：进度写入配置
  - `attribute`：对应的属性 id（用于标识这条成长属于哪个属性）
  - `operation`：ADD / MULTIPLY_BASE / MULTIPLY_TOTAL（缺省 ADD）
  - `cap`：上限
  - `nbtKey`：写入 NBT 的 key（可选）
    - 缺省时会由 `attribute` 自动推导为 `Progress_<attributeId规范化>`
    - 代码位置：[EvolutionRegistry.progressKeyFromAttribute](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/EvolutionRegistry.java#L290-L294)

最小示例：

```json
{
  "enabled": true,
  "trigger": "living_death",
  "type": "attribute",
  "item": "tcc:summer_beach",
  "kills": [
    { "entity": { "key": "minecraft:wither" }, "value": 1.0 }
  ],
  "progress": {
    "attribute": "tcc:imaginary_resistance",
    "operation": "ADD",
    "cap": 80.0
  }
}
```

## type = GRANT（发放）

GRANT 用于 “满足条件时给玩家发放物品” 的规则，支持两种触发模式：

- 玩家击杀生物时触发：`playerKilled=false`（默认）
- 玩家死亡时触发：`playerKilled=true`

关键点：

- `damageSourceTags`：同其它规则一样会生效（可以只允许“虚数伤害”触发发放）
- `item`（可选）：如果填写，表示必须装备该饰品才会触发该发放规则（相当于“该饰品自带的击杀掉落/发放逻辑”）
- `requirements.equippedCurios`：同样会检查玩家是否装备这些饰品（与 `item` 可以叠加）
- `requirements.kills`：
  - 当 `playerKilled=false`（玩家击杀触发）时：用于匹配“被击杀的实体”，命中任意一条即可触发发放
  - 当 `playerKilled=true`（玩家死亡触发）时：不支持（写了会直接判失败）
- `killer`：
  - 仅当 `playerKilled=true`（玩家死亡触发）时生效，用来匹配“凶手实体”

字段：

- `killer`：凶手实体条件（EntityRef，仅玩家死亡触发时生效）
- `grant`：
  - `item`：要发放的物品 id
  - `overflowMode`：装备槽满时的处理策略
    - `SKIP`：装备槽/背包放不下时直接跳过（不发）
    - `INVENTORY_THEN_DROP`：优先塞进玩家背包；背包满则掉落到地上
    - `DROP`：直接掉落到地上
    - `REPLACE_FIRST`：替换该饰品槽的第一个格子，旧物塞回背包
  - `oncePerPlayer`：是否每个玩家只允许触发一次（可选，默认 false）
    - true：默认用该规则的 `ruleId` 作为玩家 NBT 键（写入 true），实现“一人一次”
  - `bindToPlayer`：是否给发放物品写入绑定 NBT（BoundPlayer/BoundPlayerName/IsBound）

最小示例：

```json
{
  "enabled": true,
  "trigger": "living_death",
  "type": "grant",
  "playerKilled": false,
  "item": "tcc:summer_beach",
  "requirements": {
    "kills": [
      { "entity": { "key": "minecraft:wither" }, "count": 1 }
    ]
  },
  "grant": {
    "item": "tcc:heaven_fire_judgment",
    "overflowMode": "INVENTORY_THEN_DROP",
    "oncePerPlayer": true,
    "bindToPlayer": true
  }
}
```

## enabled = false 时：KubeJS 如何调用

场景：你希望保留规则定义（让数值与条件仍由 JSON 维护），但不让它自动随 `living_death` 触发；而是由整合包作者在 KubeJS 中按自己条件触发。

本模组在资源中提供了 [kubejs.bindings.txt](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/resources/kubejs.bindings.txt)，当安装 KubeJS 2001+（1.20.1）后，会在 **server scripts** 里注入全局对象：

- `TccEvolution` → [EvolutionScriptApi](file:///d:/TaczAttributeAdd/TaczCurios%201.20.1/TaczCurios/src/main/java/com/xlxyvergil/tcc/evolution/EvolutionScriptApi.java)

这些方法都会忽略规则的 `enabled`（即使 `enabled=false` 也会执行），用于脚本侧手动触发。

### 1) 复用 living_death 触发器（执行所有匹配 trigger 的规则）

当你把某些规则 `enabled=false` 后，可以在 KubeJS 的 `EntityEvents.death` 里自行调用：

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

### 2) 只触发单条规则

#### EVOLVE：按 ruleId 触发一次进化判定

```js
// 满足条件（装备/击杀计数/属性阈值）就会进化；不满足则返回 false
const ok = TccEvolution.tryEvolve(player, 'tcc_summer_beach_to_tcc_brahma_beasts')
```

#### ATTRIBUTE：对单条属性成长规则做一次“击杀结算”

```js
// 如果 killed 命中该规则 kills[]，就会写入 progress 并返回 true
TccEvolution.applyAttribute(player, killed, damageSource, 'tcc_summer_beach_gain_imaginary_resistance')
```

#### GRANT：对单条发放规则做一次“玩家死亡结算”

```js
TccEvolution.tryGrant(victimPlayer, killerEntity, damageSource, 'tcc_player_death_grant_tcc_summer_beach')
```

### 3) 建议的配置方式

- 想“只脚本触发”：把对应 rule 的 `enabled` 设为 false，然后在 KubeJS 中按你的条件调用上面的 API
- 想“事件自动触发 + 脚本额外触发”：保持 `enabled=true`，但要注意可能出现重复结算（一般不推荐）

## KubeJS 完全自定义规则：仅执行 A → B + NBT 复制

如果你不想依赖 `evolution_rules.json` 的任何条件/规则（完全由 KubeJS 自己决定“何时进化、进化谁、排除哪些 NBT”），可以直接调用：

- `TccEvolution.evolveCurio(entity, fromItemId, toItemId, excludeNbtKeys[, postTaczChangeEvent])`
- `TccEvolution.evolveCurioByUuid(entityUuid, fromItemId, toItemId, excludeNbtKeys[, postTaczChangeEvent])`

这两个接口只做一件事：

- 把该实体 Curios 身上匹配 `fromItemId` 的饰品替换成 `toItemId`
- 复制旧饰品的全部 NBT 到新饰品，然后按 `excludeNbtKeys` 删除指定 key
- 不检查击杀数、不检查属性阈值、不检查任何 requirements（所有条件由 KubeJS 负责）

示例：用 `EntityEvents.death` 自己写一套“击杀凋灵就进化”的逻辑：

```js
EntityEvents.death(event => {
  const killer = event.source.entity
  if (!killer || !killer.isPlayer()) return
  if (event.entity.type != 'minecraft:wither') return

  const excludes = ['KillCounts']
  TccEvolution.evolveCurio(killer, 'tcc:summer_beach', 'tcc:brahma_beasts', excludes)
})
```
