package me.owdding.tooltipthingy.features.tags

import me.owdding.tooltipthingy.config.categories.tag.TagConfig
import me.owdding.tooltipthingy.system.RegisterFeature
import me.owdding.tooltipthingy.system.TooltipFeature
import me.owdding.tooltipthingy.system.TooltipTag
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes

@RegisterFeature
data object RarityTag : TooltipFeature() {
    override val enabled: Boolean get() = TagConfig.rarity
    override val priority: Int = 10

    override fun ItemStack.leftTags(): List<TooltipTag> {
        val rarity = DataTypes.RARITY() ?: return emptyList()
        return listOf(TooltipTag.literal(rarity.displayName, rarity.color))
    }
}