package me.owdding.iconographic.features

import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import me.owdding.lib.rendering.text.textShader
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@RegisterFeature
data object ShinyHolographicFeature : TooltipFeature() {
    override val enabled: Boolean = MiscConfig.shinyHolographic
    override val priority: Int = -1000 // Run at the Very Last

    override fun ItemStack.applies(): Boolean = DataTypes.SHINY() == true

    override fun ItemStack.nameReplacement(original: Component): Component {
        val mutable = original.copy()
        mutable.modifyColor(mutable.color)
        mutable.modifySiblings(mutable.color)
        return mutable
    }

    private fun MutableComponent.modifySiblings(parentColor: Int): MutableComponent {
        siblings.replaceAll { it.copy().modifySiblings(it.style.color?.value ?: parentColor) }
        modifyColor(parentColor)
        return this
    }

    private fun MutableComponent.modifyColor(parentColor: Int) {
        val currentColor = style.color?.value ?: parentColor
        val lighterColor = ARGB.scaleRGB(currentColor, 3f)
        textShader = GradientTextShader(
            colors = listOf(
                currentColor,
                lighterColor,
                currentColor,
                currentColor,
                currentColor,
            ),
            direction = GradientTextShader.Direction.RIGHT,
            speed = 3f
        )
    }
}