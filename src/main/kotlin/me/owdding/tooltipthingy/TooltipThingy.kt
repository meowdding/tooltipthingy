package me.owdding.tooltipthingy

import com.google.gson.JsonParser
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import me.owdding.ktmodules.AutoCollect
import me.owdding.ktmodules.Module
import me.owdding.lib.utils.MeowddingLogger
import me.owdding.tooltipthingy.TooltipInformation.Companion.toInformation
import me.owdding.tooltipthingy.config.Config
import me.owdding.tooltipthingy.generated.BuildInfo
import me.owdding.tooltipthingy.generated.TooltipThingyApiDebug
import me.owdding.tooltipthingy.generated.TooltipThingyModules
import me.owdding.tooltipthingy.generated.TooltipThingyTooltipFeatures
import me.owdding.tooltipthingy.render.TooltipHeader
import me.owdding.tooltipthingy.system.CustomTooltip
import me.owdding.tooltipthingy.utils.debug.DebugBuilder
import me.owdding.tooltipthingy.utils.debug.RegisterTttCommandEvent
import me.owdding.tooltipthingy.utils.debug.RegisterTttDebugEvent
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import org.joml.component1
import org.joml.component2
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import kotlin.math.max

@Module
object TooltipThingy : ClientModInitializer, MeowddingLogger by MeowddingLogger.autoResolve() {
    @Volatile
    @JvmField
    var extractingItemTooltip: ItemStack? = null

    override fun onInitializeClient() {
        info("Loaded tooltip thingy!")

        TooltipThingyModules.init { SkyBlockAPI.eventBus.register(it) }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, context ->
            RegisterTttCommandEvent(dispatcher, context).apply {
                post(SkyBlockAPI.eventBus)
                RegisterTttDebugEvent(this).post(SkyBlockAPI.eventBus)
            }
        }
    }

    @Subscription
    context(event: RegisterTttCommandEvent)
    fun onCommands() {
        event.registerBaseCallback {
            McClient.setScreenAsync { ResourcefulConfigScreen.getFactory("tooltipthingy").apply(null) }
        }

        event.registerDevWithCallback("give") {
            if (McPlayer.self?.gameMode()?.isCreative != true && McClient.self.isSingleplayer) return@registerDevWithCallback
            val item = JsonParser.parseString(McClient.clipboard).toDataOrThrow(ItemStack.CODEC)
            McClient.self.player?.inventory?.add(item)
        }
    }

    val configurator = Configurator("tooltipthingy")
    val config = Config.register(configurator)

    @JvmStatic
    fun GuiGraphicsExtractor.createTooltip(
        item: ItemStack,
        font: Font,
        lines: List<ClientTooltipComponent>,
        xo: Int,
        yo: Int,
        positioner: ClientTooltipPositioner,
        style: Identifier?,
    ): Runnable = {
        val tooltipInfo = lines.toInformation()

        val tooltip = CustomTooltip.update(item, tooltipInfo)
        val (
            rarity,
            isRarityUpgraded,
        ) = tooltip

        val entries = tooltip.entries.toMutableList()
        entries.addFirst(TooltipHeader(tooltip))
        var totalWidth = 0
        var totalHeight = 0

        for (line in entries) {
            totalWidth = max(line.getWidth(font), totalWidth)
            totalHeight += line.getHeight(font)
        }

        val [x, y] = positioner.positionTooltip(
            this.guiWidth(),
            this.guiHeight(),
            xo,
            yo,
            totalWidth,
            totalHeight
        )

        blitSprite(
            RenderPipelines.GUI_TEXTURED,
            id("background"),
            x - 5,
            y - 5,
            totalWidth + 10,
            totalHeight + 10,
            ARGB.opaque(rarity.color)
        )

        var yOffset = 0
        for (line in entries) {
            when (line) {
                is ExtractableTooltipLine -> {
                    line.extract(this, totalWidth, x, y + yOffset)
                }

                is ClientTooltipComponent -> {
                    line.extractText(this, font, x, y)
                    line.extractImage(font, x, y, totalWidth, line.getHeight(font), this)
                }
            }
            yOffset += line.getHeight(font)
        }
    }

    fun id(path: String) = Identifiers.of("tooltipthingy", path)


    @Subscription
    context(event: RegisterTttDebugEvent)
    internal fun registerDebugs() {
        TooltipThingyApiDebug.collected.forEach {
            val debug = it.annotations.filterIsInstance<ApiDebug>().first()
            val name = debug.name
            val commandName = debug.commandName.takeUnless(String::isEmpty) ?: name.lowercase().replace(" ", "_")

            event.tttRegister(name, commandName) {
                it.invoke(this)
            }
        }
    }

    @ApiDebug("General Info", commandName = "general")
    internal fun debug(builder: DebugBuilder) = with(builder) {
        field("Version", BuildInfo.VERSION)
        field("Modules", TooltipThingyModules.collected.size)
        field("Features", TooltipThingyTooltipFeatures.collected.size)
        field("Git ref", BuildInfo.GIT_REF)
        field("Git branch", BuildInfo.GIT_BRANCH)
    }
}

@AutoCollect
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
internal annotation class ApiDebug(
    val name: String,
    val commandName: String = "",
)
