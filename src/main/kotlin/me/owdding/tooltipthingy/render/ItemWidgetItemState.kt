package me.owdding.tooltipthingy.render

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.FilterMode
import com.mojang.blaze3d.textures.GpuTextureView
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import earth.terrarium.olympus.client.pipelines.pips.OlympusPictureInPictureRenderState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.item.TrackingItemStackRenderState
import net.minecraft.client.renderer.state.gui.BlitRenderState
import net.minecraft.client.renderer.state.gui.GuiRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.LightCoordsUtil
import org.joml.Matrix3x2f
import java.util.function.Function

// Taken from SkyOcean
data class ItemWidgetItemState(
    val x0: Int,
    val y0: Int,
    val x1: Int,
    val y1: Int,
    val scissorArea: ScreenRectangle?,
    val pose: Matrix3x2f,
    val rotation: Float,
    val item: TrackingItemStackRenderState,
) : OlympusPictureInPictureRenderState<ItemWidgetItemState> {
    override fun getFactory(): Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<ItemWidgetItemState>> =
        Function { buffer -> ItemWidgetRenderer(buffer) }

    override fun x0() = x0
    override fun y0() = y0
    override fun x1() = x1
    override fun y1() = y1
    override fun scale() = 1f
    override fun scissorArea() = scissorArea
    override fun pose(): Matrix3x2f = pose
    override fun bounds(): ScreenRectangle = ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose)
}

class ItemWidgetRenderer(source: MultiBufferSource.BufferSource) : PictureInPictureRenderer<ItemWidgetItemState>(source) {

    private var textureView: GpuTextureView? = null

    override fun getRenderStateClass(): Class<ItemWidgetItemState> = ItemWidgetItemState::class.java
    override fun getTextureLabel(): String = "tooltip_thingy_item_rotate"

    override fun renderToTexture(state: ItemWidgetItemState, stack: PoseStack) {
        this.textureView = RenderSystem.outputColorTextureOverride

        stack.scale(1.0f, -1.0f, -1.0f)

        if (state.item.usesBlockLight()) {
            Minecraft.getInstance().gameRenderer.lighting.setupFor(Lighting.Entry.ITEMS_3D)
        } else {
            Minecraft.getInstance().gameRenderer.lighting.setupFor(Lighting.Entry.ITEMS_FLAT)
        }

        stack.pushPose()
        stack.mulPose(Axis.YN.rotationDegrees(state.rotation))

        stack.scale(13.0f, 13.0f, 13.0f)

        val featureRenderDispatcher = Minecraft.getInstance().gameRenderer.featureRenderDispatcher

        state.item.submit(
            stack,
            featureRenderDispatcher.submitNodeStorage,
            LightCoordsUtil.FULL_BRIGHT,
            OverlayTexture.NO_OVERLAY,
            0,
        )

        featureRenderDispatcher.renderAllFeatures()
        stack.popPose()
    }

    override fun blitTexture(state: ItemWidgetItemState, gui: GuiRenderState) {
        val view = this.textureView ?: return

        gui.addBlitToCurrentLayer(
            BlitRenderState(
                RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA,
                TextureSetup.singleTexture(
                    view,
                    RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR)
                ),
                state.pose(),
                state.x0, state.y0,
                state.x1, state.y1,
                0.0f, 1.0f, 1.0f, 0.0f,
                -1,
                state.scissorArea,
                null
            )
        )
    }

    override fun getTranslateY(height: Int, guiScale: Int): Float {
        return height / 2.0f
    }
}