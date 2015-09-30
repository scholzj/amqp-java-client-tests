package com.deutscheboerse.qpid.jms;

import com.deutscheboerse.configuration.Settings;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.NamingException;

/**
 * Created by schojak on 02.09.2015.
 */
public class TestHeartbeat {
    private static final Integer IDLE_TIMEOUT = 1000; //milliseconds
    private static final Integer WAIT_TIME = 5000; //milliseconds
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");

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
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1);

        Thread.sleep(WAIT_TIME);

        receiver.receive(1);

        session.close();
        connection.close();
    }
}
