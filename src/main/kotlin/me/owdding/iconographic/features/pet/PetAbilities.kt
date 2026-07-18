package me.owdding.iconographic.features.pet

import me.owdding.iconographic.ComponentAlignment
import me.owdding.iconographic.ComponentLike
import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@RegisterFeature
data object PetAbilities : TooltipFeature() {
    override val enabled: Boolean get() = MiscConfig.petAbilities
    override val priority: Int = 20

    override fun ItemStack.applies(): Boolean = DataTypes.SKYBLOCK_ID()?.isPet == true

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        addUntil {
            it.isAbilityLine()
        }

        while (canRead() && peek().isAbilityLine()) {
            originalMerger.destination.add(ComponentLike(read(), ComponentAlignment.Center, true))
            while (canRead() && peek().stripped.isNotBlank()) {
                originalMerger.destination.add(ComponentLike(read(), ComponentAlignment.Center))
            }
            if (canRead()) copy()
        }

        Result.modified
    }

    fun Component.isAbilityLine(): Boolean {
        val colors = mutableSetOf<Int>()

        this.visualOrderText.accept { _, style, codepoint ->
            if (Character.isWhitespace(codepoint)) return@accept true
            colors.add(style.color?.value ?: TextColor.GRAY)
            true
        }

        return colors.size == 1 && TextColor.GOLD in colors && !this.stripped.startsWith("Held Item:")
    }
}