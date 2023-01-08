package io.github.apricotfarmer11.mods.tubion.core.tubnet.game.mode;

import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.TeamType;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.TubnetGame;

public class BattleRoyale implements TubnetGame {
    @Override
    public String getName() {
        return "Battle Royale";
    }

    @Override
    public TeamType getTeamType() {
        return TeamType.SOLOS;
    }

}
