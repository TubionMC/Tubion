package io.github.apricotfarmer11.mods.tubion.feature;

import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.event.ui.ChatMessageEvent;
import io.github.apricotfarmer11.mods.tubion.misc.ChatHudMixin$VisibleMessageGetter;
import io.github.apricotfarmer11.mods.tubion.multiport.TextUtils;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

import java.util.List;

import static io.github.apricotfarmer11.mods.tubion.feature.discord.TubnetDiscordIntegration.CLIENT;
public class CompactChat {
    private static String lastMessage = "";
    private static int amount;
    private static boolean ignore = false;

    public static void onChat(ChatMessageEvent ev) {
        Text text = ev.getMessage();
        if (ignore) return;
        if (!TubionMod.getConfig().enableCompactChat) return;
        ChatHud chat = CLIENT.inGameHud.getChatHud();
        if (lastMessage.equals(text.getString())) {
            //#if MC>=11902
            //$$ List<ChatHudLine.Visible> visibleMessages = ((ChatHudMixin$VisibleMessageGetter) ui).getVisibleMessages();
            //#else
            List<ChatHudLine> visibleMessages = ((ChatHudMixin$VisibleMessageGetter) chat).getVisibleMessages();
            //#endif
            if (visibleMessages.size() > 0) visibleMessages.remove(0);
            amount++;
            lastMessage = text.getString();
            ignore = true;
            chat.addMessage(
                    TextUtils.literal("").append(text)
                            .append(TextUtils.literal(" (" + amount + ")")
                                    .formatted(Formatting.DARK_GRAY))
            );
            ignore = false;
            ev.setCancelled(true);

        } else {
            amount = 1;
            lastMessage = text.getString();
        }
    }
}
