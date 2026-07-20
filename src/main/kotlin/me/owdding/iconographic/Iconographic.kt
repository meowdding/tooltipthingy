package me.owdding.iconographic

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import me.owdding.iconographic.TooltipInformation.Companion.toInformation
import me.owdding.iconographic.api.ImcHandler
import me.owdding.iconographic.config.Config
import me.owdding.iconographic.generated.BuildInfo
import me.owdding.iconographic.generated.IconographicApiDebug
import me.owdding.iconographic.generated.IconographicModules
import me.owdding.iconographic.generated.IconographicTooltipFeatures
import me.owdding.iconographic.render.TooltipHeader
import me.owdding.iconographic.system.CustomTooltip
import me.owdding.iconographic.system.IconographicTooltipComponent
import me.owdding.iconographic.utils.chat.DisplayColor.displayColor
import me.owdding.iconographic.utils.chat.sendWithPrefix
import me.owdding.iconographic.utils.debug.DebugBuilder
import me.owdding.iconographic.utils.debug.RegisterIconCommandEvent
import me.owdding.iconographic.utils.debug.RegisterTttDebugEvent
import me.owdding.ktmodules.AutoCollect
import me.owdding.ktmodules.Module
import me.owdding.lib.utils.MeowddingLogger
import me.owdding.lib.utils.MeowddingUpdateChecker
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.url
import kotlin.math.max

@Module
object Iconographic : ClientModInitializer, MeowddingLogger by MeowddingLogger.autoResolve() {
    val SELF = FabricLoader.getInstance().getModContainer("iconographic").get()
    val MOD_ID: String = SELF.metadata.id

    @Volatile
    @JvmField
    var extractingItemTooltip: ItemStack? = null

    @Volatile
    @JvmField
    var currentTooltipRarityColor: Int? = null

    fun pushPop(item: ItemStack, runnable: () -> Unit) {
        val current = this.extractingItemTooltip
        this.extractingItemTooltip = item
        runnable()
        this.extractingItemTooltip = current
    }

    override fun onInitializeClient() {
        info("Loaded Iconographic!")

        ImcHandler.setup()

        IconographicModules.init { SkyBlockAPI.eventBus.register(it) }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, context ->
            RegisterIconCommandEvent(dispatcher, context).apply {
                post(SkyBlockAPI.eventBus)
                RegisterTttDebugEvent(this).post(SkyBlockAPI.eventBus)
            }
        }

        MeowddingUpdateChecker("jrPpQCNN", SELF) { link, current, new ->
            fun MutableComponent.withLink() = this.apply {
                this.url = link
                this.hover = Text.of(link).withColor(TextColor.GRAY)
            }

            McClient.runNextTick {
                Text.of().send()
                Text.join(
                    "New version found! (",
                    Text.of(current).withColor(TextColor.RED),
                    Text.of(" -> ").withColor(TextColor.GRAY),
                    Text.of(new).withColor(TextColor.GREEN),
                    ")",
                ).withLink().sendWithPrefix()
                Text.of("Click to download.").withLink().sendWithPrefix()
                Text.of().send()
            }
        }
    }

    @Subscription
    context(event: RegisterIconCommandEvent)
    fun onCommands() {
        event.registerBaseCallback {
            McClient.setScreenAsync { ResourcefulConfigScreen.getFactory("iconographic").apply(null) }
        }
    }

    val configurator = Configurator("iconographic")
    val config = Config.register(configurator)

    @JvmStatic
    fun processTooltipComponents(
        item: ItemStack,
        font: Font,
        lines: MutableList<ClientTooltipComponent>
    ) {
        val tooltipInfo = lines.toInformation()
        val tooltip = CustomTooltip.update(item, tooltipInfo)

        currentTooltipRarityColor = tooltip.rarity.displayColor

        val entries = tooltip.entries.toMutableList()
        entries.addFirst(TooltipHeader(tooltip))

        var totalWidth = 0

        for (line in entries) {
            totalWidth = max(line.getWidth(font), totalWidth)
        }

        lines.clear()
        for (line in entries) {
            when (line) {
                is ExtractableTooltipLine -> {
                    val component = IconographicTooltipComponent(line)
                    component.totalWidth = totalWidth
                    lines.add(component)
                }

                is ClientTooltipComponent -> {
                    lines.add(line)
                }
            }
        }
    }

    fun id(path: String) = Identifiers.of("iconographic", path)


    @Subscription
    context(event: RegisterTttDebugEvent)
    internal fun registerDebugs() {
        IconographicApiDebug.collected.forEach {
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
        field("Modules", IconographicModules.collected.size)
        field("Features", IconographicTooltipFeatures.collected.size)
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
