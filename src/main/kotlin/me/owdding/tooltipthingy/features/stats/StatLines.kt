package me.owdding.tooltipthingy.features.stats

import me.owdding.tooltipthingy.ExtractableTooltipLine
import me.owdding.tooltipthingy.TooltipLine
import me.owdding.tooltipthingy.font
import me.owdding.tooltipthingy.system.RegisterFeature
import me.owdding.tooltipthingy.system.Result
import me.owdding.tooltipthingy.system.TooltipFeature
import me.owdding.tooltipthingy.utils.chat.ChatUtils.mc5
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.shadowColor
import kotlin.math.ceil
import kotlin.math.floor

@RegisterFeature
data object StatLines : TooltipFeature() {
    override val enabled: Boolean = true
    override val priority: Int = 20

    val statRegex = Regex("^(?<name>[\\w ]+): (?<value>[+-]?[\\d,.]+%?)(?: (?<extra>.+))?$").toComponentRegex()

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        var modified = false

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

            originalMerger.add(
                StatLine(
                Text.of {
                    append(stat.displayIcon)
                    append(" ")
                    append(name)
                },
                Text.of {
                    append(value)
                    append(" ")
                    append(extra ?: return@of)
                }
            ))

            modified = true
        }

        modified.asResult()
    }
}

val dot = Text.of("·") {
    this.color = TextColor.GRAY
    this.font = mc5
    shadowColor = null
}

data class StatLine(
    val statName: Component,
    val statValue: Component,
) : ExtractableTooltipLine {
    fun Int.nextHighest(multiple: Int) = (ceil(this / multiple.toFloat()).toInt() * multiple)
    fun Int.nextLower(multiple: Int) = (floor(this / multiple.toFloat()).toInt() * multiple)

    override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {
        graphics.text(font, statName, x, y, -1)
        val dotWidth = font.width(dot)
        val leftWidth = font.width(statName)
        val rightWidth = font.width(statValue)
        val fillerStart = leftWidth.nextHighest(dotWidth)
        val rightStart = totalWidth - rightWidth
        val fillerEnd = rightStart.nextLower(dotWidth)
        val fillerWidth = fillerEnd - fillerStart
        graphics.text(font, Text.join(List(fillerWidth / dotWidth) { dot }), x + fillerStart, y, -1)
        graphics.text(font, statValue, x + rightStart, y, -1)
    }

    override fun getWidth(font: Font): Int {
        return font.width(statName) + 10 + font.width(statValue)
    }

    override fun getHeight(font: Font): Int = font.lineHeight

}