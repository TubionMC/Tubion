package io.github.apricotfarmer11.mods.tubion.core.tubnet;

import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.event.TubnetConnectionEvents;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.GameMode;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.TubnetGame;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.mode.BattleRoyale;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.mode.CrystalRush;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.mode.LightStrike;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.mode.Lobby;
import io.github.apricotfarmer11.mods.tubion.core.websocket.WebsocketHandler;
import io.github.apricotfarmer11.mods.tubion.event.ChatMessageEvent;
import io.github.apricotfarmer11.mods.tubion.event.GameHudEvents;
import io.github.apricotfarmer11.mods.tubion.multiport.TextUtils;
import io.netty.channel.local.LocalAddress;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TubnetCore {
    private static final Logger LOGGER = LoggerFactory.getLogger("Tubion/CoreTubNet");
    private static final TubnetCore INSTANCE = new TubnetCore();
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    // Regex and stuff
    private static final Pattern SERVER_IDENTIFIER_PATTERN = Pattern.compile("tubnet\\.gg ([a-z0-9]{5})");
    private static final Pattern PARTY_CREATED_PATTERN = Pattern.compile("(?:You have joined \"([a-zA-Z0-9_]{2,16})\"'s party!|Party has been created!|\"[a-zA-Z0-9_]{2,16}\" has been invited to the party by ([a-zA-Z0-9_]{2,16})!)");
    private static final Pattern PARTY_DELETED_PATTERN = Pattern.compile("(?:You were kicked from your party\\.|You left the party\\.)");
    private static final Pattern PRIVATE_MESSAGE_PREFIX_PATTERN = Pattern.compile("([a-zA-Z0-9_]{2,16}) -> You");
    private static final Pattern DISCORD_PARTY_JOIN = Pattern.compile("tubionPartyJoin\\.(\\d+)\\.([0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12})");
    // Networking
    public boolean connecting = false;
    public boolean connected = false;

    private String serverId;
    public boolean eventServer = false;

    // TubNet related stuff
    private GameMode gameMode;
    private TubnetGame currentGame;
    public TubnetParty currentParty;
    private WebsocketHandler socket;

    public TubnetCore() {
        // Load event handlers
        ClientLoginConnectionEvents.INIT.register((handler, client) -> {
            SocketAddress addr = handler.getConnection().getAddress();
            if (addr instanceof LocalAddress) return; // singleplayer
            InetSocketAddress socketAddress = (InetSocketAddress) handler.getConnection().getAddress();
            if (socketAddress == null) return;
            if (!socketAddress.getHostName().toLowerCase().matches("^((.*)\\.)?tubnet\\.gg\\.?$")) return;
            // We don't really know if the login attempt will
            // be successful, so we approach this with caution
            connecting = true;
            LOGGER.info("Connecting to TubNet[phase: login|OK]");
        });
        ClientLoginConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (!connecting) return;
            connecting = false;
            LOGGER.info("Disconnected from TubNet[phase: login]");
            TubnetConnectionEvents.LOGIN_ERROR.invoker().onLoginEnd();
        });

        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            if (connected) return; // We are switching to a different server, don't fire again
            SocketAddress addr = handler.getConnection().getAddress();
            if (addr instanceof LocalAddress) return; // singleplayer
            InetSocketAddress socketAddress = (InetSocketAddress) addr;
            if (socketAddress == null) return;
            if (!socketAddress.getHostName().toLowerCase().matches("^((.*)\\.)?tubnet\\.gg\\.?$")) return;
            connecting = false;
            connected = true;
            LOGGER.info("Connected to TubNet[phase:play|OK]");
            this.gameMode = GameMode.LOBBY;
            this.currentParty = new TubnetParty(
                    new TubnetPlayer(
                            MinecraftClient.getInstance().getSession().getUsername(),
                            null
                    ),
                    null,
                    null
            );
            try {
                this.socket = new WebsocketHandler();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            TubnetConnectionEvents.CONNECT.invoker().onConnect();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (!connected) return;
            connecting = false;
            connected = false;
            this.currentParty = null;
            LOGGER.info("Disconnected from TubNet[phase:play]");
            try {
                TubnetConnectionEvents.DISCONNECT.invoker().onDisconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.client.close();
            this.socket = null;
        });
        ChatMessageEvent.EVENT.register((msg) -> {
            if (!connected) return ActionResult.PASS;
            Matcher partyJoinMatcher = PARTY_CREATED_PATTERN.matcher(msg.getString());
            if (partyJoinMatcher.find()) {
                String playerName = MinecraftClient.getInstance().player.getName().getString();
                if (partyJoinMatcher.groupCount() >= 1) playerName = partyJoinMatcher.group(1);
                currentParty = new TubnetParty(new TubnetPlayer(
                        playerName,
                        null
                ), TubionMod.discordIntegration.currentPartyUUID, TubionMod.discordIntegration.currentPartySecret);
                LOGGER.info("Party created!");
                return ActionResult.PASS;
            }
            Matcher partyLeaveMatcher = PARTY_DELETED_PATTERN.matcher(msg.getString());
            if (partyLeaveMatcher.find() && this.currentParty != null) {
                this.currentParty.deleted = true;
                this.currentParty = null;
                LOGGER.info("Party deleted.");
                currentParty = new TubnetParty(
                        new TubnetPlayer(
                                MinecraftClient.getInstance().getSession().getUsername(),
                                null
                        ),
                        null,
                        null
                );
                return ActionResult.PASS;
            }
            Matcher discordPartyRequestInvMatcher = DISCORD_PARTY_JOIN.matcher(msg.getString());
            Matcher pmMatcher = PRIVATE_MESSAGE_PREFIX_PATTERN.matcher(msg.getString());
            if (pmMatcher.find() && discordPartyRequestInvMatcher.find()) {
                String discordId = discordPartyRequestInvMatcher.group(1);
                String uuid = discordPartyRequestInvMatcher.group(2);
                LOGGER.info(discordId + uuid + pmMatcher.group(1));
                if (UUID.fromString(uuid).equals(this.currentParty.partyIdSecret)) {
                    this.currentParty.invitePlayer(pmMatcher.group(1));
                }
                return ActionResult.CONSUME;
            }

            return ActionResult.PASS;
        });
    }

    public void onScoreboardUpdate() {
        if (CLIENT.world != null && CLIENT.player != null && (this.connected || this.connecting)) {
            Scoreboard scoreboard = CLIENT.player.getScoreboard();
            if (scoreboard != null) {
                ScoreboardObjective targetObjective = scoreboard.getObjectiveForSlot(1);
                if (targetObjective == null) return;
                String serverGameMode = targetObjective.getDisplayName().getString().toLowerCase().replaceAll("[^a-z0-9]| ", "");
                if (serverGameMode.equals("tubnet")) {
                    this.gameMode = GameMode.LOBBY;
                    if (!(this.currentGame instanceof Lobby)) this.currentGame = new Lobby();
                } else if (serverGameMode.contains("lightstrike")) {
                    this.gameMode = GameMode.LIGHT_STRIKE;
                    if (!(this.currentGame instanceof LightStrike)) this.currentGame = new LightStrike();
                } else if (serverGameMode.equals("crystal rush")) {
                    this.gameMode = GameMode.CRYSTAL_RUSH;
                    if (!(this.currentGame instanceof CrystalRush)) this.currentGame = new CrystalRush();
                } else if (serverGameMode.equals("battle royale")) {
                    this.gameMode = GameMode.BATTLE_ROYALE;
                    if (!(this.currentGame instanceof BattleRoyale)) this.currentGame = new BattleRoyale();
                    TubnetGame GAME = this.currentGame;
                    AtomicBoolean bl = new AtomicBoolean(false);
                    GameHudEvents.TITLE_SET.register((text) -> {
                        if (bl.get()) return;
                        if (this.currentGame == GAME) {
                            if (text.getString().contains("go!")) {
                                // Start
                            }
                        }
                        bl.set(true);
                    });
                }
                this.currentGame.recomputeTeamType();
                ScoreboardPlayerScore[] scoreboardPlayerScores = scoreboard.getAllPlayerScores(targetObjective).toArray(ScoreboardPlayerScore[]::new);

                if (scoreboardPlayerScores.length == 0) return;
                Matcher match = SERVER_IDENTIFIER_PATTERN.matcher(getDecoratedPlayerName(scoreboardPlayerScores[0]).getString());
                if (match.find()) {
                    serverId = match.group(1);
                } else {
                    serverId = "unkno";
                }

                if (serverId.equals("insta")) {
                    // This server is an event server - showdown.tubnet.gg always has this id
                    eventServer = true;
                } else {
                    eventServer = false;
                }
            }
        }
    }

    public GameMode getGameMode() {
        return gameMode;
    }
    public TubnetGame getCurrentGame() {
        return currentGame;
    }

    public static MutableText getDecoratedPlayerName(ScoreboardPlayerScore score) {
        return Team.decorateName(
                score.getScoreboard().getPlayerTeam(score.getPlayerName()),
                TextUtils.literal(score.getPlayerName())
        );
    }

    public static TubnetCore getInstance() {
        return INSTANCE;
    }
}
