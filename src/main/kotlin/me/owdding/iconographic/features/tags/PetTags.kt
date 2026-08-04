package me.owdding.iconographic.features.tags

import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.config.categories.tag.TagConfig
import me.owdding.iconographic.font
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeatureWithContext
import me.owdding.iconographic.system.TooltipTag
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.regex.component.match
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.Text.prefix
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.substring

@RegisterFeature
data object PetTags : TooltipFeatureWithContext<MutableList<String>>() {
    override val enabled: Boolean get() = TagConfig.pet
    override fun createContext(): MutableList<String> = mutableListOf()
    override val priority: Int = 2

    private val skinRegex = Regex(".* (?:Pet|Mount|Morph),\\s(?<skinName>.* Skin)").toComponentRegex()

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
        if (category.lineModifier != null) {
            add(category.lineModifier(line))
        } else {
            skipSpace()
        }

        skinRegex.match(line, "skinName") { [skinName] ->
            originalMerger.destination.add(1,skinLine(skinName))
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

    data class skinLine(
        val skinComponent: Component,
    ) : ExtractableTooltipLine {

        override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {
            graphics.text(font, skinComponent, x, y, -1)
        }

        override fun getWidth(font: Font): Int {
            return font.width(skinComponent)
        }

        override fun getHeight(font: Font): Int = font.lineHeight
    }
}
