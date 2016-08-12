package io.github.syncsocket;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;

public class Connection extends Emitter {
    private Socket socket;
    private ConnectionListener listener;

    public Connection(String url, final ConnectionListener listener) throws URISyntaxException {
        this.listener = listener;
        IO.Options options = getSocketOptions();
        socket = IO.socket(url, options);
        bindSocketEvents();
    }

    public void connect() {
        socket.connect();
    }

    private static IO.Options getSocketOptions() {
        IO.Options options = new IO.Options();
        options.query = "instanceId=java_cli_0.1";
        options.path = "/syncsocket";
        return options;
    }

    private void bindSocketEvents() {
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
    }

}
