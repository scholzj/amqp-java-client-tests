package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

import javax.jms.*;
import javax.naming.NamingException;

import static org.testng.AssertJUnit.assertNull;

public class Expiration extends BaseTest {

    private static final int EXPIRATION_TIME = 500; // milliseconds
    private static final String TTL_QUEUE = Settings.get("routing.ttl_queue");

    // Test the sender rollback feature
    public void testMessageExpiration() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getQueue(TTL_QUEUE));
            sender.setTimeToLive(EXPIRATION_TIME);
            sender.send(session.createMessage());

            Thread.sleep(2 * EXPIRATION_TIME);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(TTL_QUEUE));
            Message received = receiver.receive(1000);
            assertNull("Received unexpected message", received);
            receiver.close();
            session.close();
        }
    }
}
