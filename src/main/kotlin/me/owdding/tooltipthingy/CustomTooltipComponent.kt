package me.owdding.tooltipthingy

import me.owdding.tooltipthingy.TooltipThingy.id
import me.owdding.tooltipthingy.system.TooltipTag
import me.owdding.tooltipthingy.utils.floorToInt
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.BundleContents
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

data class TooltipInformation(
    val bundleContents: BundleContents?,
    val entries: List<TooltipLine>,
) {
    companion object {
        fun List<ClientTooltipComponent>.toInformation(): TooltipInformation {
            var bundleContents: BundleContents? = null
            val entries: MutableList<TooltipLine> = mutableListOf()

            this.forEach {
                when (it) {
                    is ClientTextTooltip -> entries.add(ComponentLike(it.text))
                    is ClientBundleTooltip -> bundleContents = it.contents
                    else -> {
                        entries.add(it)
                    }
                }
            }

            return TooltipInformation(bundleContents, entries)
        }
    }
}

data class Tooltip(
    val item: ItemStack,
    val name: Component,
    val leftTags: List<TooltipTag>,
    val rightTags: List<TooltipTag>,
    val topRightIcon: Identifier?,
    val rarity: SkyBlockRarity,
    val isRarityUpgraded: Boolean,
    val entries: List<TooltipLine>,
)

interface ExtractableTooltipLine : TooltipLine {
    fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int)
}

interface TooltipLine {
    fun getWidth(font: Font): Int
    fun getHeight(font: Font): Int
}

inline val font get() = McFont.self

interface ComponentAlignment {

    companion object {

        val Left: ComponentAlignment = of(.0f)
        val Center = of(.5f)
        val Right = of(1f)

        fun of(value: Float): ComponentAlignment {
            val value = value.coerceIn(0f, 1f)
            return object : ComponentAlignment {
                override fun extract(graphics: GuiGraphicsExtractor, sequence: FormattedCharSequence, totalWidth: Int, x: Int, y: Int, color: Int) {
                    graphics.text(font, sequence, x + ((totalWidth - font.width(sequence)) * value).floorToInt(), y, color)
                }
            }
        }

    }

    fun extract(graphics: GuiGraphicsExtractor, sequence: FormattedCharSequence, totalWidth: Int, x: Int, y: Int, color: Int = -1)
}

data class ComponentLike(
    val charSequence: FormattedCharSequence,
    val alignment: ComponentAlignment = ComponentAlignment.Left,
    val lines: Boolean = false,
) : ExtractableTooltipLine {

    constructor(component: Component, alignment: ComponentAlignment = ComponentAlignment.Left, lines: Boolean = false) : this(component.visualOrderText, alignment, lines)

    val component by lazy {
        val result: MutableComponent = Text.of()
        var currentStyle: Style? = null
        var current = Text.of()
        var builder = StringBuilder()

        charSequence.accept { _, style, codepoint ->
            if (currentStyle == null) {
                currentStyle = style
                current.style = style
            }
            if (currentStyle != style) {
                currentStyle = style
                current.append(builder.toString())
                result.append(current)
                builder = StringBuilder()
                current = Text.of()
                current.style = currentStyle
            }
            builder.appendCodePoint(codepoint)

            true
        }

        current.append(builder.toString())
        current.style = currentStyle ?: Style.EMPTY
        result.append(current)
        result
    }

    val stripped by lazy { component.stripped }
    val width by lazy { font.width(charSequence) }

    override fun getWidth(font: Font): Int {
        return width
    }

    override fun getHeight(font: Font): Int {
        return font.lineHeight
    }

    override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {
        if (lines && totalWidth - font.width(charSequence) - 12 > 0) {
            val separatorWidth = ((totalWidth - font.width(charSequence)) - 2) / 2
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                id("separator_left"),
                x,
                y,
                separatorWidth,
                7,
                ARGB.opaque(-1)
            )
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                id("separator_right"),
                x + totalWidth - separatorWidth - 1,
                y,
                separatorWidth,
                7,
                ARGB.opaque(-1)
            )
        }
        alignment.extract(graphics, this.charSequence, totalWidth, x, y)
    }

}
