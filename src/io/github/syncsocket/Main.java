package io.github.syncsocket;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws URISyntaxException {
        Connection c = new Connection("http://localhost:3000", new ConnectionListener() {
            public void onConnected() {
                System.out.println("connected");
            }

            public void onConnectionError() {

            }

            public void onConnectionTimeout() {

            }

            public void onConnecting() {
                System.out.println("connecting");
            }
        });
        c.connect();
    }
}
