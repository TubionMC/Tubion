package io.github.apricotfarmer11.mods.tubion.feature.discord;

import de.jcm.discordgamesdk.Core;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SDKInitializer {
    public static Logger LOGGER = LoggerFactory.getLogger("Tubion/Discord");
    public static void loadSDK() {
        LOGGER.info("[SDKInitializer] Loading Discord GameSDK v3.1.0");
        File home = new File("config/tubion/discord");
        if (!home.exists() && !home.mkdirs()) {
            throw new IllegalStateException("[SDKInitializer] For some reason, the filesystem did not let us create the Discord GameSDK folder :(");
        }
        String fileName;
        if (SystemUtils.IS_OS_WINDOWS)
            fileName = "discord_game_sdk.dll";
        else if (SystemUtils.IS_OS_MAC)
            fileName = "discord_game_sdk.dylib";
        else if (SystemUtils.IS_OS_LINUX)
            fileName = "discord_game_sdk.so";
        else
            throw new IllegalStateException("Cannot determine OS type: " + System.getProperty("os.name"));

        File sdk = new File("config/tubion/discord/" + fileName);
        File jni = new File("config/tubion/discord/" + (SystemUtils.IS_OS_WINDOWS ? "discord_game_sdk_jni.dll" : "libdiscord_game_sdk_jni" +
                (SystemUtils.IS_OS_MAC ? ".dylib" : ".so")));

        if (!sdk.exists()) {
            try {
                downloadSDK(sdk, fileName);
            } catch(IOException ex) {
                LOGGER.error("[SDKInitializer] Failed to download SDK from Discord: " + ex.getMessage());
            }
        }
        if (!jni.exists()) {
            try {
                extractJNI(jni);
            } catch(IOException ex) {
                LOGGER.error("[DiscordGameSDKLoader] Failed to extract JNI: " + ex.getMessage());
            }
        }
        if (sdk.exists() && jni.exists()) {
            loadNative(sdk, jni);
        }
    }
    private static void downloadSDK(File sdk, String fileName) throws IOException {
        String architecture = System.getProperty("os.arch").toLowerCase();
        if (architecture.equals("amd64")) architecture = "x86_64";

        URL downloadURL = new URL("https://dl-game-sdk.discordapp.net/3.1.0/discord_game_sdk.zip");
        URLConnection connection = downloadURL.openConnection();
        connection.setRequestProperty("User-Agent", "Tubion/discord");
        ZipInputStream zipStream = new ZipInputStream(connection.getInputStream());
        ZipEntry entry;
        while ((entry = zipStream.getNextEntry()) != null) {
            if (entry.getName().equals("lib/" + architecture + "/" + fileName)) {
                LOGGER.info("[DiscordGameSDKLoader Downloader] Successfully found SDK!");
                Files.copy(zipStream, sdk.toPath(), StandardCopyOption.REPLACE_EXISTING);
                break;
            }
            zipStream.closeEntry();
        }
        zipStream.close();
    }
    private static void extractJNI(File jni) throws IOException {
        String architecture = System.getProperty("os.arch").toLowerCase();
        if (architecture.equals("x86_64")) architecture = "amd64";
        String path = "/native/" + (SystemUtils.IS_OS_WINDOWS ? "windows" : (SystemUtils.IS_OS_MAC ? "macos" : "linux"))
                + "/" + architecture + "/" + (SystemUtils.IS_OS_WINDOWS ? "discord_game_sdk_jni.dll" : "libdiscord_game_sdk_jni" +
                (SystemUtils.IS_OS_MAC ? ".dylib" : ".so"));
        InputStream in = TubnetDiscordIntegration.class.getResourceAsStream(path);
        Files.copy(in, jni.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    private static void loadNative(File sdk, File jni) {
        if (SystemUtils.IS_OS_WINDOWS)
            System.load(sdk.getAbsolutePath());
        System.load(jni.getAbsolutePath());
        Core.initDiscordNative(sdk.getAbsolutePath());
        LOGGER.info("[SDKInitializer] Discord GameSDK Library 3.1.0 initialized");
    }
}
