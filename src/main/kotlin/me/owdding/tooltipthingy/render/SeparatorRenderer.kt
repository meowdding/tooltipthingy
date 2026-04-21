package me.owdding.tooltipthingy.render

import me.owdding.tooltipthingy.ExtractableTooltipLine
import me.owdding.tooltipthingy.TooltipThingy.id
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.util.ARGB

object SeparatorRenderer : ExtractableTooltipLine {

    fun GuiGraphicsExtractor.renderSeparator(x: Int, y: Int, width: Int) {
        val sideWidth = (width - 9) / 2
        blitSprite(
            RenderPipelines.GUI_TEXTURED,
            id("separator_left"),
            x,
            y,
            sideWidth,
            7,
            ARGB.opaque(-1)
        )
        blitSprite(
            RenderPipelines.GUI_TEXTURED,
            id("separator_center"),
            x + sideWidth,
            y,
            9,
            7,
            ARGB.opaque(-1)
        )
        blitSprite(
            RenderPipelines.GUI_TEXTURED,
            id("separator_right"),
            x + sideWidth + 9,
            y,
            sideWidth,
            7,
            ARGB.opaque(-1)
        )
    }

    override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) = graphics.renderSeparator(x, y, totalWidth)

    override fun getWidth(font: Font): Int = 12
    override fun getHeight(font: Font): Int = 7

}