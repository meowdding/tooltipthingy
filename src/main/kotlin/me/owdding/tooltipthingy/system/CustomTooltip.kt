package me.owdding.tooltipthingy.system

import me.owdding.tooltipthingy.ComponentLike
import me.owdding.tooltipthingy.Tooltip
import me.owdding.tooltipthingy.TooltipInformation
import me.owdding.tooltipthingy.generated.TooltipThingyTooltipFeatures
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.extentions.get

object CustomTooltip {
    val features: List<TooltipFeatureWithContext<*>> by lazy {
        TooltipThingyTooltipFeatures.collected.sortedByDescending {
            it.priority
        }
    }

    fun update(item: ItemStack, tooltipInfo: TooltipInformation): Tooltip {
        val tooltipFeatures = features.filter { it.enabled }

        val name = tooltipInfo.entries.firstNotNullOfOrNull { (it as? ComponentLike)?.component } ?: CommonComponents.EMPTY
        val lines = tooltipInfo.entries.drop(1).toMutableList()
        val tags: MutableList<TooltipTag> = mutableListOf()
        val rightTags: MutableList<TooltipTag> = mutableListOf()

        var modifiedItem: ItemStack = item.copy()
        var topRightIcon: Identifier? = null
        var nameOverride: Component? = null
        var nameReplacement: Component? = null
        var rarityOverride: SkyBlockRarity? = null
        var isRarityUpgraded = false

        var result: Result? = null
        val usedFeatures: MutableSet<TooltipFeatureWithContext<*>> = mutableSetOf()

        for (feature in tooltipFeatures) with(feature) {
            fun <T : Any> addIfNotNull(element: T?): T? = element?.apply {
                usedFeatures.add(feature)
            }

            fun <ContextType> TooltipFeatureWithContext<ContextType>.update() {
                val context = createContext()
                context(context) {
                    result = item.modifyEntries(if (result?.propagateFurther == false) lines.toMutableList() else lines, result)
                    if (result.modified) {
                        usedFeatures.add(feature)
                    }
                    modifiedItem = addIfNotNull(modifiedItem.modify()) ?: modifiedItem
                    nameOverride = nameOverride ?: addIfNotNull(item.nameOverride())
                    nameReplacement = nameReplacement ?: addIfNotNull(item.nameReplacement(name))
                    rightTags.addAll(item.rightTags().also {
                        addIfNotNull(it.takeUnless { it.isEmpty() })
                    })
                    tags.addAll(item.leftTags().also {
                        addIfNotNull(it.takeUnless { it.isEmpty() })
                    })
                    topRightIcon = topRightIcon ?: addIfNotNull(item.topRightIcon())
                    rarityOverride = rarityOverride ?: addIfNotNull(item.rarityOverride())
                    isRarityUpgraded = isRarityUpgraded || item.isRarityUpgraded().apply {
                        if (this) usedFeatures.add(feature)
                    }
                }
            }

            if (item.applies()) {
                update()
            }
        }
        val rarity = modifiedItem[DataTypes.RARITY] ?: SkyBlockRarity.COMMON

        return Tooltip(
            item = modifiedItem,
            name = nameOverride ?: nameReplacement ?: name,
            leftTags = tags,
            rightTags = rightTags,
            topRightIcon = topRightIcon,
            rarity = rarity,
            isRarityUpgraded = isRarityUpgraded,
            entries = lines
        )
    }
}