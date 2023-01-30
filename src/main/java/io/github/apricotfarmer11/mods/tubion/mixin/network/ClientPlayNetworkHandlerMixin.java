package io.github.apricotfarmer11.mods.tubion.mixin.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.websocket.WebsocketHandler;
import io.github.apricotfarmer11.mods.tubion.event.ScoreboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("TAIL"), method = "onScoreboardObjectiveUpdate")
    public void onObjectiveChange(ScoreboardObjectiveUpdateS2CPacket packet, CallbackInfo ci) {
        ScoreboardEvents.SCOREBOARD_UPDATE.invoker().onScoreboardUpdate();
    }
    @Inject(at = @At("TAIL"), method = "onTeam")
    public void onTeamUpdate(TeamS2CPacket packet, CallbackInfo ci) {
        ScoreboardEvents.SCOREBOARD_UPDATE.invoker().onScoreboardUpdate();
    }
    @Inject(at = @At("TAIL"), method = "onScoreboardPlayerUpdate")
    public void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket packet, CallbackInfo ci) {
        ScoreboardEvents.SCOREBOARD_UPDATE.invoker().onScoreboardUpdate();
    }

    @Inject(at = @At("TAIL"), method = "onPlayerList")
    public void onPlrList(PlayerListS2CPacket playerListS2CPacket, CallbackInfo ci) throws URISyntaxException, IOException {
        Iterator entries = playerListS2CPacket.getEntries().iterator();
        while (entries.hasNext()) {
            PlayerListS2CPacket.Entry entry = (PlayerListS2CPacket.Entry) entries.next();
            if (playerListS2CPacket.getAction() == PlayerListS2CPacket.Action.REMOVE_PLAYER) {
                TubionMod.playersUsingTubion.remove(entry.getProfile().getId());
            } else {
                switch(playerListS2CPacket.getAction()) {
                    case UPDATE_DISPLAY_NAME -> {
                        String json;
                        try {
                            json = IOUtils.toString(new URI("http" + WebsocketHandler.API_URL + "/player/info/" + entry.getProfile().getId()), StandardCharsets.UTF_8);
                        } catch(FileNotFoundException ex) {
                            continue;
                        } catch(IOException ex) {
                            continue;
                        }
                        JsonElement obj = JsonParser.parseString(json);
                        boolean exists = obj.getAsJsonObject().get("uuid") == null;
                        // 0: NORMAL
                        // 1: MODERATOR
                        // 2: ADMIN
                        int rank = exists ? 0 : -1;
                        if (exists) {
                            switch(obj.getAsJsonObject().get("rank").getAsString()) {
                                case "ADMIN": {
                                    rank = 2;
                                    break;
                                }
                                case "MODERATOR": {
                                    rank = 1;
                                    break;
                                }
                                default: {
                                    rank = 0;
                                }
                            }
                        }
                        TubionMod.playersUsingTubion.put(entry.getProfile().getId(), rank);
                    }
                }
            }
        }
    }
}
