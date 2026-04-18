package me.owdding.tooltipthingy.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.tooltipthingy.TooltipThingy;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @WrapOperation(method = "extractTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V"))
    public void extractTooltip(
            GuiGraphicsExtractor instance,
            Font font,
            List<Component> texts,
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
            Optional<TooltipComponent> optionalImage,
            int xo,
            int yo,
            @Nullable Identifier style,
            Operation<Void> original,
            @Local(name = "item") ItemStack item
    ) {
        TooltipThingy.extractingItemTooltip = item;
        original.call(instance, font, texts, optionalImage, xo, yo, style);
        TooltipThingy.extractingItemTooltip = null;
    }

}
