package io.github.apricotfarmer11.mods.tubion.core.tubnet.game.mode;

import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.TeamType;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.TubnetGame;

public class LightStrike implements TubnetGame {
    @Override
    public String getName() {
        return "Light Strike";
    }

    @Override
    public TeamType getTeamType() {
        return TeamType.SQUADS;
    }

}
