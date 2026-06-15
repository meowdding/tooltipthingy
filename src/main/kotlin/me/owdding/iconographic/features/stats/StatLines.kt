package me.owdding.iconographic.features.stats

import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.font
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.utils.chat.ChatUtils.mc5
import me.owdding.iconographic.utils.debug.debugToggle
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.shadowColor
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

@RegisterFeature
data object StatLines : TooltipFeature() {
    override val enabled: Boolean = true
    override val priority: Int = 25

    val statRegex = Regex("^(?<name>[\\w ]+): (?<value>[+-]?[\\d,.]+%?)(?: (?<extra>.+))?$").toComponentRegex()

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        var modified = false

        val maxIconWidth = AtomicInteger(0)
        while (canRead()) {
            val line = read()

            val result = statRegex.match(line)
            if (result == null) {
                add(line)
                continue
            }

            val name = result["name"]
            val value = result["value"]
            val extra = result["extra"]

            val stat = StatType.fromName(name?.stripped?.trim() ?: "")

            if (name == null || value == null || stat == null) {
                add(line)
                continue
            }

            maxIconWidth.set(max(maxIconWidth.get(), stat.displayIcon.width))

            originalMerger.add(
                StatLine(
                    maxIconWidth,
                    stat,
                    stat.displayIcon,
                    name,
                    Text.of {
                        append(value)
                        result["icon"]?.let { append(it) }
                        append(" ")
                        append(extra ?: return@of)
                    }
                ))

            modified = true
        }

        modified.asResult()
    }
}

val showStatIds by debugToggle("stat_ids", "Render stat ids next to the line")

val dot = Text.of("·") {
    this.color = TextColor.GRAY
    this.font = mc5
    shadowColor = null
}

data class StatLine(
    val maxIconWidth: AtomicInteger,
    val stat: StatType,
    val statIcon: MutableComponent,
    val statName: Component,
    val statValue: Component,
) : ExtractableTooltipLine {
    fun Int.nextHighest(multiple: Int) = (ceil(this / multiple.toFloat()).toInt() * multiple)
    fun Int.nextLower(multiple: Int) = (floor(this / multiple.toFloat()).toInt() * multiple)

    override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {
        graphics.centeredText(font, statIcon, x + maxIconWidth.get() / 2, y, -1)
        graphics.text(font, statName, maxIconWidth.get() + x + 3, y, -1)
        val dotWidth = font.width(dot)
        val leftWidth = maxIconWidth.get() + font.width(statName) + 3
        val rightWidth = font.width(statValue)
        val fillerStart = leftWidth.nextHighest(dotWidth)
        val totalWidth = if (showStatIds) {
            if (stat.isUnknown) graphics.text(font, stat.idComponent, x + totalWidth - 45, y, -1)
            totalWidth - 50
        } else totalWidth
        val rightStart = totalWidth - rightWidth
        val fillerEnd = rightStart.nextLower(dotWidth)
        val fillerWidth = fillerEnd - fillerStart
        graphics.text(font, Text.join(List((fillerWidth / dotWidth).coerceAtLeast(0)) { dot }), x + fillerStart, y, -1)
        graphics.text(font, statValue, x + rightStart, y, -1)
    }

    override fun getWidth(font: Font): Int {
        return font.width(statIcon) + 3 + font.width(statName) + 10 + font.width(statValue) + if (showStatIds) 50 else 0
    }

    override fun getHeight(font: Font): Int = font.lineHeight

}