package me.owdding.iconographic.config.categories.pets

import com.teamresourceful.resourcefulconfig.api.types.info.Translatable
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.owdding.iconographic.config.AutoTranslated
import me.owdding.lib.extensions.shorten
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString

object PetsConfig : CategoryKt("pets"), AutoTranslated {
    override val translationBase: String = "iconographic.config.pets"
    override val name: TranslatableValue = Translated(translationBase)

    init { autoSeparator("main") }
    val petAbilities by autoBoolean(true)
    val petHeldItem by autoBoolean(true)
    val petLevel by autoBoolean(true)
    val experienceFormat by autoEnum(PetDisplayType.INTEGER)
    val maxExperienceFormat by autoEnum(PetDisplayType.SHORTENED)
    val petFavourite by autoBoolean(true)
}

enum class PetDisplayType : Translatable {
    SHORTENED {
        override fun format(number: Number): String = number.shorten()
    },
    DECIMALS {
        override fun format(number: Number): String = number.toFormattedString()
    },
    INTEGER {
        override fun format(number: Number): String = number.toInt().toFormattedString()
    },
    ;

    override fun getTranslationKey(): String = PetsConfig.translationBase + ".display_type.${this.name.lowercase()}"


    operator fun invoke(number: Number): String = format(number)

    abstract fun format(number: Number): String
}
