package me.owdding.iconographic.utils.chat

import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.config.categories.visuals.VisualsConfig
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.utils.text.SkyBlockColor
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

object DisplayColor {
    private val config get() = VisualsConfig.skyBlockColor

    val BLACK: Int get() = if (config) SkyBlockColor.BLACK else TextColor.BLACK
    val DARK_BLUE: Int get() = if (config) SkyBlockColor.DARK_BLUE else TextColor.DARK_BLUE
    val DARK_GREEN: Int get() = if (config) SkyBlockColor.DARK_GREEN else TextColor.DARK_GREEN
    val DARK_AQUA: Int get() = if (config) SkyBlockColor.DARK_AQUA else TextColor.DARK_AQUA
    val DARK_RED: Int get() = if (config) SkyBlockColor.DARK_RED else TextColor.DARK_RED
    val DARK_PURPLE: Int get() = if (config) SkyBlockColor.DARK_PURPLE else TextColor.DARK_PURPLE
    val GOLD: Int get() = if (config) SkyBlockColor.GOLD else TextColor.GOLD
    val GRAY: Int get() = if (config) SkyBlockColor.GRAY else TextColor.GRAY
    val DARK_GRAY: Int get() = if (config) SkyBlockColor.DARK_GRAY else TextColor.DARK_GRAY
    val BLUE: Int get() = if (config) SkyBlockColor.BLUE else TextColor.BLUE
    val GREEN: Int get() = if (config) SkyBlockColor.GREEN else TextColor.GREEN
    val AQUA: Int get() = if (config) SkyBlockColor.AQUA else TextColor.AQUA
    val RED: Int get() = if (config) SkyBlockColor.RED else TextColor.RED
    val LIGHT_PURPLE: Int get() = if (config) SkyBlockColor.LIGHT_PURPLE else TextColor.LIGHT_PURPLE
    val YELLOW: Int get() = if (config) SkyBlockColor.YELLOW else TextColor.YELLOW
    val WHITE: Int get() = if (config) SkyBlockColor.WHITE else TextColor.WHITE

    val SkyBlockRarity.displayColor get() = if (config) skyBlockColor else color
}
