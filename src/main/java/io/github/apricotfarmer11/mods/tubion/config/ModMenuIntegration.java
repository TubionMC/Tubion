package io.github.apricotfarmer11.mods.tubion.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.apricotfarmer11.mods.tubion.multiport.TextUtils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screen.Screen;


public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> getConfigurationBuilder().setParentScreen(parent).build();
    }

    public static Screen getScreen() {
        return getConfigurationBuilder().build();
    }

    private static ConfigBuilder getConfigurationBuilder() {
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

        // BATTLE ROYALE CATEGORY
        ConfigCategory battleRoyaleCategory = configBuilder.getOrCreateCategory(TextUtils.translatable("text.tubion.settings.battleRoyale.title"));

        battleRoyaleCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.battleRoyale.hideWoolLimitMessage"), TubionConfigManager.getConfig().hideWoolLimitMessage)
                        .setDefaultValue(false)
                        .setTooltip(TextUtils.translatable("text.tubion.settings.battleRoyale.hideWoolLimitMessage.tooltip"))
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().hideWoolLimitMessage = val;
                        })
                        .build()
        );
        // DISCORD CATEGORY
        ConfigCategory discordCategory = configBuilder.getOrCreateCategory(TextUtils.translatable("text.tubion.settings.discord.title"));

        discordCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.discord.enableRPC"), TubionConfigManager.getConfig().enableDiscordRPC)
                        .setDefaultValue(true)
                        .setTooltip(
                                TextUtils.translatable("text.tubion.settings.discord.enableRPC.tooltip")
                        )
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().enableDiscordRPC = val;
                        })
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
        tubnetTweaksCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(TextUtils.translatable("text.tubion.settings.tubnet_tweaks.compactchat"), TubionConfigManager.getConfig().enableCompactChat)
                        .setDefaultValue(false)
                        .setSaveConsumer(val -> {
                            TubionConfigManager.getConfig().enableCompactChat = val;
                        })
                        .build()
        );

        return configBuilder;
    }
}
