package com.xlxyvergil.tcc.client;

import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.event.RavenKeyAimHandler;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * 渡鸦神之键「开镜蓄力」进度条：仅在开镜蓄力期间显示于准心正下方。
 *
 * <p>进度在客户端本地按与服务端相同的「整秒阶梯」换算，配置值由 {@code SyncConfigS2CPacket}
 * 从服务端同步，因此显示与服务端结算一致，且无需每 tick 发包。</p>
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = TaczCurios.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class RavenKeyChargeHud {

    private static final int BAR_WIDTH = 64;
    private static final int BAR_HEIGHT = 4;
    /** 准心中心到进度条的垂直距离 */
    private static final int Y_OFFSET = 14;

    private static final int BORDER = 0x60FFFFFF;
    private static final int FILL = 0xFF7A4DFF;
    private static final int FILL_FULL = 0xFFFFD54F;

    /** 客户端本地累计的连续开镜 tick 数 */
    private static int aimTicks;
    private static boolean charging;

    private RavenKeyChargeHud() {
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR,
                ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "raven_key_charge"),
                (graphics, partialTick) -> render(graphics));
    }

    /** 由 FORGE 总线的 {@link ClientEventHandler} 每客户端 tick 调用一次。 */
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        // 单人暂停时服务端不推进计时，HUD 同样冻结，避免与服务端不一致
        if (player == null || mc.level == null || mc.isPaused()) {
            resetCharge();
            return;
        }
        charging = RavenKeyAimHandler.hasRavenKeyEquipped(player)
                && GunTypeChecker.isHoldingSniper(player)
                && IClientPlayerGunOperator.fromLocalPlayer(player).isAim();
        if (charging) {
            aimTicks++;
        } else {
            aimTicks = 0;
        }
    }

    /** 清零本地蓄力计时（服务端命中清零后由 S2C 包调用）。 */
    public static void resetCharge() {
        charging = false;
        aimTicks = 0;
    }

    private static void render(GuiGraphics graphics) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (!charging || player == null || Minecraft.getInstance().options.hideGui) {
            return;
        }

        double ratio = RavenKeyAimHandler.getChargeRatio(player, aimTicks);

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int x = (screenWidth - BAR_WIDTH) / 2;
        int y = screenHeight / 2 + Y_OFFSET;

        // 1px 白色描边，框内保持全透明，仅绘制进度填充
        graphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y, BORDER);
        graphics.fill(x - 1, y + BAR_HEIGHT, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, BORDER);
        graphics.fill(x - 1, y, x, y + BAR_HEIGHT, BORDER);
        graphics.fill(x + BAR_WIDTH, y, x + BAR_WIDTH + 1, y + BAR_HEIGHT, BORDER);

        int filled = (int) Math.round(BAR_WIDTH * ratio);
        if (filled > 0) {
            graphics.fill(x, y, x + filled, y + BAR_HEIGHT, ratio >= 1.0 ? FILL_FULL : FILL);
        }
    }
}
