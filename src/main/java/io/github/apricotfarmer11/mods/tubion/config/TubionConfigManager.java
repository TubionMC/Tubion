package io.github.apricotfarmer11.mods.tubion.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.multiport.TextUtils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
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
            TubionMod.getInstance().initDiscord();
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
    public static ConfigBuilder getConfigurationBuilder() {
        ConfigBuilder configBuilder = ConfigBuilder.create()
                .setTitle(TextUtils.translatable("text.tubion.settings.title"))
                .setEditable(true)
                .setTransparentBackground(true)
                .setSavingRunnable(TubionConfigManager::updateJSON);

        // LOBBY CATEGORY
        ConfigCategory lobbyCategory = configBuilder.getOrCreateCategory(TextUtils.translatable("text.tubion.settings.lobby.title"));

        lobbyCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.lobby.hideDuplicateWelcomeMessage"), TubionConfigManager.getConfig().hideWelcomeMessage)
                        .setDefaultValue(true)
                        .setTooltip(TextUtils.translatable("text.tubion.settings.lobby.hideDuplicateWelcomeMessage.tooltip"))
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().hideWelcomeMessage = val;
                        })
                        .build()
        );
        lobbyCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.lobby.betterNpcMessages"), TubionConfigManager.getConfig().betterNpcMessages)
                        .setDefaultValue(true)
                        .setTooltip(TextUtils.translatable("text.tubion.settings.lobby.betterNpcMessages.tooltip"))
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().betterNpcMessages = val;
                        })
                        .build()
        );
        lobbyCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.lobby.hideTips"), TubionConfigManager.getConfig().hideTips)
                        .setDefaultValue(true)
                        .setTooltip(TextUtils.translatable("text.tubion.settings.lobby.hideTips.tooltip"))
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().hideTips = val;
                        })
                        .build()
        );

        // LIGHT STRIKE CATEGORY
        ConfigCategory lightStrikeCategory = configBuilder.getOrCreateCategory(TextUtils.translatable("text.tubion.settings.lightStrike.title"));

        lightStrikeCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.lightStrike.broadSwordGlint"), TubionConfigManager.getConfig().broadSwordGlint)
                        .setDefaultValue(true)
                        .setTooltip(TextUtils.translatable("text.tubion.settings.lightStrike.broadSwordGlint.tooltip"))
                        .setSaveConsumer(val -> TubionConfigManager.getConfig().broadSwordGlint = val)
                        .build()
        );

        // BATTLE ROYALE CATEGORY
        ConfigCategory battleRoyaleCategory = configBuilder.getOrCreateCategory(TextUtils.translatable("text.tubion.settings.battleRoyale.title"));

        battleRoyaleCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.battleRoyale.hideWoolLimitMessage"), TubionConfigManager.getConfig().hideWoolLimitMessage)
                        .setDefaultValue(false)
                        .setTooltip(TextUtils.translatable("text.tubion.settings.battleRoyale.hideWoolLimitMessage.tooltip"))
                        .setSaveConsumer(val -> TubionConfigManager.getConfig().hideWoolLimitMessage = val)
                        .build()
        );

        // TUBNET TWEAKS CATEGORY
        ConfigCategory tubnetTweaksCategory = configBuilder.getOrCreateCategory(TextUtils.translatable("text.tubion.settings.tubnet_tweaks.title"));

        tubnetTweaksCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.tubnet_tweaks.enableTubnetTweaks"), TubionConfigManager.getConfig().enableTubnetTweaks)
                        .setDefaultValue(true)
                        .setTooltip(
                                TextUtils.translatable("text.tubion.settings.tubnet_tweaks.enableTubnetTweaks.tooltip")
                        )
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().enableTubnetTweaks = val;
                        })
                        .build()
        );
        tubnetTweaksCategory.addEntry(
                configBuilder.entryBuilder()
                        .startEnumSelector(TextUtils.translatable("text.tubion.settings.tubnet_tweaks.resourcePackType"), TubionConfig.TubnetTweaksPackTypes.class, TubionConfigManager.getConfig().tubnetTweaksPackType)
                        .setDefaultValue(TubionConfig.TubnetTweaksPackTypes.DEFAULT)
                        .setTooltip(TextUtils.translatable("text.tubion.settings.tubnet_tweaks.resourcePackType.tooltip"))
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().tubnetTweaksPackType = val;
                        })
                        .build()
        );
        tubnetTweaksCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.tubnet_tweaks.customLoadingScreen"), TubionConfigManager.getConfig().customLoadingScreen)
                        .setDefaultValue(true)
                        .setTooltip(TextUtils.translatable("text.tubion.settings.tubnet_tweaks.customLoadingScreen.tooltip"))
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().customLoadingScreen = val;
                        })
                        .build()
        );
        tubnetTweaksCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.tubnet_tweaks.customPanorama"), TubionConfigManager.getConfig().customPanorama)
                        .setDefaultValue(true)
                        .setTooltip(TextUtils.translatable("text.tubion.settings.tubnet_tweaks.customPanorama.tooltip"))
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().customPanorama = val;
                        })
                        .build()
        );

        // QOL CATEGORY
        ConfigCategory qolCategory = configBuilder.getOrCreateCategory(TextUtils.translatable("text.tubion.settings.qol.title"));
        qolCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.qol.autogg"), TubionConfigManager.getConfig().autoGg)
                        .setDefaultValue(false)
                        .setTooltip(TextUtils.translatable("text.tubion.settings.qol.autogg.tooltip"))
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().autoGg = val;
                        })
                        .build()
        );
        qolCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.qol.autogf"), TubionConfigManager.getConfig().autoGf)
                        .setDefaultValue(false)
                        .setTooltip(TextUtils.translatable("text.tubion.settings.qol.autogf.tooltip"))
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().autoGf = val;
                        })
                        .build()
        );
        qolCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.tubnet_tweaks.compactchat"), TubionConfigManager.getConfig().enableCompactChat)
                        .setDefaultValue(false)
                        .setTooltip(TextUtils.translatable("text.tubion.settings.tubnet_tweaks.compactchat.tooltip"))
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().enableCompactChat = val;
                        })
                        .build()
        );
        qolCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.discord.enableRPC"), TubionConfigManager.getConfig().enableDiscordRPC)
                        .setDefaultValue(true)
                        .setTooltip(
                                TextUtils.translatable("text.tubion.settings.discord.enableRPC.tooltip")
                        )
                        .setSaveConsumer(val -> TubionConfigManager.getConfig().enableDiscordRPC = val)
                        .build()
        );

        return configBuilder;
    }
}
