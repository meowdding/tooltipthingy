package me.owdding.iconographic.features

import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.font
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.utils.chat.ChatUtils
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.regex.component.match
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font

@RegisterFeature
data object ItemAbility : TooltipFeature() {
    override val enabled: Boolean get() = MiscConfig.itemAbility
    override val priority: Int = 20

    private val sneak = Text.of("s") { this.font = ChatUtils.clicks }
    private val left = Text.of("<") { this.font = ChatUtils.clicks }
    private val right = Text.of(">") { this.font = ChatUtils.clicks }

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
                    var key = key
                    if (MiscConfig.itemAbilityIcons) {
                        val strippedKey = key.stripped
                        val newKeys = Text.of()
                        if (strippedKey.contains("SNEAK")) {
                            newKeys.append(sneak)
                            key = newKeys
                        }
                        if (strippedKey.contains("LEFT CLICK")) {
                            newKeys.append(left)
                            key = newKeys
                        }
                        if (strippedKey.contains("RIGHT CLICK")) {
                            newKeys.append(right)
                            key = newKeys
                        }

                    }
                    originalMerger.add(AbilityHeaderLine(name, key))
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
            graphics.text(font, keyComponent, x + totalWidth - font.width(keyComponent), y, -1)
        }

        override fun getWidth(font: Font): Int {
            return font.width(nameComponent) + font.width(keyComponent) + 15
        }

        override fun getHeight(font: Font): Int = font.lineHeight
    }
}
