package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

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
}
