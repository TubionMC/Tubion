package io.github.apricotfarmer11.mods.tubion.mixin.gui;

import io.github.apricotfarmer11.mods.tubion.event.GameHudEvents;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(at = @At("HEAD"), method = "setTitle")
    public void setTitle(Text title, CallbackInfo ci) {
        GameHudEvents.TITLE_SET.invoker().onTitleSet(title);
    }
}
