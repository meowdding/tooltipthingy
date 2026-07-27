package me.owdding.iconographic.features.drill

import me.owdding.lib.extensions.shorten
import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.Iconographic.id
import me.owdding.iconographic.config.categories.mining.MiningConfig
import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.font
import me.owdding.iconographic.lines.SpacerLine
import me.owdding.iconographic.render.SeparatorRenderer
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.utils.chat.ChatUtils
import me.owdding.iconographic.utils.chat.DisplayColor
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedFloat
import tech.thatgravyboat.skyblockapi.utils.extentions.scissor
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.shadowColor
import kotlin.math.max
import kotlin.math.roundToInt

@RegisterFeature
data object DrillFuel : TooltipFeature() {
    override val enabled: Boolean get() = MiningConfig.drillFuel
    override val priority: Int = 10

    val DARK_GREEN get() = ARGB.opaque(DisplayColor.DARK_GREEN)
    val regex = Regex("^Fuel: (?<current>[\\d,.]+[kKmMbB]?)/(?<max>[\\d,.]+[kKmMbB]?)$")

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        if (!hasNext { it.stripped.matches(regex) }) return@withComponentMerger Result.unmodified

        addUntil { it.stripped.matches(regex) }

        val matched = regex.match(read().stripped.trim(), "current", "max") { [current, max] ->
            val fuelLine = FuelProgressBarLine(current.parseFormattedFloat(), max.parseFormattedFloat())
            originalMerger.destination.add(SpacerLine(height = 3))
            originalMerger.destination.add(SeparatorRenderer)
            originalMerger.destination.add(fuelLine)
            originalMerger.destination.add(SeparatorRenderer)
            originalMerger.destination.add(SpacerLine(height = 3))
        }

        if (matched) return@withComponentMerger Result.unmodified else Result.unmodified
    }

    data class FuelProgressBarLine(
        val current: Float,
        val maxFuel: Float,
    ) : ExtractableTooltipLine {

        val titleComponent = Text.of {
            this.font = ChatUtils.mc5
            this.shadowColor = null
            this.color = DARK_GREEN
            append("Drill Fuel")
        }

        val fuelValueComponent = Text.of {
            this.font = ChatUtils.mc5
            this.shadowColor = null
            this.color = -1
            append("${current.toFormattedString()}/${maxFuel.shorten()}")
        }

        override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {

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
            graphics.centeredText(font, titleComponent, x + totalWidth / 2, y + 2, DARK_GREEN)

            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                id("triangle_down"),
                x + 5,
                y + 15,
                totalWidth - 10,
                14,
                ARGB.opaque(-1)
            )

            val progress = if (maxFuel <= 0) 0f else (current / maxFuel).coerceIn(0f, 1f)

            graphics.scissor((x + 11)..(x + 11 + ((totalWidth - 23) * progress).roundToInt()), (y + 18)..(y + 25)) {
                graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    id("progress_bar"),
                    x + 11,
                    y + 18,
                    totalWidth - 23,
                    7,
                    DARK_GREEN,
                )
            }
            graphics.centeredText(font, fuelValueComponent, x + totalWidth / 2, y + 17, -1)
        }

        override fun getWidth(font: Font): Int = max(font.width(titleComponent), font.width(fuelValueComponent)) + 40

        override fun getHeight(font: Font): Int = 30
    }
}
