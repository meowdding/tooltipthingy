package me.owdding.tooltipthingy.render

import me.owdding.tooltipthingy.ExtractableTooltipLine
import me.owdding.tooltipthingy.Tooltip
import me.owdding.tooltipthingy.TooltipThingy.id
import me.owdding.tooltipthingy.config.Config
import me.owdding.tooltipthingy.font
import me.owdding.tooltipthingy.system.TooltipTag
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.item.TrackingItemStackRenderState
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Matrix3x2f
import org.joml.Quaternionf
import org.joml.Vector3f
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.scissor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width
import kotlin.math.max

data class TooltipHeader(
    val item: ItemStack,
    val name: Component,
    val leftTags: List<TooltipTag>,
    val rightTags: List<TooltipTag>,
    val icon: Identifier?,
    val rarity: SkyBlockRarity
) : ExtractableTooltipLine {

    constructor(tooltip: Tooltip) : this(tooltip.item, tooltip.name, tooltip.leftTags, tooltip.rightTags, tooltip.topRightIcon, tooltip.rarity)

    val leftTagWidth = leftTags.sumOf { it.width }
    val rightTagWidth = rightTags.sumOf { it.width }
    val tagWidthTotal = when {
        leftTagWidth != 0 && rightTagWidth != 0 -> leftTagWidth + 5 + rightTagWidth
        else -> leftTagWidth + rightTagWidth
    }

    private val entity = ArmorStand(McClient.self.level!!, 0.0, 0.0, 0.0)

    override fun extract(graphics: GuiGraphicsExtractor, totalWidth: Int, x: Int, y: Int) {
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            id("tag"),
            x - 1,
            y - 1,
            24,
            24,
            ARGB.opaque(rarity.color)
        )
        graphics.extractItem(item, x + 3, y + 3)
        graphics.text(font, name, x + 25, y, -1)

        var tags = 24
        for (tag in leftTags) {
            tag.extract(graphics, x + tags, y + 10)
            tags += tag.width
        }

        tags = totalWidth - rightTagWidth
        for (tag in rightTags) {
            tag.extract(graphics, x + tags, y + 10)
            tags += tag.width
        }
    }

    override fun getWidth(font: Font): Int = 25 + max(
        tagWidthTotal - 2,
        name.width + if (icon != null) 13 else 0
    )

    override fun getHeight(font: Font): Int = 26

    // Taken from SkyOcean
    private fun GuiGraphicsExtractor.extractItem(item: ItemStack, x: Int, y: Int) {
        if (!Config.spinny) {
            this.item(item, x, y)
            return
        }

        val width = 16
        val height = 16

        val rotation = 45f + (System.currentTimeMillis() / 20) % 360

        val slot = item[DataComponents.EQUIPPABLE]?.slot?.takeIf { it.type == EquipmentSlot.Type.HUMANOID_ARMOR }

        this.scissor(x..x + width, y..y + height) {
            if (slot != null) {
                entity.setItemSlot(slot, item)
                entity.isInvisible = true

                val yOffset = when (slot) {
                    EquipmentSlot.HEAD -> 1.5f
                    EquipmentSlot.CHEST -> 1.0f
                    EquipmentSlot.LEGS -> 0.4f
                    EquipmentSlot.FEET -> 0.0f
                    else -> 1.0f
                } + 0.25f

                val angle = Quaternionf().rotateYXZ(rotation * 0.017453292f, 180 * 0.017453292f, 0f)
                renderEntityInInventory(
                    this,
                    x,
                    y,
                    x + width,
                    y + height,
                    15f,
                    Vector3f(0f, yOffset, 0f),
                    angle,
                    null,
                    entity,
                )
            } else {
                val itemState = TrackingItemStackRenderState()
                McClient.self.itemModelResolver.updateForTopItem(itemState, item, ItemDisplayContext.GUI, McLevel.self, McPlayer.self, 0)

                this.guiRenderState.addPicturesInPictureState(
                    ItemWidgetItemState(
                        x, y, x + width, y + height,
                        this.scissorStack.peek(),
                        Matrix3x2f(this.pose()),
                        rotation,
                        itemState,
                    ),
                )
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun renderEntityInInventory(
        graphics: GuiGraphicsExtractor,
        x0: Int,
        y0: Int,
        width: Int,
        height: Int,
        scale: Float,
        translation: Vector3f,
        rotation: Quaternionf,
        overrideCameraAngle: Quaternionf?,
        entity: LivingEntity,
    ) {
        val renderState = InventoryScreen.extractRenderState(entity)
        graphics.entity(renderState, scale, translation, rotation, overrideCameraAngle, x0, y0, width, height)
    }
}
