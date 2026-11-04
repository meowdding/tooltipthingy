package me.owdding.iconographic.utils.debug

import me.owdding.iconographic.Iconographic.id
import me.owdding.iconographic.utils.chat.sendWithPrefix
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.devutils.DevUtils
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugToggle
import java.nio.file.Path
import kotlin.io.path.Path

internal fun debugToggle(path: String, description: String = path): DebugToggle {
    return DebugToggle(id(path), description, TttDevUtils)
}

@Module
internal data object TttDevUtils : DevUtils() {
    override val commandName: String = "iconographic dev toggle"
    override fun send(component: MutableComponent) = component.sendWithPrefix()
    override val path: Path = System.getProperty("iconographic.property_path")?.let { Path(it) } ?: McClient.config.resolve("iconographic.properties")
    override val supportsChangingDefault: Boolean = true
    override val namespace: String = "iconographic"
}
