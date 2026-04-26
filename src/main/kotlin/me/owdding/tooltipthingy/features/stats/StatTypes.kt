package me.owdding.tooltipthingy.features.stats

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor


@Suppress("unused")
enum class StatType(
    val icon: String,
    val color: Int,
    vararg val names: String,
    val format: StatFormat = StatFormat.SIGNED_NUMBER
) {
    // Combat stats

    HEALTH('❤', TextColor.RED),
    DAMAGE("?", TextColor.RED),
    DEFENSE('❈', TextColor.GREEN),
    STRENGTH('❁', TextColor.RED),
    INTELLIGENCE('✎', TextColor.AQUA),
    CRIT_DAMAGE('☠', TextColor.BLUE),
    CRIT_CHANCE('☣', TextColor.BLUE),
    ATTACK_SPEED('⚔', TextColor.YELLOW, "Bonus Attack Speed"),
    ABILITY_DAMAGE('๑', TextColor.RED),
    TRUE_DEFENSE('❂', TextColor.WHITE),
    FEROCITY('⫽', TextColor.RED),
    HEALTH_REGEN('❣', TextColor.RED),
    VITALITY('♨', TextColor.DARK_RED),
    MENDING('☄', TextColor.GREEN),
    SWING_RANGE('Ⓢ', TextColor.YELLOW),

    // Gathering Stats

    MINING_SPEED('⸕', TextColor.GOLD),
    MINING_SPREAD('▚', TextColor.YELLOW),
    GEMSTONE_SPREAD(MINING_SPREAD),
    PRISTINE('✧', TextColor.DARK_PURPLE),
    BASE_FORTUNE('☘', TextColor.GOLD), // helper
    MINING_FORTUNE(BASE_FORTUNE),
    ORE_FORTUNE(BASE_FORTUNE),
    BLOCK_FORTUNE(BASE_FORTUNE),
    DWARVEN_METAL_FORTUNE(BASE_FORTUNE),
    GEMSTONE_FORTUNE(BASE_FORTUNE),
    FORAGING_FORTUNE(BASE_FORTUNE),
    FARMING_FORTUNE(BASE_FORTUNE),
    WHEAT_FORTUNE(BASE_FORTUNE),
    CARROT_FORTUNE(BASE_FORTUNE),
    POTATO_FORTUNE(BASE_FORTUNE),
    PUMPKIN_FORTUNE(BASE_FORTUNE),
    MELON_FORTUNE(BASE_FORTUNE),
    MUSHROOM_FORTUNE(BASE_FORTUNE),
    CACTUS_FORTUNE(BASE_FORTUNE),
    SUGAR_CANE_FORTUNE(BASE_FORTUNE),
    NETHER_WART_FORTUNE(BASE_FORTUNE),
    COCOA_BEANS_FORTUNE(BASE_FORTUNE),
    FIG_FORTUNE(BASE_FORTUNE),
    MANGROVE_FORTUNE(BASE_FORTUNE),

    // Wisdom Stats

    BASE_WISDOM('☯', TextColor.DARK_AQUA),
    COMBAT_WISDOM(BASE_WISDOM),
    MINING_WISDOM(BASE_WISDOM),
    FARMING_WISDOM(BASE_WISDOM),
    FORAGING_WISDOM(BASE_WISDOM),
    FISHING_WISDOM(BASE_WISDOM),
    ENCHANTING_WISDOM(BASE_WISDOM),
    ALCHEMY_WISDOM(BASE_WISDOM),
    CARPENTRY_WISDOM(BASE_WISDOM),
    RUNECRAFTING_WISDOM(BASE_WISDOM),
    SOCIAL_WISDOM(BASE_WISDOM),
    TAMING_WISDOM(BASE_WISDOM),
    HUNTING_WISDOM(BASE_WISDOM),

    // Misc Stats

    SPEED('✦', TextColor.WHITE),
    MAGIC_FIND('✯', TextColor.AQUA),
    PET_LUCK('♣', TextColor.LIGHT_PURPLE),
    FISHING_SPEED('☂', TextColor.AQUA),
    SEA_CREATURE_CHANCE('α', TextColor.DARK_AQUA),
    DOUBLE_HOOK_CHANCE('⚓', TextColor.BLUE),
    TROPHY_FISH_CHANCE('♔', TextColor.GOLD),
    BONUS_PEST_CHANCE('ൠ', TextColor.DARK_GREEN),
    HEAT_RESISTANCE('♨', TextColor.RED),
    COLD_RESISTANCE('❄', TextColor.AQUA),
    FEAR('☠', TextColor.DARK_PURPLE),

    PULL('ᛷ', TextColor.AQUA),
    SWEEP('∮', TextColor.DARK_GREEN),
    RESPIRATION('⚶', TextColor.DARK_AQUA),
    PRESSURE_RESISTANCE('❍', TextColor.BLUE),
    SYPHON_LUCK('♣', TextColor.LIGHT_PURPLE),
    ;

    constructor(stat: StatType) : this(stat.icon, stat.color)
    constructor(
        icon: Char,
        color: Int,
        vararg names: String,
    ) : this(icon.toString(), color, names = names)

    private val displayName: String = names.firstOrNull() ?: toFormattedName()
    val displayIcon = Text.of {
        append(icon, this@StatType.color)
    }

    override fun toString(): String = displayName

    val displayText: Component = Text.of {
        append(icon, this@StatType.color)
        append(" ")
        append(displayName, TextColor.GRAY)
    }

    companion object {
        fun fromName(name: String): StatType? {
            return entries.find { it.displayName.equals(name, ignoreCase = true) || it.names.any { it.equals(name, ignoreCase = true) } }
        }
    }
}


enum class StatFormat {
    NUMBER,
    PERCENTAGE,
    SIGNED_NUMBER,
}