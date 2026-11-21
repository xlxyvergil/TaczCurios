# 泰辛工作台商店（B 方案 · 列表 + 购物车 · 纯代码绘制）实现方案

> 目标版本：NeoForge 1.21.1（neo_version 21.1.248），mod `tcc` 1.5.2，Java 21。
> 本文只描述方案，不含实现代码；伪代码用于锁定时序与调用点。
>
> 依据：
> - m00195：不做存档迁移兼容；走 B 方案（自实现 GUI）；不要村民随机/分级。
> - m00347/m00348：UI 参照截图（顶部余额 + 搜索 + 列表行带复选框/数量步进/小计，
>   右上「购买所选商品」，底部「购买预览」与合计）——**不使用贴图，全部用 `fill` / 文字 /
>   `renderItem` 直接绘制**。
> - ask_user_question 结果：货币沿用**下界合金废料**；购买方式取**完整版购物车**；保留搜索框。

---

## 1. 概要

右键「泰辛工作台」打开一个 340×240 的商店界面：

- 顶部：左侧废料余额（图标 + 数量），中间搜索框，右侧「购买所选商品」按钮；
- 中部：37 件商品的可滚动列表，每行 = 复选框 + 图标 + 名称 + 持有量 + `-`/数量/`+` + 该行小计；
- 底部：购物车预览（每行 `名称 ×N 单价·小计`）+ 底部合计「需要支付：N」；
- 勾选即加入购物车，`+`/`-` 调整数量，点「购买所选商品」一次性扣废料并发货。

界面**没有任何自定义贴图**：面板、行、复选框、按钮、滚动条全部用 `GuiGraphics.fill` 画，
物品用 `renderItem`，文字用 `drawString`；只有搜索框使用原版 `EditBox`、
右上购买按钮使用原版 `Button`（自带原版纹理，不需要我们生成资源）。

与上一版（6×7 网格 + 单件购买）相比，本版新增：数量步进、购物车、合计校验、搜索过滤、
滚动列表、S2C 结果包。工作量约为网格版的 2 倍，但交互与参考图一致。

---

## 2. 架构总览

```
右键方块
  └─ TeshinWorkbenchBlock.useWithoutItem（服务端）
       └─ ServerPlayer.openMenu(new TccShopMenuProvider(pos))
            └─ writeClientSideData 写入 BlockPos → AdvancedOpenScreenPayload（原版通道）
                 └─ 客户端 TccShopMenu(id, inv, buf) 读 pos
                      └─ TeshinShopScreen（列表 / 搜索 / 购物车）

点「购买所选商品」
  └─ NetworkHandler.sendShopBuy(List<Line>)            Line = (index, count)
       └─ PacketDistributor.sendToServer(ShopBuyC2SPacket)
            └─ 服务端 ShopPurchaseHandler.buy(player, lines)
                 ├─ 菜单 / 距离 / 索引 / 数量 / 总价校验
                 ├─ 容量模拟（canFit）→ 扣料 → 发全部物品（原子性）
                 ├─ 反馈：playNotifySound + displayClientMessage
                 └─ PacketDistributor.sendToPlayer(ShopResultS2CPacket(success))
                      └─ 客户端成功时清空购物车
```

无 BlockEntity、无菜单槽位、无 Merchant。背包变化走原版同步，界面每帧自己读背包。
共 2 个新网络包（C2S 购买、S2C 结果），都是 `VarInt` 级别的小包。

---

## 3. 文件变更清单

工程根：`D:\TaczAttributeAdd\TaczCurios 1.21.1\TaczCurios 1.21.1`

### 新增（11 个 Java，0 个资源贴图）

| 路径 | 作用 |
|---|---|
| `src/main/java/com/xlxyvergil/tcc/registries/TeshinWorkbenchBlock.java` | 工作台方块子类，重写 `useWithoutItem` |
| `src/main/java/com/xlxyvergil/tcc/shop/TccShopEntry.java` | `record TccShopEntry(Item item, int cost)` |
| `src/main/java/com/xlxyvergil/tcc/shop/TccShopCatalog.java` | 37 条商品表 + 排序 + 安全读取 |
| `src/main/java/com/xlxyvergil/tcc/shop/TccShopMenuProvider.java` | `MenuProvider`，开界面并写 BlockPos |
| `src/main/java/com/xlxyvergil/tcc/shop/TccShopMenu.java` | 0 槽位菜单，只存 BlockPos |
| `src/main/java/com/xlxyvergil/tcc/registries/TccMenus.java` | 注册 `MenuType<TccShopMenu>` |
| `src/main/java/com/xlxyvergil/tcc/network/ShopBuyC2SPacket.java` | C2S 购买包（Line 列表） |
| `src/main/java/com/xlxyvergil/tcc/network/ShopResultS2CPacket.java` | S2C 购买结果（boolean） |
| `src/main/java/com/xlxyvergil/tcc/network/ShopPurchaseHandler.java` | 服务端事务：校验/容量模拟/扣料/发货 |
| `src/main/java/com/xlxyvergil/tcc/client/shop/TeshinShopScreen.java` | 商店界面（列表 + 搜索 + 购物车 + 绘制） |
| `src/main/java/com/xlxyvergil/tcc/client/shop/TccShopScreens.java` | `RegisterMenuScreensEvent` 订阅 |

