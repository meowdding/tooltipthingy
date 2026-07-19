package me.owdding.iconographic.features.tags

import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.config.categories.tag.TagConfig
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeatureWithContext
import me.owdding.iconographic.system.TooltipTag
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@RegisterFeature
data object PetCandyTag : TooltipFeatureWithContext<MutableList<TooltipTag>>() {
    override val enabled: Boolean get() = TagConfig.petCandy == true
    override fun createContext(): MutableList<TooltipTag> = mutableListOf()
    override val priority: Int = 1

    override fun ItemStack.applies(): Boolean = (DataTypes.PET_DATA()?.candyUsed ?: 0) > 0

    val petCandy = Regex("(?i)\\((\\d+)/10\\) Pet Candy Used")

    context(context: MutableList<TooltipTag>)
    override fun ItemStack.leftTags(): List<TooltipTag> = context.take(1)

    context(context: MutableList<TooltipTag>)
    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        if (!hasNext { it.stripped matches petCandy }) return@withComponentMerger Result.modified
        addUntil {
            it.stripped matches petCandy
        }
        if (!canRead()) return@withComponentMerger Result.unmodified
        val line = read()
        val candyAmount = petCandy.find(line.stripped)?.groups[1]?.value ?: return@withComponentMerger Result.unmodified
        skipSpace()

        val color = when (candyAmount.toInt()) { // this should maybe account for the max candy amount, but I doubt the admins will ever increase it :clueless:
            in 1..3 -> TextColor.YELLOW
            in 4..6 -> TextColor.ORANGE
            in 7..9 -> TextColor.RED
            10 -> TextColor.DARK_RED
            else -> TextColor.GRAY
        }

        val comp = Text.of("CANDY:$candyAmount/10", color)

        context.add(TooltipTag.literal(comp, color))
        Result.modified
    }
}
