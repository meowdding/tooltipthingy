package me.owdding.tooltipthingy.features.tags

import me.owdding.tooltipthingy.config.categories.misc.MiscConfig
import me.owdding.tooltipthingy.config.categories.tag.TagConfig
import me.owdding.tooltipthingy.system.RegisterFeature
import me.owdding.tooltipthingy.system.TooltipFeature
import me.owdding.tooltipthingy.system.TooltipTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.substring
import kotlin.math.min

@RegisterFeature
data object StarsFeature : TooltipFeature() {
    override val enabled: Boolean get() = TagConfig.stars
    override val priority: Int = 2

    private val starIcons = setOf("✪", "➊", "➋", "➌", "➍", "➎")
    val starIconRegex = Regex("(?<name>.+?) [${starIcons.joinToString("|")}]+")

    private val colors = listOf(TextColor.GOLD, TextColor.RED, TextColor.PINK)

    override fun ItemStack.applies(): Boolean = DataTypes.STAR_COUNT() != null

    override fun ItemStack.rightTags(): List<TooltipTag> {
        val stars = DataTypes.STAR_COUNT() ?: 0
        if (stars == 0) {
            return emptyList()
        }

        val baseTier = min(0, stars - 5) / 5
        val moreTier = stars - 5 * (baseTier + 1)

        return buildList {
            val amount = min(5, stars)
            repeat(amount) {
                val color = if (it < moreTier) colors[baseTier + 1] else colors[baseTier]
                add(TooltipTag.identifier(MiscConfig.starStyle.identifier, 11, 11, color))
            }
        }
    }

    override fun ItemStack.nameReplacement(original: Component): Component {
        return original.substring(0, original.stripped.replace(starIconRegex, "$1").length)
    }
}