### 修改（4 个）

| 路径 | 改动 |
|---|---|
| `src/main/java/com/xlxyvergil/tcc/registries/TccBlocks.java` | `TESHIN_WORKBENCH` 改为 `new TeshinWorkbenchBlock(...)`，类型仍是 `Block` |
| `src/main/java/com/xlxyvergil/tcc/TaczCurios.java` | 删 POI/职业注册与 `TccVillagers::onVillagerTrades`；加 `event.register(Registries.MENU, TccMenus::init)` |
| `src/main/java/com/xlxyvergil/tcc/network/NetworkHandler.java` | 加两条 `playToServer` / `playToClient` 注册 + `sendShopBuy(...)` 助手 |
| `src/main/resources/assets/tcc/lang/{zh_cn,en_us}.json` | 删 `entity.minecraft.villager.tcc.teshin`；加 `tcc.shop.*` |

### 删除（3 个）

- `src/main/java/com/xlxyvergil/tcc/villagers/TccVillagers.java`
- `src/main/java/com/xlxyvergil/tcc/registries/TccPoiTypes.java`
- `src/main/resources/data/minecraft/tags/point_of_interest_type/acquirable_job_site.json`

> 方块本体相关资源（`blockstates`、`models`、`loot_table`、`recipe`、`mineable/pickaxe`）全部保留。
> 不做任何存档迁移代码（用户明确要求）。

---

## 4. 商品目录（37 条，价格沿用原交易表）

- 排序：按 `cost` 升序，同价保持原 `TccVillagers.java:59-99` 顺序（稳定排序）。
- 每行可购买数量：1..64。
- 无使用次数上限（村民的 `maxUses=64` 不迁移）。

| # | `TccItems` 字段 | 废料/个 |
|---|---|---|
| 0-8 | `CORE_FUSION` `PROPHECY_PACT` `BURST_RELOAD` `SUSTAINED_FIRE` `OPPRESSION_POINT` `SWORD_WIND` `SHOTGUN_EXPANSION` `MAGAZINE_BOOST` `TANDEM_MAGAZINE` | 2 |
| 9-15 | `LETHAL_CRIT` `THUNDER_BARREL` `PISTOL_MASTERY` `STEEL_SLASH` `DISMEMBERMENT` `FRAGMENT_SHOT` `HYDRAULIC_CROSSHAIR` | 6 |
| 16-24 | `RIFLING` `CLOSE_RANGE_SHOT` `WASP_STINGER` `URAL_WOLF_TAG` `SOLDIER_BASIC_TAG` `RED_MOVEMENT_TAG` `LIMIT_SPEED` `FEROCIOUS_EXTENSION` `TACTICAL_RELOAD` | 12 |
| 25-28 | `WEAKNESS_MASTERY` `SHARP_BULLET` `LASER_SCOPE` `SHARP_AMMO` | 24 |
| 29-35 | `ALLOY_DRILL` `EVIL_ACCURACY` `CHAMBER` `SPLIT_CHAMBER` `BULLET_SPREAD` `WEAKNESS_SENSE` `DESTRUCTION` | 48 |
| 36 | `RIFT_SILVER` | **64** |

> 原表最后一条是 64 不是 48（`TccVillagers.java:99`），迁移时逐条照抄。
> 改价只动 `TccShopCatalog` 里那一个数组。

---

## 5. 布局与绘制规格（340 × 240，全部代码绘制）

坐标一律相对面板左上角 `(leftPos, topPos)`；数字单位是 GUI 像素。

