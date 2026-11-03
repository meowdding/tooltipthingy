package me.owdding.iconographic.lines

import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.TooltipLine
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor

data class SpacerLine(val width: Int = 0, val height: Int = 0) : ExtractableTooltipLine {
    override fun getWidth(font: Font): Int = width
    override fun getHeight(font: Font): Int = height
    override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) = Unit
}
