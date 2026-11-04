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
data object PetTags : TooltipFeatureWithContext<MutableList<Pair<String, Int>>>() {
    override val enabled: Boolean get() = TagConfig.pet
    override fun createContext(): MutableList<Pair<String, Int>> = mutableListOf()
    override val priority: Int = 2

    val petCategories = buildMap {
        fun pet(skill: String, color: Int) {
            put(
                "$skill Pet",
                PetCategory(skill, color = color) {
                    it.substring("$skill Pet, ".length)
                },
            )
        }

        fun mount(skill: String, color: Int) {
            put(
                "$skill Mount",
                PetCategory(skill, "Mount", color = color) {
                    it.substring("$skill Mount, ".length)
                },
            )
        }

        put("All Skills", PetCategory(emptyList(), color = 0xAAAAAA))
        pet("Mining", 0xFF8C00)
        mount("Mining", 0xD97706)
        pet("Combat", 0xEF4444)
        mount("Combat", 0xDC2626)
        put(
            "Combat Morph",
            PetCategory("Combat", "Morph", color = 0xA72323) {
                it.substring("Combat Morph, ".length)
            },
        )
        pet("Foraging", 0x22C55E)
        pet("Enchanting", 0x7A33ED)
        pet("Taming", 0xEC4899)
        pet("Farming", 0xEAB308)
        put(
            "Gabagool Pet, feed to gain XP",
            PetCategory("Gabagool", color = 0xC084FC) {
                it.substring("Gabagool Pet, ".length)
            },
        )
        pet("Alchemy", 0x5190F4)
        pet("Fishing", 0x2F5ADB)
        pet("Fractured Soul", 0x9C56DC)
    }.mapKeys { (key) -> key.lowercase() }

    fun String.removeSkin() = if (this.endsWith("Skin")) this.substringBeforeLast(",") else this

    context(context: MutableList<Pair<String, Int>>)
    override fun ItemStack.leftTags(): List<TooltipTag> = context.map { [tag, color] ->
        TooltipTag.literal(tag, color = color)
    }

    context(context: MutableList<Pair<String, Int>>)
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

        context.addAll(category.tags.map { it to category.color })
        Result.modified
    }

    data class PetCategory(
        val tags: List<String>,
        val color: Int = -1,
        val lineModifier: ((Component) -> Component)? = null,
    ) {
        constructor(vararg tags: String, color: Int = -1, lineModifier: ((Component) -> Component)? = null) : this(tags.asList(), color, lineModifier)
    }
}
