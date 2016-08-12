package io.github.syncsocket;

public interface ConnectionListener {
    void onConnected();
    void onConnectionError();
    void onConnectionTimeout();
    void onConnecting();
    void onDisconnected();
}
