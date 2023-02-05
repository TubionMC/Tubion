package io.github.apricotfarmer11.mods.tubion.feature;

import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.config.TubionConfig;
import io.github.apricotfarmer11.mods.tubion.core.tubient.model.types.ChatInfoLevel;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.event.TubnetConnectionEvents;
import io.github.apricotfarmer11.mods.tubion.event.api.EventManager;
import io.github.apricotfarmer11.mods.tubion.event.api.EventTarget;
import io.github.apricotfarmer11.mods.tubion.event.ui.ChatMessageEvent;
import io.github.apricotfarmer11.mods.tubion.multiport.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static int lobbyEntered = 0;
    private static final Pattern DYKREGEX = Pattern.compile("^\n . Did you know\\?\n\n (.*)\n$");
    private static final Pattern MAXIMUM_WOOL_LIMIT_REGEX = Pattern.compile("^. You can only carry a maximum of 64 wool at a time\\.$");
    public ChatUtils() {
        TubnetConnectionEvents.DISCONNECT.register(() -> {
            lobbyEntered = 0;
        });
        EventManager.register(this);
    }
    public static void register() {
        new ChatUtils();
    }
    @EventTarget
    public void onChat(ChatMessageEvent ev) {
        if (!TubnetCore.getInstance().connected) return;
        TubionConfig config = TubionMod.getConfig();
        Text text = ev.getMessage();
        String message = text.getString();
        switch (TubnetCore.getInstance().getGameMode()) {
            case LOBBY: {
                Matcher m = DYKREGEX.matcher(message);
                if (config.hideTips && m.find()) {
                    break;
                }
                if (config.hideWelcomeMessage && message.contains("Welcome to New Block City")) {
                    if (++lobbyEntered > 1) {
                        ev.setCancelled(true);
                        break;
                    }
                    break;
                }
                if (config.betterNpcMessages) {
                    Pattern npc1Liner = Pattern.compile("^\n . ([a-zA-Z ]+)\n\n {2}(.*)\n$");
                    Pattern npc2Liner = Pattern.compile("^\n . ([a-zA-Z ]+)\n\n {2}(.*)\n {2}(.*)\n$");
                    Pattern npc3Liner = Pattern.compile("^\n . ([a-zA-Z ]+)\n\n {2}(.*)\n {2}(.*)\n {2}(.*)\n$");

                    Matcher npcLiner3Match = npc3Liner.matcher(message);
                    String npcMessage;
                    String npcName;
                    if (npcLiner3Match.find()) {
                        npcName = npcLiner3Match.group(1);
                        npcMessage = npcLiner3Match.group(2) + " " + npcLiner3Match.group(3) + " " + npcLiner3Match.group(4);
                    } else {
                        Matcher npcLiner2Match = npc2Liner.matcher(message);
                        if (npcLiner2Match.find()) {
                            npcName = npcLiner2Match.group(1);
                            npcMessage = npcLiner2Match.group(2) + " " + npcLiner2Match.group(3);
                        } else {
                            Matcher npcLiner1Match = npc1Liner.matcher(message);
                            if (npcLiner1Match.find()) {
                                npcName = npcLiner1Match.group(1);
                                npcMessage = npcLiner1Match.group(2);
                            } else {
                                break;
                            }
                        }
                    }
                    if (npcName != null) {
                        CLIENT.inGameHud.getChatHud().addMessage(
                                TextUtils.literal("\uA0E3 ")
                                        .append(
                                                TextUtils.literal(npcName)
                                                        .formatted(Formatting.YELLOW)
                                        )
                                        .append(" \uA01C ")
                                        .append(npcMessage)
                        );
                    }
                }
                break;
            }
            case BATTLE_ROYALE: {
                if (config.hideWoolLimitMessage && MAXIMUM_WOOL_LIMIT_REGEX.matcher(message).find()) {
                    ev.setCancelled(true);
                    return;
                }
                break;
            }
        }
        CompactChat.onChat(ev);
    }
}
