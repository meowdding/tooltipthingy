package me.owdding.tooltipthingy.features

import me.owdding.lib.extensions.shorten
import me.owdding.tooltipthingy.ExtractableTooltipLine
import me.owdding.tooltipthingy.TooltipLine
import me.owdding.tooltipthingy.TooltipThingy.id
import me.owdding.tooltipthingy.config.categories.misc.MiscConfig
import me.owdding.tooltipthingy.font
import me.owdding.tooltipthingy.lines.SpacerLine
import me.owdding.tooltipthingy.render.SeparatorRenderer
import me.owdding.tooltipthingy.system.RegisterFeature
import me.owdding.tooltipthingy.system.Result
import me.owdding.tooltipthingy.system.TooltipFeature
import me.owdding.tooltipthingy.utils.chat.ChatUtils
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedFloat
import tech.thatgravyboat.skyblockapi.utils.extentions.scissor
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.shadowColor
import kotlin.math.max
import kotlin.math.roundToInt

@RegisterFeature
data object PetLevel : TooltipFeature() {
    override val enabled: Boolean get() = MiscConfig.petLevel
    override val priority: Int = 10

    const val ARROW = "▸"
    val regex = Regex("^Progress to Level (?<level>\\d+): .*|MAX LEVEL$")

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        if (!hasNext { it.stripped.matches(regex) }) return@withComponentMerger Result.unmodified

        addUntil { it.stripped.matches(regex) }
        read()

        val line = read().stripped.trim()
        val petLevelLine = if (line.startsWith(ARROW)) {
            PetLevelLine(
                line.filter { it.isDigit() }.toFloat(),
            )
        } else {
            val [first, second] = line.split("/")
            PetLevelLine(
                first.parseFormattedFloat(),
                second.parseFormattedFloat(),
            )
        }

        skipSpace()

        originalMerger.destination.add(1, SpacerLine(height = 3))
        originalMerger.destination.add(1, SeparatorRenderer)
        originalMerger.destination.add(1, petLevelLine)
        originalMerger.destination.add(1, SeparatorRenderer)
        originalMerger.destination.add(1, SpacerLine(height = 3))

        return@withComponentMerger Result.unmodified
    }

    data class PetLevelLine(
        val isMaxed: Boolean,
        val owned: Float,
        val required: Float,
    ) : ExtractableTooltipLine {
        val titleComponent = Text.of {
            this.font = ChatUtils.mc5
            this.shadowColor = null
            if (isMaxed) {
                append("Max Level")
            } else {
                append("Next Level")
            }
        }
        val xpComponent = Text.of {
            this.font = ChatUtils.mc5
            this.shadowColor = null

            append(owned.toFormattedString())
            if (isMaxed) return@of
            append("/")
            append(required.shorten())
        }

        constructor(owner: Float) : this(true, owner, 0f)
        constructor(owner: Float, required: Float) : this(false, owner, required)

        override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {

            /*
            TITLE
             */

            val titleWidth = titleComponent.width
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                id("triangle_up"),
                x + (totalWidth - titleWidth) / 2 - 15,
                y,
                titleWidth + 30,
                14,
                ARGB.opaque(-1)
            )
            graphics.centeredText(font, titleComponent, x + totalWidth / 2, y + 2, -1)


            /*
            XP BAR
             */
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                id("triangle_down"),
                x + 5,
                y + 15,
                totalWidth - 10,
                14,
                ARGB.opaque(-1)
            )

            val progress = if (this.isMaxed || this.required <= 0) 1f else this.owned / this.required

            graphics.scissor((x + 11)..(x + (totalWidth - 11) * progress).roundToInt(), (y - 20)..(y + 30)) {
                graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    if (this.isMaxed) id("progress_bar_max") else id("progress_bar"),
                    x + 11,
                    y + 18,
                    totalWidth - 23,
                    7,
                    ARGB.opaque(-1)
                )
            }
            graphics.centeredText(font, xpComponent, x + totalWidth / 2, y + 17, -1)
        }

        override fun getWidth(font: Font): Int = max(font.width(titleComponent), font.width(xpComponent))

        override fun getHeight(font: Font): Int = 30
    }
}