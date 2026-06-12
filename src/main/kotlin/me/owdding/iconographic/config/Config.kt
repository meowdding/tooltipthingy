package me.owdding.iconographic.config

import com.google.gson.JsonObject
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigLink
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import me.owdding.iconographic.ApiDebug
import me.owdding.iconographic.Iconographic
import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.config.categories.tag.TagConfig
import me.owdding.iconographic.generated.BuildInfo
import me.owdding.iconographic.utils.debug.DebugBuilder
import java.util.function.UnaryOperator


object Config : ConfigKt("iconographic/config"), AutoTranslated {

    init {
        categories(TagConfig, MiscConfig)
    }

    override val translationBase: String = "iconographic.config"

    override val name: TranslatableValue = TranslatableValue("Iconographic")
    override val description: TranslatableValue = TranslatableValue("Iconographic (v${BuildInfo.VERSION})")
    override val links: Array<ResourcefulConfigLink> = emptyArray()

    @JvmStatic @get:JvmName("isEnabled")
    val enabled by autoBoolean(true)

    @JvmStatic @get:JvmName("skyblockOnly")
    val onlyInSkyblock by autoBoolean(true)

    val spinny by autoBoolean(false)
    val vanillaBackground by autoBoolean(false)

    override val patches: Map<Int, UnaryOperator<JsonObject>> = configPatches.withIndex().associate { (index, value) -> index to UnaryOperator(value) }
    override val version: Int = patches.size + 1

    fun save() = Iconographic.config.save()

    @ApiDebug("Config", commandName = "config")
    internal fun debug(builder: DebugBuilder) = with(builder) {
    }
}