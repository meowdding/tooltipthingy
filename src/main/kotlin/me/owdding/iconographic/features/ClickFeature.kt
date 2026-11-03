package me.owdding.iconographic.features

import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.config.categories.misc.MiscConfig
import me.owdding.iconographic.lines.SpacerLine
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.utils.chat.ChatUtils
import me.owdding.lib.utils.MemoizeUtil
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.regex.component.match
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font

@RegisterFeature
data object ClickFeature : TooltipFeature() {
    override val enabled: Boolean get() = MiscConfig.clickIcons
    override val priority: Int = 20

    private val sneak = Text.of("s") { this.font = ChatUtils.clicks }
    private val shift = Text.of("S") { this.font = ChatUtils.clicks }
    private val left = Text.of("<") { this.font = ChatUtils.clicks }
    private val right = Text.of(">") { this.font = ChatUtils.clicks }
    private val middle = Text.of("v") { this.font = ChatUtils.clicks }
    private val mouseAll = Text.of("^") { this.font = ChatUtils.clicks }
    private val clickRegex = Regex("(?<key>.*?-click|Click)(?<text> (?:to|for) .*)").toComponentRegex()

    val lookup = MemoizeUtil.memoize { key: String ->
        val keys = key.split(" ").mapNotNull { key ->
            when (key.lowercase()) {
                "shift" -> shift
                "sneak" -> sneak
                "right-click" -> right
                "left-click" -> left
                "middle-click" -> middle
                "click" -> mouseAll
                else -> null
            }
        }
        if (keys.isEmpty()) return@memoize null
        Text.join(keys, separator = CommonComponents.SPACE)
    }

    private fun Component.isClickLine() = clickRegex.matches(this)
    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {

        var modified = false
        while (canRead()) {
            addUntil { it.isClickLine() }
            if (!canRead()) break

            var line = read()

            var match = false
            clickRegex.match(line, "key", "text") { [key, text] ->
                val icons = lookup(key.stripped) ?: return@match
                line = Text.join(icons, text)
                match = true
            }

            modified = match || modified
            if (match) {
                this.originalMerger.add(SpacerLine(height = 3))
            }
            add(line)
        }


        modified.asResult()
    }

    fun getIcon(hasSneak: Boolean, hasLeft: Boolean, hasRight: Boolean) = Text.of().apply {
        if (hasSneak) append(sneak)
        if (hasLeft) append(left)
        if (hasRight) append(right)
    }
}
