package io.github.apricotfarmer11.mods.tubion.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class TubionConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("Tubion/ConfigManager");
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final File configFolder = new File("config/tubion");
    private static final File configFile = new File(configFolder, "tubion.config.json");
    private static final Gson gsonSerializer = new GsonBuilder().setPrettyPrinting().create();

    private static TubionConfig config = new TubionConfig();
    private static TubionConfig beforeSavedConfig = new TubionConfig();

    /**
     * Gets the config
     */
    public static TubionConfig getConfig() {
        return config;
    }
    /**
     * Load the configuration if it exists, otherwise create the file and write to it.
     */
    public static void init() throws IOException {
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        if (configFolder.isDirectory()) {
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                    String json = gsonSerializer.toJson(config);
                    FileWriter writer = new FileWriter(configFile);
                    writer.write(json);
                    writer.close();
                } catch(IOException ex) {
                    throw new IOException("(Tubion) [Config] Failed to create '.minecraft/config/tubion/tubion.config.json' - Please replace tubion.config.json with {}");
                }
            }
            loadJson();
        } else {
            throw new IllegalStateException("(Tubion) [Config] '.minecraft/config/tubion' MUST be a folder, not a file!");
        }
    }

    public static void loadJson() {
        try {
            config = gsonSerializer.fromJson(new FileReader(configFile), TubionConfig.class);
            beforeSavedConfig = gsonSerializer.fromJson(new FileReader(configFile), TubionConfig.class);
            if (config == null) {
                throw new JsonSyntaxException("");
            }
        } catch (JsonSyntaxException e) {
            LOGGER.info("Invalid config! Re-creating config..");
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            LOGGER.error("um thats not supposed to happen");
            config = new TubionConfig();
            beforeSavedConfig = new TubionConfig();
        }
    }

    public static void updateJSON() {
        saveJSON();
        if (TubnetCore.getInstance().connected && ((beforeSavedConfig.enableTubnetTweaks != config.enableTubnetTweaks) || (beforeSavedConfig.tubnetTweaksPackType != config.tubnetTweaksPackType))) {
            MinecraftClient.getInstance().reloadResources();
        }
        if (TubnetCore.getInstance().connected && (beforeSavedConfig.enableDiscordRPC != config.enableDiscordRPC)) {
            TubionMod.discordIntegration.reloadClient();
        }
        try {
            beforeSavedConfig = gsonSerializer.fromJson(new FileReader(configFile), TubionConfig.class);
        } catch(IOException ex) {
            LOGGER.error("Error loading SAVED_INSTANCE");
        }
    }
    private static void saveJSON() {
        try {
            String json = gsonSerializer.toJson(config);
            FileWriter writer = new FileWriter(configFile);
            writer.write(json);
            writer.close();
        } catch(IOException ex) {
            LOGGER.info("Config failed to save");
            ex.printStackTrace();
        }
    }
}
