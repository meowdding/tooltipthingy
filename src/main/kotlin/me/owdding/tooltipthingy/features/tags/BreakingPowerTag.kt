package me.owdding.tooltipthingy.features.tags

import me.owdding.tooltipthingy.TooltipLine
import me.owdding.tooltipthingy.system.RegisterFeature
import me.owdding.tooltipthingy.system.Result
import me.owdding.tooltipthingy.system.TooltipFeatureWithContext
import me.owdding.tooltipthingy.system.TooltipTag
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.MutableList

@RegisterFeature
data object BreakingPowerTag : TooltipFeatureWithContext<AtomicInteger>() {
    override fun createContext(): AtomicInteger = AtomicInteger(-1)
    override val priority: Int = 2

    val breakingPower = Regex("(?i)Breaking Power (\\d+)")

    context(context: AtomicInteger)
    override fun ItemStack.rightTags(): List<TooltipTag> {
        val breakingPower = context.get()
        if (breakingPower == -1) {
            return emptyList()
        }

        return listOf(TooltipTag.literal("bp $breakingPower", TextColor.DARK_GREEN))
    }


    context(context: AtomicInteger)
    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        if (!hasNext { it.stripped.lowercase().matches(breakingPower) }) return@withComponentMerger Result.modified
        addUntil { it.stripped.lowercase().matches(breakingPower) }
        if (!canRead()) return@withComponentMerger Result.unmodified
        val line = read()
        val breakingPower = line.stripped.replace(breakingPower, "$1").toInt()

        skipSpace()

        context.set(breakingPower)

        Result.modified
    }
}