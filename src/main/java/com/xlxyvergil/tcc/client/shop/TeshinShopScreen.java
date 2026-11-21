package com.xlxyvergil.tcc.client.shop;

import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.network.ShopBuyC2SPacket;
import com.xlxyvergil.tcc.shop.TccShopCatalog;
import com.xlxyvergil.tcc.shop.TccShopEntry;
import com.xlxyvergil.tcc.shop.TccShopMenu;
import com.xlxyvergil.tcc.shop.ShopCurrency;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * 泰辛商店界面：340×240，全部用 fill / renderItem / drawString 画出来，不依赖任何贴图。
 * 列表 + 搜索 + 购物车 + 购买按钮，对应 docs/teshin-shop-plan.md §5。
 */
public class TeshinShopScreen extends AbstractContainerScreen<TccShopMenu> {

    // 设计基准 340×240，运行时铺满整个 GUI 空间（见 layout()）
    private static final int BASE_W = 340;
    private static final int BASE_H = 240;

    private static final int PAD = 8;
    private static final int HEADER_H = 22;
    // 顶部只放搜索框与购买按钮，货币信息挪到面板左下角
    private static final int BASE_ROW_H = 24;
    private static final int MIN_ROW_H = 20;
    private static final int BASE_CART_ROW_H = 20;
    // 购物车一行至少要放得下 16px 物品图标 + 上下留白，否则图标和文字会挤在一起
    private static final int MIN_CART_ROW_H = 18;
    private static final int CART_TITLE_H = 14;
    /** 表头（名称 / 单价 / 数量 / 总计）占的高度；比字体高 5px，让表头文字和下面的行之间留出空隙。 */
    private static final int CART_HEADER_H = 13;
    /** 商品列表表头（名称 / 单价 / 数量 / 总计）占的高度，和购物车表头等高。 */
    private static final int LIST_HEADER_H = CART_HEADER_H;
    /** 面板底部条：左下角放货币图标与持有量，右下角放「需要支付」。 */
    private static final int CART_FOOTER_H = 18;
    private static final int GAP = 6;
    // 行内四段：图标+名称列、单价列、−/数量/＋ 控件、价格区。剩余宽度都归价格区，价格在区内居中。
    private static final int NAME_COL_W = 130;
    // 行左侧图标占的宽度（16px 物品图标 + 与名称之间的 6px 空隙）
    private static final int NAME_LEAD_W = 20;
    // 单价列（窄列，只放 1~3 位数字）
    private static final int UNIT_COL_W = 28;
    private static final int UNIT_GAP = 6;
    // 列中心按行宽百分比定位：单价 42%、数量 63%，窗口缩放时四列位置等比移动
    private static final int UNIT_CENTER_PCT = 42;
    private static final int QTY_CENTER_PCT = 63;
    // 「− 数量 ＋」控件块：12px 按钮 + 2 间距 + 数量格 + 2 间距 + 12px 按钮
    private static final int STEP_BTN_W = 12;
    private static final int QTY_W = 34;
    private static final int STEP_W = STEP_BTN_W + 2 + QTY_W + 2 + STEP_BTN_W;
    private static final int PRICE_GAP = 6;
    // 价格区最小宽度：999 × 64 = 63936 这类 5 位数也能完整居中
    private static final int PRICE_MIN_W = 34;
    // 行内容与行左右边的最小留白，窗口再小价格也不会贴边
    private static final int ROW_SIDE_PAD = 8;
    private static final int MIN_BODY_H = 96;

    private static final int COLOR_FRAME = 0xFF3A3A3A;
    private static final int COLOR_PANEL = 0xF0181818;
    private static final int COLOR_ROW = 0xFF2A2A2A;
    private static final int COLOR_ROW_HOVER = 0xFF343434;
    private static final int COLOR_ROW_SELECTED = 0xFF2E4A78;
    private static final int COLOR_ROW_LINE = 0xFF1A1A1A;
    private static final int COLOR_TEXT = 0xFFFFFFFF;
    private static final int COLOR_TEXT_DIM = 0xFF9A9A9A;
    private static final int COLOR_PRICE = 0xFFFFC84A;
    private static final int COLOR_BAD = 0xFFFF5555;
    private static final int COLOR_BUTTON = 0xFF3A3A3A;
    private static final int COLOR_QTY_BG = 0xFF202020;
    private static final int COLOR_SCROLL_TRACK = 0xFF2A2A2A;
    private static final int COLOR_SCROLL_THUMB = 0xFF6A6A6A;

    /** 拖拽滚动条的三种状态。 */
    private static final int DRAG_NONE = 0;
    private static final int DRAG_LIST = 1;
    private static final int DRAG_CART = 2;
    /** 滚动条的可点宽度：画出来 4px，实际给光标留 12px 的容错。 */
    private static final int SCROLL_HIT_W = 12;

    private final Map<Integer, Integer> cart = new LinkedHashMap<>();
    /** 每行记住的数量（与是否勾选无关），只有重新打开界面时才回到 1。 */
    private final Map<Integer, Integer> quantities = new LinkedHashMap<>();
    private final List<Integer> filtered = new ArrayList<>();
    private int scroll;
    private int cartScroll;
    /** 正在拖拽哪一根滚动条（0 没有 / 1 商品列表 / 2 购物车）。 */
    private int draggingScrollbar = DRAG_NONE;
    /** 按下时光标相对拇指顶部的偏移，拖动时保持这个偏移，拇指不会跳。 */
    private double dragGrabOffset;
    private int hoveredRow = -1;
    private String queryText = "";
    private String loadedQuery = "";
    private EditBox search;
    private EditBox qtyEdit;
    private Button buyButton;
    /** 正在手动输入数量的行号（-1 表示没有在输入）。 */
    private int editingRow = -1;

