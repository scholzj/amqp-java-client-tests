package jms;

import com.deutscheboerse.configuration.Settings;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by schojak on 02.09.2015.
 */
public class TestFiltering {
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    @Test
    public void testCorrelationIDFilteringJMSStyle() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getAdminConnectionBuilder().build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        String correlationID = UUID.randomUUID().toString();

        MessageProducer sender = session.createProducer(utils.Utils.getQueue(RTG_QUEUE));
        Message msg = session.createMessage();
        msg.setJMSCorrelationID(correlationID);
        sender.send(msg);

        MessageConsumer receiver = session.createConsumer(utils.Utils.getQueue(RTG_QUEUE), "JMSCorrelationID = '" + UUID.randomUUID().toString() + "'");
        Message notRcvMsg = receiver.receive(1000);

        assertNull("Received unexpected message", notRcvMsg);

        MessageConsumer receiver2 = session.createConsumer(utils.Utils.getQueue(RTG_QUEUE), "JMSCorrelationID = '" + correlationID + "'");
        Message rcvMsg = receiver2.receive(1000);

        assertNotNull("Didn't received expected message", rcvMsg);
        assertEquals("CorrelationID is wrong", correlationID, rcvMsg.getJMSCorrelationID());

        rcvMsg.acknowledge();

        session.close();
        connection.close();
    }

    @Test
    public void testPropertiesFilteringWithPeriod() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getAdminConnectionBuilder().build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        String key = UUID.randomUUID().toString();

        MessageProducer sender = session.createProducer(Utils.getQueue(RTG_QUEUE));
        Message msg = session.createMessage();
        msg.setStringProperty("filter.test", key);
        sender.send(msg);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE), "\"filter.test\" = '" + UUID.randomUUID().toString() + "'");
        Message notRcvMsg = receiver.receive(1000);

        assertNull("Received unexpected message", notRcvMsg);

        MessageConsumer receiver2 = session.createConsumer(Utils.getQueue(RTG_QUEUE), "\"filter.test\" = '" + key + "'");
        Message rcvMsg = receiver2.receive(1000);

        assertNotNull("Didn't received expected message", rcvMsg);
        assertEquals("Key is wrong", key, rcvMsg.getStringProperty("filter.test"));

        rcvMsg.acknowledge();

        session.close();
        connection.close();
    }

    @Test
    public void testPropertiesFilteringWithoutPeriod() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getAdminConnectionBuilder().build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        String key = UUID.randomUUID().toString();

        MessageProducer sender = session.createProducer(Utils.getQueue(RTG_QUEUE));
        Message msg = session.createMessage();
        msg.setStringProperty("filterTest", key);
        sender.send(msg);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE), "filterTest = '" + UUID.randomUUID().toString() + "'");
        Message notRcvMsg = receiver.receive(1000);

        assertNull("Received unexpected message", notRcvMsg);

        MessageConsumer receiver2 = session.createConsumer(Utils.getQueue(RTG_QUEUE), "filterTest = '" + key + "'");
        Message rcvMsg = receiver2.receive(1000);

        assertNotNull("Didn't received expected message", rcvMsg);
        assertEquals("Key is wrong", key, rcvMsg.getStringProperty("filterTest"));

        rcvMsg.acknowledge();

        session.close();
        connection.close();
    }
}
