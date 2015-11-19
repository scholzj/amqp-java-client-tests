package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.NamingException;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

public class Heartbeat extends BaseTest {
    private static final int IDLE_TIMEOUT = 1000; //milliseconds
    private static final long WAIT_TIME = 5000; //milliseconds
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");
    
    // Test the idle timeout
    public void testHeartbeat() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder()
                .brokerOption(String.format("amqp.idleTimeout=%d", IDLE_TIMEOUT)).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1);
            Thread.sleep(WAIT_TIME);
            receiver.receive(1);
        }
    }
}
