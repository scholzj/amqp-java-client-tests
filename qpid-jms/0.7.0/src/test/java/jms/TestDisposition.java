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
public class TestDisposition {
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.qpid.jms.provider.amqp.FRAMES", "trace");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    @Test
    public void testAcceptDisposition() throws JMSException, NamingException, QmfException {
        Connection connection = Utils.getAdminConnectionBuilder().build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        // Clean the queue first
        GlobalUtils.purgeQueue(RTG_QUEUE);

        MessageProducer sender = session.createProducer(Utils.getQueue(RTG_QUEUE));
        Message msg = session.createMessage();
        sender.send(msg);

        // Acknowledge the message
        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);
        assertNotNull("Didn't received expected message", received);

        // JMS_AMQP_ACK_TYPE=1 ... Accept
        received.setIntProperty("JMS_AMQP_ACK_TYPE", 1);
        received.acknowledge();

        receiver.close();

        // Is the queue really empty?
        receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        received = receiver.receive(1000);
        assertNull("Received unexpected message", received);

        receiver.close();

        connection.close();
    }

    // TODO: Rejected messages are discarded by the Qpid broker
    /*@Test
    public void testRejectDisposition() throws JMSException, NamingException, QmfException {
        Connection connection = Utils.getAdminConnectionBuilder().option("amqp.traceFrames=true").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        // Clean the queue first
        GlobalUtils.purgeQueue(RTG_QUEUE);

        MessageProducer sender = session.createProducer(Utils.getQueue(RTG_QUEUE));
        Message msg = session.createMessage();
        sender.send(msg);

        // Acknowledge the message
        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);
        assertNotNull("Didn't received expected message", received);

        // JMS_AMQP_ACK_TYPE=2 ... Reject
        received.setIntProperty("JMS_AMQP_ACK_TYPE", 2);
        received.acknowledge();

        receiver.close();

        // Is the rejected message still there
        receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        received = receiver.receive(1000);
        assertNotNull("Didn't received rejected message", received);

        receiver.close();

        connection.close();
    }*/

    @Test
    public void testReleasedDisposition() throws JMSException, NamingException, QmfException {
        Connection connection = Utils.getAdminConnectionBuilder().option("amqp.traceFrames=true").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        // Clean the queue first
        GlobalUtils.purgeQueue(RTG_QUEUE);

        MessageProducer sender = session.createProducer(Utils.getQueue(RTG_QUEUE));
        Message msg = session.createMessage();
        sender.send(msg);

        // Acknowledge the message
        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);
        assertNotNull("Didn't received expected message", received);

        // JMS_AMQP_ACK_TYPE=3 ... Released
        received.setIntProperty("JMS_AMQP_ACK_TYPE", 3);
        received.acknowledge();

        // Was the release messages passed again to the same receiver?
        received = receiver.receive(1000);
        assertNotNull("Didn't received released message again", received);
        // TODO: Released messages are set as redelivered
        //assertEquals("Released message is set as redelivered on a new receiver", false, received.getJMSRedelivered());

        receiver.close();

        // Is the released message still there
        receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        received = receiver.receive(1000);
        assertNotNull("Didn't received released message", received);
        // TODO: Released messages are set as redelivered
        //assertEquals("Released message is set as redelivered on a new receiver", false, received.getJMSRedelivered());

        receiver.close();

        connection.close();
    }

    @Test
    public void testModifiedFailedDisposition() throws JMSException, NamingException, QmfException {
        Connection connection = Utils.getAdminConnectionBuilder().build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        // Clean the queue first
        GlobalUtils.purgeQueue(RTG_QUEUE);

        MessageProducer sender = session.createProducer(Utils.getQueue(RTG_QUEUE));
        Message msg = session.createMessage();
        sender.send(msg);

        // Acknowledge the message
        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);
        assertNotNull("Didn't received expected message", received);

        // JMS_AMQP_ACK_TYPE=4 ... Failed
        received.setIntProperty("JMS_AMQP_ACK_TYPE", 4);
        received.acknowledge();

        // Was the modified messages passed again to the same receiver?
        received = receiver.receive(1000);
        assertNotNull("Didn't received modified message again", received);
        assertEquals("Modified message is not set as redelivered on the same receiver", true, received.getJMSRedelivered());

        receiver.close();

        // Is the modified message still there
        receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        received = receiver.receive(1000);
        assertNotNull("Didn't received released message", received);
        assertEquals("Modified message is not set as redelivered on a new receiver", true, received.getJMSRedelivered());

        receiver.close();

        connection.close();
    }

    @Test
    public void testModifiedUndeliverableDisposition() throws JMSException, NamingException, QmfException {
        Connection connection = Utils.getAdminConnectionBuilder().build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        // Clean the queue first
        GlobalUtils.purgeQueue(RTG_QUEUE);

        MessageProducer sender = session.createProducer(Utils.getQueue(RTG_QUEUE));
        Message msg = session.createMessage();
        sender.send(msg);

        // Acknowledge the message
        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);
        assertNotNull("Didn't received expected message", received);

        // JMS_AMQP_ACK_TYPE=5 ... Undeliveryble
        received.setIntProperty("JMS_AMQP_ACK_TYPE", 5);
        received.acknowledge();

        // TODO: I don't think the message should be redelivered
        // Was the modified messages passed again to the same receiver?
        received = receiver.receive(1000);
        assertNotNull("Didn't received modified message again", received);
        assertEquals("Modified message is not set as redelivered on the same receiver", true, received.getJMSRedelivered());

        receiver.close();

        // Is the modified message still there
        receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        received = receiver.receive(1000);
        assertNotNull("Didn't received released message", received);
        assertEquals("Modified message is not set as redelivered on a new receiver", true, received.getJMSRedelivered());

        receiver.close();

        connection.close();
    }
}
