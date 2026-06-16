package me.owdding.iconographic.features.tags

import com.mojang.brigadier.arguments.FloatArgumentType
import me.owdding.iconographic.config.categories.tag.TagConfig
import me.owdding.iconographic.system.RegisterFeature
import me.owdding.iconographic.system.TooltipFeature
import me.owdding.iconographic.system.TooltipTag
import me.owdding.iconographic.utils.chat.ChatUtils
import me.owdding.iconographic.utils.chat.sendWithPrefix
import me.owdding.iconographic.utils.debug.RegisterIconCommandEvent
import me.owdding.ktmodules.Module
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import me.owdding.lib.rendering.text.textShader
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.utils.command.EnumArgument
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.font

@RegisterFeature
data object RarityTag : TooltipFeature() {
    override val enabled: Boolean get() = TagConfig.rarity
    override val priority: Int = 10

    override fun ItemStack.leftTags(): List<TooltipTag> {
        val rarity = DataTypes.RARITY() ?: return emptyList()

        val comp = if (DataTypes.RECOMBOBULATOR() == true) {
            val left = Text.of("> ") {
                font = ChatUtils.sparkles
                textShader = ShaderCache.getOrRegisterUpgradeShader(rarity)
            }

            val rarityText = Text.of(rarity.displayName)

            val right = Text.of(" <") {
                font = ChatUtils.sparkles
                textShader = ShaderCache.getOrRegisterUpgradeShader(rarity)
            }
            Text.join(left, rarityText, right)
        } else {
            Text.of(rarity.displayName, rarity.color)
        }

        return listOf(TooltipTag.literal(comp, rarity.color))
    }
}

@Module
object ShaderCache {
    private val cache: MutableMap<SkyBlockRarity, GradientTextShader> = mutableMapOf()

    private var shaderSpeed = 3f
        set(value) {
            field = value
            cache.clear()
        }
    private var shaderDir = GradientTextShader.Direction.UP
        set(value) {
            field = value
            cache.clear()
        }

    fun getOrRegisterUpgradeShader(rarity: SkyBlockRarity): GradientTextShader {
        return cache.getOrPut(rarity) {
            val previous = rarity.getPreviousRarity()
            val startColor = previous.color
            val endColor = rarity.color
            GradientTextShader(
                colors = listOf(
                    endColor,
                    startColor,
                    endColor,
                    endColor,
                    endColor,
                    endColor,
                ),
                direction = shaderDir,
                speed = shaderSpeed,
            )
        }
    }

    private fun SkyBlockRarity.getPreviousRarity(): SkyBlockRarity {
        if (ordinal <= 0) return this
        return SkyBlockRarity.entries[ordinal - 1]
    }

    // TODO: Remove after figuring out the values
    @Subscription
    fun onRegisterCommand(event: RegisterIconCommandEvent) {
        event.registerDev("shader") {
            then("speed", FloatArgumentType.floatArg(0f)) {
                callback {
                    val newSpeed = argument<Float>("speed")
                    shaderSpeed = newSpeed
                    Text.of("Set shader speed to $newSpeed").sendWithPrefix()
                }
            }
            then("direction", EnumArgument.create(GradientTextShader.Direction::class.java)) {
                callback {
                    val newDir = argument<GradientTextShader.Direction>("direction")
                    shaderDir = newDir
                    Text.of("Set shader direction to $newDir").sendWithPrefix()
                }
            }
        }
    }
}
