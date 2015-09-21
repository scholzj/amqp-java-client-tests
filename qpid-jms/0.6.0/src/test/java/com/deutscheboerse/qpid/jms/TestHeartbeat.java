package com.deutscheboerse.qpid.jms;

import com.deutscheboerse.configuration.Settings;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;

/**
 * Created by schojak on 02.09.2015.
 */
public class TestHeartbeat {
    protected static Integer IDLE_TIMEOUT = 1000; //milliseconds
    protected static Integer WAIT_TIME = 5000; //milliseconds
    protected static String RTG_QUEUE = Settings.get("routing.rtg_queue");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    // Test the idle timeout
    @Test
    public void testIdleTimeout() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getAdminConnection("amqp.idleTimeout=" + IDLE_TIMEOUT);
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive();

        Thread.sleep(WAIT_TIME);

        Message received2 = receiver.receive();

        session.close();
        connection.close();
    }
}
