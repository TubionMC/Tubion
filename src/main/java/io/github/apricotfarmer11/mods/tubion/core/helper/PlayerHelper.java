package io.github.apricotfarmer11.mods.tubion.core.helper;

import net.minecraft.client.MinecraftClient;

public class PlayerHelper {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static void sendChatMessage(String message) {
        //#if MC<11902
        CLIENT.player.sendChatMessage(message);
        //#elseif MC==11902
        //$$ CLIENT.player.sendChatMessage(message, null);
        //#else
        //$$ CLIENT.player.networkHandler.sendChatMessage(message);
        //#endif
    }
    public static void sendCommand(String command) {
        //#if MC<11902
        CLIENT.player.sendChatMessage("/" + command);
        //#elseif MC==11902
        //$$ CLIENT.player.sendCommand("/" + command);
        //#elseif MC>=11903
        //$$ CLIENT.player.networkHandler.sendChatCommand("/" + command);
        //#endif
    }
}
