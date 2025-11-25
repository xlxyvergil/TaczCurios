# TAA Mod - Tacz属性增强模组


### 伤害属性

- **通用枪械伤害**: `taa:bullet_gundamage` - 所有枪械的基础伤害加成

### 特定枪械伤害属性

- **手枪**: `taa:bullet_gundamage_pistol` - 手枪伤害加成
- **步枪**: `taa:bullet_gundamage_rifle` - 步枪伤害加成
- **霰弹枪**: `taa:bullet_gundamage_shotgun` - 霰弹枪伤害加成
- **狙击枪**: `taa:bullet_gundamage_sniper` - 狙击枪伤害加成
- **冲锋枪**: `taa:bullet_gundamage_smg` - 冲锋枪伤害加成
- **轻机枪**: `taa:bullet_gundamage_lmg` - 轻机枪伤害加成
- **发射器**: `taa:bullet_gundamage_launcher` - 发射器伤害加成

### 20个核心枪械属性

- **瞄准时间**: `taa:ads_time` - 瞄准速度加成（数值越小越好）
- **弹药速度**: `taa:ammo_speed` - 子弹飞行速度加成
- **护甲穿透**: `taa:armor_ignore` - 护甲穿透效果
- **有效射程**: `taa:effective_range` - 射击距离加成
- **移动速度**: `taa:move_speed` - 持枪移动速度
- **爆头倍数**: `taa:headshot_multiplier` - 爆头伤害倍率
- **击退效果**: `taa:knockback` - 子弹击退力度
- **穿透能力**: `taa:pierce` - 子弹穿透能力
- **射速**: `taa:rounds_per_minute` - 射击速度
- **后坐力**: `taa:recoil` - 枪械后坐力
- **不准确度**: `taa:inaccuracy` - 射击散布程度（数值越小越好）
- **重量**: `taa:weight` - 枪械重量
- **弹药容量加成**: `taa:magazine_capacity` - 弹匣容量的额外加成
- **装填速度加成**: `taa:reload_speed` - 装填速度的额外加成
- **近战伤害加成**: `taa:melee_damage` - 枪械近战攻击伤害的额外加成
- **近战距离加成**: `taa:melee_distance` - 枪械近战攻击距离的额外加成
- **子弹数量加成**: `taa:bullet_count` - 每次射击发射子弹数量的额外加成


### 特殊效果属性

- **消音效果**: `taa:silence` - 消音效果（<1.0时自动开启被动消音）
- **点燃效果**: `taa:ignitefire` - 子弹点燃效果

### 爆炸系统属性

- **爆炸半径**: `taa:explosion_radius` - 爆炸范围加成
- **爆炸伤害**: `taa:explosion_damage` - 爆炸伤害加成
- **爆炸击退**: `taa:explosion_knockbacknew` - 爆炸击退效果
- **破坏方块**: `taa:explosion_destroy_blocknew` - 爆炸破坏方块
- **爆炸延迟**: `taa:explosion_delay` - 爆炸延迟时间

### 配件Modifier系统属性

- **近战伤害**: `melee_damage` - 枪械近战攻击造成的伤害
- **近战距离**: `melee_distance` - 枪械近战攻击的有效距离
- **弹匣容量**: `magazine_capacity` - 枪械弹匣可装载的子弹数量
- **装填时间**: `reload_time` - 枪械装填子弹所需的时间
- **子弹数量**: `bullet_count` - 每次射击发射的子弹数量

### 属性值说明

- **基础值**: 1.0 (100%效果)
- **加成值**: 0.5 = 50%加成，1.0 = 100%加成
- **布尔属性**: 0.0表示false，1.0表示true
- **被动属性**: 某些属性在特定条件下自动触发（如消音效果<1.0时）




### 属性值说明

- **基础值**: 1.0 (100%效果)
- **加成值**: 0.5 = 50%加成，1.0 = 100%加成
- **布尔属性**: 0.0表示false，1.0表示true
- **被动属性**: 某些属性在特定条件下自动触发（如消音效果<1.0时）

## 伤害计算规则

### 根据配置项选择生效规则

模组支持三种伤害计算模式，可通过配置文件动态切换：

#### 1. MAX模式（默认）
- **规则**: 通用与特定取最大值
- **公式**: `Math.max(通用伤害, 特定伤害)`
- **描述**: 取通用伤害加成和特定枪械伤害加成中的最大值

#### 2. ADDITIVE模式
- **规则**: 通用+特定-1
- **公式**: `通用伤害 + 特定伤害 - 1.0D`
- **描述**: 将两种伤害加成相加后减去基础值

#### 3. MULTIPLICATIVE模式
- **规则**: 通用*特定
- **公式**: `通用伤害 * 特定伤害`
- **描述**: 两种伤害加成相乘

