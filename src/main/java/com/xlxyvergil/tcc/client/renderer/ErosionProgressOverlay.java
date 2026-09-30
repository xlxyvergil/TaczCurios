package com.xlxyvergil.tcc.client.renderer;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.client.ErosionHudClientData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 侵蚀进度 HUD：在玩家屏幕右侧（距右边缘 {@link #RIGHT_MARGIN} 像素、垂直中心上方
 * {@link #CENTER_OFFSET} 像素处）显示其最近伤害实体的「实体名 + 进度条 + 百分比」。
 *
 * 数据来自服务端推送的 {@link ErosionHudClientData}，仅造成伤害的玩家本人可见。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ErosionProgressOverlay {

    /** 距屏幕右边缘的像素距离。 */
    private static final int RIGHT_MARGIN = 140;
    /** HUD 整体相对屏幕中心的垂直上移量。 */
    private static final int CENTER_OFFSET = 76;
    private static final int BAR_WIDTH = 120;
    private static final int BAR_HEIGHT = 8;

    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int BORDER_COLOR = 0xFF000000;
    private static final int BAR_FILL_COLOR = 0xFFFFFFFF;

    private ErosionProgressOverlay() {}

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("erosion_progress", ErosionProgressOverlay::render);
    }

    private static void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || !ErosionHudClientData.isActive()) {
            return;
        }

        Font font = mc.font;
        float progress = ErosionHudClientData.getProgress();

        int barX = screenWidth - RIGHT_MARGIN - BAR_WIDTH;
        int barY = screenHeight / 2 - CENTER_OFFSET - BAR_HEIGHT / 2;

        // 实体名
        graphics.drawString(font, ErosionHudClientData.getName(), barX, barY - 12, TEXT_COLOR, true);

        // 进度条：背景透明，仅描边 + 白色填充（满值 → 归零）
        graphics.fill(barX - 1, barY - 1, barX + BAR_WIDTH + 1, barY, BORDER_COLOR);
        graphics.fill(barX - 1, barY + BAR_HEIGHT, barX + BAR_WIDTH + 1, barY + BAR_HEIGHT + 1, BORDER_COLOR);
        graphics.fill(barX - 1, barY, barX, barY + BAR_HEIGHT, BORDER_COLOR);
        graphics.fill(barX + BAR_WIDTH, barY, barX + BAR_WIDTH + 1, barY + BAR_HEIGHT, BORDER_COLOR);
        int fillWidth = Math.round(BAR_WIDTH * progress);
        if (fillWidth > 0) {
            graphics.fill(barX, barY, barX + fillWidth, barY + BAR_HEIGHT, BAR_FILL_COLOR);
        }

        // 百分比
        graphics.drawString(font, Math.round(progress * 100.0F) + "%", barX, barY + BAR_HEIGHT + 4, TEXT_COLOR, true);
    }
}
