package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

/**
 * 自定义 RenderType（纯客户端）：结界地面特效平铺贴图用。基于 entityTranslucent，
 * 改 CullFace 为 NO_CULL（避免俯视平面被背面剔除）、DepthTest 为 GL_ALWAYS（避免贴地 quad 被地面深度遮挡），
 * 状态写入 RenderType 而不污染全局 GL。
 */
@OnlyIn(Dist.CLIENT)
public class TccRenderTypes extends RenderType {

    public TccRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                          boolean hasCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, hasCrumbling, sortOnUpload, setup, clear);
    }

    public static RenderType barrier(ResourceLocation texture) {
        return create("tcc_zhen_wo_barrier",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                        .setTextureState(new TextureStateShard(texture, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .setCullState(NO_CULL)
                        .setDepthTestState(new DepthTestStateShard("always_depth_test", GL11.GL_ALWAYS))
                        .createCompositeState(false));
    }

    /** 无纹理纯色四边形，用于结界地面环。 */
    public static RenderType ring() {
        return create("tcc_zhen_wo_ring",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(POSITION_COLOR_SHADER)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setDepthTestState(new DepthTestStateShard("always_depth_test", GL11.GL_ALWAYS))
                        .createCompositeState(false));
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
