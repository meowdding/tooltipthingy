package me.owdding.tooltipthingy.system

import me.owdding.tooltipthingy.TooltipThingy.id
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.util.ARGB
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font
import me.owdding.tooltipthingy.font
import me.owdding.tooltipthingy.utils.chat.ChatUtils
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.utils.text.Text

interface TooltipTag {
    fun extract(graphics: GuiGraphicsExtractor, x: Int, y: Int)
    val width: Int

    companion object {
        fun literal(component: Component, color: Int = -1) = TextTag.create(component, color)
        fun literal(text: String, color: Int = -1, init: MutableComponent.() -> Unit = {}) = TextTag.create(Text.of(text, init), color)
    }
}

@ConsistentCopyVisibility
data class TextTag private constructor(val text: Component, val color: Int) : TooltipTag {
    companion object {
        fun create(component: Component, color: Int): TooltipTag = TextTag(component.copy().also {
            it.font = ChatUtils.mc5
        }, ARGB.opaque(color))
    }

    inline val textWidth get() = font.width(text)
    override val width get() = textWidth + 10

    val height get() = 13

    override fun extract(graphics: GuiGraphicsExtractor, x: Int, y: Int) {
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            id("tag"),
            x,
            y,
            width - 1,
            height,
            color
        )

        graphics.text(font, text, x + 5, y + 2, color)
    }
}