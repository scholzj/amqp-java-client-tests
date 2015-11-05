package jms; /**
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
        Connection connection = Utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test
    public void testAuthenticationPlain() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).option("amqp.saslMechanisms=PLAIN").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test
    public void testAuthenticationCramMD5() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).option("amqp.saslMechanisms=CRAM-MD5").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testAuthenticationAnonymous() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnectionBuilder().option("amqp.saslMechanisms=ANONYMOUS").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testWrongPassword() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnectionBuilder().username(USER1_USERNAME).password("wrongpassword").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testWrongUsername() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnectionBuilder().username("nonexistentuser").password("somepassword").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testACLDeniedConsumer() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(FORBIDDEN_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testACLDeniedProducerForbiddenTopic() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getTopic(FORBIDDEN_TOPIC));
        Message msg = session.createMessage();
        msg.setJMSType(RTG_ROUTING_KEY);
        sender.send(msg);

        session.close();
        connection.close();
    }

    // Works only in 0.6.0 and higher
    @Test(expected = JMSSecurityException.class)
    public void testACLDeniedProducerForbiddenRoutingKey() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).syncPublish(true).build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageProducer sender = session.createProducer(Utils.getTopic(RTG_TOPIC));
        Message msg = session.createMessage();
        msg.setJMSType(FORBIDDEN_ROUTING_KEY);
        sender.send(msg);

        session.close();
        connection.close();
    }

    @Test
    public void testMaximumAllowedConnections() throws JMSException, NamingException, InterruptedException {
        try {
            Connection connection = Utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build();
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Connection connection2 = Utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build();
            connection2.start();
            Session session2 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Connection connection3 = Utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build();
            connection3.start();
            Session session3 = connection3.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Connection connection4 = Utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build();
            connection4.start();
            Session session4 = connection4.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Connection connection5 = Utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build();
            connection5.start();
            Session session5 = connection5.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            try
            {
                Connection connection6 = Utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build();
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
