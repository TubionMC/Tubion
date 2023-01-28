package io.github.apricotfarmer11.mods.tubion.feature.discord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.GameSDKException;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.user.DiscordUser;
import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.helper.ChatHelper;
import io.github.apricotfarmer11.mods.tubion.core.helper.PlayerHelper;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.GameMode;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.TeamType;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.TubnetGame;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.mode.Lobby;
import io.github.apricotfarmer11.mods.tubion.event.ScoreboardEvents;
import io.github.apricotfarmer11.mods.tubion.event.WorldEvents;
import io.github.apricotfarmer11.mods.tubion.multiport.TextUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TubnetDiscordIntegration {
    public static final Logger LOGGER = LoggerFactory.getLogger("Tubion/Discord");
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final Text CHAT_MSG_BASE = ChatHelper.getChatPrefixWithFeature(
            TextUtils.literal("Discord")
                    .formatted(Formatting.DARK_PURPLE, Formatting.BOLD)
    );
    public Core discordGameSdk;

    private Instant timeStart;
    private String gamemode;
    private String lastGamemode = "";
    private boolean active = true;
    private boolean queuing = false;

    public UUID currentPartyUUID;
    public UUID currentPartySecret;
    public TubnetDiscordIntegration() {
        if (TubionMod.getConfig().enableDiscordRPC && this.discordGameSdk == null) {
            initialise();
        }
        // Register event listeners
        WorldEvents.INIT.register(() -> {
            // Check if this instance is active
            if (!active || discordGameSdk == null || !discordGameSdk.isOpen()) return;

            // Reset activity
            timeStart = Instant.now();
            CompletableFuture.runAsync(() -> {
                if (TubionMod.getConfig().enableDiscordRPC && this.discordGameSdk == null) {
                    initialise();
                }
            });
        });
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (!active || discordGameSdk == null || !discordGameSdk.isOpen()) return;
            try {
                discordGameSdk.runCallbacks();
            } catch(GameSDKException error) {
                LOGGER.error("Failed to run Discord Callbacks: " + error);
            }
        });
        ScoreboardEvents.SCOREBOARD_UPDATE.register(() -> {
            if (!active || discordGameSdk == null || !discordGameSdk.isOpen()) return;
            updateActivity();
        });
    }
    public void destroy() {
        // Stop DiscordRPC if it is running
        try {
            if (discordGameSdk != null && discordGameSdk.isOpen()) {
                // Remove any activity, then clear
                Core saved = this.discordGameSdk;
                this.discordGameSdk = null;
                saved.activityManager().clearActivity();
                saved.runCallbacks();
                saved.close();
            }
        } catch(GameSDKException ex) {}
        active = false;
    }
    public void initialise() {
        CreateParams params = new CreateParams();
        params.setClientID(1046493096512339968L);
        params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
        params.registerEventHandler(new DiscordEventAdapter() {
            @Override
            public void onActivityJoin(String secret) {
                String[] data = secret.split(":");
                PlayerHelper.sendCommand("msg " + data[1] + " tubionPartyJoin." + discordGameSdk.userManager().getCurrentUser().getUserId() + "." + data[2]);
            }

            @Override
            public void onActivityJoinRequest(DiscordUser user) {
                MutableText text = TextUtils.literal(user.getUsername() + "#" + user.getDiscriminator() + " wants to join your party.").formatted(Formatting.BLUE);
                MutableText accept = TextUtils.literal("Accept")
                        .setStyle(
                                Style.EMPTY.withClickEvent(
                                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tubion discord request accept " + user.getUserId())
                                ).withHoverEvent(
                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.literal("Accept the invite"))
                                )
                        )
                        .formatted(Formatting.GREEN, Formatting.BOLD);
                MutableText decline = TextUtils.literal("Decline")
                        .setStyle(
                                Style.EMPTY.withClickEvent(
                                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tubion discord request decline " + user.getUserId())
                                ).withHoverEvent(
                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.literal("Decline the invite"))
                                )
                        )
                        .formatted(Formatting.BOLD, Formatting.RED);
                CLIENT.inGameHud.getChatHud().addMessage(
                        text.append(
                                TextUtils.literal(" ").formatted(Formatting.RESET)
                        ).append(
                                accept
                        ).append(
                                TextUtils.literal(" | ").formatted(Formatting.BOLD, Formatting.RESET)
                        ).append(
                                decline
                        )
                );
            }
        });

        try {
            discordGameSdk = new Core(params);
            LOGGER.info("Successfully initialized Discord GameSDK!");
        } catch(GameSDKException ex) {
            LOGGER.error("An error occurred while attempting to initialize the SDK:\n" + ex.toString());
            assert MinecraftClient.getInstance().player != null;
            CLIENT.inGameHud.getChatHud().addMessage(this.CHAT_MSG_BASE.shallowCopy().append("Failed to connect to Discord. Run ").append(TextUtils.literal("/tubion discord reconnect").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tubion discord reconnect"))).formatted(Formatting.BOLD).append(" to attempt to reconnect.")));
        }
    }
    public void updateActivity() {
        try (Activity activity = new Activity()) {
            TubnetCore infoHaus = TubnetCore.getInstance();
            TubnetGame currentGame = TubnetCore.getInstance().getCurrentGame();
            String gamestate = "";

            if (currentGame != null) {
                gamemode = currentGame.getName();
                if (infoHaus.getGameMode() == GameMode.CRYSTAL_RUSH) {
                    if (currentGame.getTeamType() == TeamType.SOLOS) {
                        gamemode += " (Solos)";
                    } else {
                        gamemode += " (Duos)";
                    }
                }
                if (currentGame.isInQueue()) {
                    gamestate = "In queue";
                    queuing = true;
                } else if (queuing) {
                    timeStart = Instant.now();
                    queuing = false;
                    gamestate = "In game";
                } else if (currentGame instanceof Lobby) gamestate = "Hovering around the lobby";

                if (!gamemode.equals(lastGamemode)) LOGGER.info("Setting activity to " + gamemode);
                lastGamemode = gamemode;
            } else {
                gamemode = "";
                gamestate = "";
                activity.setDetails("Unknown");
                activity.setState("");
            }
            activity.setDetails(gamemode);
            activity.setState(gamestate);
            if (timeStart == null) timeStart = Instant.now();
            activity.timestamps().setStart(Instant.ofEpochSecond(timeStart.toEpochMilli()));

            activity.assets().setLargeImage("tubnet_logo");
            activity.assets().setLargeText("Powered by Tubion v" + TubionMod.VERSION);

            String secret = "////:" + CLIENT.getSession().getUsername() + ":" + infoHaus.currentParty.partyIdSecret.toString();
            activity.secrets().setJoinSecret(secret);

            activity.party().size().setCurrentSize(infoHaus.currentParty.members.size());
            activity.party().size().setMaxSize(4);
            activity.party().setID(infoHaus.currentParty.partyId.toString());

            discordGameSdk.activityManager().updateActivity(activity);
        } catch(GameSDKException ex) {
            LOGGER.info("Failed to update Rich Presence");
        }
    }
}