```
0                                                                340
┌──────────────────────────────────────────────────────────────────┐ 0
│ [废料图标] 12       [搜索框________________]        [购买所选商品] │ 6..22
├──────────────────────────────────────────────────────────────────┤ 26
│ ☑ [icon] 铁锭            持有 3   - [ 2 ] +            ×2 = 24    │
│ ☐ [icon] 铳锭            持有 0   - [ 1 ] +            ×1 = 6     │
│ …（视口 5 行，可滚轮滚动，右侧滚动条）                              │
├──────────────────────────────────────────────────────────────────┤ 150
│ 购买预览                                                          │ 154
│ [icon] 合金钻 ×2   单价 48   96                                   │
│ [icon] 铤银   ×1   单价 64   64                                   │
│ …（超过 3 行可滚轮滚动）                                            │
│ 需要支付：160                                                     │ 224
└──────────────────────────────────────────────────────────────────┘ 240
```

### 5.1 区域坐标表

| 区域 | 矩形 | 说明 |
|---|---|---|
| 面板 | (0, 0, 340, 240) | 外框 + 底色 |
| 废料图标 | (8, 6) 16×16 | `Items.NETHERITE_SCRAP` |
| 废料数量 | (28, 10) | 白色文字，右对齐可到 x=90 |
| 搜索框 | (100, 6, 120, 16) | 原版 `EditBox`，提示词「搜索」 |
| 购买按钮 | (236, 6, 96, 16) | 原版 `Button`，标签「购买所选商品」 |
| 列表视口 | (8, 26, 324, 124) | 每行 24 高，可见 5 行（26..146） |
| 滚动条轨 | (334, 26, 4, 124) | 背景轨 |
| 滚动条滑块 | (334, 26 + thumbY, 4, thumbH) | `thumbH = max(12, 124 * 5 / 37)` |
| 购物车面板 | (8, 150, 324, 84) | 标题 + 3 行 + 合计 |
| 购物车行 | (8, 166 + i*20, 324, 18) | i ∈ 0..2 |
| 合计文字 | (12, 224) | 金色 |

### 5.2 列表行内部坐标（行左上角 `(8, 26 + (r - scroll) * 24)`，宽 324）

| 元素 | 相对行左上角 | 命中区 |
|---|---|---|
| 复选框 | (5, 7) 10×10 | (4, 6, 12, 12) |
| 物品图标 | (20, 4) 16×16 | 整行（点名选中） |
| 名称 | (40, 4) | — |
| 持有量 | (40, 14)，灰色 | — |
| `-` 按钮 | (150, 6) 12×12 | (149, 5, 14, 14) |
| 数量文字 | (170, 7) 居中 | (163, 5, 22, 14) 点击 = +1 |
| `+` 按钮 | (186, 6) 12×12 | (185, 5, 14, 14) |
| 小计 | 右对齐到 x=318 | — |

### 5.3 配色（ARGB）

| 用途 | 颜色 |
|---|---|
| 面板外框 | `0xFF3A3A3A` |
| 面板底 | `0xF0181818` |
| 列表区底 | `0xF0222222` |
| 行底 | `0xFF2A2A2A` |
| 行底（悬停） | `0xFF343434` |
| 行底（已选） | `0xFF2E4A78` |
| 行分隔线 | `0xFF1A1A1A` |
| 复选框框 | `0xFF555555`；勾选填充 `0xFF4FA85A` |
| 主文字 | `0xFFFFFFFF` |
| 次文字（持有量） | `0xFF9A9A9A` |
| 价格/合计 | `0xFFFFC84A` |
| 数量不足 | `0xFFFF5555` |
| 滚动条轨/滑块 | `0xFF2A2A2A` / `0xFF6A6A6A` |

### 5.4 绘制顺序（`render`）

1. `super.render(...)`：走 `renderBg`（画面板与列表底）+ 两个原版控件 + `renderLabels`（本类覆盖为空）。
2. 画可见行：底 → 复选框 → 图标 → 名称/持有量 → 步进按钮 → 小计。
3. 画滚动条。
4. 画购物车面板：标题 → 行 → 合计。
5. 悬停行 tooltip（`renderTooltip(font, List<Component>, Optional.empty(), mx, my)`）。
6. 更新 `buyButton.active = !cart.isEmpty() && scrap >= totalCost`。

> 行与购物车都在 `super.render` 之后画，因此会盖住原版控件之外的一切；搜索框与购买按钮
> 只占顶部 0..22，与 26 以下的内容不重叠。

---

## 6. 逐类设计

### 6.1 `TeshinWorkbenchBlock`

```java
public class TeshinWorkbenchBlock extends Block {
    public TeshinWorkbenchBlock(BlockBehaviour.Properties properties) { super(properties); }

    @Override protected MapCodec<? extends Block> codec() {
        return simpleCodec(TeshinWorkbenchBlock::new);        // BlockBehaviour.java:143
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                         Player player, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new TccShopMenuProvider(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
```

