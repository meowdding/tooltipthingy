package me.owdding.iconographic.features

import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.TooltipLine.Companion.asComponentOrNull
import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockEnchantmentsRepo
import tech.thatgravyboat.skyblockapi.utils.extentions.getLore
import tech.thatgravyboat.skyblockapi.utils.text.SkyBlockColor
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color


@RegisterFeature
data object EnchantedBookFeature : TooltipFeature() {
    override val enabled: Boolean get() = MiscConfig.enchantedBookNames
    override val priority: Int = 0

    const val ANVIL_LINE: String = "Combinable in Anvil"

    val rngMeterCustomName = Regex("Enchanted Book \\((?<bookName>.*)\\)")

    override fun ItemStack.applies(): Boolean = DataTypes.SKYBLOCK_ID()?.isEnchantment == true

    private fun ItemStack.enchantTitle(): Component? {
        val name = customName?.stripped ?: return null

        // Covers finding the name for normal books in inventory
        if (name == "Enchanted Book") {
            return getLore().firstOrNull {
                val stripped = it.stripped
                stripped.isNotBlank() && stripped.trim() != ANVIL_LINE
            }
        }

        // Covers finding the name for books in rng meter menu e.g: Enchanted Book (Wisdom I)
        val matchedName = rngMeterCustomName.find(name)?.groups?.get(1)?.value ?: return null
        val entry = DataTypes.ENCHANTMENTS()?.entries?.firstOrNull() ?: return null
        val ultimate = SkyBlockEnchantmentsRepo.get(entry.key)?.isUltimate ?: return null

        return matchedName.asComponent {
            if (ultimate) {
                color = SkyBlockColor.LIGHT_PURPLE
                bold = true
            } else {
                color = SkyBlockColor.BLUE
            }
        }
    }

    // Replaces "Enchanted Book" item name with the enchant's title
    override fun ItemStack.nameReplacement(original: Component): Component = enchantTitle() ?: original

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result {
        val titleText = enchantTitle()?.stripped ?: return Result.unmodified
        val strippedLines = list.mapNotNull { it.asComponentOrNull()?.stripped?.trim() }

        // Finds the index for the first empty line
        val firstEmptyIndex = strippedLines.indexOfFirst { it.isBlank() }
        if (firstEmptyIndex == -1) return Result.unmodified

        // Finds the index for the [Combinable in Anvil] line
        val combinableInAnvilIndex = strippedLines.indexOfFirst { it == ANVIL_LINE }
        if (combinableInAnvilIndex == -1) return Result.unmodified

        // Finds the index for the enchant name + level line (e.g. [Dedication IV], [First Impression I])
        val titleIndex = strippedLines.indexOfFirst { it == titleText }
        if (titleIndex == -1) return Result.unmodified

        // performs the removal process
        listOf(titleIndex, firstEmptyIndex, combinableInAnvilIndex).forEach { list.removeAt(it) }
        return Result.modified
    }


}
