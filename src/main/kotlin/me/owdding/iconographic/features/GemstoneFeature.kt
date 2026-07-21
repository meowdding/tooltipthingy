package me.owdding.iconographic.features

import me.owdding.iconographic.ComponentAlignment
import me.owdding.iconographic.ComponentLike
import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.Iconographic
import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.font
import me.owdding.iconographic.lines.SpacerLine
import me.owdding.iconographic.render.SeparatorRenderer
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.utils.chat.DisplayColor
import me.owdding.iconographic.utils.chat.DisplayColor.displayColor
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneQuality
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneSlotData
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.shadowColor
import kotlin.math.max

@RegisterFeature
data object GemstoneFeature : TooltipFeature() {
    override val enabled: Boolean get() = true // TODO OPTION
    override val priority: Int get() = 15

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        val gemstones = DataTypes.GEMSTONES()?.toMutableList() ?: mutableListOf()

        if (!hasNext { it.stripped.trim().startsWith("Gemstones:") }) return@withComponentMerger Result.unmodified

        addUntil { it.stripped.trim().startsWith("Gemstones:") }

        val parsedSlots = mutableListOf<ParsedSlot>()

        fun parseComponentForSlots(component: Component) {
            var inBracket = false
            var currentSymbol: MutableComponent = Text.of("")
            var isEmpty = true

            component.visualOrderText.accept { _, style, codepoint ->
                val char = String(Character.toChars(codepoint))
                if (char == "[") {
                    inBracket = true
                    currentSymbol = Text.of("")

                    val color = style.color?.value
                    isEmpty = color == null || color == TextColor.GRAY || color == TextColor.DARK_GRAY
                } else if (char == "]") {
                    if (inBracket) {
                        parsedSlots.add(ParsedSlot(currentSymbol, isEmpty))
                    }
                    inBracket = false
                } else if (inBracket) {
                    if (char.isNotBlank()) {
                        currentSymbol.append(Text.of(char).withStyle(style))
                    }
                }
                true
            }
        }

        if (canRead()) {
            parseComponentForSlots(read())
        }

        while (canRead() && peek().stripped.trim().startsWith("[")) {
            parseComponentForSlots(read())
        }

        val itemSlots = mutableListOf<VisualGemstoneSlot>()

        for (slot in parsedSlots) {
            if (!slot.isEmpty && gemstones.isNotEmpty()) {
                itemSlots.add(VisualGemstoneSlot.Filled(gemstones.removeAt(0)))
            } else {
                itemSlots.add(VisualGemstoneSlot.Empty(slot.symbol))
            }
        }

        for (gem in gemstones) {
            itemSlots.add(VisualGemstoneSlot.Filled(gem))
        }

        val headerComponent = Text.of {
            this.shadowColor = null
            this.color = DisplayColor.GRAY
            append("Gemstones")
        }

        originalMerger.add(SpacerLine(height = 3))
        originalMerger.add(ComponentLike(headerComponent, ComponentAlignment.Center, lines = true))
        originalMerger.add(SpacerLine(height = 4))

        originalMerger.add(GemstoneSlotsLine(itemSlots))

        originalMerger.add(SpacerLine(height = 4))
        originalMerger.add(SeparatorRenderer)
        originalMerger.add(SpacerLine(height = 3))

        Result.modified
    }

    data class ParsedSlot(val symbol: Component, val isEmpty: Boolean)

    sealed interface VisualGemstoneSlot {
        data class Filled(val data: GemstoneSlotData) : VisualGemstoneSlot
        data class Empty(val symbol: Component) : VisualGemstoneSlot
    }

    data class GemstoneSlotsLine(val visualSlots: List<VisualGemstoneSlot>) : ExtractableTooltipLine {
        private val slotSize = 26
        private val xSpacing = 3
        private val ySpacing = 3
        private val columns = 6
        private val chunks = visualSlots.chunked(columns)

        override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {
            var currentY = y

            chunks.forEach { slots ->
                val rowWidth = slots.size * (slotSize + xSpacing) - xSpacing
                val startX = x + (totalWidth - rowWidth) / 2

                slots.forEachIndexed { index, slot ->
                    val renderX = startX + (index * (slotSize + xSpacing))

                    val [color, extraRenderer] = when (slot) {
                        is VisualGemstoneSlot.Filled -> getQualityColor(slot.data.quality) to {
                            graphics.item(slot.data.skyblockId.toItem(), renderX + (slotSize - 16) / 2, currentY + (slotSize - 16) / 2)
                        }

                        is VisualGemstoneSlot.Empty -> DisplayColor.DARK_GRAY to {
                            val charX = renderX + slotSize / 2
                            val charY = currentY + (slotSize - font.lineHeight) / 2 + 1
                            graphics.centeredText(font, slot.symbol, charX, charY, -1)
                        }
                    }

                    graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        Iconographic.id("gemstone_slot"),
                        renderX,
                        currentY,
                        slotSize,
                        slotSize,
                        ARGB.opaque(boostSaturation(color, 2.5f)) // idk if the boost saturation does anything here
                    )
                    extraRenderer()
                }
                currentY += slotSize + ySpacing
            }
        }

        override fun getWidth(font: Font): Int {
            val maxSlots = if (visualSlots.size >= columns) columns else visualSlots.size
            return maxSlots * (slotSize + xSpacing) - xSpacing
        }

        override fun getHeight(font: Font): Int {
            return chunks.size * slotSize + max(0, chunks.size - 1) * ySpacing
        }

        private fun getQualityColor(quality: GemstoneQuality): Int = when (quality) {
            GemstoneQuality.ROUGH -> SkyBlockRarity.COMMON.displayColor
            GemstoneQuality.FLAWED -> SkyBlockRarity.UNCOMMON.displayColor
            GemstoneQuality.FINE -> SkyBlockRarity.RARE.displayColor
            GemstoneQuality.FLAWLESS -> SkyBlockRarity.EPIC.displayColor
            GemstoneQuality.PERFECT -> SkyBlockRarity.LEGENDARY.displayColor
        }
    }

    private fun boostSaturation(color: Int, multiplier: Float): Int {
        val r = ARGB.red(color)
        val g = ARGB.green(color)
        val b = ARGB.blue(color)

        val lum = r * 0.3f + g * 0.59f + b * 0.11f

        val newR = (lum + (r - lum) * multiplier).toInt().coerceIn(0, 255)
        val newG = (lum + (g - lum) * multiplier).toInt().coerceIn(0, 255)
        val newB = (lum + (b - lum) * multiplier).toInt().coerceIn(0, 255)

        return ARGB.color(ARGB.alpha(color), newR, newG, newB)
    }
}