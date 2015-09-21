package com.deutscheboerse.qpid.jms;

import com.deutscheboerse.configuration.Settings;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;

import static org.junit.Assert.assertNull;

/**
 * Created by schojak on 02.09.2015.
 */
public class TestExpiration {
    protected static int EXPIRATION_TIME = 500; // milliseconds
    protected static String TTL_QUEUE = Settings.get("routing.ttl_queue");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    // Test the sender rollback feature
    @Test
    public void testMessageExpiration() throws JMSException, NamingException, InterruptedException {


        Connection connection = Utils.getAdminConnection();
        connection.start();

        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getQueue(TTL_QUEUE));
        sender.setTimeToLive(EXPIRATION_TIME);
        sender.send(session.createMessage());

        Thread.sleep(2*EXPIRATION_TIME);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(TTL_QUEUE));
        Message received = receiver.receive(1000);
        assertNull("Received unexpected message", received);

        connection.close();
    }
}