    // 每帧/每次 init 计算出的布局（GUI 空间坐标）
    private int panelW = BASE_W;
    private int panelH = BASE_H;
    private int listX;
    private int listY;
    private int listW;
    private int listH;
    private int rowW;
    private int visibleRows = 5;
    private int cartX;
    private int cartY;
    private int cartW;
    private int cartH;
    private int cartVisible = 3;
    private int rowH = BASE_ROW_H;
    private int cartRowH = BASE_CART_ROW_H;

    // 绘制层级：z 越大越靠前。界面内容统一抬到 600，原版控件再高一档，
    // 这样画在 z=0 的其它界面元素不会盖到商店界面上。
    private static final double CONTENT_Z = 600.0D;
    private static final double WIDGET_Z = 610.0D;
    private static final double TOOLTIP_Z = 700.0D;

    public TeshinShopScreen(TccShopMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        this.layout();
        super.init();
        this.createWidgets();
        this.refreshFilter();
    }

    private void createWidgets() {
        // 顶部一行：货币图标 + 搜索框 + 购买按钮，宽度不够时按钮和搜索框一起收
        int searchX = this.leftPos + PAD;
        int buyW = Mth.clamp(this.panelW / 4, 60, 96);
        int buyX = this.leftPos + this.panelW - PAD - buyW;
        int searchW = Math.max(60, buyX - 6 - searchX);
        int headerY = this.topPos + 4;
        this.search = new LayeredEditBox(this.font, searchX, headerY, searchW, 16,
                Component.translatable("tcc.shop.search"), WIDGET_Z);
        this.search.setMaxLength(32);
        this.search.setHint(Component.translatable("tcc.shop.search_hint"));
        this.search.setValue(this.queryText);
        this.addRenderableWidget(this.search);

        this.buyButton = new LayeredButton(buyX, headerY, buyW, 16,
                Component.translatable("tcc.shop.buy_selected"), button -> this.sendPurchase(), WIDGET_Z);
        this.addRenderableWidget(this.buyButton);

        // 数量输入框：平时藏着，点某行的数量格子时才盖到那一格上。
        // 上限与协议一致（9999），正常交易到不了这个数。
        this.editingRow = -1;
        this.qtyEdit = new LayeredEditBox(this.font, this.leftPos, this.topPos, QTY_W, 12,
                Component.empty(), WIDGET_Z);
        int maxDigits = Integer.toString(ShopBuyC2SPacket.MAX_COUNT_PER_LINE).length();
        this.qtyEdit.setMaxLength(maxDigits);
        this.qtyEdit.setFilter(text -> text.matches("\\d{0," + maxDigits + "}"));
        this.qtyEdit.setVisible(false);
        this.addRenderableWidget(this.qtyEdit);
    }

    /**
     * 面板居中，高度占屏幕 3/4，宽度取 3:2 上限（比 4:3 更宽，行内横排放得下四列），
     * 但不越过屏幕宽的 3/4。
     * 行高随可用高度拉伸但不超过基准行高，多出来的像素全部给购物车，
     * 这样一行里的名称和控件不会离得太远，行与行之间也不会松散。
     */
    private void layout() {
        int panelH = Math.max(1, this.height * 3 / 4);
        this.panelW = Math.max(1, Math.min(this.width * 3 / 4, panelH * 3 / 2));

        this.listX = PAD;
        this.rowW = this.panelW - PAD * 2;
        this.listW = this.rowW;
        this.listY = HEADER_H + 4;

        // 列表 + 购物车一共能用的高度（底部还要给货币 / 需要支付那条留位置）
        int bodyH = Math.max(MIN_BODY_H, panelH - this.listY - GAP - CART_FOOTER_H - PAD);

        int maxRows = Math.max(1, TccShopCatalog.size());
        // 购物车至少保证能看见两条明细，否则表头加一行字就没什么可看的了
        int cartMin = CART_TITLE_H + CART_HEADER_H + 2 * MIN_CART_ROW_H;
        // 购物车最多占三分之一高度，剩下的全给列表；列表按最小行高尽量多放行
        int cartBlockH = Mth.clamp(bodyH / 3, cartMin,
                CART_TITLE_H + CART_HEADER_H + 6 * MIN_CART_ROW_H);
        int listSpace = Math.max(MIN_ROW_H + LIST_HEADER_H, bodyH - cartBlockH);
        // 表头先从列表区里切走一块，剩下的才是行区
        int rowsSpace = Math.max(MIN_ROW_H, listSpace - LIST_HEADER_H);
        int listRows = Mth.clamp(rowsSpace / MIN_ROW_H, 1, maxRows);
        // 行高封顶在基准值：再高就会把名称和控件拉散、一屏也放不下几行
        this.rowH = Mth.clamp(rowsSpace / listRows, MIN_ROW_H, BASE_ROW_H);
        this.visibleRows = listRows;
        this.listH = LIST_HEADER_H + listRows * this.rowH;

        int cartBlock = Math.max(cartMin, bodyH - this.listH);
        this.cartVisible = Mth.clamp((cartBlock - CART_TITLE_H - CART_HEADER_H) / MIN_CART_ROW_H, 1, 8);
        this.cartRowH = Math.max(MIN_CART_ROW_H,
                (cartBlock - CART_TITLE_H - CART_HEADER_H) / this.cartVisible);
        this.cartH = cartBlock;

        this.cartX = PAD;
        this.cartW = this.rowW;
        this.cartY = this.listY + this.listH + GAP;

        // 内容放不下时把面板收一点，正常情况下就是整屏；底部条始终留在面板最下面
        this.panelH = Math.min(panelH, this.cartY + this.cartH + CART_FOOTER_H + PAD);
        this.imageWidth = this.panelW;
        this.imageHeight = this.panelH;

        // 窗口缩放变化后可见行数可能变少，滚动位置要跟着收敛，否则会停在空白区
        this.scroll = Mth.clamp(this.scroll, 0, Math.max(0, this.filtered.size() - this.visibleRows));
        this.cartScroll = Mth.clamp(this.cartScroll, 0, Math.max(0, this.cart.size() - this.cartVisible));
    }

