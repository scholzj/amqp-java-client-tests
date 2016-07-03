package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.Assert;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

public class Security extends BaseTest {

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

    public void testAuthenticationDefault() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
        }
    }

    public void testAuthenticationPlain() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).brokerOption("amqp.saslMechanisms=PLAIN").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
        }
    }

    public void testAuthenticationCramMD5() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).brokerOption("amqp.saslMechanisms=CRAM-MD5").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
        }
    }

    public void testAuthenticationAnonymous() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().brokerOption("amqp.saslMechanisms=ANONYMOUS").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
        }
    }

    public void testWrongPassword() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password("wrongpassword").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
        }
    }

    public void testWrongUsername() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username("nonexistentuser").password("mypassword").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
        }
    }

    public void testACLDeniedConsumer() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(FORBIDDEN_QUEUE));
            receiver.receive(10000);
        }
    }

    public void testACLDeniedProducerForbiddenTopic() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).syncPublish(true).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getTopic(FORBIDDEN_TOPIC));
            Message msg = session.createMessage();
            msg.setJMSType(RTG_ROUTING_KEY);
            sender.send(msg);
        }
    }

    // Works only in 0.6.0 and higher
    public void testACLDeniedProducerForbiddenRoutingKey() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER1_USERNAME).password(USER1_PASSWORD).syncPublish(Boolean.TRUE).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getTopic(RTG_TOPIC));
            Message msg = session.createMessage();
            msg.setJMSType(FORBIDDEN_ROUTING_KEY);
            sender.send(msg);
        }
    }

    public void testMaximumAllowedConnections() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build();
                AutoCloseableConnection connection2 = this.utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build();
                AutoCloseableConnection connection3 = this.utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build();
                AutoCloseableConnection connection4 = this.utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build();
                AutoCloseableConnection connection5 = this.utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            connection2.start();
            Session session2 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            connection3.start();
            Session session3 = connection3.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            connection4.start();
            Session session4 = connection4.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            connection5.start();
            Session session5 = connection5.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            try (AutoCloseableConnection connection6 = this.utils.getConnectionBuilder().username(USER2_USERNAME).password(USER2_PASSWORD).build()) {
                connection6.start();
                Session session6 = connection6.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                Assert.fail("Managed to open 6th connection");
            } catch (JMSException expected) {
                // "Expected" exception ... nothing to do :-o
            }
        } catch (JMSException e) {
            Assert.fail("Failed to open 5 connections!");
        }
    }

    public void testUserID() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().brokerOption("jms.populateJMSXUserID=true").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getTopic(RTG_TOPIC));
            Message msg = session.createMessage();
            msg.setJMSType(RTG_ROUTING_KEY);
            sender.send(msg);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(5000);
            received.acknowledge();
            Assert.assertNotNull(received, "Didn't receive expected message with jms.populateJMSXUserID=true");
            Assert.assertEquals(received.getStringProperty("JMSXUserID"), "admin", "Received unexpected user ID with jms.populateJMSXUserID=true");
        }

        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().brokerOption("jms.populateJMSXUserID=false").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getTopic(RTG_TOPIC));
            Message msg = session.createMessage();
            msg.setJMSType(RTG_ROUTING_KEY);
            sender.send(msg);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(5000);
            received.acknowledge();
            Assert.assertNotNull(received, "Didn't receive expected message with jms.populateJMSXUserID=false");
            Assert.assertNotEquals(received.getStringProperty("JMSXUserID"), "admin", "Received unexpected user ID with jms.populateJMSXUserID=false");
        }

        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageProducer sender = session.createProducer(this.utils.getTopic(RTG_TOPIC));
            Message msg = session.createMessage();
            msg.setJMSType(RTG_ROUTING_KEY);
            sender.send(msg);

            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(5000);
            received.acknowledge();
            Assert.assertNotNull(received, "Didn't receive expected message");
            Assert.assertNotEquals(received.getStringProperty("JMSXUserID"), "admin", "Received unexpected user ID");
        }
    }
}
