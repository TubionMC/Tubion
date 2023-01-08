package io.github.apricotfarmer11.mods.tubion.core.tubnet.game.mode;

import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.TeamType;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.TubnetGame;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;

public class CrystalRush implements TubnetGame {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private TeamType teamType;
    @Override
    public String getName() {
        return "Crystal Rush";
    }

    @Override
    public TeamType getTeamType() {
        return teamType;
    }

    @Override
    public void recomputeTeamType() {
        Scoreboard scoreboard = CLIENT.world.getScoreboard();
        if (scoreboard == null) return;
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(1);
        ScoreboardPlayerScore[] scoreboardPlayerScores = scoreboard.getAllPlayerScores(objective).toArray(ScoreboardPlayerScore[]::new);
        String teamIdentifier = TubnetCore.getDecoratedPlayerName(scoreboardPlayerScores[scoreboardPlayerScores.length - 1]).getString();

        if (teamIdentifier.contains("solos")) {
            this.teamType = TeamType.SOLOS;
        } else {
            this.teamType = TeamType.DUOS;
        }
    }
}
