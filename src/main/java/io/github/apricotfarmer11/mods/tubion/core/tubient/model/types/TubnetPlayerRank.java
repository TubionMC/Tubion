package io.github.apricotfarmer11.mods.tubion.core.tubient.model.types;

public enum TubnetPlayerRank {
    // Owner
    OWNER,
    // Admins
    ADMIN,
    // Moderator
    MOD,
    // Crew
    CREW,
    // Mascot
    MASCOT,
    // Star+
    STAR_PLUS,
    // Star
    STAR,
    // Player
    PLAYER,
    // NPC
    NPC;
    public static String[] badges = {
            OWNER.getBadge(),
            ADMIN.getBadge(),
            MOD.getBadge(),
            CREW.getBadge(),
            MASCOT.getBadge(),
            STAR_PLUS.getBadge(),
            STAR.getBadge(),
            PLAYER.getBadge(),
            NPC.getBadge()
    };
    public static String[] ranks = {
            OWNER.getRank(),
            ADMIN.getRank(),
            MOD.getRank(),
            CREW.getRank(),
            MASCOT.getRank(),
            STAR_PLUS.getRank(),
            STAR.getRank(),
            PLAYER.getRank(),
            NPC.getRank()
    };
    public String toString() {
        return getRank();
    }
    public String getBadge() {
        return switch(this) {
            case OWNER -> "\uA12D";
            case ADMIN -> "\uA128";
            case MOD -> "\uA12B";
            case CREW -> "\uA129";
            case MASCOT -> "\uA12A";
            case STAR_PLUS -> "\uA130";
            case STAR -> "\uA12F";
            case PLAYER -> "\uA12E";
            case NPC -> "\uA12C";
        };
    }
    public String getRank() {
        return switch(this) {
            case OWNER -> "\uA029";
            case ADMIN -> "\uA02A";
            case MOD -> "\uA02C";
            case CREW -> "\uA02B";
            case MASCOT -> "\uA0E2";
            case STAR_PLUS -> "\uA075";
            case STAR -> "\uA02E";
            case PLAYER -> "\uA076";
            case NPC -> "\uA0E3";
        };
    }
}
