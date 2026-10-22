package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import java.util.OptionalDouble;

/**
 * 本模组的自定义 RenderType（纯客户端）：把绘制状态写在 RenderType 里，而不是逐帧改全局 GL。
 *
 * <p>1.21.1 侧全部走 {@code MultiBufferSource + VertexConsumer}（见 {@link HighlightOutlineRenderer}
 * 与 {@link ZhenWoBarrierRenderer}）：真我结界的贴地花朵/边界环与高亮线框各自一条通道。
 */
@OnlyIn(Dist.CLIENT)
public class TccRenderTypes extends RenderType {

    public TccRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                          boolean hasCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, hasCrumbling, sortOnUpload, setup, clear);
    }

    /**
     * 真我结界的地面边界环：纯色四边形，半透明、不剔除背面、不写深度
     * （只留深度测试，使环带被地形正常遮挡，但不会挡住之后画的东西）。
     */
    private static final RenderType ZHEN_WO_BOUNDARY = create("tcc_zhen_wo_boundary",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));

    public static RenderType zhenWoBoundary() {
        return ZHEN_WO_BOUNDARY;
    }

    /**
     * 真我结界的脚下花朵：贴图四边形，用<strong>标准 alpha 混合</strong>（不是加算）。
     *
     * 加算混合会把贴图亮度叠到地面上，草地一衬就糊成一片白，透明度也不好控制；
     * 这里改用 {@code TRANSLUCENT_TRANSPARENCY}（SRC_ALPHA / ONE_MINUS_SRC_ALPHA），
     * 贴图按自身颜色与顶点 alpha 正常叠在地表，强度由顶点 alpha 直接决定。
     *
     * 1.21.1 没有 position_tex_color 核心着色器，这里用 vanilla 现成的
     * POSITION_COLOR_TEX_LIGHTMAP + 同名 shard：顶点色提供 alpha，贴图提供花纹，
     * 亮度由顶点灯光值（全亮）决定，不受环境明暗影响。
     *
     * 单例持有（与 highlightOutline 同理）：BufferSource 以 RenderType 实例为 key，
     * 每次新建实例会让 endBatch 找不到批次，花朵画不出来。
     */
    private static final RenderType ZHEN_WO_FLOWER = create("tcc_zhen_wo_flower",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                    .setTextureState(new TextureStateShard(
                            ResourceLocation.fromNamespaceAndPath("tcc", "textures/mob_effect/zhen_wo_barrier.png"),
                            false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));

    public static RenderType zhenWoFlower() {
        return ZHEN_WO_FLOWER;
    }

    /**
     * 方块线框轮廓（高亮技能用）：DEBUG_LINES + POSITION_COLOR，关闭深度测试使轮廓隔墙可见，
     * 不写深度避免影响后续渲染，线宽 2px。
     *
     * 单例持有——BufferSource 以 RenderType 实例为 key，每次新建实例会导致 endBatch 找不到批次。
     */
    private static final RenderType HIGHLIGHT_OUTLINE = create("tcc_highlight_outline",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.DEBUG_LINES,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .setLineState(new LineStateShard(OptionalDouble.of(2.0D)))
                    .createCompositeState(false));

    public static RenderType highlightOutline() {
        return HIGHLIGHT_OUTLINE;
    }
}
