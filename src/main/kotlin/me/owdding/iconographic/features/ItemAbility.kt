package me.owdding.iconographic.features

import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.features.ClickFeature.Style
import me.owdding.iconographic.font
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.regex.component.match
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@RegisterFeature
data object ItemAbility : TooltipFeature() {
    override val enabled: Boolean get() = MiscConfig.alignedItemAbility || MiscConfig.clickIcons != Style.OFF
    override val priority: Int = 20

    private val abilityRegex = Regex("(?<name>.*Ability: .+?)\\s{2,}(?<key>[A-Z ]+ CLICK)").toComponentRegex()

    override fun ItemStack.applies() = DataTypes.SKYBLOCK_ID() != null

    private fun Component.isItemAbility() = abilityRegex.matches(this)

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        var modified = false

        while (hasNext { it.isItemAbility() }) {
            addUntil {
                it.isItemAbility()
            }

            if (canRead()) {
                abilityRegex.match(read(), "name", "key") { [name, key] ->
                    var finalKey = key

                    when (MiscConfig.clickIcons) {
                        Style.OFF -> {}
                        Style.REPLACE -> {
                            val icons = ClickFeature.lookup(key.stripped.removeSuffix("CLICK"), false)
                            if (icons != null) {
                                finalKey = icons
                                modified = true
                            }
                        }

                        Style.INLINE -> {
                            val icons = ClickFeature.lookup(key.stripped.removeSuffix("CLICK"), true)
                            if (icons != null) {
                                finalKey = Text.join(key, Text.of(" "), icons)
                                modified = true
                            }
                        }
                    }

                    originalMerger.add(AbilityHeaderLine(name, finalKey))
                    modified = true
                }
            }
        }

        return@withComponentMerger modified.asResult()
    }

    data class AbilityHeaderLine(
        val nameComponent: Component,
        val keyComponent: Component,
    ) : ExtractableTooltipLine {

        override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {
            graphics.text(font, nameComponent, x, y, -1)

            val keyX = if (MiscConfig.alignedItemAbility) {
                x + totalWidth - font.width(keyComponent)
            } else {
                x + font.width(nameComponent) + font.width("  ")
            }

            graphics.text(font, keyComponent, keyX, y, -1)
        }

        override fun getWidth(font: Font): Int {
            val baseWidth = font.width(nameComponent) + font.width(keyComponent)
            return baseWidth + if (MiscConfig.alignedItemAbility) 15 else font.width("  ")
        }

        override fun getHeight(font: Font): Int = font.lineHeight
    }
}