`useWithoutItem` 是 `protected`（`BlockBehaviour.java:222`）；`TccBlocks.java:21` 只换构造，注册、BlockItem、
模型、掉落、配方全部不动。

### 6.2 `TccShopMenuProvider`

```java
public record TccShopMenuProvider(BlockPos pos) implements MenuProvider {
    @Override public Component getDisplayName() { return Component.translatable("block.tcc.teshin_workbench"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new TccShopMenu(id, inv, pos);
    }
    @Override public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }
}
```

服务端 `ServerPlayer.openMenu(provider)`（ServerPlayer.java:1147）先建菜单、再写 `extraData`，
非空则发 `AdvancedOpenScreenPayload`；客户端由 `MenuScreens.getScreenFactory` + `menuType.create(id, inv, buf)`
还原菜单与界面。**不需要自己写开界面包。**

### 6.3 `TccShopMenu`

```java
public class TccShopMenu extends AbstractContainerMenu {
    private final BlockPos pos;

    public TccShopMenu(int id, Inventory inv, RegistryFriendlyByteBuf data) { this(id, inv, data.readBlockPos()); }
    public TccShopMenu(int id, Inventory inv, BlockPos pos) {
        super(TccMenus.TESHIN_SHOP, id);                       // AbstractContainerMenu.java:65
        this.pos = pos;
    }
    public BlockPos getPos() { return this.pos; }
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
    @Override public boolean stillValid(Player player) {
        return !player.isSpectator()
            && player.canInteractWithBlock(this.pos, 4.0)          // Player.java:2302
            && player.level().getBlockState(this.pos).is(TccBlocks.TESHIN_WORKBENCH);
    }
}
```

0 个槽位；玩家走远/拆方块 → 原版自动关界面；无物品需要归还，`removed` 不处理。

### 6.4 `TccMenus`

```java
public final class TccMenus {
    public static final MenuType<TccShopMenu> TESHIN_SHOP = IMenuTypeExtension.create(TccShopMenu::new);

    public static void init(RegisterEvent.RegisterHelper<MenuType<?>> helper) {
        helper.register(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "teshin_shop"), TESHIN_SHOP);
    }
}
```

`TaczCurios` 构造器里加 `event.register(Registries.MENU, TccMenus::init);`。

### 6.5 `TccShopCatalog` / `TccShopEntry`

```java
public record TccShopEntry(Item item, int cost) {
    public static TccShopEntry of(Item item, int cost) { return new TccShopEntry(item, cost); }
    public ItemStack stack() { return new ItemStack(this.item); }   // 每次新建，避免共享可变 ItemStack
    public String searchKey() { return new ItemStack(this.item).getHoverName().getString().toLowerCase(Locale.ROOT); }
}

public final class TccShopCatalog {
    public static final int ROW_HEIGHT = 24;
    private static final List<TccShopEntry> ENTRIES = build();
    private static List<TccShopEntry> build() {
        List<TccShopEntry> list = new ArrayList<>(37);
        list.add(TccShopEntry.of(TccItems.CORE_FUSION, 2));
        // … 37 条照抄 §4 …
        list.sort(Comparator.comparingInt(TccShopEntry::cost));   // 稳定
        return List.copyOf(list);
    }
    public static List<TccShopEntry> entries() { return ENTRIES; }
    public static Optional<TccShopEntry> get(int index) {
        return index >= 0 && index < ENTRIES.size() ? Optional.of(ENTRIES.get(index)) : Optional.empty();
    }
}
```

`TccItems.X` 是普通 `Item` 静态字段（类初始化即创建），静态表里引用安全；`ItemStack` 延迟到 `stack()` 新建。

### 6.6 网络：购买与结果

