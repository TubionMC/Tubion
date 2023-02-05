package io.github.apricotfarmer11.mods.tubion.mixin.gui;

import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.event.api.EventManager;
import io.github.apricotfarmer11.mods.tubion.event.api.events.callables.EventCancellable;
import io.github.apricotfarmer11.mods.tubion.event.ui.ChatMessageEvent;
import io.github.apricotfarmer11.mods.tubion.misc.ChatHudMixin$VisibleMessageGetter;
import io.github.apricotfarmer11.mods.tubion.multiport.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
//#if MC>=11902
//$$ import net.minecraft.client.gui.hud.MessageIndicator;
//$$ import net.minecraft.network.message.MessageSignatureData;
//#endif
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements ChatHudMixin$VisibleMessageGetter {
    @Shadow
    private
        //#if MC>=11902
        //$$ List<ChatHudLine.Visible>
        //#else
        List<ChatHudLine>
        //#endif
            visibleMessages;
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private static Logger LOGGER;

    // Rank Chars: (Owner|Admin|Crew|Mod|YouTube Rank|Star|Star+|Player|Mascot)
    @Unique
    private Pattern CHAT_MESSAGE_MATCHER = Pattern.compile("(\uA029|\uA02A|\uA02B|\uA02C|\uA02D|\uA02E|\uA075|\uA076|\uA0E2) ([a-zA-Z0-9_]{2,16}) \uA01C (.*)");

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
        if (!TubnetCore.getInstance().connected) return;
        EventCancellable ev = (EventCancellable) EventManager.call(new ChatMessageEvent(message));

        if (ev.isCancelled()) {
            ci.cancel();
        }
    }
    //#if MC>=11902
    //$$ @ModifyVariable(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", ordinal = 0)
    //$$ public Text injectaa(Text message) {
    //#else
    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"), ordinal = 0)
    public Text injectaa(Text message) {
        //#endif
        Matcher matcher = CHAT_MESSAGE_MATCHER.matcher(message.getString());
        if (matcher.find() && client.getNetworkHandler() != null) {
            PlayerListEntry player = client.getNetworkHandler().getPlayerListEntry(matcher.group(1));
            if (player == null) return message;

            Integer rank = TubionMod.playersUsingTubion.get(player.getProfile().getId());
            String prefix;

            if (client.getSession().getProfile().getId() == player.getProfile().getId() && rank == null) {
                rank = 0;
            }
            if (rank == null) {
                LOGGER.info("[ui] no rank found");
                return message;
            }


            switch(rank.intValue()) {
                case 2: {
                    prefix = "\u132a";
                    break;
                }
                case 1: {
                    prefix = "\u132b";
                    break;
                }
                case 0: {
                    prefix = "\u132c";
                    break;
                }
                default: {
                    prefix = "";
                    break;
                }
            }
            LOGGER.info("[ui] prefix: " + prefix);
            return TextUtils.literal(prefix)
                    .setStyle(
                            Style.EMPTY.withFont(new Identifier("tubion:icons"))
                    )
                    .append(
                            TextUtils.literal("")
                                    .setStyle(
                                            Style.EMPTY.withFont(new Identifier("minecraft:default"))
                                    )
                                    .formatted(Formatting.RESET)

                    )
                    .append(
                            message
                    );
        }
        return message;
    }
}