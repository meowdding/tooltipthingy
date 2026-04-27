package me.owdding.tooltipthingy.features.tags

import me.owdding.tooltipthingy.TooltipLine
import me.owdding.tooltipthingy.TooltipLine.Companion.asComponentOrNull
import me.owdding.tooltipthingy.config.categories.tag.TagConfig
import me.owdding.tooltipthingy.system.RegisterFeature
import me.owdding.tooltipthingy.system.Result
import me.owdding.tooltipthingy.system.TooltipFeature
import me.owdding.tooltipthingy.system.TooltipTag
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@RegisterFeature
data object CategoryTags : TooltipFeature() {
    override val enabled: Boolean get() = TagConfig.category
    override val priority: Int = 0

    override fun ItemStack.leftTags(): List<TooltipTag> {
        val category = DataTypes.CATEGORY() ?: return emptyList()

        if (!category.isDungeon && category.name.isEmpty()) return emptyList()

        return buildList {
            add(TooltipTag.literal(category.name, 0xAAAAAA))
            if (category.isDungeon) {
                add(TooltipTag.literal("Dungeonized", 0xAAAAAA))
            }
        }
    }

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        if (!addUntilRarityLine(DataTypes.RARITY() ?: return@withComponentMerger Result.unmodified)) {
            return@withComponentMerger Result.unmodified
        }

        while (originalMerger.destination.lastOrNull()?.asComponentOrNull()?.stripped?.isBlank() == true) originalMerger.destination.removeLastOrNull()
        if (!canRead()) return@withComponentMerger Result.unmodified
        read()
        Result.modified
    }
}