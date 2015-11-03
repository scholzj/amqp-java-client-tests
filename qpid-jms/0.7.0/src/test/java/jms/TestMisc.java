package jms;

import com.deutscheboerse.configuration.Settings;
import com.deutscheboerse.utils.GlobalUtils;
import org.apache.qpid.qmf2.common.QmfException;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by schojak on 02.09.2015.
 */
public class TestMisc {
    private static final String USER1_USERNAME = Settings.get("user1.username");
    private static final String USER1_PASSWORD = Settings.get("user1.password");

    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    // Test the sender rollback feature
    @Test
    public void testDuplicateClientID() throws JMSException, NamingException {
        Connection connection = Utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).clientID("myTestClient").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Connection connection2 = Utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).clientID("myTestClient").build();
        connection2.start();
        Session session2 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        session.close();
        connection.close();
        session2.close();
        connection2.close();
    }

    @Test
    public void testMessageIDFormatUUID() throws JMSException, NamingException, QmfException {
        GlobalUtils.purgeQueue(RTG_QUEUE);

        Connection connection = Utils.getAdminConnectionBuilder().option("jms.forceAsyncSend=True").option("jms.messageIDType=UUID").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(RTG_QUEUE));
        Message msg = session.createMessage();
        sender.send(msg);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);
        assertNotNull("Didn't receive expected message", received);
        assertEquals("The Message IDs are different", msg.getJMSMessageID(), received.getJMSMessageID());

        connection.close();
    }

    @Test
    public void testMessageIDFormatUUIDString() throws JMSException, NamingException, QmfException {
        GlobalUtils.purgeQueue(RTG_QUEUE);

        Connection connection = Utils.getAdminConnectionBuilder().option("jms.forceAsyncSend=True").option("jms.messageIDType=UUID_STRING").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(RTG_QUEUE));
        Message msg = session.createMessage();
        sender.send(msg);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);
        assertNotNull("Didn't receive expected message", received);
        assertEquals("The Message IDs are different", msg.getJMSMessageID(), received.getJMSMessageID());

        connection.close();
    }
}
