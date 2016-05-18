package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.NamingException;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

public class Heartbeat extends BaseTest {
    private static final Integer HEARTBEAT = 1; //seconds
    private static final Integer WAIT_TIME = 5000; //milliseconds
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");
    
    // Test the idle timeout
    public void testHeartbeat() throws JMSException, NamingException, InterruptedException {
       System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
       System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
       System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
       System.setProperty("slf4j.logger.org.apache.qpid", "trace");


        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().brokerOption("heartbeat='" + HEARTBEAT.toString() + "'").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1);
            Thread.sleep(WAIT_TIME);
            receiver.receive(1);
        }
    }
}
