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

@RegisterFeature
data object ItemAbility : TooltipFeature() {
    override val enabled: Boolean get() = MiscConfig.itemAbility
    override val priority: Int = 20

    private val abilityRegex = Regex("(?<name>.*Ability: .+?)\\s{2,}(?<key>[A-Z ]+ CLICK)").toComponentRegex()

    override fun ItemStack.applies() = DataTypes.SKYBLOCK_ID() != null

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        var modified = false

        while (canRead()) {
            val line = read()

            val matched = abilityRegex.match(line, "name", "key") { [name, key] ->
                originalMerger.add(AbilityHeaderLine(name, key))
                modified = true
            }

            if (!matched) {
                add(line)
            }
        }

        return@withComponentMerger modified.asResult()
    }

    data class AbilityHeaderLine(
        val nameComponent: Component,
        val keyComponent: Component
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