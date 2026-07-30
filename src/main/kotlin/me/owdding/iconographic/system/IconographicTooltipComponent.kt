package me.owdding.iconographic.system

import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.Iconographic
import me.owdding.iconographic.SideTooltipLine
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.util.ARGB

class IconographicTooltipComponent(val line: ExtractableTooltipLine) : ClientTooltipComponent {

    var totalWidth: Int = 0
    var sideWidth: Int = 0
    var isSideBlockStart: Boolean = false
    var sideBlockHeight: Int = 0

    override fun getWidth(font: Font): Int {
        return line.getWidth(font)
    }

    override fun getHeight(font: Font): Int {
        return line.getHeight(font)
    }

    override fun extractImage(font: Font, x: Int, y: Int, width: Int, height: Int, graphics: GuiGraphicsExtractor) {
        line.extract(graphics, this.totalWidth, x, y)

        if (isSideBlockStart && sideBlockHeight > 0) {
            val color = Iconographic.currentTooltipRarityColor ?: -1
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                Iconographic.id("background"),
                x + totalWidth + 10,
                y - 6,
                sideWidth + 12,
                sideBlockHeight + 12,
                ARGB.opaque(color)
            )
        }

        if (line is SideTooltipLine) {
            line.extractSide(graphics, this.totalWidth, this.sideWidth, x, y)
        }
    }

    override fun extractText(graphics: GuiGraphicsExtractor, font: Font, x: Int, y: Int) {
    }
}
