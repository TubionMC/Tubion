package io.github.apricotfarmer11.mods.tubion.feature.discord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.GameSDKException;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.user.DiscordUser;
import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.helper.ChatHelper;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.event.TubnetConnectionEvents;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.GameMode;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.TeamType;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.TubnetGame;
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

public class TubnetDiscordIntegration {
    public static final Logger LOGGER = LoggerFactory.getLogger("Tubion/Discord");
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static Core discordCore;
    public static final MutableText BASE = ChatHelper.getChatPrefixWithFeature(
            TextUtils.literal("Discord").formatted(Formatting.BOLD, Formatting.DARK_PURPLE).append(TextUtils.literal("").formatted(Formatting.RESET))
    );
    private int threads = 0;
    private boolean initialized = false;
    private String last = "";
    private Instant time;
    private String gamemode;
    private String gamestate;
    private boolean inQueue = false;

    public UUID currentPartyUUID;
    public UUID currentPartySecret;
    public TubnetDiscordIntegration() {
        WorldEvents.INIT.register(() -> {
            if (!(TubnetCore.getInstance().connected || TubnetCore.getInstance().connecting)) return;
            time = Instant.now();
            gamemode = "Lobby";
            gamestate = "";
            inQueue = false;
            if (discordCore == null || !discordCore.isOpen()) {
                (new Thread("Discord Initializer Thread #" + ++threads) {
                    @Override
                    public void run() {
                        initializeRpc();
                    }
                }).start();
            }
        });
        TubnetConnectionEvents.DISCONNECT.register(() -> {
            if (discordCore != null && discordCore.isOpen()) {
                discordCore.activityManager().clearActivity();
                try {
                    discordCore.close();
                } catch(GameSDKException ex) {}
                discordCore = null;
                initialized = false;
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (discordCore != null && discordCore.isOpen()) {
                try {
                    discordCore.runCallbacks();
                } catch(Exception ex) {
                    LOGGER.info("Error when running callbacks: " + ex.toString());
                }
            }
        });
        ScoreboardEvents.SCOREBOARD_UPDATE.register(() -> {
            if (discordCore != null && discordCore.isOpen()) {
                updateRpc();
            }
        });
    }
    public void reloadClient() {
        if (discordCore != null) {
            if (discordCore.isOpen()) discordCore.close();
            discordCore = null;
            initialized = false;
        }
        if (!TubionMod.getConfig().enableDiscordRPC) return;
        CLIENT.inGameHud.getChatHud().addMessage(BASE.shallowCopy().append("Reconnecting to Discord"));
        if (initializeRpc()) {
            CLIENT.inGameHud.getChatHud().addMessage(BASE.shallowCopy().append("Connected to Discord"));
        } else {
            CLIENT.inGameHud.getChatHud().addMessage(this.BASE.shallowCopy().append("Failed to connect to Discord. Run ").append(TextUtils.literal("/tubion discord reconnect").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tubion discord reconnect"))).formatted(Formatting.BOLD).append(" to attempt to reconnect.")));
        }
    }
    public void reloadActivityManager() {
        discordCore.activityManager().clearActivity();
        updateRpc();
    }
    public boolean initializeRpc() {
        if (!TubionMod.getConfig().enableDiscordRPC) return true;
        CreateParams params = new CreateParams();
        params.setClientID(1046493096512339968L);
        params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
        params.registerEventHandler(new DiscordEventAdapter() {
            @Override
            public void onActivityJoin(String secret) {
                String[] data = secret.split(":");
                CLIENT.player
                //#if MC==11902
                //$$    .sendCommand(
                //#elseif MC>=11903
                //$$    .networkHandler.sendChatCommand(
                //#else
                        .sendChatMessage("/" +
                //#endif
                                "msg " + data[1] + " tubionPartyJoin." + discordCore.userManager().getCurrentUser().getUserId() + "." + data[2]
                        );
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
            discordCore = new Core(params);
            LOGGER.info("Successfully initialized Discord GameSDK!");
            initialized = true;
            return true;
        } catch(GameSDKException ex) {
            LOGGER.error("An error occurred while attempting to initialize the SDK:\n" + ex.toString());
            initialized = false;
            assert MinecraftClient.getInstance().player != null;
            CLIENT.inGameHud.getChatHud().addMessage(this.BASE.shallowCopy().append("Failed to connect to Discord. Run ").append(TextUtils.literal("/tubion discord reconnect").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tubion discord reconnect"))).formatted(Formatting.BOLD).append(" to attempt to reconnect.")));
            return false;
        }
    }
    public void updateRpc() {
        if (discordCore == null && !discordCore.isOpen()) return;
        if (!TubionMod.getConfig().enableDiscordRPC) return;
        try (Activity activity = new Activity()) {
            TubnetCore Tubnet = TubnetCore.getInstance();
            TubnetGame game = Tubnet.getCurrentGame();
            if (game != null) {
                if (Tubnet.getGameMode() == GameMode.CRYSTAL_RUSH) {
                    if (game.getTeamType() == TeamType.SOLOS) {
                        gamemode += " (Solos)";
                    } else {
                        gamemode += " (Duos)";
                    }
                }
                if (game.isInQueue()) {
                    gamestate = "In queue";
                    inQueue = true;
                } else if (inQueue) {
                    time = Instant.now();
                    inQueue = false;
                }
                if (!last.equals(gamemode)) {
                    LOGGER.info("New game identified: " + gamemode);
                }
                last = gamemode;
                activity.setDetails(gamemode);
                if (Tubnet.getGameMode() == GameMode.LOBBY) {
                    activity.setState("");
                } else {
                    activity.setState(gamestate);
                }
            } else {
                activity.setDetails("Unknown");
            }
            if (time == null) time = Instant.now();
            activity.timestamps().setStart(Instant.ofEpochSecond(time.toEpochMilli()));
            activity.assets().setLargeImage("tubnet_logo");
            activity.assets().setLargeText("Powered by Tubion v" + TubionMod.VERSION);
            activity.party().setID(Tubnet.currentParty.partyId.toString());
            String secret = "////:" + CLIENT.getSession().getUsername() + ":" + Tubnet.currentParty.partyIdSecret.toString();
            activity.secrets().setJoinSecret(secret);
            activity.party().size().setCurrentSize(Tubnet.currentParty.members.size());
            activity.party().size().setMaxSize(4);
            discordCore.activityManager().updateActivity(activity);
        } catch(GameSDKException ex) {
            LOGGER.error("Failed to send Activity Update: " + ex.toString());
        }
    }
}
