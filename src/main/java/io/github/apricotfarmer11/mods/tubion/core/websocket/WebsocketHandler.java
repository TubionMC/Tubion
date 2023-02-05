package io.github.apricotfarmer11.mods.tubion.core.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;
import io.socket.engineio.parser.Base64;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PublicKey;

public class WebsocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("Tubion/WS");
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final String API_URL = "s://tubion-api-cf.minestadon.net/v0"; // "s://tubion-api.minestadon.net/v0/websocket"; // FabricLoader.getInstance().isDevelopmentEnvironment() ? "localhost:3000" : "tubion-api.minestadon.net";
    private static final String WEBSOCKET_URL = "ws" + API_URL + "/websocket";

    public WebSocketClient client;
    private boolean authorised = false;
    public static String API_TOKEN;
    public WebsocketHandler() throws URISyntaxException {
        // Create socket
        LOGGER.info("Creating connection");
        connect();
    }
    private void connect() {
        try {
            client = new WebSocketClient(new URI(WEBSOCKET_URL), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    LOGGER.info("Connected to WebSocket server!");
                    JsonObject obj = new JsonObject();
                    obj.addProperty("startAuth", true);
                    obj.addProperty("username", CLIENT.getSession().getUsername());
                    obj.addProperty("uuid", CLIENT.getSession().getUuid().toString());

                    client.send(obj.toString());
                }

                @Override
                public void onMessage(String message) {
                    LOGGER.info("Packet: " + message);
                    JsonElement object = JsonParser.parseString(message);
                    if (authorised) {
                        switch (object.getAsJsonObject().get("code").getAsString()) {
                            case "AUTHENTICATED": {
                                API_TOKEN = object.getAsJsonObject().get("token").getAsString();
                            }
                        }
                    } else {
                        if (object.getAsJsonObject().get("code").getAsString() == "INIT") return;
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

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("finishAuth", true);
                        jsonObject.addProperty("sharedSecret", Base64.encodeToString(secretEncodedKey, Base64.DEFAULT));

                        client.send(jsonObject.toString());
                        authorised = true;
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    if (remote) {
                        this.connect();
                    }
                }

                @Override
                public void onError(Exception ex) {

                }
            };
            client.connect();
        } catch(URISyntaxException ex) {
            // Ignore
        }
    }
}
