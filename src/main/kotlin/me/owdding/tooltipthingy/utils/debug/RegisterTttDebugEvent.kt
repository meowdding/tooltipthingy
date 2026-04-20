package me.owdding.tooltipthingy.utils.debug

import me.owdding.tooltipthingy.utils.chat.ChatUtils
import me.owdding.tooltipthingy.utils.extensions.literal
import tech.thatgravyboat.skyblockapi.api.events.misc.AbstractModRegisterDebugEvent
import tech.thatgravyboat.skyblockapi.utils.text.Text.send

internal class RegisterTttDebugEvent(base: RegisterTttCommandEvent) :
    AbstractModRegisterDebugEvent(ChatUtils.prefix, true, base) {

    fun tttRegister(name: String, commandName: String, init: DebugBuilder.() -> Unit) {
        base.registerWithCallback(name(commandName)) {
            RootDebugBuilder(name.literal()).apply(init).build().send()
        }
    }

}