```java
public record ShopBuyC2SPacket(List<Line> lines) implements CustomPacketPayload {
    public record Line(int index, int count) {}

    public static final Type<ShopBuyC2SPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "shop_buy"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShopBuyC2SPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override public ShopBuyC2SPacket decode(RegistryFriendlyByteBuf buf) {
            int n = buf.readVarInt();
            List<Line> lines = new ArrayList<>(Math.min(n, 64));
            for (int i = 0; i < n && i < 64; i++) lines.add(new Line(buf.readVarInt(), buf.readVarInt()));
            return new ShopBuyC2SPacket(lines);
        }
        @Override public void encode(RegistryFriendlyByteBuf buf, ShopBuyC2SPacket packet) {
            buf.writeVarInt(packet.lines().size());
            for (Line l : packet.lines()) { buf.writeVarInt(l.index()); buf.writeVarInt(l.count()); }
        }
    };
    public static void handle(ShopBuyC2SPacket packet, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> ShopPurchaseHandler.buy(player, packet.lines()));
        }
    }
}

public record ShopResultS2CPacket(boolean success) implements CustomPacketPayload {
    public static final Type<ShopResultS2CPacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "shop_result"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShopResultS2CPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override public ShopResultS2CPacket decode(RegistryFriendlyByteBuf buf) { return new ShopResultS2CPacket(buf.readBoolean()); }
        @Override public void encode(RegistryFriendlyByteBuf buf, ShopResultS2CPacket packet) { buf.writeBoolean(packet.success()); }
    };
    public static void handle(ShopResultS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleShopResult(packet));   // 若成功则清空购物车
    }
}
```

`NetworkHandler` 增加：

```java
registrar.playToServer(ShopBuyC2SPacket.TYPE, ShopBuyC2SPacket.STREAM_CODEC, ShopBuyC2SPacket::handle);
registrar.playToClient(ShopResultS2CPacket.TYPE, ShopResultS2CPacket.STREAM_CODEC, ShopResultS2CPacket::handle);

public static void sendShopBuy(List<ShopBuyC2SPacket.Line> lines) {
    PacketDistributor.sendToServer(new ShopBuyC2SPacket(List.copyOf(lines)));
}
```

> 结果包只回 `boolean`，文案由服务端 `displayClientMessage` 发；客户端只负责「成功就清空购物车」。
> `count` 上限 64、`lines` 上限 64，解码时截断，防止伪造包撑爆内存。

### 6.7 `ShopPurchaseHandler`（服务端事务）

```java
public static void buy(ServerPlayer player, List<Line> lines) {
    if (!(player.containerMenu instanceof TccShopMenu menu) || !menu.stillValid(player)) return;
    if (lines.isEmpty()) return;

    // 1) 归并 + 上限 + 总价（long 防溢出）
    Map<Integer, Integer> merged = new TreeMap<>();
    for (Line line : lines) {
        if (line.count() <= 0) return;
        merged.merge(line.index(), Math.min(line.count(), 64), (a, b) -> Math.min(64, a + b));
    }
    long total = 0;
    for (var e : merged.entrySet()) {
        TccShopEntry entry = TccShopCatalog.get(e.getKey()).orElse(null);
        if (entry == null) return;                     // 伪造索引：整单作废
        total += (long) entry.cost() * e.getValue();
    }
    if (total > Integer.MAX_VALUE) return;

    // 2) 废料检查
    if (countScrap(player) < total) {
        player.displayClientMessage(Component.translatable("tcc.shop.not_enough", total), true);
        player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);
        PacketDistributor.sendToPlayer(player, new ShopResultS2CPacket(false));
        return;
    }

    // 3) 批量结果与容量模拟（不修改背包）
    List<ItemStack> results = new ArrayList<>();
    for (var e : merged.entrySet()) results.add(new ItemStack(TccShopCatalog.get(e.getKey()).get().item(), e.getValue()));
    if (!canFit(player.getInventory(), results)) {
        player.displayClientMessage(Component.translatable("tcc.shop.full"), true);
        player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);
        PacketDistributor.sendToPlayer(player, new ShopResultS2CPacket(false));
        return;
    }

    // 4) 原子执行：扣料只会释放空间，模拟结论继续成立
    if (!removeScrap(player, (int) total)) return;
    for (ItemStack stack : results) player.getInventory().add(stack);
    player.containerMenu.broadcastChanges();
    player.getInventory().setChanged();

    player.displayClientMessage(Component.translatable("tcc.shop.bought", (int) total), true);
    player.playNotifySound(SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0F, 1.0F);
    PacketDistributor.sendToPlayer(player, new ShopResultS2CPacket(true));
}
```

`canFit(Inventory inv, List<ItemStack> stacks)`：把 `inv` 的 41 个槽位复制成 `List<ItemStack>`（`copy()`），
对每个待加物品依次「合并到同物品未满的槽 → 放进空槽」，全部放得下返回 `true`，否则 `false`。
纯读操作，不修改玩家背包。

`countScrap` / `removeScrap`：遍历 `inv.getContainerSize()`（41 格），`stack.is(Items.NETHERITE_SCRAP)`，
累加 / `shrink(min(剩余, count))`。

