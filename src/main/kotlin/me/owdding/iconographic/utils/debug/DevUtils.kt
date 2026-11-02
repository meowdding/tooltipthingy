package me.owdding.iconographic.utils.debug

import me.owdding.iconographic.Iconographic.id
import me.owdding.iconographic.utils.chat.sendWithPrefix
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.DevUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import java.nio.file.Path
import kotlin.io.path.Path

internal fun debugToggle(path: String, description: String = path): DebugToggle {
    return DebugToggle(id(path), description, TttDevUtils)
}

@Module
internal object TttDevUtils : DevUtils() {
    override val commandName: String = "iconographic dev toggle"
    override fun send(component: MutableComponent) = component.sendWithPrefix()
    override val path: Path = System.getProperty("iconographic.property_path")?.let { Path(it) } ?: McClient.config.resolve("iconographic.properties")

    init {
        loadFromProperties(path)
    }

    override val supportsChangingDefault: Boolean = true
    override val namespace: String = "iconographic"

    fun getString(key: String): String? = this.propertiesInstance.getProperty(key)

    fun getInt(key: String, default: Int = 0): Int = getString(key).parseFormattedInt(default)

    fun getBoolean(key: String): Boolean = getString(key) == "true"

    @Subscription
    private fun commandRegister(event: RegisterCommandsEvent) = super.onCommandRegister(event)
}
