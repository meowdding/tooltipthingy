package me.owdding.tooltipthingy.render

import me.owdding.tooltipthingy.ExtractableTooltipLine
import me.owdding.tooltipthingy.Tooltip
import me.owdding.tooltipthingy.TooltipThingy.id
import me.owdding.tooltipthingy.font
import me.owdding.tooltipthingy.system.TooltipTag
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
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

    val leftTagWidth = leftTags.sumOf { it.width }
    val rightTagWidth = rightTags.sumOf { it.width }
    val tagWidthTotal = when {
        leftTagWidth != 0 && rightTagWidth != 0 -> leftTagWidth + 5 + rightTagWidth
        else -> leftTagWidth + rightTagWidth
    }

    override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int,  x: Int, y: Int) {
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            id("tag"),
            x - 1,
            y - 1,
            24,
            24,
            ARGB.opaque(rarity.color)
        )
        graphics.item(item, x + 3, y + 3)
        graphics.text(font, name, x + 25, y, -1)

        var tags = 24
        for (tag in leftTags) {
            tag.extract(graphics, x + tags, y + 10)
            tags += tag.width
        }

        tags = totalWidth - rightTagWidth
        for (tag in rightTags) {
            tag.extract(graphics, x + tags, y + 10)
            tags += tag.width
        }
    }

    override fun getWidth(font: Font): Int = 25 + max(
        tagWidthTotal - 2,
        name.width + if (icon != null) 13 else 0
    )

    override fun getHeight(font: Font): Int = 26

}