**原子性**：先模拟容量 → 再扣料 → 再发货。扣料只减少物品、不增加物品，所以模拟成立；
不存在「扣了料却发不出货」的中间态，因此不需要退款逻辑。

服务端反馈音：`SoundEvents.VILLAGER_YES` / `VILLAGER_NO`（`SoundEvents.java:1465/1463`）。
`ServerPlayer` 覆盖了 `playNotifySound`，只发给本人。

### 6.8 `TeshinShopScreen`

```java
public class TeshinShopScreen extends AbstractContainerScreen<TccShopMenu> {
    private static final int LIST_X = 8, LIST_Y = 26, LIST_W = 324, LIST_H = 124, ROW_H = 24, VISIBLE = 5;
    private static final int CART_X = 8, CART_Y = 150, CART_W = 324, CART_H = 84, CART_ROW_H = 20, CART_VISIBLE = 3;

    private final Map<Integer, Integer> cart = new LinkedHashMap<>();   // catalogIndex -> count
    private final List<Integer> filtered = new ArrayList<>();           // 过滤后的 catalog 索引
    private int scroll = 0;          // 列表滚动（单位：行）
    private int cartScroll = 0;      // 购物车滚动
    private int hoveredRow = -1;
    private EditBox search;
    private Button buyButton;

    public TeshinShopScreen(TccShopMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 340;
        this.imageHeight = 240;
    }

    @Override protected void init() {
        super.init();                                            // leftPos/topPos（AbstractContainerScreen.java:118）
        this.search = new EditBox(this.font, this.leftPos + 100, this.topPos + 6, 120, 16,
                                  Component.translatable("tcc.shop.search"));
        this.search.setMaxLength(32);
        this.search.setHint(Component.translatable("tcc.shop.search_hint"));
        this.addRenderableWidget(this.search);

        this.buyButton = Button.builder(Component.translatable("tcc.shop.buy_selected"), b -> this.purchase())
            .bounds(this.leftPos + 236, this.topPos + 6, 96, 16).build();
        this.addRenderableWidget(this.buyButton);
        this.rebuildFilter();
    }

    @Override protected void renderLabels(GuiGraphics g, int mx, int my) { /* 全部自绘，屏蔽默认标题与背包标签 */ }

    @Override protected void renderBg(GuiGraphics g, float partialTick, int mx, int my) {
        g.fill(this.leftPos, this.topPos, this.leftPos + 340, this.topPos + 240, 0xF0181818);  // 面板底
        g.fill(this.leftPos - 1, this.topPos - 1, this.leftPos + 341, this.topPos + 241, 0xFF3A3A3A); // 外框（先画外框再画面板也可）
        g.fill(this.leftPos + LIST_X, this.topPos + LIST_Y, this.leftPos + LIST_X + LIST_W,
               this.topPos + LIST_Y + LIST_H, 0xF0222222);
    }

    @Override public void render(GuiGraphics g, int mx, int my, float partialTick) {
        super.render(g, mx, my, partialTick);    // 背景 + EditBox + Button
        drawHeader(g);                           // 废料图标 + 数量
        drawList(g, mx, my);                     // 行（含复选框/步进/小计）
        drawScrollbar(g);
        drawCart(g);
        if (hoveredRow >= 0) {
            int idx = filtered.get(hoveredRow);
            TccShopEntry e = TccShopCatalog.entries().get(idx);
            g.renderTooltip(this.font,
                List.of(e.stack().getHoverName(), Component.translatable("tcc.shop.price", e.cost())),
                Optional.empty(), mx, my);
        }
        this.buyButton.active = !this.cart.isEmpty() && this.scrap() >= this.totalCost();
    }

    // ---- 命中 ----
    @Override public boolean mouseClicked(double mx, double my, int button) {
        if (super.mouseClicked(mx, my, button)) return true;    // 先给 EditBox / Button
        if (this.toggleAt(mx, my)) return true;                 // 复选框
        if (this.stepAt(mx, my)) return true;                   // - / 数量 / +
        if (this.inList(mx, my)) return true;                   // 行内其他位置：只让搜索框失焦
        return false;
    }

    @Override public boolean mouseScrolled(double mx, double my, double dx, double dy) {
        if (this.inList(mx, my)) { setScroll(this.scroll - (int) Math.signum(dy)); return true; }
        if (this.inCart(mx, my)) { this.cartScroll = clamp(this.cartScroll - (int) Math.signum(dy), 0, max(0, cart.size() - CART_VISIBLE)); return true; }
        return super.mouseScrolled(mx, my, dx, dy);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 搜索框聚焦时优先吃键，避免输入 'e' 触发原版「关闭容器」
        if (keyCode != 256 && this.search.isFocused() && this.search.keyPressed(keyCode, scanCode, modifiers)) {
            this.rebuildFilter();  return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
```

