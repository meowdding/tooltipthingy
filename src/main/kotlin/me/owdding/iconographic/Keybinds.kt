package me.owdding.iconographic

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import me.owdding.lib.utils.MeowddingKeybind
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyReleasedEvent

@Module
object Keybinds {
    @JvmField
    @Volatile
    @get:JvmStatic
    @set:JvmStatic
    var isTogglePressed = false

    private val category = Iconographic.id("iconographic.keybind.category")

    @JvmStatic @get:JvmName("TOGGLE_KEYBIND")
    val TOGGLE_KEYBIND = MeowddingKeybind(
        category,
        "iconographic.keybind.toggle",
        InputConstants.UNKNOWN.value,
    )

    @Subscription
    context(event: ScreenKeyPressedEvent)
    fun onKeyDown() {
        if (TOGGLE_KEYBIND.matches(event)) {
            this.isTogglePressed = true
        }
    }
    @Subscription
    context(event: ScreenKeyReleasedEvent)
    fun onKeyUp() {
        if (TOGGLE_KEYBIND.matches(event)) {
            this.isTogglePressed = false
        }
    }

}