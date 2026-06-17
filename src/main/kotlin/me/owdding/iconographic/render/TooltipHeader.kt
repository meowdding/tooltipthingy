package me.owdding.iconographic.render

import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.Iconographic.id
import me.owdding.iconographic.Tooltip
import me.owdding.iconographic.config.Config
import me.owdding.iconographic.config.NonSkyBlockItemMode
import me.owdding.iconographic.font
import me.owdding.iconographic.system.TooltipTag
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.item.TrackingItemStackRenderState
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Matrix3x2f
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.scissor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width
import kotlin.math.max

data class TooltipHeader(
    val item: ItemStack,
    val name: Component,
    val leftTags: List<TooltipTag>,
    val rightTags: List<TooltipTag>,
    val icon: Identifier?,
    val rarity: SkyBlockRarity
) : ExtractableTooltipLine {

    constructor(tooltip: Tooltip) : this(tooltip.item, tooltip.name, tooltip.leftTags, tooltip.rightTags, tooltip.topRightIcon, tooltip.rarity)

    private val showIcon = item.getSkyBlockId() != null || Config.nonSkyBlockItemMode != NonSkyBlockItemMode.NO_ICON

    val leftTagWidth = leftTags.sumOf { it.width }
    val rightTagWidth = rightTags.sumOf { it.width }
    val tagWidthTotal = when {
        leftTagWidth != 0 && rightTagWidth != 0 -> leftTagWidth + 5 + rightTagWidth
        else -> leftTagWidth + rightTagWidth
    }

    override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {
        if (showIcon) {
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                id("tag"),
                x - 1,
                y - 1,
                24,
                24,
                ARGB.opaque(rarity.color)
            )
            graphics.extractItem(item, x + 3, y + 3)
        }

        val xOffset = if (showIcon) 25 else 2
        val yOffsetText = if (showIcon) 0 else 2

        graphics.text(font, name, x + xOffset, y + yOffsetText, -1)

        var tags = if (showIcon) 24 else 2
        val yOffsetTags = if (showIcon) 10 else 14

        for (tag in leftTags) {
            tag.extract(graphics, x + tags, y + yOffsetTags)
            tags += tag.width
        }

        tags = totalWidth - rightTagWidth
        for (tag in rightTags) {
            tag.extract(graphics, x + tags, y + yOffsetTags)
            tags += tag.width
        }
    }

    override fun getWidth(font: Font): Int {
        val baseWidth = if (showIcon) 25 else 2
        return baseWidth + max(
            tagWidthTotal - 2,
            name.width + if (icon != null) 13 else 0
        )
    }

    override fun getHeight(font: Font): Int = if (showIcon) 26 else 24

    // Taken and modified from SkyOcean
    private fun GuiGraphicsExtractor.extractItem(item: ItemStack, x: Int, y: Int) {
        if (!Config.spinny) {
            this.item(item, x, y)
            return
        }

        val width = 16
        val height = 16

        val rotation = 45f + (System.currentTimeMillis() / 20) % 360

        this.scissor(x..x + width, y..y + height) {
            val itemState = TrackingItemStackRenderState()
            McClient.self.itemModelResolver.updateForTopItem(itemState, item, ItemDisplayContext.GUI, McLevel.self, McPlayer.self, 0)

            this.guiRenderState.addPicturesInPictureState(
                ItemWidgetItemState(
                    x, y, x + width, y + height,
                    this.scissorStack.peek(),
                    Matrix3x2f(this.pose()),
                    rotation,
                    itemState,
                ),
            )
        }
    }
}