关键实现要点：

- **过滤**：`rebuildFilter()` 读 `search.getValue().toLowerCase(Locale.ROOT).trim()`，为空则全量；
  否则 `entry.searchKey().contains(q)`。重建后 `scroll = 0`。
  `charTyped` 走原版 `ContainerEventHandler` 自动转发给聚焦控件，但过滤需要在每次输入后重算：
  在 `keyPressed` 与 `charTyped` 覆写里各调一次 `rebuildFilter()`（或在 `render` 里检测 `getValue()` 变化）。
- **滚轮**：列表按行滚；购物车仅在 `cart.size() > 3` 时可滚。
- **滚动条**：只画不拖（`thumbY = scroll * (LIST_H - thumbH) / max(1, filtered.size() - VISIBLE)`），
  想拖拽再加 `mouseDragged` 命中判断，非必需。
- **数量**：`cart` 里存 `1..64`；`-` 到 1 后再点 = 取消勾选；`+` 到 64 停止。
- **排序不变**：`filtered` 保存的是 catalog 索引，购物车也存 catalog 索引，搜索过滤不影响已选内容。
- **托底**：`scroll` 在每次 `rebuildFilter` / 数据变化后 clamp 到 `0..max(0, filtered.size()-VISIBLE)`。

### 6.9 `TccShopScreens`

```java
@EventBusSubscriber(modid = TaczCurios.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TccShopScreens {
    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(TccMenus.TESHIN_SHOP, TeshinShopScreen::new);   // RegisterMenuScreensEvent.java:27-33
    }
}
```

`MenuScreens.register` 已废弃；`value = Dist.CLIENT` 保证专用服务端不加载 `Screen` 类。

---

## 7. 交互与时序

### 7.1 打开

1. 服务端 `useWithoutItem` → `openMenu(new TccShopMenuProvider(pos))`
2. 服务端建菜单、`writeClientSideData` 写 `pos`、发 `AdvancedOpenScreenPayload`
3. 客户端 `TccShopMenu(id, inv, buf)` 读 `pos` → `TeshinShopScreen`

### 7.2 选购

1. 勾选行 → `cart.put(idx, 1)`；再点复选框 → 移除
2. `-` / `+` / 点数量 → 调整 `cart`（1..64）
3. 搜索框输入 → `filtered` 变化，列表重排；购物车不受影响
4. 底栏实时显示：每行 `名称 ×N 单价·小计`，合计「需要支付：N」
5. 废料不足时购买按钮置灰、合计文字变红

### 7.3 购买

1. 点「购买所选商品」→ `NetworkHandler.sendShopBuy(cart 转 Line 列表)`
2. 服务端：菜单与距离复验 → 归并/限幅 → 总价 → 废料 → 容量模拟 → 扣料 → 发货 → 广播
3. 服务端发 `ShopResultS2CPacket(true)`；客户端收到后 `cart.clear()`，按钮回灰
4. 失败：服务端发 actionbar 提示（不足/背包满）+ 失败音 + `ShopResultS2CPacket(false)`，购物车保留

### 7.4 关闭

- ESC / 走远 4 格 / 拆方块 → `stillValid` 失败 → 原版关界面；购物车是纯客户端状态，随界面丢弃。

---

## 8. 服务端校验与防刷

| 校验 | 位置 | 作用 |
|---|---|---|
| 菜单必须是 `TccShopMenu` 且 `stillValid` | `buy` 首行 | 拦住未开界面 / 走远 / 方块已拆 |
| `lines` 非空、`count ∈ 1..64`、行数 ≤ 64 | 解码 + 归并 | 拦伪造包 |
| 索引必须存在于目录 | 归并时 `TccShopCatalog.get` | 越界整单作废 |
| 同索引归并后再算价 | 归并 | 防止重复行绕过限幅 |
| `countScrap >= total` | 扣料前 | 余额 |
| `canFit` 容量模拟 | 扣料前 | 背包满整单拒绝，不产生「扣料无货」 |
| 先扣料后发货 | 事务顺序 | 扣料只释放空间，模拟结论不变 |

无需限流：每次成功购买都消耗材料。

---

## 9. 语言键

`zh_cn.json`：

