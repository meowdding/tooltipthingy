package me.owdding.tooltipthingy.api

@Suppress("FunctionName")
interface TooltipThingyCompat {

    fun `tooltipthingy$isSkyblocker`(): Boolean

}

val TooltipThingyCompat.isSkyblocker get() = this.`tooltipthingy$isSkyblocker`()