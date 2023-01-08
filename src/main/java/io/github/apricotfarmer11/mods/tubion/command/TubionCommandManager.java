package io.github.apricotfarmer11.mods.tubion.command;

import com.mojang.brigadier.CommandDispatcher;
//#if MC>=11902
//$$ import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
//$$ import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
//#else
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
//#endif
import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.multiport.TextUtils;
import net.minecraft.client.MinecraftClient;

public class TubionCommandManager {
    private static CommandDispatcher<FabricClientCommandSource> dispatcher;
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public TubionCommandManager(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        this.dispatcher = dispatcher;
    }
    public void loadCommands() {
        this.dispatcher.register(ClientCommandManager.literal("tubion")
                .then(
                        DiscordSubcommand.DISCORD_SUBCOMMAND
                )
                .executes(context -> {
                    CLIENT.inGameHud.getChatHud().addMessage(
                            TextUtils.literal("You're running Tubion v" + TubionMod.VERSION + ".")
                                    .append(
                                            "\n"
                                    )
                                    .append(
                                            "No new updates are available."
                                    )
                    );
                    return 1;
                })
        );
    }
}