```json
"tcc.shop.search": "搜索",
"tcc.shop.search_hint": "搜索物品",
"tcc.shop.buy_selected": "购买所选商品",
"tcc.shop.preview": "购买预览",
"tcc.shop.price": "单价：%s 下界合金废料",
"tcc.shop.owned": "持有 %s",
"tcc.shop.line": "×%s   单价 %s   %s",
"tcc.shop.pay": "需要支付：%s",
"tcc.shop.not_enough": "下界合金废料不足（需要 %s）",
"tcc.shop.full": "背包空间不足",
"tcc.shop.bought": "已购买，支付 %s 下界合金废料"
```

`en_us.json` 对应英文；两个文件删掉 `entity.minecraft.villager.tcc.teshin`。

---

## 10. 删除清单与 `TaczCurios.java` 改动

```java
// 删除
event.register(Registries.POINT_OF_INTEREST_TYPE, TccPoiTypes::init);
event.register(Registries.VILLAGER_PROFESSION, TccVillagers::init);
NeoForge.EVENT_BUS.addListener(TccVillagers::onVillagerTrades);
// 删除 import com.xlxyvergil.tcc.villagers.TccVillagers;

// 新增
event.register(Registries.MENU, TccMenus::init);
```

删除三个文件（见 §3）。旧存档不写迁移：村民降级为无职业、POI 段一次重建，均为原版兜底，不崩溃。

---

## 11. 明确不做的事

- 不做自定义贴图（用户要求纯绘制）；仅用原版 `EditBox` 与 `Button` 控件。
- 不做右侧 3D 玩家预览、不做物品「备注」面板（参考图里的装饰与备注数据，本商店没有）。
- 不做等级/随机价格/使用次数。
- 不做存档迁移。
- 不做拖拽滚动条（只做滚轮；拖拽可后续追加）。
- 不做数量输入框直接键入（只 `+`/`-`，避免每行一个 `EditBox` 的开销与焦点问题）。

---

## 12. 验收清单

- [ ] 空手右键工作台 → 340×240 深色面板，标题区无「玩家背包」标签
- [ ] 37 行按价格 2→64 排列，仅 5 行可见，滚轮可滚到第 37 行
- [ ] 搜索「钻」只剩含该字的物品；清空搜索恢复全量且滚动归零
- [ ] 勾选 → 行变蓝、底栏出现该行；`+`/`-`/点数量可改 1..64，减到 0 即取消
- [ ] 合计实时等于 Σ 单价×数量；余额不足时按钮灰、合计红
- [ ] 购买成功：废料扣除、物品进包、购物车清空、成功音 + actionbar
- [ ] 废料不足：actionbar 提示、失败音、购物车保留
- [ ] 背包塞满：整单拒绝、废料一分没扣
- [ ] 走远/拆方块 → 界面关闭
- [ ] 两人同时开店互不影响
- [ ] 伪造索引/超长列表发包 → 服务端无副作用
- [ ] 专用服务端可启动；`mc_gradle build` 通过

---

## 13. 风险与回退

| 风险 | 影响 | 应对 |
|---|---|---|
| 搜索框聚焦时按 `e` 关界面 | 无法输入 | `keyPressed` 先转发给 `search`（§6.8） |
| 行内命中区与滚动计算不一致 | 点错行 | 命中一律用 `rowIndexAt(mx,my)` 与绘制同源计算 |
| 背包模拟与真实 `add` 行为不一致 | 极少数堆叠上限为 1 的物品误判 | 模拟用 `getMaxStackSize()`；交付前用堆叠 1 的物品实测一次 |
| `EditBox` 在 `super.render` 之后被行覆盖 | 打字时看不见输入框 | 行从 y=26 开始，搜索框在 6..22，不重叠 |
| 专用服务端误加载 `Screen` | 启动失败 | `@EventBusSubscriber(value = Dist.CLIENT)` |

回退：`TccBlocks` 换回 `new Block(...)`，删新增文件与 `MENU` 注册，`git checkout` 恢复村民文件。

---

## 14. 实施顺序

1. `TccShopEntry` + `TccShopCatalog`（纯数据）
2. `TeshinWorkbenchBlock` + 改 `TccBlocks`
3. `TccMenus` + `TccShopMenu` + `TccShopMenuProvider` + `TaczCurios` 注册
4. `ShopBuyC2SPacket` + `ShopResultS2CPacket` + `ShopPurchaseHandler` + `NetworkHandler`
5. `TeshinShopScreen`（先画面板/列表，再补搜索/购物车/购买）
6. `TccShopScreens`
7. 语言键
8. 删 `TccVillagers` / `TccPoiTypes` / `acquirable_job_site.json`
9. `mc_gradle` 构建 + 客户端实机验收
