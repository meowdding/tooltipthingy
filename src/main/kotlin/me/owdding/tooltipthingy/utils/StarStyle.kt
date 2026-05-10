package me.owdding.tooltipthingy.utils

import me.owdding.tooltipthingy.TooltipThingy

enum class StarStyle(private val id: String) {
    FOUR_POINT("4"),
    FOUR_POINT_NO_BORDER("4_no_border"),
    FIVE_POINT("5"),
    FIVE_POINT_NO_BORDER("5_no_border"),
    ;

    val identifier = TooltipThingy.id("stars/star_${id}")
}