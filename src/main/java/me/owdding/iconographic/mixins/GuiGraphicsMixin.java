package me.owdding.iconographic.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.iconographic.Iconographic;
import me.owdding.iconographic.config.Config;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsMixin {

    @WrapOperation(
            method = "setTooltipForNextFrameInternal",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;deferredTooltip:Ljava/lang/Runnable;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    public void modifyTooltipAssignment(
            GuiGraphicsExtractor instance,
            Runnable value,
            Operation<Void> original,
            @Local(argsOnly = true) Font font,
            @Local(argsOnly = true) List<ClientTooltipComponent> lines,
            @Local(argsOnly = true, ordinal = 0) int xo,
            @Local(argsOnly = true, ordinal = 1) int yo,
            @Local(argsOnly = true) ClientTooltipPositioner positioner,
            @Local(argsOnly = true) @Nullable Identifier style
    ) {
        Runnable runnable = value;
        try {
            var item = Iconographic.extractingItemTooltip;
            if (Config.isEnabled() && item != null && (!Config.skyblockOnly() || LocationAPI.INSTANCE.isOnSkyBlock())) {
                runnable = Iconographic.createTooltip(instance, item, font, lines, xo, yo, positioner, style);
            }
        } catch (RuntimeException e) {
            Iconographic.INSTANCE.error("Failed to build tooltip!", e);
        } finally {
            original.call(instance, runnable);
        }
    }

}
