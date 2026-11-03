package me.owdding.iconographic.config.categories.misc

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.owdding.iconographic.config.AutoTranslated

object MiscConfig : CategoryKt("misc"), AutoTranslated {
    override val translationBase: String = "iconographic.config.misc"
    override val name: TranslatableValue = Translated(translationBase)

    init { autoSeparator("misc") }
    val itemAbility by autoBoolean(false)
    val enchantedBookNames by autoBoolean(true)
    val skillLevelBar by autoBoolean(true)

    init { autoSeparator("enchantments") }
    val itemEnchants by autoBoolean(true)
    val romanEnchant by autoBoolean(true)
    val smallEnchantText by autoBoolean(true)
    val enchantNameBackground by autoBoolean(false)
    val centerEnchantments by autoBoolean(true)
    val stackingProgress by autoBoolean(true)
}
