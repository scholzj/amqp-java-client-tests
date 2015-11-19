package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;

import javax.jms.*;
import javax.naming.NamingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.testng.Assert;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

public class LVQ extends BaseTest {

    private static final String LVQ_QUEUE = Settings.get("routing.lvq_queue");
    private static final String LVQ_KEY = Settings.get("routing.lvq_key");

    // Test the LVQ feature
    public void testLVQQueueBasic() throws JMSException, NamingException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(LVQ_QUEUE));

            Message lvq1 = session.createTextMessage("A");
            lvq1.setStringProperty(LVQ_KEY, "1");
            sender.send(lvq1);

            Message lvq2 = session.createTextMessage("B");
            lvq2.setStringProperty(LVQ_KEY, "1");
            sender.send(lvq2);

            Message lvq3 = session.createTextMessage("C");
            lvq3.setStringProperty(LVQ_KEY, "1");
            sender.send(lvq3);

            Message lvq4 = session.createTextMessage("D");
            lvq4.setStringProperty(LVQ_KEY, "1");
            sender.send(lvq4);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(LVQ_QUEUE));
            TextMessage received = (TextMessage) receiver.receive(1000);

            if (received == null) {
                Assert.fail("LVQ test received no messages");
            } else {
                received.acknowledge();
                Assert.assertEquals(received.getText(), "D", "LVQ test received unexpected message");
            }

            TextMessage received2 = (TextMessage) receiver.receive(1000);
            Assert.assertNull(received2, "Lvq test - expected to receive only one message, not two");
        }
    }

    // Test the LVQ feature
    public void testLVQQueueManyMessages() throws JMSException, NamingException {
        int MESSAGE_COUNT = 10000;

        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(LVQ_QUEUE));

            List<String> keys = new LinkedList<>();
            keys.add("A");
            keys.add("B");
            keys.add("C");
            keys.add("D");
            keys.add("E");
            keys.add("F");
            keys.add("G");
            keys.add("H");
            keys.add("I");
            keys.add("J");

            Random generator = new Random();

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                Message lvqMessage = session.createTextMessage("A");
                lvqMessage.setStringProperty(LVQ_KEY, keys.get(generator.nextInt(keys.size())));
                sender.send(lvqMessage);
            }

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(LVQ_QUEUE));
            TextMessage received = (TextMessage) receiver.receive(1000);

            while (received != null) {
                received.acknowledge();

                if (keys.contains(received.getStringProperty(LVQ_KEY))) {
                    keys.remove(received.getStringProperty(LVQ_KEY));
                } else {
                    Assert.fail("received message with wrong LVQ key" + received.getStringProperty(LVQ_KEY));
                }

                received = (TextMessage) receiver.receive(1000);
            }
            Assert.assertEquals(0, keys.size(), "Didn't receive all messages");
        }
    }

    // Test the LVQ feature
    public void testLVQQueueInTxn() throws JMSException, NamingException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(LVQ_QUEUE));

            Message lvq1 = session.createTextMessage("A");
            lvq1.setStringProperty(LVQ_KEY, "1");
            sender.send(lvq1);

            Message lvq2 = session.createTextMessage("B");
            lvq2.setStringProperty(LVQ_KEY, "1");
            sender.send(lvq2);

            Message lvq3 = session.createTextMessage("C");
            lvq3.setStringProperty(LVQ_KEY, "1");
            sender.send(lvq3);

            Message lvq4 = session.createTextMessage("D");
            lvq4.setStringProperty(LVQ_KEY, "1");
            sender.send(lvq4);

            session.commit();

            Session session2 = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session2.createConsumer(this.utils.getQueue(LVQ_QUEUE));
            TextMessage received = (TextMessage) receiver.receive(1000);

            if (received == null) {
                Assert.fail("LVQ test received no messages");
            } else {
                received.acknowledge();
                Assert.assertEquals("D", received.getText(), "LVQ test received unexpected message");
            }

            TextMessage received2 = (TextMessage) receiver.receive(1000);
            Assert.assertNull(received2, "LVQ test - expected to receive only one message, not two");
        }
    }
}
