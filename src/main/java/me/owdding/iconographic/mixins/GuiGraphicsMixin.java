package me.owdding.iconographic.mixins;

import me.owdding.iconographic.Iconographic;
import me.owdding.iconographic.Keybinds;
import me.owdding.iconographic.config.Config;
import me.owdding.iconographic.config.NonSkyBlockItemMode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
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

}
