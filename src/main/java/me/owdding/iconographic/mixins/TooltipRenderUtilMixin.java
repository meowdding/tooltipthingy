package me.owdding.iconographic.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.owdding.iconographic.Iconographic;
import me.owdding.iconographic.config.categories.visuals.VisualsConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TooltipRenderUtil.class)
public class TooltipRenderUtilMixin {

    @Shadow
    @Final
    private static Identifier BACKGROUND_SPRITE;

    @Shadow
    @Final
    private static Identifier FRAME_SPRITE;

    @Inject(method = "extractTooltipBackground", at = @At("HEAD"), cancellable = true)
    private static void onRenderTooltipBackground(GuiGraphicsExtractor graphics, int x, int y, int w, int h, Identifier style, CallbackInfo ci) {
        var color = Iconographic.currentTooltipRarityColor;
        if (!VisualsConfig.INSTANCE.getVanillaBackground() && color != null) {
            ci.cancel();

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    Iconographic.INSTANCE.id("background"),
                    x - 6,
                    y - 6,
                    w + 12,
                    h + 12,
                    ARGB.opaque(color)
            );
        }
    }

    @ModifyReturnValue(method = "getBackgroundSprite", at = @At("RETURN"))
    private static Identifier modifyBackground(Identifier original) {
        return VisualsConfig.INSTANCE.getForceDefaultVanillaBackground() ? BACKGROUND_SPRITE : original;
    }

    @ModifyReturnValue(method = "getFrameSprite", at = @At("RETURN"))
    private static Identifier modifyFrame(Identifier original) {
        return VisualsConfig.INSTANCE.getForceDefaultVanillaBackground() ? FRAME_SPRITE : original;
    }
}
