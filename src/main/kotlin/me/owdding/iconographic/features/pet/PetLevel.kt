package me.owdding.iconographic.features.pet

import me.owdding.iconographic.ComponentLike
import me.owdding.lib.extensions.shorten
import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.Iconographic.id
import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.font
import me.owdding.iconographic.lines.SpacerLine
import me.owdding.iconographic.render.SeparatorRenderer
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.utils.chat.ChatUtils
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
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

    override fun ItemStack.applies(): Boolean = DataTypes.SKYBLOCK_ID()?.isPet == true

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        if (!hasNext { it.stripped.matches(regex) }) return@withComponentMerger Result.unmodified

        var modified = false
        val extractedLines = mutableListOf<PetLevelLine>()

        while (canRead()) {
            if (!peek().stripped.matches(regex)) {
                copy()
                continue
            }

            val headerLine = read().stripped.trim()
            val isMaxedHeader = headerLine == "MAX LEVEL"

            val targetLevel = regex.find(headerLine)?.groups?.get("level")?.value?.toIntOrNull()

            if (!canRead()) break
            val line = read().stripped.trim()
            val petLevelLine = if (line.startsWith(ARROW)) {
                PetLevelLine(
                    line.filter { it.isDigit() }.toFloat(),
                    targetLevel = targetLevel
                )
            } else if (line.contains("/")) {
                val parts = line.substringAfterLast(" ").split("/")

                if (parts.size != 2) {
                    originalMerger.destination.add(ComponentLike(Text.of(headerLine)))
                    originalMerger.destination.add(ComponentLike(Text.of(line)))
                    continue
                }

                PetLevelLine(
                    parts[0].parseFormattedFloat(),
                    parts[1].parseFormattedFloat(),
                    targetLevel = targetLevel
                )
            } else {
                if (isMaxedHeader) PetLevelLine(line.parseFormattedFloat(), targetLevel = targetLevel) else continue
            }

            skipSpace()
            extractedLines.add(petLevelLine)
            modified = true
        }

        if (extractedLines.isNotEmpty()) {
            var insertIndex = 1

            originalMerger.destination.add(insertIndex++, SpacerLine(height = 3))
            originalMerger.destination.add(insertIndex++, SeparatorRenderer)

            for (petLevel in extractedLines) {
                originalMerger.destination.add(insertIndex++, petLevel)
                originalMerger.destination.add(insertIndex++, SeparatorRenderer)
            }

            originalMerger.destination.add(insertIndex++, SpacerLine(height = 3))
        }

        return@withComponentMerger modified.asResult()
    }

    data class PetLevelLine(
        val isMaxed: Boolean,
        val owned: Float,
        val required: Float,
        val targetLevel: Int? = null
    ) : ExtractableTooltipLine {
        val titleComponent = Text.of {
            this.font = ChatUtils.mc5
            this.shadowColor = null
            if (isMaxed) {
                append("Max Level")
            } else if (targetLevel != null) {
                append("Level $targetLevel")
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

        constructor(owner: Float, targetLevel: Int? = null) : this(true, owner, 0f, targetLevel)
        constructor(owner: Float, required: Float, targetLevel: Int? = null) : this(false, owner, required, targetLevel)

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