package me.owdding.tooltipthingy.render

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import earth.terrarium.olympus.client.pipelines.pips.OlympusPictureInPictureRenderState
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.item.TrackingItemStackRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.LightCoordsUtil
import org.joml.Matrix3x2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
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

    override fun getRenderStateClass(): Class<ItemWidgetItemState> = ItemWidgetItemState::class.java
    override fun getTextureLabel(): String = "tooltip_thingy_item_rotate"

    override fun renderToTexture(state: ItemWidgetItemState, stack: PoseStack) {
        stack.pushPose()
        stack.translate(0f, state.bounds().height() / -2f - 5f, 0f)
        stack.scale(13f, -13f, 13f)
        stack.mulPose(Axis.ZN.rotationDegrees(180f))
        stack.mulPose(Axis.YN.rotationDegrees(state.rotation))

        McClient.self.gameRenderer.lighting.setupFor(if (state.item.usesBlockLight()) Lighting.Entry.ITEMS_3D else Lighting.Entry.ITEMS_FLAT)

        state.item.submit(
            stack,
            McClient.self.gameRenderer.featureRenderDispatcher.submitNodeStorage,
            LightCoordsUtil.FULL_BRIGHT,
            OverlayTexture.NO_OVERLAY,
            0,
        )

        stack.popPose()
    }
}