package me.owdding.iconographic.features.gui

import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.features.pet.PetLevel
import me.owdding.iconographic.lines.SpacerLine
import me.owdding.iconographic.render.SeparatorRenderer
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedFloat
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@RegisterFeature
data object SkillLevelBar : TooltipFeature() {
    override val enabled: Boolean get() = MiscConfig.skillLevelBar
    override val priority: Int = 10

    private val progressRegex = Regex("^(?:Progress(?: to (?:Level|Mastery) \\d+)?: .*|Max Skill level reached!)$")
    private val titleRegex = Regex("Your Skills|Dungeoneering|.+ Skill|.+ Class Perks")

    // TODO: abstract petlevelline away as im reusing it (for drill fuel too)

    override fun ItemStack.applies() = McScreen.self?.title?.stripped?.let { titleRegex.matches(it) } == true

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        if (!hasNext { it.stripped.matches(progressRegex) }) return@withComponentMerger Result.Companion.unmodified

        addUntil { it.stripped.matches(progressRegex) }
        if (!canRead()) return@withComponentMerger Result.Companion.unmodified

        val headerLine = read().stripped.trim()
        val isMaxed = headerLine == "Max Skill level reached!"

        if (!canRead()) return@withComponentMerger Result.Companion.unmodified
        val xpLine = read().stripped.trim()

        val levelLine = if (isMaxed) {
            PetLevel.PetLevelLine(xpLine.parseFormattedFloat())
        } else if (xpLine.contains("/")) {
            val parts = xpLine.split("/")
            PetLevel.PetLevelLine(
                parts[0].parseFormattedFloat(),
                parts[1].parseFormattedFloat()
            )
        } else {
            return@withComponentMerger Result.Companion.unmodified
        }

        skipSpace()

        originalMerger.destination.add(SpacerLine(height = 3))
        originalMerger.destination.add(SeparatorRenderer)
        originalMerger.destination.add(levelLine)
        originalMerger.destination.add(SeparatorRenderer)
        originalMerger.destination.add(SpacerLine(height = 3))

        return@withComponentMerger Result.Companion.modified
    }
}