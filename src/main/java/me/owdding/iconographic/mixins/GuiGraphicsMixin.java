package me.owdding.iconographic.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import me.owdding.iconographic.Iconographic;
import me.owdding.iconographic.Keybinds;
import me.owdding.iconographic.config.Config;
import me.owdding.iconographic.config.NonSkyBlockItemMode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI;

import java.util.ArrayList;
import java.util.List;

import static tech.thatgravyboat.skyblockapi.utils.extentions.ItemStackExtensionsKt.getSkyBlockId;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsMixin {

    @ModifyVariable(
            method = "setTooltipForNextFrameInternal",
            at = @At("HEAD"),
            argsOnly = true
    )
    public List<ClientTooltipComponent> modifyTooltipLines(
            List<ClientTooltipComponent> lines,
            Font font
    ) {
        try {
            var item = Iconographic.extractingItemTooltip;

            final boolean isEnabled;
            if (Keybinds.isTogglePressed) {
                isEnabled = !Config.isEnabled();
            } else {
                isEnabled = Config.isEnabled();
            }

            Iconographic.currentTooltipRarityColor = null;

            if (isEnabled && item != null && (!Config.skyblockOnly() || LocationAPI.INSTANCE.isOnSkyBlock())) {
                if (lines.isEmpty()) {
                    return lines;
                }

                boolean hasSkyBlockId = getSkyBlockId(item) != null;
                if (hasSkyBlockId || Config.nonSkyBlockItemMode() != NonSkyBlockItemMode.NOTHING) {
                    List<ClientTooltipComponent> mutableLines = new ArrayList<>(lines);

                    Iconographic.processTooltipComponents(item, font, mutableLines);

                    return mutableLines;
                }
            }
        } catch (RuntimeException e) {
            Iconographic.INSTANCE.error("Failed to build tooltip!", e);
        }
        return lines;
    }

    @WrapOperation(method = "tooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;getWidth(Lnet/minecraft/client/gui/Font;)I"))
    private int wrap(ClientTooltipComponent instance, Font font, Operation<Integer> original, @Share("with_extra_width") LocalIntRef ref) {
        if (instance instanceof Iconographic.TooltipWidthLine line) {
            ref.set(line.getWidth(font));
            return line.getMainWidth();
        }
        return original.call(instance, font);
    }

    @WrapOperation(method = "tooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"))
    private Vector2ic position(ClientTooltipPositioner instance, int screenWidth, int screenHeight, int x, int y, int tooltipWidth, int tooltipHeight, Operation<Vector2ic> original, @Share("with_extra_width") LocalIntRef ref) {
        return original.call(instance, screenWidth, screenHeight, x, y, Math.max(tooltipWidth, ref.get()), tooltipHeight);
    }

    @WrapMethod(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V")
    public void setTooltip(Font font, ItemStack itemStack, int xo, int yo, Operation<Void> original) {
        var item = Iconographic.extractingItemTooltip;
        var style = Iconographic.currentTooltipStyle;
        Iconographic.extractingItemTooltip = itemStack;
        original.call(font, itemStack, xo, yo);
        Iconographic.extractingItemTooltip = item;
        Iconographic.currentTooltipStyle = style;
    }
}
