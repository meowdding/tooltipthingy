package me.owdding.iconographic.features.tags

import me.owdding.iconographic.config.categories.tag.TagConfig
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.system.TooltipTag
import me.owdding.iconographic.utils.chat.ChatUtils
import me.owdding.iconographic.utils.chat.DisplayColor.displayColor
import me.owdding.ktmodules.Module
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import me.owdding.lib.rendering.text.textShader
import net.minecraft.util.Util
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font

@RegisterFeature
data object RarityTag : TooltipFeature() {
    override val enabled: Boolean get() = TagConfig.rarity
    override val priority: Int = 10

    // TODO: probably repofy this?
    private val disallowedIds = listOf(
        SkyBlockId.item("skyblock_menu"),
    )

    override fun ItemStack.applies(): Boolean = DataTypes.SKYBLOCK_ID() != null && DataTypes.SKYBLOCK_ID() !in disallowedIds

    override fun ItemStack.leftTags(): List<TooltipTag> {
        val rarity = DataTypes.RARITY() ?: return emptyList()

        val comp = if (DataTypes.RECOMBOBULATOR() == true) {
            val left = Text.of("> ") {
                font = ChatUtils.sparkles
                textShader = ShaderCache.getOrRegisterUpgradeShader(rarity)
            }

            val rarityText = Text.of(rarity.displayName)

            val right = Text.of(" <") {
                font = ChatUtils.sparkles
                textShader = ShaderCache.getOrRegisterUpgradeShader(rarity)
            }
            Text.join(left, rarityText, right)
        } else {
            Text.of(rarity.displayName, rarity.displayColor)
        }

        return listOf(TooltipTag.literal(comp, rarity.displayColor))
    }
}

@Module
object ShaderCache {

    private val cache = Util.memoize { rarity: SkyBlockRarity ->
        val previous = rarity.getPreviousRarity()
        val startColor = previous.displayColor
        val endColor = rarity.displayColor
        GradientTextShader(
            colors = listOf(
                endColor,
                startColor,
                endColor,
                endColor,
                endColor,
                endColor,
            ),
            direction = GradientTextShader.Direction.UP,
            speed = 3f,
        )
    }

    fun getOrRegisterUpgradeShader(rarity: SkyBlockRarity): GradientTextShader = cache.apply(rarity)

    private fun SkyBlockRarity.getPreviousRarity(): SkyBlockRarity {
        if (ordinal <= 0) return this
        return SkyBlockRarity.entries[ordinal - 1]
    }
}
