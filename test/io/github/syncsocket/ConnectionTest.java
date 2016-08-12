package io.github.syncsocket;

import junit.framework.TestCase;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

import org.json.JSONException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.net.URISyntaxException;

public class ConnectionTest {

    private static final String SERVER_URL = "http://localhost:3000";
    private static final String CHANNEL_ID = "myAudioSystem";

    @Test(expected = URISyntaxException.class)
    public void testConnectionInvalidUrl() throws Exception {
        Connection c = new Connection("#@$%@", new ConnectionListener() {
            public void onConnected() {}
            public void onConnectionError() {}
            public void onConnectionTimeout() {}
            public void onConnecting() {}
            public void onDisconnected() {}
        });
        fail();
    }

    @Test
    public void testConnectLocalhost() throws Exception {
        ConnectionListener listener = mock(ConnectionListener.class);
        Connection c = new Connection(SERVER_URL, listener);
        c.connect();
        verify(listener, after(250).atLeastOnce()).onConnecting();
        verify(listener, after(500).times(1)).onConnected();
    }

    @Test
    public void testConnectToInvalidHost() throws Exception {
        ConnectionListener listener = mock(ConnectionListener.class);
        Connection c = new Connection("http://localhost:3001", listener);
        c.connect();
        verify(listener, after(250).atLeastOnce()).onConnecting();
        verify(listener, after(500).atLeastOnce()).onConnectionError();
    }

    @Test
    public void testCloseDisconnects() throws Exception {
        final Connection c = new Connection(SERVER_URL);
        c.connect();
        Thread.sleep(200);
        c.close();
        Thread.sleep(200);
        assertFalse(c.connected());
    }

    @Test
    public void testJoinChannel() throws Exception {
        final Connection.ChannelJoinListener joinListener = mock(Connection.ChannelJoinListener.class);
        final Connection c = new Connection(SERVER_URL);
        c.setListener(new OnConnectedListener() {
            public void onConnected() {
                try {
                    c.joinChannel(CHANNEL_ID, true, joinListener);
                } catch (JSONException e) {
                    fail();
                }
            }
        });
        c.connect();
        ArgumentCaptor<Channel> arg = ArgumentCaptor.forClass(Channel.class);
        verify(joinListener, after(1000).times(1)).joinSuccess(arg.capture());
        assertEquals(CHANNEL_ID, arg.getValue().getChannelId());
    }

}
