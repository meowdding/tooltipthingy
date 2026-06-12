package me.owdding.iconographic.features.tags

import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.config.categories.tag.TagConfig
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.system.TooltipTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import kotlin.math.max
import kotlin.math.min

@RegisterFeature
data object StarsFeature : TooltipFeature() {
    override val enabled: Boolean get() = TagConfig.stars
    override val priority: Int = 2

    private val starIcons = setOf("✪", "➊", "➋", "➌", "➍", "➎")
    private val starIconRegex = Regex("\\s*(?:${starIcons.joinToString("|")})+")

    private val colors = listOf(TextColor.GOLD, TextColor.RED, TextColor.PINK)

    override fun ItemStack.applies(): Boolean = DataTypes.STAR_COUNT() != null

    override fun ItemStack.rightTags(): List<TooltipTag> {
        val stars = DataTypes.STAR_COUNT()?.takeUnless { it == 0 } ?: return emptyList()

        val baseTier = max(0, stars - 5) / 5
        val moreTier = stars - 5 * (baseTier + 1)

        return buildList {
            val amount = min(5, stars)
            repeat(amount) {
                val color = if (it < moreTier) colors[(baseTier + 1).coerceAtMost(colors.lastIndex)] else colors[baseTier]
                add(TooltipTag.identifier(MiscConfig.starStyle.identifier, 11, 11, color))
            }
        }
    }

    override fun ItemStack.nameReplacement(original: Component): Component? {
        if (starIcons.none { it in original.stripped }) return null

        return Component.empty().withStyle(original.style).also { result ->
            original.siblings.forEach { sibling ->
                val trimmed = sibling.stripped.replace(starIconRegex, "")
                result.append(Component.literal(trimmed).withStyle(sibling.style))
            }
        }
    }
}