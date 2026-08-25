package com.xlxyvergil.tcc.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * 自定义 RenderType：结界地面特效平铺贴图用。
 * <p>
 * 与 entityTranslucent 同款（POSITION_COLOR_TEX_LIGHTMAP + 实体半透明 shader），
 * 但将 CullFace 改为 NO_CULL，避免从上方俯视平面时被背面剔除。
 * 参考 MoonsTeams MRender 的写法，零额外依赖、不污染全局 GL 状态。
 */
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
                        .createCompositeState(false));
    }
}
