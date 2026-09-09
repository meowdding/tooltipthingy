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
import tech.thatgravyboat.skyblockapi.utils.extentions.getLore
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped


@RegisterFeature
data object EnchantedBookFeature : TooltipFeature() {
    override val enabled: Boolean get() = MiscConfig.enchantedBookNames
    override val priority: Int = 0

    const val ANVIL_LINE: String = "Combinable in Anvil"

    override fun ItemStack.applies(): Boolean = DataTypes.SKYBLOCK_ID()?.isEnchantment == true

    private fun ItemStack.enchantTitle(): Component? {
        return getLore().firstOrNull { it.stripped.isNotBlank() && it.stripped.trim() != ANVIL_LINE }
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
