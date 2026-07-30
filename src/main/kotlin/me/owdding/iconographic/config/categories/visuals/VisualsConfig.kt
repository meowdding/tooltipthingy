package me.owdding.iconographic.config.categories.visuals

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.owdding.iconographic.config.AutoTranslated
import me.owdding.iconographic.features.stats.ExtraStatDisplay

object VisualsConfig : CategoryKt("visuals"), AutoTranslated {
    override val translationBase: String = "iconographic.config.visuals"
    override val name: TranslatableValue = Translated(translationBase)

    init { autoSeparator("general") }
    val spinny by autoBoolean(false)
    val vanillaBackground by autoBoolean(false)
    val skyBlockColor by autoBoolean(true)

    init { autoSeparator("specific") }
    val shinyHolographic by autoBoolean(true)
    val alignedStats by autoBoolean(true)
    val extraStatsDisplay by autoEnum(ExtraStatDisplay.NORMAL)
}
