package io.github.syncsocket;

public class Channel {
    private Connection connection;
    private String channelId;

    public Channel(Connection connection, String channelId) {
        this.connection = connection;
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }
}
