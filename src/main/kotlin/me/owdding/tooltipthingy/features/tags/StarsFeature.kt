package me.owdding.tooltipthingy.features.tags

import me.owdding.tooltipthingy.system.RegisterFeature
import me.owdding.tooltipthingy.system.TooltipFeature

@RegisterFeature
data object StarsFeature : TooltipFeature() {
    override val priority: Int = 2
}