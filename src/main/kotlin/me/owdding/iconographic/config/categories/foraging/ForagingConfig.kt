package me.owdding.iconographic.config.categories.foraging

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.owdding.iconographic.config.AutoTranslated

object ForagingConfig : CategoryKt("foraging"), AutoTranslated {
    override val translationBase: String = "iconographic.config.foraging"
    override val name: TranslatableValue = Translated(translationBase)

    val boosters by autoBoolean(true)
}
