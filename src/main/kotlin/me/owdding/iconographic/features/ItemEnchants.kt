package me.owdding.iconographic.features

import me.owdding.iconographic.ExtractableTooltipLine
import me.owdding.iconographic.Iconographic.id
import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.font
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.utils.chat.DisplayColor
import me.owdding.ktmodules.Module
import me.owdding.lib.rendering.text.TextShader
import me.owdding.lib.rendering.text.withTextShader
import me.owdding.lib.repo.EnchantmentRepoData
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.Style
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.extentions.parseRomanOrArabic
import tech.thatgravyboat.skyblockapi.utils.extentions.toRomanNumeral
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.isEmpty
import java.util.*

@Module
@RegisterFeature
data object ItemEnchants : TooltipFeature() {
    override val enabled: Boolean get() = MiscConfig.itemEnchants
    override val priority: Int = 15

    private val singleEnchantMatcher = ComponentRegex("(?<=^|, )(?<name>[A-Z][A-Za-z-]* (?:[A-Za-z-]* )*)(?<lvl>[MDCLXVI]+)(?<stacking> [\\d.,]+)?,?\\b")
    private val enchantLineRegex = ComponentRegex("(?:[A-Z][A-Za-z-]* (?:[A-Za-z-]* )*[MDCLXVI]+(?:, )?)+(?: [\\d.,+]+)?")

    private fun Component.isEnchantmentLine() = enchantLineRegex.matches(this)

    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        if (getData(DataTypes.ENCHANTMENTS) == null || !hasNext { it.isEnchantmentLine() }) return@withComponentMerger Result.unmodified
        addUntil {
            it.isEnchantmentLine()
        }

        var modified = false
        val enchants = mutableListOf<EnchantLine>()
        val skyblockId = getSkyBlockId()
        while (canRead() && !peek().isEmpty()) {
            val line = read()
            if (line.isEnchantmentLine()) {
                singleEnchantMatcher.replace(line) { enchantMatch ->
                    val name = enchantMatch["name"]!!
                    val lvl = enchantMatch["lvl"]!!
                    val stackingProgress = enchantMatch["stacking"]?.string?.trim()
                    val enchantName = name.string.trim()
                    val enchantLevel = lvl.string.parseRomanOrArabic()
                    val data = EnchantmentRepoData.data.find { it.name == enchantName }
                    val unlocked = name.color != TextColor.GRAY
                    val ultimate = data?.isUltimate ?: false
                    val maxLevel = data?.perItemOverrides[skyblockId] ?: data?.maxLevel
                    val isMaxed = maxLevel == enchantLevel

                    enchants += EnchantLine(
                        enchantName,
                        enchantLevel,
                        unlocked,
                        ultimate,
                        isMaxed,
                        // TODO: add config option to show stacking progress even after maxed
                        stackingProgress,
                    )

                    modified = true
                    Component.empty()
                }
            } else {
                enchants.lastOrNull()?.description?.add(line)
            }
        }
        readSafe()

