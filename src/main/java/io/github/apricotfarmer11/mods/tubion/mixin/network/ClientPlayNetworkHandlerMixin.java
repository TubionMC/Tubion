package io.github.apricotfarmer11.mods.tubion.mixin.network;

import io.github.apricotfarmer11.mods.tubion.event.ScoreboardEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
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
}
