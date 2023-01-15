package io.github.apricotfarmer11.mods.tubion.command;

import com.mojang.brigadier.CommandDispatcher;
//#if MC>=11902
//$$ import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
//$$ import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
//#else
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
//#endif
import io.github.apricotfarmer11.mods.tubion.config.TubionConfigManager;
import io.github.apricotfarmer11.mods.tubion.core.helper.UpdateHelper;
import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.multiport.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.attribute.HashPrintJobAttributeSet;
import java.io.IOException;

public class TubionCommandManager {
    private static CommandDispatcher<FabricClientCommandSource> dispatcher;
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger("Tubion/CommandManager");

    public TubionCommandManager(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        this.dispatcher = dispatcher;
    }
    public void loadCommands() {
        this.dispatcher.register(ClientCommandManager.literal("tubion")
                .then(
                        DiscordSubcommand.DISCORD_SUBCOMMAND
                )
                        .then(
                                ClientCommandManager.literal("settings")
                                        .executes(context -> {
                                            CLIENT.setScreen(
                                                    TubionConfigManager.getConfigurationBuilder().build()
                                            );
                                            return 1;
                                        })
                        )
                .executes(context -> {
                    String version = TubionMod.VERSION;
                    try {
                        version = UpdateHelper.getLatestVersion();
                    } catch (IOException e) {
                        LOGGER.error("Failed to fetch latest version from Modrinth.");
                        e.printStackTrace();
                    }
                    net.minecraft.text.Text updateText = TextUtils.literal("No new updates");
                    if (!version.equals(TubionMod.VERSION)) {
                        updateText = TextUtils.literal("Latest version: " + version + " (click ")
                                .append(
                                        TextUtils.literal("here")
                                                .setStyle(Style.EMPTY.withClickEvent(
                                                        new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/tubion")
                                                ).withHoverEvent(
                                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.literal("Open the Tubion Downloads Page"))
                                                ))
                                                .formatted(Formatting.GREEN, Formatting.BOLD)
                                )
                                .append(
                                        TextUtils.literal(")")
                                                .formatted(Formatting.RESET)
                                ).append(" to update");
                    }
                    context.getSource().sendFeedback(
                            TextUtils.literal("\uA0C9 You are running ")
                                    .append(
                                            TextUtils.literal("Tubion")
                                                    .formatted(Formatting.GOLD, Formatting.BOLD)
                                    )
                                    .append(
                                            TextUtils.literal(" ").formatted(Formatting.RESET)
                                    )
                                    .append(
                                            TextUtils.literal("v" + TubionMod.VERSION)
                                                    .formatted(Formatting.GREEN)
                                    ).append(
                                            TextUtils.literal("\n  ")
                                                    .formatted(Formatting.RESET)
                                    ).append(
                                            TextUtils.literal("Settings")
                                                    .setStyle(Style.EMPTY.withHoverEvent(
                                                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.literal("Open the Tubion Settings Screen"))
                                                    ).withClickEvent(
                                                            new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tubion settings")
                                                    ))
                                                    .formatted(Formatting.BLUE, Formatting.BOLD)
                                    ).append(" | ").append(
                                            TextUtils.literal("Discord")
                                                    .setStyle(Style.EMPTY.withHoverEvent(
                                                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.literal("Join the Tubion Discord"))
                                                    ).withClickEvent(
                                                            new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tubion discord jointubiondiscord")
                                                    ).withFormatting(Formatting.LIGHT_PURPLE, Formatting.BOLD))

                                    ).append(" | ").append(
                                            TextUtils.literal("Tubble thread")
                                                    .setStyle(Style.EMPTY.withHoverEvent(
                                                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.literal("Talk on the Tubble Thread!"))
                                                    ).withClickEvent(
                                                            new ClickEvent(ClickEvent.Action.OPEN_URL, "https://forums.tubnet.gg/threads/5958/")
                                                    ).withFormatting(Formatting.YELLOW, Formatting.BOLD))
                                    ).append("\n  ").append(updateText)
                    );
                    return 1;
                })
        );
    }
}
