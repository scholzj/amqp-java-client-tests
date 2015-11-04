package jms;

import com.deutscheboerse.configuration.Settings;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.NamingException;
import utils.AutoCloseableConnection;

public class Misc extends BaseTest {
    private static final String USER1_USERNAME = Settings.get("user1.username");
    private static final String USER1_PASSWORD = Settings.get("user1.password");

    // Test the sender rollback feature
    public void testDuplicateClientID() throws JMSException, NamingException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).clientID("myTestClient").build();
             AutoCloseableConnection connection2 = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).clientID("myTestClient").build()) {
        connection.start();
        connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        connection2.start();
        connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        }
    }
}
