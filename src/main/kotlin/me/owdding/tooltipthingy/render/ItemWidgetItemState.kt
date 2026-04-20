package me.owdding.tooltipthingy.render

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import earth.terrarium.olympus.client.pipelines.pips.OlympusPictureInPictureRenderState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.item.TrackingItemStackRenderState
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.Mth
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

    val itemBounds by lazy {
        val aabb = item.modelBoundingBox
        if (aabb.xsize <= 16 && aabb.ysize <= 16) {
            ScreenRectangle(this.x0, this.y0, 16, 16)
        } else {
            ScreenRectangle(
                this.x0 + Mth.floor(aabb.minX * 16f) + 8,
                this.y0 - Mth.floor(aabb.maxY * 16f) + 8,
                Mth.ceil(aabb.xsize * 16.0f),
                Mth.ceil(aabb.ysize * 16.0f)
            )
        }
    }

    override fun getFactory(): Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<ItemWidgetItemState>> =
        Function { buffer -> ItemWidgetRenderer(buffer) }

    override fun x0() = x0
    override fun y0() = y0
    override fun x1() = x1
    override fun y1() = y1
    override fun scale() = 16f
    override fun scissorArea(): ScreenRectangle? = scissorArea
    override fun pose(): Matrix3x2f = pose
    override fun bounds(): ScreenRectangle? = PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea)
}

class ItemWidgetRenderer(source: MultiBufferSource.BufferSource) : PictureInPictureRenderer<ItemWidgetItemState>(source) {

    override fun getRenderStateClass(): Class<ItemWidgetItemState> = ItemWidgetItemState::class.java
    override fun getTextureLabel(): String = "tooltip_thingy_item_rotate"
    override fun getTranslateY(height: Int, guiScale: Int): Float = height / 2f

    override fun renderToTexture(state: ItemWidgetItemState, stack: PoseStack) {
        val renderer = Minecraft.getInstance().gameRenderer

        stack.scale(1.0f, -1.0f, -1.0f)
        stack.mulPose(Axis.YP.rotationDegrees(state.rotation))
        stack.translate(
            ((state.x0 + 8) - (state.itemBounds.left() + state.itemBounds.right()) / 2f) / 16.0f,
            ((state.itemBounds.top() + state.itemBounds.bottom()) / 2f - (state.y0 + 8)) / 16.0F,
            0.0F
        )

        renderer.lighting.setupFor(if (state.item.usesBlockLight()) Lighting.Entry.ITEMS_3D else Lighting.Entry.ITEMS_FLAT)

        val dispatcher = renderer.featureRenderDispatcher
        val storage = dispatcher.submitNodeStorage

        state.item.submit(stack, storage, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0)
        dispatcher.renderAllFeatures()
    }
}