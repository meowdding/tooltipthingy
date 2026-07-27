package me.owdding.iconographic.config.categories.mining

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.owdding.iconographic.config.AutoTranslated

object MiningConfig : CategoryKt("mining"), AutoTranslated {
    override val translationBase: String = "iconographic.config.mining"
    override val name: TranslatableValue = Translated(translationBase)

    init { autoSeparator("drills") }
    val drillFuel by autoBoolean(true)
    val drillComponents by autoBoolean(true)

    init { autoSeparator("gemstones") }
    val gemstoneSlots by autoBoolean(true)
}
