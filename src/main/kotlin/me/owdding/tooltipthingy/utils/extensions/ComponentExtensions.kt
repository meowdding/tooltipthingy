package me.owdding.tooltipthingy.utils.extensions

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.builders.TooltipBuilder
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.collections.addAll

fun String.literal() = Text.of(this)
fun componentList(init: TooltipBuilder.() -> Unit) = TooltipBuilder().apply(init).lines()
fun TooltipBuilder.addAll(iterable: Iterable<Component>) = lines().addAll(iterable)