### 配置方式

通过修改配置文件 `taa-attributes.toml` 中的 `damageCalculationMode` 选项来切换计算模式。

## 技术实现

### 核心组件

- **PropertyCalculator**: 属性计算器，负责所有属性的计算逻辑
- **PlayerAttributeHelper**: 玩家属性助手类，负责从玩家身上获取属性值
- **PropertyCacheUpdater**: 缓存更新器，将计算结果更新到附件缓存
- **GunPropertiesInitializer**: 枪械属性初始化器，动态获取玩家属性值
- **AttributeConfig**: 配置系统，管理伤害计算模式等配置项
- **GunTypeContext**: 枪械类型上下文，跟踪当前处理的枪械类型

### 客户端/服务端兼容性

#### 客户端安全设计
- 所有客户端UI显示方法均使用 `@OnlyIn(Dist.CLIENT)` 注解标识
- 客户端专用的Mixin类被正确配置在 `taa.mixins.json` 的 `client` 数组中
- 服务端不会加载任何客户端专用代码，确保服务端稳定性

#### 数据一致性保障
- Modifier系统完全兼容TACZ原版配件机制
- 属性计算在服务端进行，客户端仅负责显示计算结果
- 所有属性修改均通过标准的AttachmentPropertyManager进行处理

### 属性计算特性

#### 数值属性计算
- 基于乘法因子模式：`原始值 × 玩家属性因子`
- 兼容配件系统，不覆盖已有配件效果

#### 特殊属性计算
- **消音系统**: 支持被动消音（属性值<1.0时自动开启）
- **爆炸系统**: 完全兼容TACZ的ExplosionData格式
- **点燃效果**: 使用布尔逻辑判断

#### 缓存处理
- 正确处理枪械子弹数据中的爆炸字段
- 兼容配件和枪械本身的爆炸效果
- 智能判断属性生效条件

### 属性获取流程

1. **事件触发** - 枪械属性处理事件发生时
2. **类型识别** - 通过Tacz API获取当前枪械类型
3. **上下文设置** - 设置当前枪械类型到GunTypeContext
4. **属性计算** - 根据配置选择伤害计算规则
5. **缓存更新** - 将计算结果更新到附件缓存系统
6. **动态应用** - TACZ系统自动应用更新后的属性

## 配件系统兼容性

### 自定义Modifier支持
本模组实现了5个全新的自定义Modifier，完全兼容TACZ配件系统：
- **近战伤害Modifier** (`melee_damage`) - 控制枪械近战攻击伤害
- **近战距离Modifier** (`melee_distance`) - 控制枪械近战攻击距离
- **弹匣容量Modifier** (`magazine_capacity`) - 控制枪械弹匣容量
- **装填时间Modifier** (`reload_time`) - 控制枪械装填时间
- **子弹数量Modifier** (`bullet_count`) - 控制每次射击发射的子弹数量

### 配件制作兼容性
- 配件制作者可以创建使用上述Modifier ID的自定义配件
- 所有Modifier遵循TACZ原版标准实现模式，确保与其他配件兼容
- 客户端UI显示完全集成到TACZ原版配件界面中

### 属性叠加规则
- 玩家属性与配件属性完全独立计算
- 配件属性通过Modifier系统应用，不影响玩家属性计算
- 多个相同类型的配件可以同时安装，其效果会按照TACZ标准规则叠加

## 配置文件

配置文件位置：`config/taa-attributes.toml`

### 主要配置项

``toml
[枪械伤害计算设置]
# 枪械伤害计算模式
# MAX: 通用与特定取最大值
# ADDITIVE: 通用+特定-1
# MULTIPLICATIVE: 通用*特定
damageCalculationMode = "MAX"

[调试日志设置]
# 是否启用调试日志记录
# true: 启用调试日志，将记录属性计算等详细信息
# false: 禁用调试日志，不记录任何调试信息（默认）
enableDebugLogging = false
```

## 版本信息

- **当前版本**: 1.0.7（基于时间戳的快照版本）
- **Minecraft版本**: 1.20.1
- **Tacz兼容版本**: 1.1.6-hotfix
- **最后更新**: 2025年11月26日

## 依赖要求

- **Minecraft Forge**: 对应1.20.1版本
- **Tacz Guns Mod**: 1.1.6-hotfix或兼容版本
- **Java**: 17或更高版本

## 注意事项

- 属性效果与TACZ原版配件系统完全兼容
- 爆炸系统支持枪械本身子弹数据中的爆炸效果
- 消音系统支持被动触发模式
- 所有属性计算都基于乘法因子，保持数值平衡
- 客户端UI显示不影响服务端运行，确保多人游戏兼容性
