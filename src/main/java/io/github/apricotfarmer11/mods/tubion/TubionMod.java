package io.github.apricotfarmer11.mods.tubion;

import io.github.apricotfarmer11.mods.tubion.command.TubionCommandManager;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.TubnetCore;
import io.github.apricotfarmer11.mods.tubion.config.TubionConfig;
import io.github.apricotfarmer11.mods.tubion.config.TubionConfigManager;
import io.github.apricotfarmer11.mods.tubion.core.tubnet.event.TubnetConnectionEvents;
import io.github.apricotfarmer11.mods.tubion.feature.AutoGGL;
import io.github.apricotfarmer11.mods.tubion.feature.ChatUtils;
import io.github.apricotfarmer11.mods.tubion.feature.discord.SDKInitializer;
import io.github.apricotfarmer11.mods.tubion.feature.discord.TubnetDiscordIntegration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
//#if MC>=11902
//$$ import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
//#else
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
//#endif

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.UUID;

public class TubionMod implements ClientModInitializer {
    private static TubnetCore tubnetConHandler;
    public static HashMap<UUID, Integer> playersUsingTubion = new HashMap<>();
    public static final String VERSION = FabricLoader.getInstance().getModContainer("tubion").get().getMetadata().getVersion().getFriendlyString();

    public static TubnetDiscordIntegration discordIntegration;
    public static TubionCommandManager commandManager;
    private static TubionMod instance;
    public static boolean deferReload = false;
    public void onInitializeClient() {
        instance = this;
        try {
            TubionConfigManager.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tubnetConHandler = TubnetCore.getInstance();
        SDKInitializer.loadSDK();
        //#if MC>=11902
        //$$ ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {commandManager = new TubionCommandManager(dispatcher);commandManager.loadCommands();});
        //#else
        commandManager = new TubionCommandManager(ClientCommandManager.DISPATCHER);
        commandManager.loadCommands();
        //#endif

        // Load ui utilities
        new AutoGGL();
        ChatUtils.register();
        TubnetConnectionEvents.CONNECT.register(() -> {
            this.initDiscord();
        });
        TubnetConnectionEvents.DISCONNECT.register(() -> {
            discordIntegration.destroy();
            discordIntegration = null;
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (handler.getConnection().getAddress() instanceof InetSocketAddress) {
                if (((InetSocketAddress) handler.getConnection().getAddress()).getHostName().toLowerCase().matches("^((.*)\\.)?tubnet\\.gg\\.?$")) {
                    deferReload = true;
                }
            }
        });
    }
    public void initDiscord() {
        if (discordIntegration != null) {
            discordIntegration.destroy();
            discordIntegration = null;
        }
        discordIntegration = new TubnetDiscordIntegration();
    }

    public static TubionMod getInstance() {return instance;}
    public static TubionConfig getConfig() {
        return TubionConfigManager.getConfig();
    }
}