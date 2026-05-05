package me.owdding.tooltipthingy.utils.debug

import me.owdding.ktmodules.Module
import me.owdding.tooltipthingy.TooltipThingy.id
import me.owdding.tooltipthingy.utils.chat.sendWithPrefix
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.DevUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.notExists
import kotlin.io.path.reader

internal fun debugToggle(path: String, description: String = path): DebugToggle {
    return DebugToggle(id(path), description, TttDevUtils)
}

@Module
object TttDevUtils : DevUtils() {
    override val commandName: String = "tooltipthingy dev toggle"
    override fun send(component: MutableComponent) = component.sendWithPrefix()
    val properties: Map<String, String> = loadFromProperties()

    fun getInt(key: String, default: Int = 0): Int {
        return properties[key].parseFormattedInt(default)
    }

    fun getBoolean(key: String): Boolean {
        return properties[key] == "true"
    }

    private fun loadFromProperties(): Map<String, String> {
        val properties = Properties()
        val path = System.getProperty("tooltipthingy.property_path")?.let { Path(it) } ?: McClient.config.resolve("tooltipthingy.properties")
        if (path.notExists()) return emptyMap()
        path.reader(Charsets.UTF_8).use {
            properties.load(it)
        }
        val map = mutableMapOf<String, String>()
        properties.forEach { (key, value) ->
            Identifiers.parseWithSeparator(key.toString(), '@')?.let {
                if (value.toString() == "true") {
                    states[it] = true
                }
            }
            map[key.toString()] = value.toString()
        }
        return map
    }

    @Subscription
    fun commandRegister(event: RegisterCommandsEvent) = super.onCommandRegister(event)
}