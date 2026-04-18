package me.owdding.tooltipthingy

import me.owdding.tooltipthingy.TooltipInformation.Companion.toInformation
import me.owdding.tooltipthingy.render.TooltipHeader
import me.owdding.tooltipthingy.system.CustomTooltip
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import org.joml.component1
import org.joml.component2
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import kotlin.math.max

object TooltipThingy : ClientModInitializer {
    @Volatile
    @JvmField
    var extractingItemTooltip: ItemStack? = null

    override fun onInitializeClient() {
        ItemTooltipCallback.EVENT
    }

    @JvmStatic
    fun GuiGraphicsExtractor.createTooltip(
        item: ItemStack,
        font: Font,
        lines: List<ClientTooltipComponent>,
        xo: Int,
        yo: Int,
        positioner: ClientTooltipPositioner,
        style: Identifier?,
    ): Runnable = {
        val tooltipInfo = lines.toInformation()

        val tooltip = CustomTooltip.update(item, tooltipInfo)
        val (
            rarity,
            isRarityUpgraded,
        ) = tooltip

        val entries = tooltip.entries.toMutableList()
        entries.addFirst(TooltipHeader(tooltip))
        var totalWidth = 0
        var totalHeight = 0

        for (line in entries) {
            totalWidth = max(line.getWidth(font), totalWidth)
            totalHeight += line.getHeight(font)
        }

        val [x, y] = positioner.positionTooltip(
            this.guiWidth(),
            this.guiHeight(),
            xo,
            yo,
            totalWidth,
            totalHeight
        )

            blitSprite(
                RenderPipelines.GUI_TEXTURED,
                id("background"),
                x - 5,
                y - 5,
                totalWidth + 10,
                totalHeight + 10,
                ARGB.opaque(rarity.color)
            )

            var yOffset = 0
            for (line in entries) {
                when (line) {
                    is ExtractableTooltipLine -> {
                        line.extract(this, totalWidth, x, y + yOffset)
                        yOffset += line.getHeight(font)
                    }

                    is ClientTooltipComponent -> {
                        line.extractText(this, font, x, y)
                        line.extractImage(font, x, y, totalWidth, line.getHeight(font), this)
                        yOffset += line.getHeight(font)
                    }
                }

        }
    }

    fun id(path: String) = Identifiers.of("tooltipthingy", path)
}