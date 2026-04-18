package me.owdding.tooltipthingy.features.base

import me.owdding.tooltipthingy.system.RegisterFeature
import me.owdding.tooltipthingy.system.TooltipFeature
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.extentions.get

@RegisterFeature
data object Defaults : TooltipFeature() {
    override val priority: Int = Int.MIN_VALUE

    override fun ItemStack.rarityOverride(): SkyBlockRarity? = this[DataTypes.RARITY]


}