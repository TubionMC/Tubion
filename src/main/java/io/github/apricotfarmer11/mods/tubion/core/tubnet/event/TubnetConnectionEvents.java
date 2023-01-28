package io.github.apricotfarmer11.mods.tubion.core.tubnet.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class TubnetConnectionEvents {
    public static final Event<LoginStart> INIT = EventFactory.createArrayBacked(LoginStart.class, callbacks -> () -> {
        for (LoginStart callback : callbacks) {
            callback.onLoginStart();
        }
    });
    public static final Event<LoginEnd> LOGIN_ERROR = EventFactory.createArrayBacked(LoginEnd.class, callbacks -> () -> {
        for (LoginEnd callback : callbacks) {
            callback.onLoginEnd();
        }
    });
    public static final Event<Connect> CONNECT = EventFactory.createArrayBacked(Connect.class, callbacks -> () -> {
        for (Connect callback : callbacks) {
            callback.onConnect();
        }
    });
    public static final Event<Disconnect> DISCONNECT = EventFactory.createArrayBacked(Disconnect.class, callbacks -> () -> {
        for (Disconnect callback : callbacks) {
            callback.onDisconnect();
        }
    });
    public interface LoginStart {
        void onLoginStart();
    }
    public interface LoginEnd {
        void onLoginEnd();
    }
    public interface Connect {
        void onConnect();
    }
    public interface Disconnect {
        void onDisconnect() throws NoSuchFieldException, IllegalAccessException;
    }
}
