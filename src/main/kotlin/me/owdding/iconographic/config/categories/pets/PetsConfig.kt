package me.owdding.iconographic.config.categories.pets

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.owdding.iconographic.config.AutoTranslated

object PetsConfig : CategoryKt("pets"), AutoTranslated {
    override val translationBase: String = "iconographic.config.pets"
    override val name: TranslatableValue = Translated(translationBase)

    init { autoSeparator("main") }
    val petAbilities by autoBoolean(true)
    val petHeldItem by autoBoolean(true)
    val petLevel by autoBoolean(true)
    val petFavourite by autoBoolean(true)
}
