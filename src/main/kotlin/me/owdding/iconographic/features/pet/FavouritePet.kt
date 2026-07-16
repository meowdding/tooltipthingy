package me.owdding.iconographic.features.pet

import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.TooltipFeatureWithContext
import me.owdding.iconographic.system.TooltipTag
import me.owdding.iconographic.utils.Stars
import me.owdding.iconographic.utils.chat.DisplayColor
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.substring
import java.util.concurrent.atomic.AtomicBoolean

@RegisterFeature
data object FavouritePet : TooltipFeatureWithContext<AtomicBoolean>() {
    override val enabled: Boolean get() = MiscConfig.petFavourite

    override val priority: Int = 2

    val starIconRegex = Regex("⭐ (?<name>.+?)")

    override fun createContext(): AtomicBoolean {
        return AtomicBoolean(false)
    }

    override fun ItemStack.applies(): Boolean = DataTypes.SKYBLOCK_ID()?.isPet == true

    context(context: AtomicBoolean)
    override fun ItemStack.rightTags(): List<TooltipTag> {
        if (!context.get()) {
            return emptyList()
        }

        return listOf((TooltipTag.identifier(Stars.POINTY.id, 11, 11, DisplayColor.YELLOW)))
    }

    context(context: AtomicBoolean)
    override fun ItemStack.nameReplacement(original: Component): Component? {
        val starInName = original.stripped.matches(starIconRegex)
        val hasPetData = this.getData(DataTypes.PET_DATA) != null
        context.set(starInName && hasPetData)
        if (!context.get()) return null
        return original.substring(2, original.stripped.length)
    }
}