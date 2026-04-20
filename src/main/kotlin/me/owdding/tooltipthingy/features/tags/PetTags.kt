package me.owdding.tooltipthingy.features.tags

import me.owdding.tooltipthingy.TooltipLine
import me.owdding.tooltipthingy.config.categories.tag.TagConfig
import me.owdding.tooltipthingy.system.RegisterFeature
import me.owdding.tooltipthingy.system.Result
import me.owdding.tooltipthingy.system.TooltipFeatureWithContext
import me.owdding.tooltipthingy.system.TooltipTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.text.Text.prefix
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.substring
import kotlin.collections.MutableList

@RegisterFeature
data object PetTags : TooltipFeatureWithContext<MutableList<String>>() {
    override val enabled: Boolean get() = TagConfig.pet
    override fun createContext(): MutableList<String> = mutableListOf()
    override val priority: Int = 2

    val petCategories = buildMap {
        fun pet(skill: String) {
            put("$skill Pet", PetCategory(skill, "Pet"))
        }
        fun mount(skill: String) {
            put("$skill Mount", PetCategory(skill, "Mount"))
        }
        put("All Skills", PetCategory("Pet"))
        pet("Mining")
        mount("Mining")
        pet("Combat")
        mount("Combat")
        put("Combat Morph", PetCategory("Combat", "Morph"))
        pet("Foraging")
        pet("Enchanting")
        pet("Taming")
        pet("Farming")
        put("Gabagool Pet, feed to gain XP", PetCategory("Gabagool", "Pet") {
            val line = it.substring("Gabagool Pet, ".length + 1)
            line.prefix("F").withStyle(line.style)
        })
        pet("Alchemy")
        pet("Fishing")
        pet("Fractured Soul")
    }.mapKeys { (key) -> key.lowercase() }

    context(context: MutableList<String>)
    override fun ItemStack.leftTags(): List<TooltipTag> = context.map(TooltipTag::literal)

    context(context: MutableList<String>)
    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        if (!hasNext { it.stripped.lowercase() in petCategories }) return@withComponentMerger Result.modified
        addUntil {
            it.stripped.lowercase() in petCategories
        }
        if (!canRead()) return@withComponentMerger Result.unmodified
        val line = read()
        val category = petCategories[line.stripped.trim().lowercase()] ?: return@withComponentMerger Result.unmodified

        if (category.lineModifier != null) {
            add(category.lineModifier(line))
        } else {
            skipSpace()
        }

        context.addAll(category.tags)
        Result.modified
    }

    data class PetCategory(
        val tags: List<String>,
        val lineModifier: ((Component) -> Component)? = null
    ) {
        constructor(vararg tags: String, lineModifier: ((Component) -> Component)? = null) : this(tags.asList(), lineModifier)
    }
}