package me.owdding.tooltipthingy.lines

import me.owdding.tooltipthingy.TooltipLine
import net.minecraft.client.gui.Font

data class SpacerLine(val width: Int = 0, val height: Int = 0) : TooltipLine {
    override fun getWidth(font: Font): Int = width
    override fun getHeight(font: Font): Int = height
}