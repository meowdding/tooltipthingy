package me.owdding.iconographic.features.tags

import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.TooltipLine.Companion.asComponentOrNull
import me.owdding.iconographic.config.categories.tag.TagConfig
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.system.TooltipTag
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@RegisterFeature
data object CategoryTags : TooltipFeature() {
    override val enabled: Boolean get() = TagConfig.category
    override val priority: Int = 0

    private val idRegex = Regex("(?i)^(?<name>.+?)\\s*\\(\\s*ID\\s+(?<id>[A-Z0-9]+)\\s*\\)$")

    override fun ItemStack.leftTags(): List<TooltipTag> {
        val category = DataTypes.CATEGORY() ?: return emptyList()

        if (!category.isDungeon && category.name.isEmpty()) return emptyList()

        return buildList {
            var name = category.name
            var shardId: String? = null

            if (TagConfig.shardId) {
                idRegex.match(name, "name", "id") { [n, id] ->
                    name = n
                    shardId = id
                }
            }

            add(TooltipTag.literal(name, 0xAAAAAA))
            if (shardId != null) {
                add(TooltipTag.literal(shardId, 0xAAAAAA))
            }
            if (category.isDungeon) {
                add(TooltipTag.literal("Dungeonized", 0xAAAAAA))
            }
        }
    }

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        val category = DataTypes.CATEGORY() ?: return@withComponentMerger Result.unmodified

        if (canRead()) {
            val topText = peek().stripped.trim()

            if (topText.isNotEmpty() && (topText.equals(category.name, ignoreCase = true) || topText.equals("Lava Rod", ignoreCase = true))) {
                read()
                skipSpace()
            }
        }

        if (!addUntilRarityLine(DataTypes.RARITY() ?: return@withComponentMerger Result.unmodified)) {
            return@withComponentMerger Result.unmodified
        }

        while (originalMerger.destination.lastOrNull()?.asComponentOrNull()?.stripped?.isBlank() == true) originalMerger.destination.removeLastOrNull()
        if (!canRead()) return@withComponentMerger Result.unmodified
        read()
        Result.modified
    }
}