package com.deutscheboerse.qpid.jms;

import com.deutscheboerse.configuration.Settings;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by schojak on 02.09.2015.
 */
public class TestLVQ {
    private static final String LVQ_QUEUE = Settings.get("routing.lvq_queue");
    private static final String LVQ_KEY = Settings.get("routing.lvq_key");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    // Test the LVQ feature
    @Test
    public void testLVQQueueBasic() throws JMSException, NamingException {
        Connection connection = Utils.getAdminConnectionBuilder().option("jms.validatePropertyNames=False").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(LVQ_QUEUE));

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

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(LVQ_QUEUE));
        TextMessage received = (TextMessage)receiver.receive(1000);

        if (received == null)
        {
            fail("LVQ test received no messages");
        }
        else {
            received.acknowledge();
            assertEquals("LVQ test received unexpected message", received.getText(), "D");
        }

        TextMessage received2 = (TextMessage)receiver.receive(1000);
        assertNull("Lvq test - expected to receive only one message, not two", received2);

        session.close();
        connection.close();
    }

    // Test the LVQ feature
    @Test
    public void testLVQQueueManyMessages() throws JMSException, NamingException {
        int MESSAGE_COUNT = 10000;

        Connection connection = Utils.getAdminConnectionBuilder().option("jms.forceAsyncSend=True").option("jms.validatePropertyNames=False").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(LVQ_QUEUE));

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

        for (int i = 0; i < MESSAGE_COUNT; i++)
        {
            Message lvqMessage = session.createTextMessage("A");
            lvqMessage.setStringProperty(LVQ_KEY, keys.get(generator.nextInt(keys.size())));
            sender.send(lvqMessage);
        }

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(LVQ_QUEUE));
        TextMessage received = (TextMessage)receiver.receive(1000);

        while(received != null) {
            received.acknowledge();

            if (keys.contains(received.getStringProperty(LVQ_KEY)))
            {
                keys.remove(received.getStringProperty(LVQ_KEY));
            }
            else
            {
                fail("received message with wrong LVQ key" + received.getStringProperty(LVQ_KEY));
            }

            received = (TextMessage)receiver.receive(1000);
        }
        assertEquals("Didn't received all messages", 0, keys.size());

        session.close();
        connection.close();
    }

    // Test the LVQ feature
    @Test
    public void testLVQQueueInTxn() throws JMSException, NamingException {
        Connection connection = Utils.getAdminConnectionBuilder().option("jms.validatePropertyNames=False").build();
        connection.start();
        Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(LVQ_QUEUE));

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
        MessageConsumer receiver = session2.createConsumer(Utils.getQueue(LVQ_QUEUE));
        TextMessage received = (TextMessage)receiver.receive(1000);

        if (received == null)
        {
            fail("LVQ test received no messages");
        }
        else {
            received.acknowledge();
            assertEquals("LVQ test received unexpected message", "D", received.getText());
        }

        TextMessage received2 = (TextMessage)receiver.receive(1000);
        assertNull("LVQ test - expected to receive only one message, not two", received2);

        session.close();
        session2.close();
        connection.close();
    }
}
