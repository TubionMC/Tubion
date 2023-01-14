package io.github.apricotfarmer11.mods.tubion.feature;

import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.config.TubionConfig;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.event.TubnetConnectionEvents;
import io.github.apricotfarmer11.mods.tubion.multiport.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static int lobbyEntered = 0;
    public ChatUtils() {
        TubnetConnectionEvents.DISCONNECT.register(() -> {
            lobbyEntered = 0;
        });
    }
    public ActionResult onChat(Text msg) {
        if (!TubnetCore.getInstance().connected) return ActionResult.PASS;
        TubionConfig config = TubionMod.getConfig();
        ActionResult result = ActionResult.PASS;
        switch (TubnetCore.getInstance().getGameMode()) {
            case LOBBY: {
                if (config.hideWelcomeMessage && msg.getString().contains("Welcome to New Block City")) {
                    if (lobbyEntered++ > 0) {
                        result = ActionResult.CONSUME;
                        break;
                    }
                }
                if (config.betterNpcMessages) {
                    String message = msg.getString();
                    Pattern npc1Liner = Pattern.compile("\n . ([a-zA-Z ]+)\n\n {2}(.*)\n$");
                    Pattern npc2Liner = Pattern.compile("\n . ([a-zA-Z ]+)\n\n {2}(.*)\n {2}(.*)\n$");
                    Pattern npc3Liner = Pattern.compile("\n . ([a-zA-Z ]+)\n\n {2}(.*)\n {2}(.*)\n {2}(.*)\n$");

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
                        result = ActionResult.CONSUME;
                    }
                }
                break;
            }
            case BATTLE_ROYALE: {
                if (config.hideWoolLimitMessage && msg.getString().contains("maximum of 64 wool")) {
                    result = ActionResult.CONSUME;
                }
                break;
            }
        }
        if (result != ActionResult.PASS) return result;
        return CompactChat.onChat(msg);
    }
}
