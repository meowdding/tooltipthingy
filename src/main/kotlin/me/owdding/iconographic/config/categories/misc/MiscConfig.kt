package me.owdding.iconographic.config.categories.misc

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.owdding.iconographic.config.AutoTranslated

object MiscConfig : CategoryKt("misc"), AutoTranslated {
    override val translationBase: String = "iconographic.config.misc"
    override val name: TranslatableValue = Translated(translationBase)

    val skyBlockColor by autoBoolean(true)

    val shinyHolographic by autoBoolean(true)

    val alignedStats by autoBoolean(true)

    val itemAbility by autoBoolean(true)

    val petAbilities by autoBoolean(true)
    val petHeldItem by autoBoolean(true)
    val petLevel by autoBoolean(true)
    val petFavourite by autoBoolean(true)

    val skillLevelBar by autoBoolean(true)

    val drillFuel by autoBoolean(true)
    val drillComponents by autoBoolean(true)
}