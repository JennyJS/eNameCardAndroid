package com.scu.jenny.enamecard.network;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by manhong on 3/14/16.
 */
public class SocketManager {
    private Socket socket;

    private static SocketManager singleton;

    private SocketManager() {
        try {
            socket = IO.socket(HttpHelper.domain);

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.w("SocketIO", "Server close connection");
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static SocketManager getInstance() {
        if (singleton == null) {
            singleton = new SocketManager();
        }

        return singleton;
    }

    public static void registerListenerOnChannel(String channel, Emitter.Listener listener) {
        synchronized (singleton.socket) {
            singleton.socket.on(channel, listener);
        }
    }

    public static void unRegisterListenerFromChannel(String channel) {
        synchronized (singleton.socket) {
            singleton.socket.off(channel);
        }
    }

    public void connect() {
        this.socket.connect();
    }

    public void disconnect() {
        this.socket.disconnect();
    }

    public void emit(String channel, JSONObject jsonObject) {
        this.emit(channel, jsonObject.toString());
    }

    public void emit(String channel, String msg) {
        this.socket.emit(channel, msg);
    }
}