    /**
     * 行内布局：左右各留 {@link #ROW_SIDE_PAD}，依次是「图标 + 名称列 … 单价列 … −/数量/＋ + 总计」。
     * 单价列与数量列的中心按行宽的百分比定位（{@link #UNIT_CENTER_PCT} / {@link #QTY_CENTER_PCT}），
     * 名称列只吃「名称起点到单价列左缘」这段，总计列居中在剩下那块价格区里，
     * 所以四列的位置只跟面板宽度有关，不随商品名长短或数量位数变化。
     */
    private int contentLeft() {
        return ROW_SIDE_PAD;
    }

    /** 名称列宽度：名称起点到单价列左缘，夹在 40..{@link #NAME_COL_W}。 */
    private int nameColW() {
        return Mth.clamp(this.unitCenterX() - UNIT_COL_W / 2 - UNIT_GAP - this.nameX(), 40, NAME_COL_W);
    }

    /** 商品列表行区（相对面板）的顶部 Y：列表区顶部 + 表头占的高度。 */
    private int listRowsTop() {
        return this.listY + LIST_HEADER_H;
    }

    /** 名称文字左边界（相对行）。 */
    private int nameX() {
        return this.contentLeft() + NAME_LEAD_W;
    }

    /** 两张表共用的名称列宽度。 */
    private int tableNameW() {
        return this.nameColW();
    }

    /** 单价列的竖直轴：按行宽百分比定位，并夹在「名称最小宽度之后」与「步进器左侧」之间。 */
    private int unitCenterX() {
        int wanted = this.rowW * UNIT_CENTER_PCT / 100;
        int min = this.nameX() + 40 + UNIT_COL_W / 2 + UNIT_GAP;
        int max = this.clusterLeft() - 8 - UNIT_COL_W / 2;
        return Mth.clamp(wanted, min, Math.max(min, max));
    }

    /** 数量列的竖直轴：就是步进器里数量格的中心。 */
    private int qtyCenterX() {
        return this.qtyX() + QTY_W / 2;
    }

    /** 总计列的竖直轴：价格区的中心。 */
    private int totalCenterX() {
        return this.priceLeft() + this.priceWidth() / 2;
    }

    /** 价格区宽度（行右缘到价格区左边界）。 */
    private int priceWidth() {
        return Math.max(12, this.rowW - ROW_SIDE_PAD - this.priceLeft());
    }

    /** 「− 数量 ＋」控件块的左边界：数量格中心按行宽百分比定位，整块跟着它走。 */
    private int clusterLeft() {
        int wanted = this.rowW * QTY_CENTER_PCT / 100 - STEP_W / 2;
        int min = this.nameX() + 40 + UNIT_COL_W + UNIT_GAP + 8;
        int max = this.rowW - ROW_SIDE_PAD - PRICE_MIN_W - PRICE_GAP - STEP_W;
        return Mth.clamp(wanted, min, Math.max(min, max));
    }

    /** 价格区左边界。 */
    private int priceLeft() {
        return this.clusterLeft() + STEP_W + PRICE_GAP;
    }

    private int minusX() {
        return clusterLeft();
    }

    private int qtyX() {
        return this.clusterLeft() + STEP_BTN_W + 2;
    }

    private int plusX() {
        return this.qtyX() + QTY_W + 2;
    }

    /**
     * 绘制顺序：先画自己抬高层级的内容（面板、列表、购物车），再交给容器渲染控件，
     * 最后画提示框。每一段切换层级前都 flush 一次，保证批次按层输出。
     */
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.refreshFilterIfNeeded();
        this.hoveredRow = this.rowAt(mouseX, mouseY);
        if (this.buyButton != null) {
            this.buyButton.active = !this.cart.isEmpty() && this.scrapCount() >= this.totalCost();
        }