        if (enchants.isNotEmpty()) originalMerger.add(EnchantLines(enchants))
        return@withComponentMerger modified.asResult()
    }

    class EnchantLine(
        val enchantment: String,
        val level: Int,
        val unlocked: Boolean,
        val ultimate: Boolean,
        val isMaxed: Boolean,
        val stackingProgress: String? = null,
        var description: MutableList<Component> = mutableListOf()
    ) {
        fun toComponent() = fromLevelAndName(
            enchantmentName = enchantment,
            nameColor = if (ultimate) DisplayColor.LIGHT_PURPLE else DisplayColor.BLUE,
            level = level,
            levelColor = when {
                !unlocked -> DisplayColor.GRAY
                isMaxed -> DisplayColor.GOLD
                else -> DisplayColor.BLUE
            },
            roman = MiscConfig.romanEnchant,
            stackingProgress = stackingProgress,
        )
    }

    fun List<EnchantLine>.compose(): List<List<Component>> {
        if (this.any { it.description.isNotEmpty() }) {
            val result = mutableListOf<List<Component>>()
            for (enchant in this) {
                result += listOf(enchant.toComponent())
                if (enchant.description.isNotEmpty()) {
                    result.addAll(enchant.description.chunked(1))
                }
            }
            return result
        } else {
            return this.map { it.toComponent() }.chunked(3)
        }
    }

    data class EnchantLines(val enchants: List<EnchantLine>) : ExtractableTooltipLine {
        val lines = enchants.compose()
        override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {
            var currentY = y + BORDER - 4
            lines.forEach { line ->
                val lineWidth = line.sumOf { font.width(it) } + (line.size - 1) * HORIZONTAL_PADDING + 2 * BORDER
                var currentX = if (MiscConfig.centerEnchantments) x + (totalWidth / 2) - (lineWidth / 2) else x + BORDER
                for (enchant in line) {
                    graphics.text(font, enchant, currentX, currentY, -1)
                    currentX += font.width(enchant) + HORIZONTAL_PADDING
                }
                currentY += height + VERTICAL_PADDING
            }
        }

        override fun getWidth(font: Font): Int = lines.maxOf { line ->
            line.sumOf { font.width(it) } + (line.size - 1) * HORIZONTAL_PADDING + 2 * BORDER
        }
        override fun getHeight(font: Font): Int = lines.size * (height + VERTICAL_PADDING) - VERTICAL_PADDING + 2 * BORDER
    }


    const val HORIZONTAL_PADDING = 3
    const val VERTICAL_PADDING = 2
    const val BORDER = 2
    val height get() = if (MiscConfig.smallEnchantText) 9 else 11

    const val OPEN_TAG = "\uE900\uE910"
    val TAG_MAP = TreeMap(
        mapOf(
            24 to "\uE907\uE910",
            6 to "\uE904\uE910",
            3 to "\uE903\uE910",
            2 to "\uE902\uE910",
            1 to "\uE901\uE910",
        ),
    )
    const val CLOSE_TAG = "\uE905\uE910"
    const val MID_TAG = "\uE906\uE911"
    val NEGATIVE_SPACE_MAP = TreeMap(
        mapOf(
            24 to "\uE915",
            12 to "\uE914",
            6 to "\uE913",
            3 to "\uE912",
            2 to "\uE911",
            1 to "\uE910",
        ),
    )
    val SPACE_MAP = TreeMap(
        mapOf(
            2 to "\uE917",
            1 to "\uE916",
        ),
    )

    private fun space(pixelCount: Int): String {
        return buildString {
            if (pixelCount < 0) {
                var remaining = -pixelCount
                while (remaining > 0) {
                    val (key, value) = NEGATIVE_SPACE_MAP.floorEntry(remaining)
                    remaining -= key
                    append(value)
                }
            } else {
                var remaining = pixelCount
                while (remaining > 0) {
                    val (key, value) = SPACE_MAP.floorEntry(remaining)
                    remaining -= key
                    append(value)
                }
            }
        }
    }

    private fun background(pixelCount: Int): String {
        return buildString {
            var remaining = pixelCount
            while (remaining > 0) {
                val (key, value) = TAG_MAP.floorEntry(remaining)
                remaining -= key
                append(value)
            }
        }
    }

    val smallFont = FontDescription.Resource(id("enchantment_tags_mc5"))
    val normalFont = FontDescription.Resource(id("enchantment_tags"))
    fun font() = if (MiscConfig.smallEnchantText) smallFont else normalFont

    fun fromLevelAndName(
        enchantmentName: String,
        nameColor: Int,
        level: Int,
        levelColor: Int,
        roman: Boolean = false,
        stackingProgress: String? = null,
        shader: TextShader? = null,
    ): Component {
        val levelTag = tagFromLevel(level, levelColor, roman, shader)
        val stackingProgress = tagFromStackingProgress(stackingProgress, shader)

        if (!MiscConfig.enchantNameBackground) {
            return Component.literal(enchantmentName)
                .withStyle(
                    Style.EMPTY
                        .withFont(font())
                        .withColor(nameColor)
                        .withBold(false)
                        .withTextShader(null),
                )
                .append(space(2))
                .append(levelTag)
                .apply {
                    if (stackingProgress != null) {
                        append(space(2)).
                            append(stackingProgress)
                    }
                }
        }

        val nameComponent = Component.literal(enchantmentName)
            .withStyle(
                Style.EMPTY
                    .withShadowColor(ARGB.scaleRGB(0xbfffffff.toInt(), 0.25f))
                    .withFont(font())
                    .withColor(DisplayColor.WHITE)
                    .withBold(false)
                    .withTextShader(null)
            )
        val innerTextWidth = McFont.width(nameComponent) + 2
        val textBackground = buildString {
            append(OPEN_TAG)
            append(background(innerTextWidth))
            append(space(-innerTextWidth))
        }

        return Component.literal(textBackground)
            .withStyle(
                Style.EMPTY
                    .withFont(font())
                    .withColor(nameColor)
                    .withTextShader(shader)
            )
            .append(nameComponent)
            .append(space(2))
            .append(MID_TAG)
            .append(levelTag)
            .apply {
                if (stackingProgress != null) {
                    append(space(-1))
                    append(
                        Component.literal(background(2))
                            .append(MID_TAG)
                            .withColor(levelColor)
                    )
                    append(stackingProgress)
                }
            }
    }

    fun tagFromStackingProgress(progress: String?, shader: TextShader? = null): Component? {
        if (progress == null || !MiscConfig.stackingProgress) return null
        val shouldBackground = MiscConfig.enchantNameBackground
        val textColor = if (shouldBackground) DisplayColor.WHITE else DisplayColor.DARK_GRAY

        val innerText = Component.literal(progress)
            .withStyle(Style.EMPTY
                .withShadowColor(ARGB.scaleRGB(0xbfffffff.toInt(), 0.25f))
                .withFont(font())
                .withColor(textColor)
                .withTextShader(shader)
            )

        if (!shouldBackground) {
            return innerText
        }
        val innerTextWidth = McFont.width(innerText)
        val textBackground = buildString {
            append(OPEN_TAG)
            append(background(innerTextWidth))
            append(space(-innerTextWidth))
        }
        return Component.literal(textBackground)
            .withStyle(
                Style.EMPTY
                    .withFont(font())
                    .withColor(DisplayColor.DARK_GRAY)
                    .withTextShader(shader)
            )
            .append(innerText)
            .append(CLOSE_TAG)
    }

    fun tagFromLevel(level: Int, color: Int, roman: Boolean = false, shader: TextShader? = null): Component {
        val levelString = if (roman) {
            if (level >= 4000) "TOO_HIGH_NUMBER"
            else if (level == 0) "O"
            else level.toRomanNumeral(true)
        } else level.toString()
        val innerText = Component.literal(levelString)
            .withStyle(
                Style.EMPTY
                    .withShadowColor(ARGB.scaleRGB(0xbfffffff.toInt(), 0.25f))
                    .withFont(font())
                    .withColor(DisplayColor.WHITE)
                    .withTextShader(null)
            )

        val innerTextWidth = McFont.width(innerText)
        val textBackground = buildString {
            append(OPEN_TAG)
            append(background(innerTextWidth))
            append(space(-innerTextWidth))
        }

        return Component.literal(textBackground)
            .withStyle(
                Style.EMPTY
                    .withFont(font())
                    .withColor(color)
                    .withTextShader(shader)
            )
            .append(innerText)
            .append(CLOSE_TAG)
    }
}
