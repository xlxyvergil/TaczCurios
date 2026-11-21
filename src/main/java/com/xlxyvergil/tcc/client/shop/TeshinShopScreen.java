package com.xlxyvergil.tcc.client.shop;

import com.xlxyvergil.tcc.network.NetworkHandler;
import com.xlxyvergil.tcc.network.ShopBuyC2SPacket;
import com.xlxyvergil.tcc.shop.TccShopCatalog;
import com.xlxyvergil.tcc.shop.TccShopEntry;
import com.xlxyvergil.tcc.shop.TccShopMenu;
import com.xlxyvergil.tcc.shop.ShopCurrency;
import net.minecraft.ChatFormatting;
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
    private static final int MAX_SEARCH_W = 480;

    private static final int PAD = 8;
    private static final int HEADER_H = 22;
    private static final int BASE_ROW_H = 24;
    private static final int MIN_ROW_H = 16;
    private static final int BASE_CART_ROW_H = 20;
    private static final int MIN_CART_ROW_H = 12;
    private static final int CART_TITLE_H = 14;
    private static final int CART_FOOTER_H = 14;
    private static final int GAP = 6;
    private static final int PRICE_BLOCK_W = 76;
    private static final int MIN_BODY_H = 96;

    private static final int COLOR_FRAME = 0xFF3A3A3A;
    private static final int COLOR_PANEL = 0xF0181818;
    private static final int COLOR_LIST_BG = 0xF0222222;
    private static final int COLOR_ROW = 0xFF2A2A2A;
    private static final int COLOR_ROW_HOVER = 0xFF343434;
    private static final int COLOR_ROW_SELECTED = 0xFF2E4A78;
    private static final int COLOR_ROW_LINE = 0xFF1A1A1A;
    private static final int COLOR_CHECKBOX_BORDER = 0xFF555555;
    private static final int COLOR_CHECKBOX_EMPTY = 0xFF2A2A2A;
    private static final int COLOR_CHECKBOX_CHECK = 0xFF4FA85A;
    private static final int COLOR_TEXT = 0xFFFFFFFF;
    private static final int COLOR_TEXT_DIM = 0xFF9A9A9A;
    private static final int COLOR_PRICE = 0xFFFFC84A;
    private static final int COLOR_BAD = 0xFFFF5555;
    private static final int COLOR_BUTTON = 0xFF3A3A3A;
    private static final int COLOR_QTY_BG = 0xFF202020;
    private static final int COLOR_SCROLL_TRACK = 0xFF2A2A2A;
    private static final int COLOR_SCROLL_THUMB = 0xFF6A6A6A;
    private static final int COLOR_CART_BG = 0xF01C1C1C;

    private final Map<Integer, Integer> cart = new LinkedHashMap<>();
    private final List<Integer> filtered = new ArrayList<>();
    private int scroll;
    private int cartScroll;
    private int hoveredRow = -1;
    private String queryText = "";
    private String loadedQuery = "";
    private EditBox search;
    private Button buyButton;

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

    public TeshinShopScreen(TccShopMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        this.layout();
        super.init();

        int searchX = this.leftPos + PAD + 88;
        int buyX = this.leftPos + this.panelW - PAD - 96;
        int searchW = Mth.clamp(buyX - 8 - searchX, 60, MAX_SEARCH_W);
        this.search = new EditBox(this.font, searchX, this.topPos + 4, searchW, 16,
                Component.translatable("tcc.shop.search"));
        this.search.setMaxLength(32);
        this.search.setHint(Component.translatable("tcc.shop.search_hint"));
        this.search.setValue(this.queryText);
        this.addRenderableWidget(this.search);

        this.buyButton = Button.builder(Component.translatable("tcc.shop.buy_selected"), button -> this.sendPurchase())
                .bounds(buyX, this.topPos + 4, 96, 16)
                .build();
        this.addRenderableWidget(this.buyButton);

        this.refreshFilter();
    }

    /**
     * 铺满整个 GUI 空间：面板 = 整个屏幕，行高随可用高度拉伸，保证不留边、不出现空白行。
     * 列表行数不超过商品总数，剩余像素全部给购物车。
     */
    private void layout() {
        this.panelW = Math.max(1, this.width);
        int panelH = Math.max(1, this.height);

        this.listX = PAD;
        this.rowW = this.panelW - PAD * 2;
        this.listW = this.rowW;
        this.listY = HEADER_H + 4;

        // 列表 + 购物车一共能用的高度
        int bodyH = Math.max(MIN_BODY_H, panelH - this.listY - GAP - PAD);

        int maxRows = Math.max(1, TccShopCatalog.size());
        int cartRows = Mth.clamp((int) Math.round(bodyH * 0.22 / BASE_CART_ROW_H), 1, 8);
        cartRows = Math.max(1, Math.min(cartRows,
                (bodyH - CART_TITLE_H - CART_FOOTER_H) / MIN_CART_ROW_H));
        int listRows = Mth.clamp((bodyH - cartRows * MIN_CART_ROW_H - CART_TITLE_H - CART_FOOTER_H) / BASE_ROW_H,
                1, maxRows);
        while (listRows > 1 && listRows * MIN_ROW_H + cartRows * MIN_CART_ROW_H
                + CART_TITLE_H + CART_FOOTER_H > bodyH) {
            listRows--;
        }

        // 按基准行高比例分配像素，余数落给购物车，保证列表 + 购物车正好吃满 bodyH
        int baseList = listRows * BASE_ROW_H;
        int baseCart = cartRows * BASE_CART_ROW_H;
        int listPixels = bodyH * baseList / Math.max(1, baseList + baseCart);
        this.rowH = Math.max(MIN_ROW_H, listPixels / listRows);
        this.visibleRows = listRows;
        this.listH = listRows * this.rowH;

        int cartBlock = Math.max(CART_TITLE_H + CART_FOOTER_H + cartRows * MIN_CART_ROW_H,
                bodyH - this.listH);
        this.cartVisible = cartRows;
        this.cartRowH = Math.max(MIN_CART_ROW_H, (cartBlock - CART_TITLE_H - CART_FOOTER_H) / cartRows);
        this.cartH = cartBlock;

        this.cartX = PAD;
        this.cartW = this.rowW;
        this.cartY = this.listY + this.listH + GAP;

        // 内容放不下时把面板收一点，正常情况下就是整屏
        this.panelH = Math.min(panelH, this.cartY + this.cartH + PAD);
        this.imageWidth = this.panelW;
        this.imageHeight = this.panelH;

        // 窗口缩放变化后可见行数可能变少，滚动位置要跟着收敛，否则会停在空白区
        this.scroll = Mth.clamp(this.scroll, 0, Math.max(0, this.filtered.size() - this.visibleRows));
        this.cartScroll = Mth.clamp(this.cartScroll, 0, Math.max(0, this.cart.size() - this.cartVisible));
    }

    /** 每行右侧「− 数量 ＋」控件的位置（与 renderStepper 共用，保证点击区与画出来的一致）。 */
    private int stepperRight() {
        return this.listX + this.rowW - PRICE_BLOCK_W;
    }

    private int minusX() {
        return stepperRight() - 50;
    }

    private int qtyX() {
        return stepperRight() - 36;
    }

    private int plusX() {
        return stepperRight() - 12;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.refreshFilterIfNeeded();
        this.hoveredRow = this.rowAt(mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderHeader(guiGraphics);
        this.renderRows(guiGraphics);
        this.renderScrollbar(guiGraphics);
        this.renderCart(guiGraphics);
        if (this.buyButton != null) {
            this.buyButton.active = !this.cart.isEmpty() && this.scrapCount() >= this.totalCost();
        }
        this.renderHoverTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.fill(x - 1, y - 1, x + this.panelW + 1, y + this.panelH + 1, COLOR_FRAME);
        guiGraphics.fill(x, y, x + this.panelW, y + this.panelH, COLOR_PANEL);
        guiGraphics.fill(x + this.listX, y + this.listY, x + this.listX + this.listW,
                y + this.listY + this.listH, COLOR_LIST_BG);
    }

    /** 不画原版的「玩家背包」标签。 */
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    /** 菜单里的背包槽是屏幕外的同步槽，点不到也不该动。 */
    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
    }

    private void renderHeader(GuiGraphics guiGraphics) {
        int x = this.leftPos + 8;
        int y = this.topPos + 6;
        guiGraphics.renderItem(new ItemStack(ShopCurrency.item()), x, y);
        guiGraphics.drawString(this.font, Integer.toString(this.scrapCount()), x + 20, y + 4, COLOR_TEXT, true);
    }

    private void renderRows(GuiGraphics guiGraphics) {
        int x0 = this.leftPos + this.listX;
        int y0 = this.topPos + this.listY;
        int nameWidth = Math.max(48, this.minusX() - this.listX - 48);
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

            int boxX = x0 + 5;
            int boxY = rowY + (this.rowH - 10) / 2;
            guiGraphics.fill(boxX, boxY, boxX + 10, boxY + 10, COLOR_CHECKBOX_BORDER);
            guiGraphics.fill(boxX + 1, boxY + 1, boxX + 9, boxY + 9,
                    selected ? COLOR_CHECKBOX_CHECK : COLOR_CHECKBOX_EMPTY);

            int iconY = rowY + (this.rowH - 16) / 2;
            guiGraphics.renderItem(entry.stack(), x0 + 20, iconY);
            int nameY = iconY;
            guiGraphics.drawString(this.font, this.trim(entry.stack().getHoverName().getString(), nameWidth),
                    x0 + 40, nameY, COLOR_TEXT, true);
            guiGraphics.drawString(this.font, Component.translatable("tcc.shop.owned", this.owned(entry)),
                    x0 + 40, nameY + 10, COLOR_TEXT_DIM, false);

            int count = this.cart.getOrDefault(index, 1);
            this.renderStepper(guiGraphics, x0, rowY, count, selected);

            String subtotal = selected
                    ? count + " x " + entry.cost() + " = " + (count * entry.cost())
                    : Integer.toString(entry.cost());
            guiGraphics.drawString(this.font, subtotal, x0 + this.rowW - this.font.width(subtotal),
                    rowY + (this.rowH - 8) / 2, selected ? COLOR_PRICE : COLOR_TEXT_DIM, true);
        }
    }

    private void renderStepper(GuiGraphics guiGraphics, int x0, int rowY, int count, boolean active) {
        int y = rowY + (this.rowH - 12) / 2;
        int foreground = active ? 0xFFDDDDDD : 0xFF666666;

        int minusX = x0 + this.minusX();
        guiGraphics.fill(minusX, y, minusX + 12, y + 12, COLOR_BUTTON);
        guiGraphics.fill(minusX + 2, y + 5, minusX + 10, y + 7, foreground);

        int qtyX = x0 + this.qtyX();
        guiGraphics.fill(qtyX, y, qtyX + 22, y + 12, COLOR_QTY_BG);
        String text = Integer.toString(count);
        guiGraphics.drawString(this.font, text, qtyX + (22 - this.font.width(text)) / 2, y + 2,
                active ? COLOR_TEXT : COLOR_TEXT_DIM, false);

        int plusX = x0 + this.plusX();
        guiGraphics.fill(plusX, y, plusX + 12, y + 12, COLOR_BUTTON);
        guiGraphics.fill(plusX + 5, y + 2, plusX + 7, y + 10, foreground);
        guiGraphics.fill(plusX + 2, y + 5, plusX + 10, y + 7, foreground);
    }

    private void renderScrollbar(GuiGraphics guiGraphics) {
        int trackX = this.leftPos + this.panelW - 6;
        int trackY = this.topPos + this.listY;
        guiGraphics.fill(trackX, trackY, trackX + 4, trackY + this.listH, COLOR_SCROLL_TRACK);

        int total = this.filtered.size();
        int thumbHeight = Math.max(12,
                (int) ((long) this.listH * this.visibleRows / Math.max(this.visibleRows, total)));
        int maxScroll = Math.max(0, total - this.visibleRows);
        int thumbY = maxScroll == 0
                ? trackY
                : trackY + (int) ((long) (this.listH - thumbHeight) * this.scroll / maxScroll);
        guiGraphics.fill(trackX, thumbY, trackX + 4, thumbY + thumbHeight, COLOR_SCROLL_THUMB);
    }

    private void renderCart(GuiGraphics guiGraphics) {
        int x0 = this.leftPos + this.cartX;
        int y0 = this.topPos + this.cartY;
        guiGraphics.fill(x0, y0, x0 + this.cartW, y0 + this.cartH, COLOR_CART_BG);
        guiGraphics.fill(x0, y0, x0 + this.cartW, y0 + 1, COLOR_ROW_LINE);
        guiGraphics.drawString(this.font, Component.translatable("tcc.shop.preview"), x0 + 4, y0 + 4,
                COLOR_PRICE, true);

        List<Map.Entry<Integer, Integer>> lines = new ArrayList<>(this.cart.entrySet());
        this.cartScroll = Mth.clamp(this.cartScroll, 0, Math.max(0, lines.size() - this.cartVisible));
        for (int i = 0; i < this.cartVisible && this.cartScroll + i < lines.size(); i++) {
            Map.Entry<Integer, Integer> line = lines.get(this.cartScroll + i);
            TccShopEntry entry = TccShopCatalog.entries().get(line.getKey());
            int y = y0 + CART_TITLE_H + i * this.cartRowH;
            int subtotal = entry.cost() * line.getValue();
            guiGraphics.renderItem(entry.stack(), x0 + 4, y);
            guiGraphics.drawString(this.font, this.trim(entry.stack().getHoverName().getString(),
                    Math.max(48, this.cartW - 96 - this.font.width("0000 x 00 = 000000"))), x0 + 24, y + 2,
                    COLOR_TEXT, false);
            Component detail = Component.translatable("tcc.shop.line", line.getValue(), entry.cost(), subtotal,
                    ShopCurrency.name());
            guiGraphics.drawString(this.font, detail, x0 + this.cartW - 4 - this.font.width(detail), y + 2,
                    COLOR_PRICE, false);
        }

        int totalCost = this.totalCost();
        boolean enough = this.scrapCount() >= totalCost;
        guiGraphics.drawString(this.font, Component.translatable("tcc.shop.pay", totalCost, ShopCurrency.name()),
                x0 + 4, y0 + this.cartH - 12, enough ? COLOR_PRICE : COLOR_BAD, true);
    }

    private void renderHoverTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.hoveredRow < 0 || this.hoveredRow >= this.filtered.size()) {
            return;
        }
        TccShopEntry entry = TccShopCatalog.entries().get(this.filtered.get(this.hoveredRow));
        List<Component> tooltip = new ArrayList<>(2);
        tooltip.add(entry.stack().getHoverName());
        tooltip.add(Component.translatable("tcc.shop.price", entry.cost(), ShopCurrency.name())
                .withStyle(ChatFormatting.GRAY));
        guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 注意：1.20.1 的 AbstractContainerScreen.mouseClicked 末尾无条件 return true，
        // 所以必须先处理自己画的行控件，再交给 super（否则 super 会把点击全部吞掉）。
        if (button == 0 && this.handleListClick(mouseX, mouseY)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /** 处理行内的勾选框 / 减号 / 加号；命中返回 true。 */
    private boolean handleListClick(double mouseX, double mouseY) {
        int row = this.rowAt(mouseX, mouseY);
        if (row < 0) {
            return false;
        }
        int index = this.filtered.get(row);
        double rx = mouseX - (this.leftPos + this.listX);
        double ry = mouseY - (this.topPos + this.listY + (row - this.scroll) * this.rowH);
        int bandY = (this.rowH - 12) / 2;
        if (ry >= bandY && ry < bandY + 12) {
            if (rx >= this.minusX() && rx < this.minusX() + 12) {
                this.step(index, -1);
                return true;
            }
            if (rx >= this.plusX() && rx < this.plusX() + 12) {
                this.step(index, 1);
                return true;
            }
        }
        if (rx >= 0 && rx < this.rowW && ry >= 0 && ry < this.rowH) {
            this.toggle(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
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
        // 搜索框聚焦时吞掉除 Esc 之外的所有按键，避免按 e 直接把界面关掉
        if (this.search != null && this.search.isFocused() && keyCode != 256) {
            this.search.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
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

    private void toggle(int index) {
        if (this.cart.containsKey(index)) {
            this.cart.remove(index);
        } else {
            this.cart.put(index, 1);
        }
        this.cartScroll = Mth.clamp(this.cartScroll, 0, Math.max(0, this.cart.size() - this.cartVisible));
    }

    private void step(int index, int delta) {
        Integer current = this.cart.get(index);
        if (current == null) {
            if (delta > 0) {
                this.cart.put(index, 1);
            }
            return;
        }
        int next = current + delta;
        if (next <= 0) {
            this.cart.remove(index);
        } else {
            this.cart.put(index, Math.min(64, next));
        }
        this.cartScroll = Mth.clamp(this.cartScroll, 0, Math.max(0, this.cart.size() - this.cartVisible));
    }

    private int rowAt(double mouseX, double mouseY) {
        double rx = mouseX - (this.leftPos + this.listX);
        double ry = mouseY - (this.topPos + this.listY);
        if (rx < 0 || rx >= this.listW || ry < 0 || ry >= this.listH) {
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

    private int owned(TccShopEntry entry) {
        return this.countItems(entry.item());
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
}
