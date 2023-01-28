package io.github.apricotfarmer11.mods.tubion.mixin.world;

import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.GameMode;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow @Final private static Logger LOGGER;

    @Inject(at = @At("HEAD"), method = "hasGlint", cancellable = true)
    public void hasGlint(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (!TubionMod.getConfig().broadSwordGlint) return;
        if (TubnetCore.getInstance().connected && TubnetCore.getInstance().getGameMode() == GameMode.LIGHT_STRIKE) {
            if (itemStack.getName().getString().contains("Broad Sword") || itemStack.getName().getString().contains("Broadsword")) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
