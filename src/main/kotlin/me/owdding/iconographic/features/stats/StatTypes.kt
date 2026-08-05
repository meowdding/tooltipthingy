package me.owdding.iconographic.features.stats

import me.owdding.iconographic.utils.chat.ChatUtils
import me.owdding.iconographic.utils.chat.DisplayColor
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font
import java.util.concurrent.atomic.AtomicInteger

private val idCounter: AtomicInteger = AtomicInteger()

enum class StatType(
    icon: String?,
    val color: Int,
    vararg val names: String,
) {
    // Combat stats
    HEALTH('', DisplayColor.RED),
    DAMAGE('', DisplayColor.RED),
    DEFENSE('', DisplayColor.GREEN),
    STRENGTH('', DisplayColor.RED),
    INTELLIGENCE('', DisplayColor.AQUA),
    CRIT_DAMAGE('', DisplayColor.BLUE),
    CRIT_CHANCE('', DisplayColor.BLUE),
    ATTACK_SPEED('', DisplayColor.YELLOW, "Attack Speed", "Bonus Attack Speed"),
    ABILITY_DAMAGE('', DisplayColor.RED),
    TRUE_DEFENSE('', DisplayColor.WHITE),
    FEROCITY('', DisplayColor.RED),
    HEALTH_REGEN('', DisplayColor.RED),
    VITALITY('', DisplayColor.DARK_RED),
    MENDING('', DisplayColor.GREEN),
    SWING_RANGE('', DisplayColor.YELLOW),

    // Mining Stats
    BREAKING_POWER('', DisplayColor.DARK_GREEN),
    MINING_SPEED('', DisplayColor.GOLD),
    MINING_SPREAD('', DisplayColor.YELLOW),
    GEMSTONE_SPREAD('', DisplayColor.YELLOW),
    PRISTINE('', DisplayColor.DARK_PURPLE),
    BASE_MINING_FORTUNE('', DisplayColor.GOLD), // helper
    MINING_FORTUNE(BASE_MINING_FORTUNE),
    ORE_FORTUNE(BASE_MINING_FORTUNE),
    BLOCK_FORTUNE(BASE_MINING_FORTUNE),
    DWARVEN_METAL_FORTUNE(BASE_MINING_FORTUNE),
    GEMSTONE_FORTUNE(BASE_MINING_FORTUNE),

    // Farming Stats
    BONUS_PEST_CHANCE('', DisplayColor.DARK_GREEN),
    OVERBLOOM('', DisplayColor.YELLOW),
    BASE_FARMING_FORTUNE('', DisplayColor.GOLD), // helper
    FARMING_FORTUNE(BASE_FARMING_FORTUNE),
    WHEAT_FORTUNE(BASE_FARMING_FORTUNE),
    CARROT_FORTUNE(BASE_FARMING_FORTUNE),
    POTATO_FORTUNE(BASE_FARMING_FORTUNE),
    PUMPKIN_FORTUNE(BASE_FARMING_FORTUNE),
    MELON_SLICE_FORTUNE(BASE_FARMING_FORTUNE),
    CACTUS_FORTUNE(BASE_FARMING_FORTUNE),
    SUGAR_CANE_FORTUNE(BASE_FARMING_FORTUNE),
    NETHER_WART_FORTUNE(BASE_FARMING_FORTUNE),
    COCOA_BEANS_FORTUNE(BASE_FARMING_FORTUNE),
    MUSHROOM_FORTUNE(BASE_FARMING_FORTUNE),
    SUNFLOWER_FORTUNE(BASE_FARMING_FORTUNE),
    MOONFLOWER_FORTUNE(BASE_FARMING_FORTUNE),
    WILD_ROSE_FORTUNE(BASE_FARMING_FORTUNE),

    // Foraging Stats
    SWEEP('', DisplayColor.DARK_GREEN),
    BASE_FORAGING_FORTUNE('', DisplayColor.GOLD), // Helper
    FORAGING_FORTUNE(BASE_FORAGING_FORTUNE),
    FIG_FORTUNE(BASE_FORAGING_FORTUNE),
    MANGROVE_FORTUNE(BASE_FORAGING_FORTUNE),

    // Fishing Stats
    FISHING_SPEED('', DisplayColor.AQUA),
    SEA_CREATURE_CHANCE('', DisplayColor.DARK_AQUA),
    DOUBLE_HOOK_CHANCE('', DisplayColor.BLUE),
    TROPHY_CHANCE('', DisplayColor.GOLD),
    TREASURE_CHANCE('', DisplayColor.GOLD),

    // Hunting Stats
    PULL('', DisplayColor.AQUA),
    HUNTING_FORTUNE('', DisplayColor.LIGHT_PURPLE),

    // Wisdom Stats
    BASE_WISDOM('☯', DisplayColor.DARK_AQUA),
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
    SPEED('', DisplayColor.WHITE),
    MAGIC_FIND('', DisplayColor.AQUA),
    PET_LUCK('', DisplayColor.LIGHT_PURPLE),
    SHOT_COOLDOWN(null, DisplayColor.RED),
    GEAR_SCORE(null, DisplayColor.LIGHT_PURPLE),
    HEAT_RESISTANCE('', DisplayColor.RED),
    COLD_RESISTANCE('', DisplayColor.AQUA),
    RESPIRATION('', DisplayColor.DARK_AQUA),
    PRESSURE_RESISTANCE('', DisplayColor.BLUE),
    FEAR('', DisplayColor.DARK_PURPLE),
    TRACKING('', DisplayColor.LIGHT_PURPLE),
    ;

    constructor(stat: StatType) : this(stat.icon, stat.color)
    constructor(
        icon: Char,
        color: Int,
        vararg names: String,
    ) : this(icon.toString(), color, names = names)

    val id = if (icon == null) 0xe800 + idCounter.getAndIncrement() else null
    val idComponent = Text.of {
        append(id?.toString(16) ?: "null")
        font = ChatUtils.mc5
    }
    val isUnknown = icon == null
    private val defaultIcon = id?.let(::Char)

    val icon = icon ?: defaultIcon.toString()

    private val displayName: String = names.firstOrNull() ?: toFormattedName()
    val displayIcon = Text.of {
        append(icon ?: defaultIcon.toString(), this@StatType.color)
        this.font = ChatUtils.stats
    }

    override fun toString(): String = displayName

    companion object {
        fun fromName(name: String): StatType? {
            return entries.find { it.displayName.equals(name, ignoreCase = true) || it.names.any { it.equals(name, ignoreCase = true) } }
        }
    }
}
