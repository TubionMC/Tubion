package io.github.apricotfarmer11.mods.tubion.mixin.gui;

import io.github.apricotfarmer11.mods.tubion.event.ChatMessageEvent;
import io.github.apricotfarmer11.mods.tubion.misc.ChatHudMixin$VisibleMessageGetter;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
//#if MC>=11902
//$$ import net.minecraft.client.gui.hud.MessageIndicator;
//$$ import net.minecraft.network.message.MessageSignatureData;
//$$ import javax.annotation.Nullable;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public class ChatHudMixin implements ChatHudMixin$VisibleMessageGetter {
    @Shadow
    private
        //#if MC>=11902
        //$$ List<ChatHudLine.Visible>
        //#else
        List<ChatHudLine>
        //#endif
            visibleMessages;
    @Unique
    public
    //#if MC>=11902
    //$$ List<ChatHudLine.Visible>
    //#else
    List<ChatHudLine>
    //#endif
    getVisibleMessages() {
        return this.visibleMessages;
    }
    //#if MC>=11902
    //$$ @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", cancellable = true)
    //$$ private void onMessage(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
    //#else
    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", cancellable = true)
    public void onMessage(Text message, int i, int j, boolean bl, CallbackInfo ci) {
    //#endif
        ActionResult result = ChatMessageEvent.EVENT.invoker().onChat(message);
        if (result != ActionResult.PASS) {
            ci.cancel();
        }
    }
}