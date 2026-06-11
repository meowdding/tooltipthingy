package me.owdding.iconographic.config.categories.misc

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.owdding.iconographic.config.AutoTranslated
import me.owdding.iconographic.utils.StarStyle

object MiscConfig : CategoryKt("misc"), AutoTranslated {
    override val translationBase: String = "iconographic.config.misc"
    override val name: TranslatableValue = Translated(translationBase)

    val petAbilities by autoBoolean(true)
    val petLevel by autoBoolean(true)
    val petFavourite by autoBoolean(true)

    val drillFuel by autoBoolean(true)
    val drillComponents by autoBoolean(true)

    val starStyle by autoEnum(StarStyle.FIVE_POINT)
}