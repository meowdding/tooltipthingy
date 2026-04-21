package me.owdding.tooltipthingy.utils.chat

import me.owdding.tooltipthingy.TooltipThingy.id
import me.owdding.tooltipthingy.utils.chat.ChatUtils.prefix
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

object ChatUtils {
    val mc5 = id("mc5")

    val prefix = Text.of {
        append("«")
        append(Text.of {
            append("T", CatppuccinColors.Mocha.flamingo)
            append("T", CatppuccinColors.Mocha.pink)
            append("T", CatppuccinColors.Mocha.mauve)
        })
        append("»")
        this.color = TextColor.GRAY
    }
}

fun Component.sendWithPrefix() = Text.join(prefix, " ", this).send()
fun Component.sendWithPrefix(id: String) = Text.join(prefix, " ", this).send(id)

fun Component.sendSyncWithPrefix() = McClient.runOrNextTick { Text.join(prefix, " ", this).send() }
fun Component.sendSyncWithPrefix(id: String) = McClient.runOrNextTick { Text.join(prefix, " ", this).send(id) }
