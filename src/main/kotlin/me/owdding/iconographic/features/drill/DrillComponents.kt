package me.owdding.iconographic.features.drill

import me.owdding.iconographic.ComponentLike
import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.Iconographic.id
import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.font
import me.owdding.iconographic.lines.SpacerLine
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockCategory
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import kotlin.math.max

@RegisterFeature
data object DrillComponents : TooltipFeature() {
    override val enabled: Boolean get() = MiscConfig.drillComponents
    override val priority: Int = 10

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result {
        if (getData(DataTypes.CATEGORY) != SkyBlockCategory.DRILL) return Result.unmodified

        val fuelIndex = list.indexOfFirst {
            val comp = it as? ComponentLike ?: return@indexOfFirst false
            comp.stripped.trim().startsWith("Fuel:")
        }
        if (fuelIndex == -1) return Result.unmodified

        var currentIndex = fuelIndex - 1
        val foundBlocks = mutableListOf<IntRange>()

        while (currentIndex >= 0 && foundBlocks.size < 4) {
            while (currentIndex >= 0) {
                val isBlank = when (val line = list[currentIndex]) {
                    is ComponentLike -> line.stripped.isBlank()
                    is SpacerLine -> true
                    else -> false
                }
                if (!isBlank) break
                currentIndex--
            }
            if (currentIndex < 0) break

            val blockEnd = currentIndex
            while (currentIndex >= 0) {
                val isBlank = when (val line = list[currentIndex]) {
                    is ComponentLike -> line.stripped.isBlank()
                    is SpacerLine -> true
                    else -> false
                }
                if (isBlank) break
                currentIndex--
            }
            val blockStart = currentIndex + 1

            foundBlocks.add(blockStart..blockEnd)
        }

        if (foundBlocks.size < 3) return Result.unmodified

        val firstBlockLine = list[foundBlocks[0].first] as? ComponentLike ?: return Result.unmodified
        val isMechanicLine = firstBlockLine.stripped.trim().startsWith("Apply Drill Parts")
        if (isMechanicLine && foundBlocks.size < 4) return Result.unmodified

        val upgradeRange = if (isMechanicLine) foundBlocks[1] else foundBlocks[0]
        val engineRange = if (isMechanicLine) foundBlocks[2] else foundBlocks[1]
        val tankRange = if (isMechanicLine) foundBlocks[3] else foundBlocks[2]

        val tankText = (list[tankRange.first] as? ComponentLike)?.stripped?.trim() ?: ""
        if (!tankText.contains("Tank") && !tankText.startsWith("Fuel Tank:")) {
            return Result.unmodified
        }

        val upgradeComp = parseComponent(list, upgradeRange, ComponentType.UPGRADE) ?: return Result.unmodified
        val engineComp = parseComponent(list, engineRange, ComponentType.ENGINE) ?: return Result.unmodified
        val tankComp = parseComponent(list, tankRange, ComponentType.TANK) ?: return Result.unmodified

        val removeStart = tankRange.first
        val removeEnd = if (isMechanicLine) foundBlocks[0].last else foundBlocks[0].last

        for (i in removeEnd downTo removeStart) {
            list.removeAt(i)
        }

        list.add(removeStart, upgradeComp)
        list.add(removeStart, SpacerLine(height = 4))
        list.add(removeStart, engineComp)
        list.add(removeStart, SpacerLine(height = 4))
        list.add(removeStart, tankComp)

        return Result.modified
    }

    private fun parseComponent(list: MutableList<TooltipLine>, range: IntRange, type: ComponentType): DrillComponentLine? {
        val lines = range.mapNotNull { list[it] as? ComponentLike }
        if (lines.isEmpty()) return null
        val isInstalled = !lines[0].stripped.trim().endsWith("Not Installed")
        return DrillComponentLine(type, lines[0], lines.drop(1), isInstalled)
    }

    data class DrillComponentLine(
        val type: ComponentType,
        val nameLine: ComponentLike,
        val statLines: List<ComponentLike>,
        val isInstalled: Boolean
    ) : ExtractableTooltipLine {

        override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, id("drill/slot"), x, y, 16, 16, ARGB.opaque(TextColor.DARK_GRAY))

            val drawColor = if (isInstalled) type.color else ARGB.opaque(TextColor.GRAY)
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, type.id, x, y, 16, 16, drawColor)

            graphics.text(font, nameLine.charSequence, x + 22, y + 4, -1)

            var currentY = y + 16
            for (stat in statLines) {
                graphics.text(font, stat.charSequence, x + 22, currentY, -1)
                currentY += font.lineHeight + 1
            }
        }

        override fun getWidth(font: Font): Int {
            val nameWidth = nameLine.width + 22
            val statsWidth = statLines.maxOfOrNull { it.width + 22 } ?: 0
            return max(nameWidth, max(statsWidth, 100))
        }

        override fun getHeight(font: Font): Int = 18 + (statLines.size * (font.lineHeight + 1))
    }

    enum class ComponentType(location: String, val color: Int) {
        TANK("tank", ARGB.opaque(TextColor.RED)),
        ENGINE("engine", ARGB.opaque(TextColor.GOLD)),
        UPGRADE("upgrade", ARGB.opaque(TextColor.LIGHT_PURPLE));

        val id = id("drill/$location")
    }
}