package io.github.apricotfarmer11.mods.tubion.config;

import com.google.gson.annotations.SerializedName;

public class TubionConfig {
    public boolean enableDiscordRPC = true;

    public boolean enableTubnetTweaks = true;
    public TubnetTweaksPackTypes tubnetTweaksPackType = TubnetTweaksPackTypes.DEFAULT;
    public boolean hideWoolLimitMessage = true;
    public boolean customLoadingScreen = true;
    public boolean customPanorama = true;
    public boolean hideWelcomeMessage = true;
    public boolean enableCompactChat = false;
    public boolean betterNpcMessages = true;
    public boolean autoGg = false;
    public boolean autoGf = false;
    public boolean broadSwordGlint = true;


    public enum TubnetTweaksPackTypes {
        @SerializedName("DEFAULT")
        DEFAULT,
        @SerializedName("CHRISTMAS_2022")
        CHRISTMAS_2022;

        @Override
        public String toString() {
            return this == CHRISTMAS_2022 ? "Christmas 2022" : "Default";
        }
    }
}