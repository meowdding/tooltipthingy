package me.owdding.tooltipthingy.features.pet

import me.owdding.tooltipthingy.TooltipThingy.id
import me.owdding.tooltipthingy.config.categories.misc.MiscConfig
import me.owdding.tooltipthingy.system.RegisterFeature
import me.owdding.tooltipthingy.system.TooltipFeatureWithContext
import me.owdding.tooltipthingy.system.TooltipTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
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

    context(context: AtomicBoolean)
    override fun ItemStack.rightTags(): List<TooltipTag> {
        if (!context.get()) {
            return emptyList()
        }

        return listOf((TooltipTag.identifier(id("star"), 11, 11, TextColor.YELLOW)))
    }

    context(context: AtomicBoolean)
    override fun ItemStack.nameReplacement(original: Component): Component {
        val starInName = original.stripped.matches(starIconRegex)
        val hasPetData = this.getData(DataTypes.PET_DATA) != null
        context.set(starInName && hasPetData)
        if (!context.get()) return original
        return original.substring(2, original.stripped.length)
    }
}