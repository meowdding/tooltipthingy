package me.owdding.iconographic.features.stats

import me.owdding.iconographic.utils.chat.ChatUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font

enum class StatType(
    icon: String?,
    val color: Int,
    vararg val names: String,
) {
    // Combat stats
    HEALTH('', TextColor.RED),
    DAMAGE('', TextColor.RED),
    DEFENSE('', TextColor.GREEN),
    STRENGTH('', TextColor.RED),
    INTELLIGENCE('', TextColor.AQUA),
    CRIT_DAMAGE('', TextColor.BLUE),
    CRIT_CHANCE('', TextColor.BLUE),
    ATTACK_SPEED('', TextColor.YELLOW, "Bonus Attack Speed"),
    ABILITY_DAMAGE('', TextColor.RED),
    TRUE_DEFENSE('', TextColor.WHITE),
    FEROCITY('', TextColor.RED),
    HEALTH_REGEN('', TextColor.RED),
    VITALITY('', TextColor.DARK_RED),
    MENDING('', TextColor.GREEN),
    SWING_RANGE('', TextColor.YELLOW),

    // Mining Stats
    BREAKING_POWER('', TextColor.DARK_GREEN),
    MINING_SPEED('', TextColor.GOLD),
    MINING_SPREAD('', TextColor.YELLOW),
    GEMSTONE_SPREAD('', TextColor.YELLOW),
    PRISTINE('', TextColor.DARK_PURPLE),
    BASE_MINING_FORTUNE('', TextColor.GOLD), // helper
    MINING_FORTUNE(BASE_MINING_FORTUNE),
    ORE_FORTUNE(BASE_MINING_FORTUNE),
    BLOCK_FORTUNE(BASE_MINING_FORTUNE),
    DWARVEN_METAL_FORTUNE(BASE_MINING_FORTUNE),
    GEMSTONE_FORTUNE(BASE_MINING_FORTUNE),

    // Farming Stats
    BONUS_PEST_CHANCE('', TextColor.DARK_GREEN),
    OVERBLOOM('', TextColor.YELLOW),
    BASE_FARMING_FORTUNE('', TextColor.GOLD), // helper
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
    SWEEP('', TextColor.DARK_GREEN),
    BASE_FORAGING_FORTUNE('', TextColor.GOLD), // Helper
    FORAGING_FORTUNE(BASE_FORAGING_FORTUNE),
    FIG_FORTUNE(BASE_FORAGING_FORTUNE),
    MANGROVE_FORTUNE(BASE_FORAGING_FORTUNE),

    // Fishing Stats
    FISHING_SPEED('', TextColor.AQUA),
    SEA_CREATURE_CHANCE('', TextColor.DARK_AQUA),
    DOUBLE_HOOK_CHANCE('', TextColor.BLUE),
    TROPHY_CHANCE('', TextColor.GOLD),
    TREASURE_CHANCE('', TextColor.GOLD),

    // Hunting Stats
    PULL('', TextColor.AQUA),
    HUNTER_FORTUNE('', TextColor.LIGHT_PURPLE),

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
    SPEED('', TextColor.WHITE),
    MAGIC_FIND('', TextColor.AQUA),
    PET_LUCK('', TextColor.LIGHT_PURPLE),
    SHOT_COOLDOWN(null, TextColor.RED),
    GEAR_SCORE(null, TextColor.PINK),
    HEAT_RESISTANCE('', TextColor.RED),
    COLD_RESISTANCE('', TextColor.AQUA),
    RESPIRATION('', TextColor.DARK_AQUA),
    PRESSURE_RESISTANCE('', TextColor.BLUE),
    FEAR('', TextColor.DARK_PURPLE),
    TRACKING('', TextColor.LIGHT_PURPLE),
    ;

    constructor(stat: StatType) : this(stat.icon, stat.color)
    constructor(
        icon: Char,
        color: Int,
        vararg names: String,
    ) : this(icon.toString(), color, names = names)

    val id = 0xe800 + ordinal
    val idComponent = Text.of {
        append(id.toString(16))
        font = ChatUtils.mc5
    }
    val isUnknown = icon == null
    private val defaultIcon = Char(id)

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
