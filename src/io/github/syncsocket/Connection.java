package io.github.syncsocket;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class Connection extends Emitter {
    private Socket socket;
    private ConnectionListener listener = null;

    public Connection(String url) throws URISyntaxException {
        this(url, null);
    }

    public Connection(String url, final ConnectionListener listener) throws URISyntaxException {
        this.listener = listener;
        IO.Options options = getSocketOptions();
        socket = IO.socket(url, options);
        bindSocketEvents();
    }

    public void setListener(ConnectionListener connectionListener) {
        this.listener = connectionListener;
        this.bindSocketEvents();
    }

    public void connect() {
        socket.connect();
    }

    public void close() {
        socket.disconnect();
    }

    private static IO.Options getSocketOptions() {
        IO.Options options = new IO.Options();
        options.query = "instanceId=java_cli_0.1";
        options.path = "/syncsocket";
        return options;
    }

    private void bindSocketEvents() {
        if (this.listener != null) {
            socket.on(Socket.EVENT_CONNECTING, new Listener() {
                public void call(Object... objects) {
                    listener.onConnecting();
                }
            });
            socket.on(Socket.EVENT_CONNECT, new Listener() {
                public void call(Object... objects) {
                    listener.onConnected();
                }
            });
            socket.on(Socket.EVENT_CONNECT_ERROR, new Listener() {
                public void call(Object... objects) {
                    listener.onConnectionError();
                }
            });
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Listener() {
                public void call(Object... objects) {
                    listener.onConnectionTimeout();
                }
            });
            socket.on(Socket.EVENT_DISCONNECT, new Listener() {
                public void call(Object... objects) {
                    listener.onDisconnected();
                }
            });
        }
    }

    public boolean connected() {
        return this.socket.connected();
    }

    public void joinChannel(final String channelId, boolean canPublish, final ChannelJoinListener listener) throws JSONException {
        JSONObject channelDesc = new JSONObject();
        channelDesc.put("channelId", channelId);
        channelDesc.put("canPublish", canPublish);
        JSONObject requestData = new JSONObject();
        requestData.put("what", "join_channel");
        requestData.put("body", channelDesc);
        final Connection c = this;
        this.socket.emit("request", requestData, new Ack() {
            public void call(Object... objects) {
                if (objects.length == 0) {
                    Channel ch = new Channel(c, channelId);
                    listener.joinSuccess(ch);
                } else {
                    JSONObject error = (JSONObject) objects[0];
                    listener.joinFailed(error.toString());
                }
            }
        });
    }

    public interface ChannelJoinListener {
        void joinSuccess(Channel channel);
        void joinFailed(String reason);
    }
}
