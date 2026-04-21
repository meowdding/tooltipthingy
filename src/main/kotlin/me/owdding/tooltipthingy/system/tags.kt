package me.owdding.tooltipthingy.system

import me.owdding.tooltipthingy.TooltipThingy.id
import me.owdding.tooltipthingy.font
import me.owdding.tooltipthingy.utils.chat.ChatUtils
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font

interface TooltipTag {
    fun extract(graphics: GuiGraphicsExtractor, x: Int, y: Int)
    val width: Int

    companion object {
        fun literal(component: Component, color: Int = -1) = TextTag.create(component, color)
        fun literal(text: String, color: Int = -1, init: MutableComponent.() -> Unit = {}) = TextTag.create(Text.of(text, init), color)
        fun identifier(id: Identifier, width: Int, height: Int = 13, color: Int = -1, background: Boolean = false) = IdentifierTag.create(id, width, height, color, background)
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
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, id("tag"), x, y, width - 1, height, color)
        graphics.text(font, text, x + 5, y + 2, color)
    }
}

@ConsistentCopyVisibility
data class IdentifierTag private constructor(val identifier: Identifier, val iconWidth: Int, val iconHeight: Int, val color: Int, val background: Boolean) : TooltipTag {
    companion object {
        fun create(identifier: Identifier, width: Int, height: Int, color: Int, background: Boolean = false): TooltipTag = IdentifierTag(identifier, width, height, ARGB.opaque(color), background)
    }

    override val width get() = iconWidth + if (background) 10 else 1

    val height get() = 13

    override fun extract(graphics: GuiGraphicsExtractor, x: Int, y: Int) {
        if (background) {
            // This could be broken
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, id("tag"), x, y, width - 1, height, color)
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, x + 5, y + 2, iconWidth, 10, color)
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, x, y + (13 - height) / 2, iconWidth, iconHeight, color)
        }
    }
}