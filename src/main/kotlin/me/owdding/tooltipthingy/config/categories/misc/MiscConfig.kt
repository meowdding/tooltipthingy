package me.owdding.tooltipthingy.config.categories.misc

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.owdding.tooltipthingy.config.AutoTranslated

object MiscConfig : CategoryKt("misc"), AutoTranslated {
    override val translationBase: String = "tooltipthingy.config.misc"
    override val name: TranslatableValue = Translated(translationBase)

    val petAbilities by autoBoolean(true)
    val petLevel by autoBoolean(true)
}