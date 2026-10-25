package me.owdding.iconographic.features

import me.owdding.iconographic.TooltipLine
import me.owdding.iconographic.TooltipLine.Companion.asComponentOrNull
import me.owdding.iconographic.features.stats.StatType
import me.owdding.iconographic.features.tags.ShaderCache
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.Result
import me.owdding.iconographic.system.TextTag
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.system.TooltipFeatureWithContext
import me.owdding.iconographic.system.TooltipTag
import me.owdding.iconographic.utils.chat.ChatUtils
import me.owdding.iconographic.utils.extensions.contains
import me.owdding.lib.rendering.text.textShader
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.get
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font

@RegisterFeature
data object EnrichedTalismanFeature : TooltipFeatureWithContext<StatType?>() {
    override val enabled: Boolean = true
    override val priority: Int = 5

    val colorLookup = mapOf(
        "talisman_enrichment_walk_speed" to StatType.SPEED,
        "talisman_enrichment_intelligence" to StatType.INTELLIGENCE,
        "talisman_enrichment_critical_damage" to StatType.CRIT_DAMAGE,
        "talisman_enrichment_critical_chance" to StatType.CRIT_CHANCE,
        "talisman_enrichment_strength" to StatType.STRENGTH,
        "talisman_enrichment_defense" to StatType.DEFENSE,
        "talisman_enrichment_health" to StatType.HEALTH,
        "talisman_enrichment_magic_find" to StatType.MAGIC_FIND,
        "talisman_enrichment_ferocity" to StatType.FEROCITY,
        "talisman_enrichment_sea_creature_chance" to StatType.SEA_CREATURE_CHANCE,
        "talisman_enrichment_attack_speed" to StatType.ATTACK_SPEED,
        ).mapKeys { [key, _] -> SkyBlockId.item(key) }

    context(context: StatType?)
    override fun ItemStack.applies(): Boolean = context != null
    override fun ItemStack.createContext(): StatType? = colorLookup[this[DataTypes.ENRICHMENT]]



    context(context: StatType?)
    override fun ItemStack.modifyEntries(list: MutableList<TooltipLine>, previousResult: Result?): Result = withComponentMerger(list) {
        addUntil { it.stripped.startsWith("Enriched with") || it.stripped.isEmpty() }
        if (peek().stripped.startsWith("Enriched with")) {
            read()
        }
        skipSpace()

        Result.modified
    }

    context(context: StatType?)
    override fun ItemStack.leftTags(): List<TooltipTag> = buildList {
        context ?: return@buildList

        add(TooltipTag.literal(Text.of {
            append("> ") {
                font = ChatUtils.sparkles
                color = context.color
            }

            append("Accessory")

            append(" <") {
                font = ChatUtils.sparkles
                color = context.color
            }
        }, 0xAAAAAA))
    }

    context(context: StatType?)
    override fun ItemStack.rightTags(): List<TooltipTag> = buildList {
        context ?: return@buildList

        add(TooltipTag.icon(context.displayIcon))
    }

}
