package me.owdding.tooltipthingy.config

import com.google.gson.JsonObject
import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigLink
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import me.owdding.tooltipthingy.ApiDebug
import me.owdding.tooltipthingy.TooltipThingy
import me.owdding.tooltipthingy.config.categories.misc.MiscConfig
import me.owdding.tooltipthingy.config.categories.tag.TagConfig
import me.owdding.tooltipthingy.generated.BuildInfo
import me.owdding.tooltipthingy.utils.debug.DebugBuilder
import java.util.function.UnaryOperator


object Config : ConfigKt("tooltipthingy/config"), AutoTranslated {

    init {
        categories(TagConfig, MiscConfig)
    }

    override val translationBase: String = "tooltipthingy.config"

    override val name: TranslatableValue = TranslatableValue("TooltipThingy")
    override val description: TranslatableValue = TranslatableValue("TooltipThingy (v${BuildInfo.VERSION})")
    override val links: Array<ResourcefulConfigLink> = emptyArray()

    @JvmStatic @get:JvmName("isEnabled")
    val enabled by autoBoolean(true)
    val spinny by autoBoolean(false)

    override val patches: Map<Int, UnaryOperator<JsonObject>> = configPatches.withIndex().associate { (index, value) -> index to UnaryOperator(value) }
    override val version: Int = patches.size + 1

    fun save() = TooltipThingy.config.save()

    @ApiDebug("Config", commandName = "config")
    internal fun debug(builder: DebugBuilder) = with(builder) {
    }
}