        guiGraphics.flush();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0D, 0.0D, CONTENT_Z);
        this.renderPanel(guiGraphics);
        this.renderRows(guiGraphics);
        this.renderScrollbar(guiGraphics);
        this.renderCart(guiGraphics);
        this.renderCartScrollbar(guiGraphics);
        this.renderFooter(guiGraphics);
        guiGraphics.flush();
        guiGraphics.pose().popPose();

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderHoverTooltip(guiGraphics, mouseX, mouseY);
    }

    /** 面板在 render() 的分层里自己画，这里留空。 */
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }

    private void renderPanel(GuiGraphics guiGraphics) {
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.fill(x - 1, y - 1, x + this.panelW + 1, y + this.panelH + 1, COLOR_FRAME);
        guiGraphics.fill(x, y, x + this.panelW, y + this.panelH, COLOR_PANEL);
    }

    /** 不画原版的「玩家背包」标签。 */
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    /** 面板之外的边带保持全透明：不要原版的暗色遮罩/模糊，直接看到世界。 */
    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
    }

    /** 菜单里的背包槽是屏幕外的同步槽，点不到也不该动。 */
    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
    }

    /**
     * 面板底部条：左下角是货币图标与持有量，右下角是这单需要支付的总价。
     * 没有独立底色，直接露出面板本身的颜色。
     */
    private void renderFooter(GuiGraphics guiGraphics) {
        int bandTop = this.topPos + this.panelH - CART_FOOTER_H;
        guiGraphics.fill(this.leftPos, bandTop, this.leftPos + this.panelW, bandTop + 1, COLOR_ROW_LINE);

        int iconY = bandTop + (CART_FOOTER_H - 16) / 2;
        guiGraphics.renderItem(new ItemStack(ShopCurrency.item()), this.leftPos + 8, iconY);
        guiGraphics.drawString(this.font, Integer.toString(this.scrapCount()), this.leftPos + 28,
                iconY + 4, COLOR_TEXT, true);

        int totalCost = this.totalCost();
        Component pay = Component.translatable("tcc.shop.pay", totalCost, ShopCurrency.name());
        boolean enough = this.scrapCount() >= totalCost;
        guiGraphics.drawString(this.font, pay, this.leftPos + this.panelW - 8 - this.font.width(pay),
                iconY + 4, enough ? COLOR_PRICE : COLOR_BAD, true);
    }

    private void renderRows(GuiGraphics guiGraphics) {
        int x0 = this.leftPos + this.listX;
        int y0 = this.topPos + this.listRowsTop();
        int content = this.contentLeft();
        int nameWidth = this.nameColW();

        // 商品区自己的表头，列轴与购物车完全一致
        int headerY = this.topPos + this.listY + 1;
        guiGraphics.drawString(this.font, Component.translatable("tcc.shop.col_name"), x0 + this.nameX(), headerY,
                COLOR_TEXT_DIM, false);
        this.drawCentered(guiGraphics, Component.translatable("tcc.shop.col_price"), x0 + this.unitCenterX(),
                headerY, COLOR_TEXT_DIM, false);
        this.drawCentered(guiGraphics, Component.translatable("tcc.shop.col_qty"), x0 + this.qtyCenterX(),
                headerY, COLOR_TEXT_DIM, false);
        this.drawCentered(guiGraphics, Component.translatable("tcc.shop.col_total"), x0 + this.totalCenterX(),
                headerY, COLOR_TEXT_DIM, false);
        guiGraphics.fill(x0, y0 - 1, x0 + this.rowW, y0, COLOR_ROW_LINE);
        for (int i = 0; i < this.visibleRows; i++) {
            int row = this.scroll + i;
            if (row >= this.filtered.size()) {
                break;
            }
            int index = this.filtered.get(row);
            TccShopEntry entry = TccShopCatalog.entries().get(index);
            int rowY = y0 + i * this.rowH;
            boolean selected = this.cart.containsKey(index);
            int background = selected ? COLOR_ROW_SELECTED
                    : (row == this.hoveredRow ? COLOR_ROW_HOVER : COLOR_ROW);
            guiGraphics.fill(x0, rowY, x0 + this.rowW, rowY + this.rowH - 1, background);
            guiGraphics.fill(x0, rowY + this.rowH - 1, x0 + this.rowW, rowY + this.rowH, COLOR_ROW_LINE);

            // 勾选状态由整行底色表示（选中整行变蓝），不再额外画勾选框，把宽度让给名称
            int iconY = rowY + (this.rowH - 16) / 2;
            guiGraphics.renderItem(entry.stack(), x0 + content + 2, iconY);
            // 名称在行内垂直居中，并按名称列宽度截断，绝不会压到单价列
            guiGraphics.drawString(this.font, this.trim(entry.stack().getHoverName().getString(), nameWidth),
                    x0 + this.nameX(), rowY + (this.rowH - 8) / 2, COLOR_TEXT, true);

            int count = this.quantityOf(index);
            this.renderStepper(guiGraphics, x0, rowY, count, selected);

            // 单价列：这个商品本身的单价，与数量无关
            this.drawCentered(guiGraphics, Component.literal(Integer.toString(entry.cost())),
                    x0 + this.unitCenterX(), rowY + (this.rowH - 8) / 2,
                    selected ? COLOR_PRICE : COLOR_TEXT_DIM, true);

            // 总计列：当前数量 × 单价（数量是记住的，勾选与否都不变）
            String subtotal = Integer.toString(count * entry.cost());
            this.drawCentered(guiGraphics, Component.literal(subtotal), x0 + this.totalCenterX(),
                    rowY + (this.rowH - 8) / 2, selected ? COLOR_PRICE : COLOR_TEXT_DIM, true);
        }
    }

    private void renderStepper(GuiGraphics guiGraphics, int x0, int rowY, int count, boolean active) {
        int y = rowY + (this.rowH - 12) / 2;
        int foreground = active ? 0xFFDDDDDD : 0xFF666666;

        int minusX = x0 + this.minusX();
        guiGraphics.fill(minusX, y, minusX + STEP_BTN_W, y + 12, COLOR_BUTTON);
        guiGraphics.fill(minusX + 2, y + 5, minusX + 10, y + 7, foreground);

        int qtyX = x0 + this.qtyX();
        guiGraphics.fill(qtyX, y, qtyX + QTY_W, y + 12, COLOR_QTY_BG);
        String text = Integer.toString(count);
        guiGraphics.drawString(this.font, text, qtyX + (QTY_W - this.font.width(text)) / 2, y + 2,
                active ? COLOR_TEXT : COLOR_TEXT_DIM, false);

        int plusX = x0 + this.plusX();
        guiGraphics.fill(plusX, y, plusX + STEP_BTN_W, y + 12, COLOR_BUTTON);
        guiGraphics.fill(plusX + 5, y + 2, plusX + 7, y + 10, foreground);
        guiGraphics.fill(plusX + 2, y + 5, plusX + 10, y + 7, foreground);
    }

    private void renderScrollbar(GuiGraphics guiGraphics) {
        int trackX = this.leftPos + this.panelW - 6;
        int trackY = this.topPos + this.listRowsTop();
        int trackH = this.listTrackH();
        guiGraphics.fill(trackX, trackY, trackX + 4, trackY + trackH, COLOR_SCROLL_TRACK);
        int[] thumb = this.listThumb();
        if (thumb != null) {
            guiGraphics.fill(trackX, thumb[0], trackX + 4, thumb[0] + thumb[1], COLOR_SCROLL_THUMB);
        }
    }

    /** 商品列表里实际画出来的行数（数据不足时少于可见行数）。 */
    private int listRowsDrawn() {
        return Math.max(1, Math.min(this.visibleRows, Math.max(1, this.filtered.size())));
    }

    /** 购物车里实际画出来的行数。 */
    private int cartRowsDrawn() {
        return Math.max(1, Math.min(this.cartVisible, Math.max(1, this.cart.size())));
    }

    /**
     * 商品列表滚动条的轨道高度：首行顶边到末行填充的底边。
     * 末行最后 1px 是与下一行之间的分隔线，不算行本身，所以再减掉 1px，
     * 否则轨道会比最后一格多出那一像素，看着「长了一点」。
     */
    private int listTrackH() {
        return Math.max(1, this.listRowsDrawn() * this.rowH - 1);
    }

    /** 购物车滚动条的轨道高度：同理，减掉末行分隔线那 1px。 */
    private int cartTrackH() {
        return Math.max(1, this.cartRowsDrawn() * this.cartRowH - 1);
    }

    /** 购物车明细区（屏幕坐标）的顶部 Y，和商品列表一样从区域起点开始画轨道。 */
    private int cartRowsTopY() {
        return this.topPos + this.topOfCartRows();
    }

    private void renderCartScrollbar(GuiGraphics guiGraphics) {
        if (this.cart.size() <= this.cartVisible) {
            return;
        }
        // 轨道 X 与商品列表滚动条使用同一条竖线（面板右边缘内缩 6px），
        // 否则购物车轨道会因 cartX 的留白而整体左移，两根条看起来不在一条线上
        int trackX = this.leftPos + this.panelW - 6;
        int trackY = this.cartRowsTopY();
        int trackH = this.cartTrackH();
        guiGraphics.fill(trackX, trackY, trackX + 4, trackY + trackH, COLOR_SCROLL_TRACK);
        int[] thumb = this.cartThumb();
        if (thumb != null) {
            guiGraphics.fill(trackX, thumb[0], trackX + 4, thumb[0] + thumb[1], COLOR_SCROLL_THUMB);
        }
    }

    /** 商品列表滚动条拇指的 {顶部 Y, 高度}（屏幕坐标）；不需要滚动时返回 null。 */
    private int[] listThumb() {
        int total = this.filtered.size();
        int maxScroll = Math.max(0, total - this.visibleRows);
        if (maxScroll <= 0) {
            return null;
        }
        int trackY = this.topPos + this.listRowsTop();
        int trackH = this.listTrackH();
        int height = Math.max(12,
                (int) ((long) trackH * this.visibleRows / Math.max(this.visibleRows, total)));
        int y = trackY + (int) ((long) (trackH - height) * this.scroll / maxScroll);
        return new int[]{y, height};
    }

    /** 购物车滚动条拇指的 {顶部 Y, 高度}（屏幕坐标）；不需要滚动时返回 null。 */
    private int[] cartThumb() {
        int lines = this.cart.size();
        int maxScroll = lines - this.cartVisible;
        if (maxScroll <= 0) {
            return null;
        }
        int trackY = this.cartRowsTopY();
        int trackH = this.cartTrackH();
        int height = Math.max(8, (int) ((long) trackH * this.cartVisible / lines));
        int y = trackY + (int) ((long) (trackH - height) * this.cartScroll / maxScroll);
        return new int[]{y, height};
    }

    /**
     * 按下滚动条就进入拖拽：按在拇指上保留抓取偏移，按在空白轨道上直接跳到该位置。
     *
     * @return 命中滚动条返回 true
     */
    private boolean beginScrollbarDrag(double mouseX, double mouseY) {
        int listTrackX = this.leftPos + this.panelW - 4;
        int[] listThumb = this.listThumb();
        if (listThumb != null
                && mouseX >= listTrackX - SCROLL_HIT_W / 2
                && mouseX < listTrackX + 4 + SCROLL_HIT_W / 2
                && mouseY >= this.topPos + this.listRowsTop()
                && mouseY < this.topPos + this.listRowsTop() + this.listTrackH()) {
            boolean onThumb = mouseY >= listThumb[0] && mouseY < listThumb[0] + listThumb[1];
            this.dragGrabOffset = onThumb ? mouseY - listThumb[0] : listThumb[1] / 2.0D;
            this.draggingScrollbar = DRAG_LIST;
            this.dragScrollTo(mouseY);
            return true;
        }

        // 与列表条同一条竖线，命中热区也照它算
        int cartTrackX = this.leftPos + this.panelW - 4;
        int[] cartThumb = this.cartThumb();
        int cartTrackY = this.cartRowsTopY();
        int cartTrackH = this.cartTrackH();
        if (cartThumb != null
                && mouseX >= cartTrackX - SCROLL_HIT_W / 2
                && mouseX < cartTrackX + 4 + SCROLL_HIT_W / 2
                && mouseY >= cartTrackY
                && mouseY < cartTrackY + cartTrackH) {
            boolean onThumb = mouseY >= cartThumb[0] && mouseY < cartThumb[0] + cartThumb[1];
            this.dragGrabOffset = onThumb ? mouseY - cartThumb[0] : cartThumb[1] / 2.0D;
            this.draggingScrollbar = DRAG_CART;
            this.dragScrollTo(mouseY);
            return true;
        }
        return false;
    }

    /** 拖拽中：把光标位置换算成滚动位置。 */
    private void dragScrollTo(double mouseY) {
        if (this.draggingScrollbar == DRAG_LIST) {
            int[] thumb = this.listThumb();
            if (thumb == null) {
                return;
            }
            int trackY = this.topPos + this.listRowsTop();
            int travel = this.listTrackH() - thumb[1];
            double ratio = travel <= 0 ? 0.0D
                    : (mouseY - this.dragGrabOffset - trackY) / travel;
            int maxScroll = Math.max(0, this.filtered.size() - this.visibleRows);
            this.scroll = Mth.clamp((int) Math.round(ratio * maxScroll), 0, maxScroll);
        } else if (this.draggingScrollbar == DRAG_CART) {
            int[] thumb = this.cartThumb();
            if (thumb == null) {
                return;
            }
            int trackY = this.cartRowsTopY();
            int travel = this.cartTrackH() - thumb[1];
            double ratio = travel <= 0 ? 0.0D
                    : (mouseY - this.dragGrabOffset - trackY) / travel;
            int maxScroll = Math.max(0, this.cart.size() - this.cartVisible);
            this.cartScroll = Mth.clamp((int) Math.round(ratio * maxScroll), 0, maxScroll);
        }
    }

    /** 购物车明细区的顶部（相对 cartY）：标题下面那条表头之后。 */
    private int topOfCartRows() {
        return this.cartY + CART_TITLE_H + CART_HEADER_H;
    }

    /** 购物车数量列 / 总计列的宽度（表头文字比数字宽，按表头给）。 */
    private static final int CART_QTY_COL_W = 26;
    private static final int CART_TOTAL_COL_W = 34;
    /** 名称列与数量列之间的固定间隔。 */
    private static final int CART_COL_GAP = 8;

    private void renderCart(GuiGraphics guiGraphics) {
        int x0 = this.leftPos + this.cartX;
        int y0 = this.topPos + this.cartY;
        int rowsTop = this.topPos + this.topOfCartRows();
        int rowsBottom = y0 + this.cartH - CART_FOOTER_H;

        // 标题行与表头行不铺自己的底色，直接露面板底色
        guiGraphics.fill(x0, y0, x0 + this.cartW, y0 + 1, COLOR_ROW_LINE);
        guiGraphics.fill(x0, rowsTop - 1, x0 + this.cartW, rowsTop, COLOR_ROW_LINE);
        guiGraphics.fill(x0, rowsBottom, x0 + this.cartW, rowsBottom + 1, COLOR_ROW_LINE);

        guiGraphics.drawString(this.font, Component.translatable("tcc.shop.preview"), x0 + 4, y0 + 4,
                COLOR_PRICE, true);

        List<Map.Entry<Integer, Integer>> lines = new ArrayList<>(this.cart.entrySet());
        this.cartScroll = Mth.clamp(this.cartScroll, 0, Math.max(0, lines.size() - this.cartVisible));

        // 四列，横向基准直接沿用商品列表那一套：名称列同一个起点同一个宽度，
        // 单价 / 数量 / 总计各自对准列表对应列的中心轴
        int nameLeft = this.nameX();
        int nameW = this.tableNameW();
        int unitCenter = this.unitCenterX();
        int qtyCenter = this.qtyCenterX();
        int totalCenter = this.totalCenterX();

        int headerY = y0 + CART_TITLE_H + 1;
        guiGraphics.drawString(this.font, Component.translatable("tcc.shop.col_name"), x0 + nameLeft, headerY,
                COLOR_TEXT_DIM, false);
        this.drawCentered(guiGraphics, Component.translatable("tcc.shop.col_price"), x0 + unitCenter, headerY,
                COLOR_TEXT_DIM, false);
        this.drawCentered(guiGraphics, Component.translatable("tcc.shop.col_qty"), x0 + qtyCenter, headerY,
                COLOR_TEXT_DIM, false);
        this.drawCentered(guiGraphics, Component.translatable("tcc.shop.col_total"), x0 + totalCenter, headerY,
                COLOR_TEXT_DIM, false);

        for (int i = 0; i < this.cartVisible && this.cartScroll + i < lines.size(); i++) {
            Map.Entry<Integer, Integer> line = lines.get(this.cartScroll + i);
            TccShopEntry entry = TccShopCatalog.entries().get(line.getKey());
            int y = rowsTop + i * this.cartRowH;
            // 每条明细自己一块行底色，行与行之间留 1px 露出面板底色；
            // 行宽与商品列表的行完全一致（同一个 x0、同一个 rowW）
            guiGraphics.fill(x0, y, x0 + this.cartW, y + this.cartRowH - 1, COLOR_ROW);
            guiGraphics.fill(x0, y + this.cartRowH - 1, x0 + this.cartW, y + this.cartRowH, COLOR_ROW_LINE);

            int subtotal = entry.cost() * line.getValue();
            int iconY = y + (this.cartRowH - 16) / 2;
            int textY = y + (this.cartRowH - 8) / 2;
            guiGraphics.renderItem(entry.stack(), x0 + nameLeft - 20, iconY);
            guiGraphics.drawString(this.font,
                    this.trim(entry.stack().getHoverName().getString(), nameW), x0 + nameLeft, textY,
                    COLOR_TEXT, true);
            this.drawCentered(guiGraphics, Component.literal(Integer.toString(entry.cost())),
                    x0 + unitCenter, textY, COLOR_TEXT, true);
            this.drawCentered(guiGraphics, Component.literal(Integer.toString(line.getValue())),
                    x0 + qtyCenter, textY, COLOR_TEXT, true);
            this.drawCentered(guiGraphics, Component.literal(Integer.toString(subtotal)),
                    x0 + totalCenter, textY, COLOR_PRICE, true);
        }
    }

    /** 以 centerX 为竖轴居中画一段文字。 */
    private void drawCentered(GuiGraphics guiGraphics, Component text, int centerX, int y, int color,
                              boolean shadow) {
        guiGraphics.drawString(this.font, text, centerX - this.font.width(text) / 2, y, color, shadow);
    }

    private void renderHoverTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.hoveredRow < 0 || this.hoveredRow >= this.filtered.size()) {
            return;
        }
        // 只有鼠标落在左侧「图标 + 名称」这一块时才弹提示；
        // 单价列、步进器、价格区都不弹，避免整行都能弹出提示遮挡视线
        double rx = mouseX - (this.leftPos + this.listX);
        if (rx < 0.0D || rx >= this.nameX() + this.nameColW()) {
            return;
        }
        TccShopEntry entry = TccShopCatalog.entries().get(this.filtered.get(this.hoveredRow));
        // 用原版容器界面同一条取 tooltip 的路径（会触发 Forge 的物品 tooltip 事件，
        // 饰品栏位/等级/效果这些由前置模组补的行才会有）
        guiGraphics.flush();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0D, 0.0D, TOOLTIP_Z);
        guiGraphics.renderTooltip(this.font, getTooltipFromItem(this.minecraft, entry.stack()),
                Optional.empty(), mouseX, mouseY);
        guiGraphics.flush();
        guiGraphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 注意：1.20.1 的 AbstractContainerScreen.mouseClicked 末尾无条件 return true，
        // 所以必须先处理自己画的行控件，再交给 super（否则 super 会把点击全部吞掉）。
        if (this.editingRow >= 0 && !this.insideQtyBox(mouseX, mouseY)) {
            this.commitQtyEdit();
        }
        if (button == 0 && this.beginScrollbarDrag(mouseX, mouseY)) {
            return true;
        }
        if (button == 0 && this.handleListClick(mouseX, mouseY)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingScrollbar != DRAG_NONE) {
            this.dragScrollTo(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.draggingScrollbar != DRAG_NONE) {
            this.draggingScrollbar = DRAG_NONE;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean insideQtyBox(double mouseX, double mouseY) {
        if (this.editingRow < 0 || this.editingRow >= this.filtered.size()) {
            return false;
        }
        int y = this.listRowsTop() + (this.editingRow - this.scroll) * this.rowH + (this.rowH - 12) / 2;
        return this.inside(mouseX, mouseY, this.listX + this.qtyX(), y, QTY_W, 12);
    }

    /** 把某一行的数量格子变成输入框，输入的数字就是购买数量。 */
    private void beginQtyEdit(int row) {
        this.commitQtyEdit();
        int index = this.filtered.get(row);
        int y = this.listRowsTop() + (row - this.scroll) * this.rowH + (this.rowH - 12) / 2;
        this.editingRow = row;
        this.qtyEdit.setX(this.leftPos + this.listX + this.qtyX());
        this.qtyEdit.setY(this.topPos + y);
        this.qtyEdit.setValue(Integer.toString(this.quantityOf(index)));
        this.qtyEdit.setVisible(true);
        this.qtyEdit.setFocused(true);
        this.setFocused(this.qtyEdit);
    }

    /** 收下输入框里的数字：0 或空表示取消勾选，超过上限按上限算。数量本身会记住。 */
    private void commitQtyEdit() {
        if (this.editingRow < 0) {
            return;
        }
        if (this.editingRow < this.filtered.size()) {
            int index = this.filtered.get(this.editingRow);
            String text = this.qtyEdit.getValue();
            int count = 0;
            try {
                count = text.isEmpty() ? 0 : Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
                count = 0;
            }
            if (count <= 0) {
                this.cart.remove(index);
                this.quantities.put(index, 1);
            } else {
                this.setQuantity(index, count);
            }
            this.cartScroll = Mth.clamp(this.cartScroll, 0, Math.max(0, this.cart.size() - this.cartVisible));
        }
        this.hideQtyEdit();
    }

    private void hideQtyEdit() {
        this.editingRow = -1;
        if (this.qtyEdit != null) {
            this.qtyEdit.setVisible(false);
            this.qtyEdit.setFocused(false);
        }
    }

    /** 处理行内的勾选框 / 数量格 / 减号 / 加号；命中返回 true。 */
    private boolean handleListClick(double mouseX, double mouseY) {
        int row = this.rowAt(mouseX, mouseY);
        if (row < 0) {
            return false;
        }
        int index = this.filtered.get(row);
        double rx = mouseX - (this.leftPos + this.listX);
        double ry = mouseY - (this.topPos + this.listRowsTop() + (row - this.scroll) * this.rowH);
        // 整块「− 数量 ＋」都是交互区：点在这块里的空隙上什么也不做，但绝不切换勾选
        int stepLeft = this.minusX();
        int stepRight = this.plusX() + STEP_BTN_W;
        if (rx >= stepLeft && rx < stepRight) {
            int bandY = (this.rowH - 12) / 2;
            boolean onBand = ry >= bandY && ry < bandY + 12;
            if (onBand) {
                if (rx >= this.qtyX() && rx < this.qtyX() + QTY_W) {
                    this.beginQtyEdit(row);
                } else if (rx < this.qtyX()) {
                    this.step(index, -1);
                } else {
                    this.step(index, 1);
                }
            }
            return true;
        }
        if (rx >= 0 && rx < this.rowW && ry >= 0 && ry < this.rowH) {
            this.toggle(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        this.commitQtyEdit();
        if (this.inside(mouseX, mouseY, this.listX, this.listY, this.listW, this.listH)) {
            this.scroll = Mth.clamp(this.scroll - (int) Math.signum(scrollY), 0,
                    Math.max(0, this.filtered.size() - this.visibleRows));
            return true;
        }
        if (this.inside(mouseX, mouseY, this.cartX, this.cartY, this.cartW, this.cartH)) {
            this.cartScroll = Mth.clamp(this.cartScroll - (int) Math.signum(scrollY), 0,
                    Math.max(0, this.cart.size() - this.cartVisible));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 数量输入中：回车确认、Esc 取消，其余按键交给输入框
        if (this.editingRow >= 0 && this.qtyEdit.isFocused()) {
            if (keyCode == 257 || keyCode == 335) {
                this.commitQtyEdit();
                return true;
            }
            if (keyCode == 256) {
                this.hideQtyEdit();
                return true;
            }
            return this.qtyEdit.keyPressed(keyCode, scanCode, modifiers);
        }
        // 搜索框聚焦时吞掉除 Esc 之外的所有按键，避免按 e 直接把界面关掉
        if (this.search != null && this.search.isFocused() && keyCode != 256) {
            this.search.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.editingRow >= 0 && this.qtyEdit.isFocused()) {
            return this.qtyEdit.charTyped(codePoint, modifiers);
        }
        if (this.search != null && this.search.isFocused()) {
            this.search.charTyped(codePoint, modifiers);
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    /** 服务端确认购买成功后清空购物车。 */
    public void onPurchaseSuccess() {
        this.cart.clear();
        this.cartScroll = 0;
    }

    private void sendPurchase() {
        if (this.cart.isEmpty()) {
            return;
        }
        List<ShopBuyC2SPacket.Line> lines = new ArrayList<>(this.cart.size());
        for (Map.Entry<Integer, Integer> entry : this.cart.entrySet()) {
            lines.add(new ShopBuyC2SPacket.Line(entry.getKey(), entry.getValue()));
        }
        NetworkHandler.sendShopBuy(lines);
    }

    /** 某一行当前显示的数量：记住上次填过的值，勾选状态变化不会把它打回 1。 */
    private int quantityOf(int index) {
        return this.quantities.getOrDefault(index, 1);
    }

    private void setQuantity(int index, int count) {
        int clamped = Mth.clamp(count, 1, ShopBuyC2SPacket.MAX_COUNT_PER_LINE);
        this.quantities.put(index, clamped);
        if (this.cart.containsKey(index)) {
            this.cart.put(index, clamped);
        }
    }

    private void toggle(int index) {
        if (this.cart.containsKey(index)) {
            this.cart.remove(index);
        } else {
            this.cart.put(index, this.quantityOf(index));
        }
        this.cartScroll = Mth.clamp(this.cartScroll, 0, Math.max(0, this.cart.size() - this.cartVisible));
    }

    private void step(int index, int delta) {
        int next = this.quantityOf(index) + delta;
        if (next <= 0) {
            // 减到 0 等于取消勾选，数量回到 1，下次勾选从 1 开始
            this.cart.remove(index);
            this.quantities.put(index, 1);
        } else {
            this.setQuantity(index, next);
        }
        this.cartScroll = Mth.clamp(this.cartScroll, 0, Math.max(0, this.cart.size() - this.cartVisible));
    }

    private int rowAt(double mouseX, double mouseY) {
        double rx = mouseX - (this.leftPos + this.listX);
        // 行区从表头下面开始算，点表头不该选中第一行
        double ry = mouseY - (this.topPos + this.listRowsTop());
        if (rx < 0 || rx >= this.listW || ry < 0 || ry >= this.visibleRows * this.rowH) {
            return -1;
        }
        int row = this.scroll + (int) (ry / this.rowH);
        return row < this.filtered.size() ? row : -1;
    }

    private boolean inside(double mouseX, double mouseY, int x, int y, int w, int h) {
        double rx = mouseX - this.leftPos;
        double ry = mouseY - this.topPos;
        return rx >= x && rx < x + w && ry >= y && ry < y + h;
    }

    private void refreshFilterIfNeeded() {
        if (this.search == null) {
            return;
        }
        if (!this.search.getValue().equals(this.loadedQuery)) {
            this.refreshFilter();
        }
    }

    private void refreshFilter() {
        this.queryText = this.search == null ? "" : this.search.getValue();
        this.loadedQuery = this.queryText;
        String query = this.queryText.trim().toLowerCase(Locale.ROOT);
        this.filtered.clear();
        List<TccShopEntry> entries = TccShopCatalog.entries();
        for (int i = 0; i < entries.size(); i++) {
            if (query.isEmpty()
                    || entries.get(i).stack().getHoverName().getString().toLowerCase(Locale.ROOT).contains(query)) {
                this.filtered.add(i);
            }
        }
        this.scroll = 0;
    }

    private int totalCost() {
        int total = 0;
        for (Map.Entry<Integer, Integer> entry : this.cart.entrySet()) {
            total += TccShopCatalog.entries().get(entry.getKey()).cost() * entry.getValue();
        }
        return total;
    }

    private int scrapCount() {
        if (this.minecraft == null || this.minecraft.player == null) {
            return 0;
        }
        return this.countItems(ShopCurrency.item());
    }

    private int countItems(net.minecraft.world.item.Item item) {
        if (this.minecraft == null || this.minecraft.player == null) {
            return 0;
        }
        Inventory inventory = this.minecraft.player.getInventory();
        int total = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(item)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private String trim(String text, int maxWidth) {
        if (this.font.width(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "...";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (this.font.width(builder.toString() + text.charAt(i) + ellipsis) > maxWidth) {
                break;
            }
            builder.append(text.charAt(i));
        }
        return builder + ellipsis;
    }

    /**
     * 自带绘制层级的输入框。容器按 z=0 依次绘制控件，这里在控件自己的 renderWidget 里
     * 临时把 z 抬到指定层，于是它画在面板之上，而不是被面板盖住。
     */
    private static final class LayeredEditBox extends EditBox {

        private final double layerZ;

        private LayeredEditBox(Font font, int x, int y, int width, int height, Component message, double layerZ) {
            super(font, x, y, width, height, message);
            this.layerZ = layerZ;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0D, 0.0D, this.layerZ);
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.pose().popPose();
        }
    }

    /** 自带绘制层级的按钮，原理同 LayeredEditBox。 */
    private static final class LayeredButton extends Button {

        private final double layerZ;

        private LayeredButton(int x, int y, int width, int height, Component message, OnPress onPress, double layerZ) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
            this.layerZ = layerZ;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0D, 0.0D, this.layerZ);
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.pose().popPose();
        }
    }
}
