package me.owdding.tooltipthingy.utils

import me.owdding.tooltipthingy.TooltipThingy

enum class StarStyle {
    ONE,
    TWO,
    ;

    val identifier = TooltipThingy.id("stars/star_${ordinal + 1}")
}