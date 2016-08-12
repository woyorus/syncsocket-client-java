package io.github.syncsocket;

import junit.framework.TestCase;
import static org.mockito.Mockito.*;
import org.junit.Test;

import java.net.URISyntaxException;

import static junit.framework.TestCase.fail;

public class ConnectionTest {

    @Test(expected = URISyntaxException.class)
    public void testConnectionInvalidUrl() throws Exception {
        Connection c = new Connection("#@$%@", new ConnectionListener() {
            public void onConnected() {}
            public void onConnectionError() {}
            public void onConnectionTimeout() {}
            public void onConnecting() {}
        });
        fail();
    }

    @Test
    public void testConnectLocalhost() throws Exception {
        ConnectionListener listener = mock(ConnectionListener.class);
        Connection c = new Connection("http://localhost:3000", listener);
        c.connect();
        verify(listener, after(500).atLeastOnce()).onConnecting();
        verify(listener, after(1000).times(1)).onConnected();
    }
}
