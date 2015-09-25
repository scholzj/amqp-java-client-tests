package com.deutscheboerse.qpid.jms; /**
 * Created by schojak on 02.09.2015.
 */

import com.deutscheboerse.configuration.Settings;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;

import static org.junit.Assert.fail;

public class TestSecurity {
    private static final String USER1_USERNAME = Settings.get("user1.username");
    private static final String USER1_PASSWORD = Settings.get("user1.password");

    private static final String USER2_USERNAME = Settings.get("user2.username");
    private static final String USER2_PASSWORD = Settings.get("user2.password");

    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");
    private static final String RTG_TOPIC = Settings.get("routing.rtg_topic");
    private static final String RTG_ROUTING_KEY = Settings.get("routing.rtg_routing_key");
    private static final String FORBIDDEN_QUEUE = Settings.get("routing.forbidden_queue");
    private static final String FORBIDDEN_TOPIC = Settings.get("routing.forbidden_topic");
    private static final String FORBIDDEN_ROUTING_KEY = Settings.get("routing.forbidden_routing_key");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    @Test
     public void testAuthenticationDefault() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnection(USER1_USERNAME, USER1_PASSWORD);
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test
    public void testAuthenticationPlain() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnection(USER1_USERNAME, USER1_PASSWORD, "amqp.saslMechanisms=PLAIN");
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test
    public void testAuthenticationCramMD5() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnection(USER1_USERNAME, USER1_PASSWORD, "amqp.saslMechanisms=CRAM-MD5");
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testAuthenticationAnonymous() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnection("amqp://cbgd03.xeop.de:21234?amqp.saslMechanisms=ANONYMOUS");
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testWrongPassword() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnection(USER1_USERNAME, "wrongpassword");
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testWrongUsername() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnection("nonexistentuser", "mypassword");
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testACLDeniedConsumer() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnection(USER1_USERNAME, USER1_PASSWORD);
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(FORBIDDEN_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testACLDeniedProducerForbiddenTopic() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnection(USER1_USERNAME, USER1_PASSWORD);
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getTopic(FORBIDDEN_TOPIC));
        Message msg = session.createMessage();
        msg.setJMSType(RTG_ROUTING_KEY);
        sender.send(msg);

        session.close();
        connection.close();
    }

    @Test
    public void testMaximumAllowedConnections() throws JMSException, NamingException, InterruptedException {
        try {
            Connection connection = Utils.getConnection(USER2_USERNAME, USER2_PASSWORD);
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Connection connection2 = Utils.getConnection(USER2_USERNAME, USER2_PASSWORD);
            connection2.start();
            Session session2 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Connection connection3 = Utils.getConnection(USER2_USERNAME, USER2_PASSWORD);
            connection3.start();
            Session session3 = connection3.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Connection connection4 = Utils.getConnection(USER2_USERNAME, USER2_PASSWORD);
            connection4.start();
            Session session4 = connection4.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Connection connection5 = Utils.getConnection(USER2_USERNAME, USER2_PASSWORD);
            connection5.start();
            Session session5 = connection5.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            try
            {
                Connection connection6 = Utils.getConnection(USER2_USERNAME, USER2_PASSWORD);
                connection6.start();
                Session session6 = connection6.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                fail("Managed to open 6th connection");
                if (session6 != null)
                {
                    session6.close();
                }
                if (connection6 != null)
                {
                    connection6.close();
                }
            }
            catch (JMSException e)
            {
                // pass
            }

            session.close();
            connection.close();
            session2.close();
            connection2.close();
            session3.close();
            connection3.close();
            session4.close();
            connection4.close();
            session5.close();
            connection5.close();
        }
        catch (JMSException e)
        {
            fail("Failed to open 5 connections!");
        }
    }
}