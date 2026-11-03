package me.owdding.iconographic.features

import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.config.categories.misc.MiscConfig
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
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@RegisterFeature
data object ItemAbility : TooltipFeature() {
    override val enabled: Boolean get() = MiscConfig.alignedItemAbility
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

                    if (MiscConfig.clickIcons) {
                        val strippedKey = key.stripped
                        val hasSneak = "SNEAK" in strippedKey
                        val hasLeft = "LEFT CLICK" in strippedKey
                        val hasRight = "RIGHT CLICK" in strippedKey

                        if (hasSneak || hasLeft || hasRight) {
                            finalKey = ClickFeature.getIcon(hasSneak, hasLeft, hasRight)
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
