package me.owdding.iconographic.config.categories.tag

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.owdding.iconographic.config.AutoTranslated

object TagConfig : CategoryKt("tags"), AutoTranslated {
    override val translationBase: String = "iconographic.config.tags"
    override val name: TranslatableValue = Translated(translationBase)

    val rarity by autoBoolean(true)
    val category by autoBoolean(true)
    val breakingPower by autoBoolean(true)
    val furniture by autoBoolean(true)
    val pet by autoBoolean(true)
    val petCandy by autoBoolean(true)
    val shardId by autoBoolean(true)
    val stars by autoBoolean(true)
}