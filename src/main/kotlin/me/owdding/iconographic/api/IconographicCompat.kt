package me.owdding.iconographic.api

import me.owdding.iconographic.Iconographic
import me.owdding.lib.utils.MeowddingLogger
import me.owdding.lib.utils.MeowddingLogger.Companion.featureLogger
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.world.item.ItemStack
import java.util.function.BiConsumer
import java.util.function.Consumer

@Suppress("FunctionName")
interface IconographicCompat {

    fun `iconographic$isSkyblocker`(): Boolean

}

val IconographicCompat.isSkyblocker get() = this.`iconographic$isSkyblocker`()


object ImcHandler : MeowddingLogger by Iconographic.featureLogger() {

    fun setup() {
        this.setup<Runnable>("item") { stack, runnable ->
            Iconographic.pushPop(stack) { runnable.run() }
        }
    }

    private fun <Data> setup(path: String, consumer: BiConsumer<ItemStack, Data>) {
        val invokers = runCatching { FabricLoader.getInstance().getEntrypoints("iconographic:imc/$path", Consumer::class.java) }
            .onFailure(Throwable::printStackTrace)
            .getOrDefault(listOf())

        for (invoker in invokers) {
            try {
                (invoker as Consumer<BiConsumer<ItemStack, Data>>).accept(consumer)
            } catch (e: Throwable) {
                error("Failed to create imc entrypoint", e)
            }
        }
    }
}
