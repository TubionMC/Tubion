package io.github.apricotfarmer11.mods.tubion.core.tubient.model.types;

public enum ChatInfoLevel {
    // Blue
    INFO,
    // Gray
    GRAY,
    // Green
    SUCCESS,
    // Red
    ERROR,
    // Yellow
    WARN;
    public String toString() {
        return switch(this) {
            case INFO -> "\uA0CD";
            case GRAY -> "\uA0CC";
            case SUCCESS -> "\uA0C9";
            case ERROR -> "\uA0CB";
            case WARN -> "\uA0CA";
        };
    }
}
