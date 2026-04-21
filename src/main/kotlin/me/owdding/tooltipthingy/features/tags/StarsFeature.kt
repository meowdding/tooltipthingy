package me.owdding.tooltipthingy.features.tags

import me.owdding.tooltipthingy.TooltipLine
import me.owdding.tooltipthingy.TooltipThingy.id
import me.owdding.tooltipthingy.config.categories.tag.TagConfig
import me.owdding.tooltipthingy.system.RegisterFeature
import me.owdding.tooltipthingy.system.Result
import me.owdding.tooltipthingy.system.TooltipFeatureWithContext
import me.owdding.tooltipthingy.system.TooltipTag
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import java.util.concurrent.atomic.AtomicInteger

@RegisterFeature
data object StarsFeature : TooltipFeatureWithContext<AtomicInteger>() {
    override val enabled: Boolean get() = TagConfig.stars
    override fun createContext(): AtomicInteger = AtomicInteger(0)
    override val priority: Int = 2

    private val colors = listOf(TextColor.GOLD, TextColor.RED, TextColor.PINK)

    context(context: AtomicInteger)
    override fun ItemStack.rightTags(): List<TooltipTag> {
        val stars = context.get()
        if (stars == 0) {
            return emptyList()
        }

        val baseTier = stars % 5
        val moreTier = stars / 5

        return buildList {
            repeat(5) {
                val color = if (it < moreTier) colors[baseTier] else colors[baseTier + 1]
                add(TooltipTag.identifier(id("star"), 11, 11, color))
            }
        }
    }

    context(context: AtomicInteger)
    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        // TODO: clear from name

        context.set(this@modifyEntries.getData(DataTypes.STAR_COUNT) ?: 0)

        Result.unmodified
    }
}