package io.github.apricotfarmer11.mods.tubion.core.tubnet.game;

import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;

public interface TubnetGame {
    String getName();
    TeamType getTeamType();
    default boolean isInQueue() {
        Scoreboard scoreboard = MinecraftClient.getInstance().world.getScoreboard();
        if (scoreboard == null) return false;
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(1);
        ScoreboardPlayerScore[] scoreboardPlayerScores = scoreboard.getAllPlayerScores(objective).toArray(ScoreboardPlayerScore[]::new);
        if (scoreboardPlayerScores.length >= 3) {
            String row = TubnetCore.getDecoratedPlayerName(scoreboardPlayerScores[2]).getString();
            if (row.toLowerCase().contains("in-queue")) {
                return true;
            }
        }
        return false;
    }
    default void recomputeTeamType() {

    }
}
