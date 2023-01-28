package io.github.apricotfarmer11.mods.tubion.core.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;
import io.github.apricotfarmer11.mods.tubion.TubionMod;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.parser.Base64;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.PublicKey;

public class SocketCore {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger("Tubion/Socket");
    public static final String API_URL = "s://tubion-api.minestadon.net"; // FabricLoader.getInstance().isDevelopmentEnvironment() ? "localhost:3000" : "tubion-api.minestadon.net";
    private static final String WEBSOCKET_URL = "http" + API_URL;
    public static Socket socket;
    private boolean authenticated = false;
    public SocketCore() throws URISyntaxException {
        Socket socket = IO.socket(WEBSOCKET_URL);
        LOGGER.info("Created connection!");
        this.socket = socket;
        socket.on("message", packet -> {
            LOGGER.debug("TUBION SOCKET PACKET: " + packet[0].toString());
            JsonElement object = JsonParser.parseString(packet[0].toString());
            switch(object.getAsJsonObject().get("code").getAsString()) {
                case "START_AUTH": {
                    if (authenticated) {
                        LOGGER.info("Invalid packet sent! Closing Socket..");
                        socket.close();
                        return;
                    }
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("username", CLIENT.getSession().getUsername());
                    jsonObject.addProperty("mod_version", TubionMod.VERSION);

                    socket.emit("auth0", jsonObject.toString());
                    break;
                }
                case "AUTH0_REPLY": {
                    SecretKey secretKey;
                    PublicKey publicKey;
                    String string;
                    try {
                       secretKey = NetworkEncryptionUtils.generateKey();
                       publicKey = NetworkEncryptionUtils.readEncodedPublicKey(Base64.decode(object.getAsJsonObject().get("publicKey").getAsString(), Base64.DEFAULT));
                       string = (new BigInteger(NetworkEncryptionUtils.generateServerId(object.getAsJsonObject().get("serverId").getAsString(), publicKey, secretKey))).toString(16);
                    } catch (NetworkEncryptionException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        CLIENT.getSessionService().joinServer(CLIENT.getSession().getProfile(), CLIENT.getSession().getAccessToken(), string);
                    } catch (AuthenticationException e) {
                        throw new RuntimeException(e);
                    }
                    byte[] secretEncodedKey;
                    try {
                        secretEncodedKey = NetworkEncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
                    } catch (NetworkEncryptionException e) {
                        throw new RuntimeException(e);
                    }

                    JsonObject object1 = new JsonObject();
                    object1.addProperty("sharedSecret", Base64.encodeToString(secretEncodedKey, Base64.DEFAULT));
                    socket.emit("auth1", object1);
                    break;
                }
                default: {
                    break;
                }
            }
        });
        socket.on("disconnect", (reason) -> {
            LOGGER.info("Disconnected from socket: " + reason[0].toString());
        });
        socket.connect();
    }
}
