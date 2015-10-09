package jms;

import com.deutscheboerse.configuration.Settings;
import com.deutscheboerse.utils.GlobalUtils;
import org.apache.qpid.qmf2.common.QmfException;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;

import static org.junit.Assert.*;

/**
 * Created by schojak on 02.09.2015.
 */
public class TestQueueing {
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");
    private static final String RTG_TOPIC = Settings.get("routing.rtg_topic");
    private static final String RTG_ROUTING_KEY = Settings.get("routing.rtg_routing_key");
    private static final String DLQ_QUEUE = Settings.get("routing.dlq_queue");
    private static final String DLQ_TOPIC = Settings.get("routing.dlq_topic");
    private static final String DLQ_ROUTING_KEY = Settings.get("routing.dlq_routing_key");
    private static final String RING_QUEUE = Settings.get("routing.ring_queue");
    private static final String SMALL_QUEUE = Settings.get("routing.small_queue");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    @Test
    public void testRoutingKey() throws JMSException, NamingException {
        Connection connection = Utils.getAdminConnectionBuilder().build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getTopic(RTG_TOPIC));
        Message msg = session.createMessage();
        msg.setJMSType(RTG_ROUTING_KEY);
        sender.send(msg);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);
        assertNotNull("Didn't received expected message", received);

        session.close();
        connection.close();
    }

    @Test
    public void testDeadLetterQueue() throws JMSException, NamingException {
        Connection connection = Utils.getAdminConnectionBuilder().build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getTopic(DLQ_TOPIC));
        Message msg = session.createMessage();
        msg.setJMSType(DLQ_ROUTING_KEY);
        sender.send(msg);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(DLQ_QUEUE));
        Message received = receiver.receive(1000);
        assertNotNull("Didn't received expected message", received);

        session.close();
        connection.close();
    }

    @Test
    public void testRingQueue() throws JMSException, NamingException {
        Connection connection = Utils.getAdminConnectionBuilder().connectionOption("sync_publish='all'").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(RING_QUEUE));
        sender.send(session.createTextMessage("A"));
        sender.send(session.createTextMessage("B"));
        sender.send(session.createTextMessage("C"));
        sender.send(session.createTextMessage("D"));
        sender.send(session.createTextMessage("E"));
        sender.send(session.createTextMessage("F"));
        sender.send(session.createTextMessage("G"));
        sender.send(session.createTextMessage("H"));
        sender.send(session.createTextMessage("I"));
        sender.send(session.createTextMessage("J"));

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RING_QUEUE));

        assertEquals("Unexpected first message", "F", ((TextMessage) receiver.receive(1000)).getText());
        assertEquals("Unexpected second message", "G", ((TextMessage)receiver.receive(1000)).getText());
        assertEquals("Unexpected third message", "H", ((TextMessage)receiver.receive(1000)).getText());
        assertEquals("Unexpected fourth message", "I", ((TextMessage)receiver.receive(1000)).getText());
        assertEquals("Unexpected fifth message", "J", ((TextMessage)receiver.receive(1000)).getText());
        assertNull("Unexpected sixth message", receiver.receive(1000));

        session.close();
        connection.close();
    }

    // Works only in 0.6.0 and higher
    @Test
    public void testFullQueue() throws JMSException, NamingException, InterruptedException, QmfException {
        Connection connection = Utils.getAdminConnectionBuilder().connectionOption("sync_publish='all'").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        // Clean the queue first
        GlobalUtils.purgeQueue(SMALL_QUEUE);

        MessageProducer sender = session.createProducer(Utils.getQueue(SMALL_QUEUE));

        try
        {
            sender.send(session.createMessage());
            sender.send(session.createMessage());
            sender.send(session.createMessage());
            sender.send(session.createMessage());
            sender.send(session.createMessage());

            try
            {
                sender.send(session.createMessage());
                fail("Sent unexpected sixth message");
            }
            catch (JMSException e)
            {
                // pass
            }
        }
        catch (JMSException e)
        {
            fail("Didn't managed to send 5 messages");
        }

        session.close();
        connection.close();
    }
}
