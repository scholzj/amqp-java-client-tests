package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;
import org.apache.qpid.qmf2.common.QmfException;
import org.testng.Assert;

import javax.jms.*;
import javax.naming.NamingException;

public class Queueing extends BaseTest {
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");
    private static final String RTG_TOPIC = Settings.get("routing.rtg_topic");
    private static final String RTG_ROUTING_KEY = Settings.get("routing.rtg_routing_key");
    private static final String DLQ_QUEUE = Settings.get("routing.dlq_queue");
    private static final String DLQ_TOPIC = Settings.get("routing.dlq_topic");
    private static final String DLQ_ROUTING_KEY = Settings.get("routing.dlq_routing_key");
    private static final String RING_QUEUE = Settings.get("routing.ring_queue");
    private static final String SMALL_QUEUE = Settings.get("routing.small_queue");

    public void testRoutingKey() throws JMSException, NamingException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            MessageProducer sender = session.createProducer(this.utils.getTopic(RTG_TOPIC));
            Message msg = session.createMessage();
            msg.setJMSType(RTG_ROUTING_KEY);
            sender.send(msg);
            
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(5000);
            received.acknowledge();
            Assert.assertNotNull(received, "Didn't receive expected message");
            receiver.close();
            session.close();
        }
    }

    public void testDeadLetterQueue() throws JMSException, NamingException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            MessageProducer sender = session.createProducer(this.utils.getTopic(DLQ_TOPIC));
            Message msg = session.createMessage();
            msg.setJMSType(DLQ_ROUTING_KEY);
            sender.send(msg);
            
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(DLQ_QUEUE));
            Message received = receiver.receive(1000);
            received.acknowledge();
            Assert.assertNotNull(received, "Didn't receive expected message");
            receiver.close();
            session.close();
        }
    }

    public void testRingQueue() throws JMSException, NamingException {
        String messagesToBeSent[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String messagesToBeReceived[] = {"F", "G", "H", "I", "J"};
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().syncPublish(Boolean.TRUE).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(RING_QUEUE));
            for (String message : messagesToBeSent) {
                sender.send(session.createTextMessage(message));
            }
            
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RING_QUEUE));
            int index = 1;
            for (String expectedMessage : messagesToBeReceived) {
                Message message = receiver.receive(1000);
                message.acknowledge();
                Assert.assertEquals(expectedMessage, ((TextMessage) message).getText(), "Unexpected " + index + ". " + "message");
                index++;
            }
            Assert.assertNull(receiver.receive(1000), "Unexpected sixth message");
            receiver.close();
            session.close();
        }
    }

    // Works only in 0.6.0 and higher
    public void testFullQueue() throws JMSException, NamingException, InterruptedException, QmfException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().syncPublish(Boolean.TRUE).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            MessageProducer sender = session.createProducer(this.utils.getQueue(SMALL_QUEUE));
            
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
                    Assert.fail("Sent unexpected sixth message");
                }
                catch (JMSException expected)
                {
                    // pass
                }
            }
            catch (JMSException e)
            {
                Assert.fail("Didn't manage to send 5 messages");
            }
        }
    }
}
