package me.owdding.iconographic.features

import me.owdding.iconographic.ComponentAlignment
import me.owdding.iconographic.ComponentLike
import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.Iconographic
import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.config.categories.foraging.ForagingConfig
import me.owdding.iconographic.lines.SpacerLine
import me.owdding.iconographic.render.SeparatorRenderer
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.utils.ColorUtils
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
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.get
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.shadowColor
import kotlin.math.max

@RegisterFeature
data object BoostersFeature : TooltipFeature() {
    override val enabled: Boolean get() = ForagingConfig.boosters
    override val priority: Int get() = 15

    private val symbolToBooster = mapOf(
        "ꕮ" to "sweep",
        "☯" to "hunting_wisdom",
        "४" to "fighting",
        "⸙" to "foraging_wisdom",
        "⎋" to "foraging_fortune",
        "ꆤ" to "luck",
    )

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        val boosterLevels = DataTypes.BOOSTER_TIERS() ?: return@withComponentMerger Result.unmodified

        if (!hasNext { it.stripped.trim().startsWith("Boosters:") }) return@withComponentMerger Result.unmodified

        addUntil { it.stripped.trim().startsWith("Boosters:") }

        val parsedSlots = mutableListOf<ParsedSlot>()

        fun parseComponentForSlots(component: Component) {
            var currentSymbol: MutableComponent = Text.of("")
            var afterColon = false

            fun checkSymbol() {
                if (currentSymbol.stripped.isNotBlank()) {
                    val booster = symbolToBooster[currentSymbol.stripped] ?: return
                    val tier = boosterLevels[booster] ?: return
                    val id = getBoosterId(booster, tier) ?: return
                    parsedSlots.add(ParsedSlot(currentSymbol, id))
                }
            }

            component.visualOrderText.accept { _, style, codepoint ->
                val char = String(Character.toChars(codepoint))

                if (!afterColon) {
                    if (char == ":") {
                        afterColon = true
                    }
                } else if (char == "/" || char == " ") {
                    checkSymbol()
                    currentSymbol = Text.of("")
                } else {
                    currentSymbol.append(Text.of(char).withStyle(style))
                }
                true
            }

            checkSymbol()
        }

        if (canRead()) {
            parseComponentForSlots(read())
        }

        val headerComponent = Text.of {
            this.shadowColor = null
            this.color = DisplayColor.GRAY
            append("Boosters")
        }

        originalMerger.add(SpacerLine(height = 3))
        originalMerger.add(ComponentLike(headerComponent, ComponentAlignment.Center, lines = true))
        originalMerger.add(SpacerLine(height = 4))

        originalMerger.add(BoostersSlotLine(parsedSlots))

        originalMerger.add(SpacerLine(height = 4))
        originalMerger.add(SeparatorRenderer)
        originalMerger.add(SpacerLine(height = 3))

        Result.modified
    }

    data class ParsedSlot(val symbol: Component, val booster: SkyBlockId)

    data class BoostersSlotLine(val slots: List<ParsedSlot>) : ExtractableTooltipLine {
        private val slotSize = 26
        private val xSpacing = 3
        private val ySpacing = 3
        private val columns = 6
        private val chunks = slots.chunked(columns)

        override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {
            var currentY = y

            chunks.forEach { slots ->
                val rowWidth = slots.size * (slotSize + xSpacing) - xSpacing
                val startX = x + (totalWidth - rowWidth) / 2

                slots.forEachIndexed { index, slot ->
                    val renderX = startX + (index * (slotSize + xSpacing))

                    graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        Iconographic.id("gemstone/gemstone_slot"),
                        renderX,
                        currentY,
                        slotSize,
                        slotSize,
                        ARGB.opaque(ColorUtils.boostSaturation(getRarityColor(slot.booster), 2.5f)), // idk if the boost saturation does anything here
                    )
                    graphics.item(slot.booster.toItem(), renderX + (slotSize - 16) / 2, currentY + (slotSize - 16) / 2)
                }
                currentY += slotSize + ySpacing
            }
        }

        override fun getWidth(font: Font): Int {
            val maxSlots = if (slots.size >= columns) columns else slots.size
            return maxSlots * (slotSize + xSpacing) - xSpacing
        }

        override fun getHeight(font: Font): Int {
            return chunks.size * slotSize + max(0, chunks.size - 1) * ySpacing
        }

        private fun getRarityColor(id: SkyBlockId): Int = id.toItem()[DataTypes.RARITY]?.displayColor ?: DisplayColor.DARK_PURPLE
    }

    private fun getBoosterId(baseId: String, level: Int): SkyBlockId? = if (level == 1 && baseId != "hunting_wisdom") {
        SkyBlockId.item("${baseId}_booster")
    } else {
        val rarity = SkyBlockRarity.entries.getOrNull(level - 1) ?: return null
        SkyBlockId.item("${baseId}_booster_${rarity.name}")
    }
}
