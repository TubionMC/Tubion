package io.github.apricotfarmer11.mods.tubion;

import io.github.apricotfarmer11.mods.tubion.command.TubionCommandManager;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.config.TubionConfig;
import io.github.apricotfarmer11.mods.tubion.config.TubionConfigManager;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.game.GameMode;
import io.github.apricotfarmer11.mods.tubion.event.ChatMessageEvent;
import io.github.apricotfarmer11.mods.tubion.feature.AutoGGL;
import io.github.apricotfarmer11.mods.tubion.feature.ChatUtils;
import io.github.apricotfarmer11.mods.tubion.feature.discord.SDKInitializer;
import io.github.apricotfarmer11.mods.tubion.feature.discord.TubnetDiscordIntegration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
//#if MC>=11902
//$$ import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
//#else
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.RunArgs;
import net.minecraft.item.ItemStack;
//#endif

import java.io.IOException;

public class TubionMod implements ClientModInitializer {
    private static TubnetCore tubnetConHandler;
    public static final String VERSION = FabricLoader.getInstance().getModContainer("tubion").get().getMetadata().getVersion().getFriendlyString();

    public static TubnetDiscordIntegration discordIntegration;
    public static TubionCommandManager commandManager;
    private static TubionMod instance;
    public void onInitializeClient() {
        instance = this;
        try {
            TubionConfigManager.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tubnetConHandler = TubnetCore.getInstance();
        SDKInitializer.loadSDK();

        // Load Discord
        discordIntegration = new TubnetDiscordIntegration();
        //#if MC>=11902
        //$$ ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {commandManager = new TubionCommandManager(dispatcher);commandManager.loadCommands();});
        //#else
        commandManager = new TubionCommandManager(ClientCommandManager.DISPATCHER);
        commandManager.loadCommands();
        //#endif

        // Load chat utilities
        ChatMessageEvent.EVENT.register(new ChatUtils()::onChat);
        new AutoGGL();
    }

    public static TubionMod getInstance() {return instance;}
    public static TubionConfig getConfig() {
        return TubionConfigManager.getConfig();
    }
}