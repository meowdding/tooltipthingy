package me.owdding.iconographic.features.tags

import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.config.categories.tag.TagConfig
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeatureWithContext
import me.owdding.iconographic.system.TooltipTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.substring

@RegisterFeature
data object PetTags : TooltipFeatureWithContext<MutableList<String>>() {
    override val enabled: Boolean get() = TagConfig.pet
    override fun createContext(): MutableList<String> = mutableListOf()
    override val priority: Int = 2

    val petCategories = buildMap {
        fun pet(skill: String) {
            put(
                "$skill Pet",
                PetCategory(skill, "Pet") {
                    it.substring("$skill Pet, ".length)
                },
            )
        }

        fun mount(skill: String) {
            put(
                "$skill Mount",
                PetCategory(skill, "Mount") {
                    it.substring("$skill Mount, ".length)
                },
            )
        }
        put("All Skills", PetCategory("Pet"))
        pet("Mining")
        mount("Mining")
        pet("Combat")
        mount("Combat")
        put(
            "Combat Morph",
            PetCategory("Combat", "Morph") {
                it.substring("Combat Morph, ".length)
            },
        )
        pet("Foraging")
        pet("Enchanting")
        pet("Taming")
        pet("Farming")
        put(
            "Gabagool Pet, feed to gain XP",
            PetCategory("Gabagool", "Pet") {
                it.substring("Gabagool Pet, ".length)
            },
        )
        pet("Alchemy")
        pet("Fishing")
        pet("Fractured Soul")
    }.mapKeys { (key) -> key.lowercase() }

    fun String.removeSkin() = if (this.endsWith("Skin")) this.substringBeforeLast(",") else this

    context(context: MutableList<String>)
    override fun ItemStack.leftTags(): List<TooltipTag> = context.map(TooltipTag::literal)

    context(context: MutableList<String>)
    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        if (!hasNext { it.stripped.removeSkin().lowercase() in petCategories }) return@withComponentMerger Result.modified
        addUntil {
            it.stripped.removeSkin().lowercase() in petCategories
        }
        if (!canRead()) return@withComponentMerger Result.unmodified
        val line = read()
        val category = petCategories[line.stripped.removeSkin().trim().lowercase()] ?: return@withComponentMerger Result.unmodified
        if ((category.lineModifier != null)) {
            val modifiedLine = category.lineModifier(line)
            if (modifiedLine != Component.empty()) {
                add(modifiedLine)
            }
        } else {
            skipSpace()
        }

        context.addAll(category.tags)
        Result.modified
    }

    data class PetCategory(
        val tags: List<String>,
        val lineModifier: ((Component) -> Component)? = null,
    ) {
        constructor(vararg tags: String, lineModifier: ((Component) -> Component)? = null) : this(tags.asList(), lineModifier)
    }
}
