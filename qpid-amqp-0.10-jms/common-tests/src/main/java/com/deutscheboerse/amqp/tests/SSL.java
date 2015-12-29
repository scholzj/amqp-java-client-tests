package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;

import javax.jms.*;
import javax.naming.NamingException;
import org.testng.Assert;
import com.deutscheboerse.amqp.utils.AbstractConnectionBuilder;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

public class SSL extends BaseTest {
    private static final String USER1_KEYSTORE = Settings.getPath("user1.keystore");
    private static final String USER1_KEYSTORE_PASSWORD = Settings.get("user1.keystore_password");
    private static final String USER1_KEYSTORE_ALIAS = Settings.get("user1.key_alias");
    private static final String USER1_INVALID_KEYSTORE = Settings.getPath("user1.invalid_keystore");
    private static final String USER1_INVALID_KEYSTORE_PASSWORD = Settings.get("user1.invalid_keystore_password");
    private static final String USER1_INVALID_KEYSTORE_ALIAS = Settings.get("user1.invalid_key_alias");
    private static final String USER1_SIGNED_BY_KEYSTORE = Settings.getPath("user1.signedby_keystore");
    private static final String USER1_SIGNED_BY_KEYSTORE_PASSWORD = Settings.get("user1.signedby_keystore_password");
    private static final String USER1_SIGNED_BY_KEYSTORE_ALIAS = Settings.get("user1.signedby_key_alias");
    private static final String USER2_KEYSTORE = Settings.getPath("user2.keystore");
    private static final String USER2_KEYSTORE_PASSWORD = Settings.get("user2.keystore_password");
    private static final String USER2_KEYSTORE_ALIAS = Settings.get("user2.key_alias");

    private static final String ADMIN_USERNAME = Settings.get("admin.username");
    private static final String ADMIN_PASSWORD = Settings.get("admin.password");

    private static final String IP_ADDRESS = Settings.get("broker.ip_address");
    private static final String SSL_PORT = Settings.get("broker.ssl_port");
    private static final String TCP_PORT = Settings.get("broker.tcp_port");

    private static final String TRUSTSTORE = Settings.getPath("broker.truststore");
    private static final String TRUSTSTORE_PASSWORD = Settings.get("broker.truststore_password");
    private static final String INVALID_TRUSTSTORE = Settings.getPath("broker.invalid_truststore");
    private static final String INVALID_TRUSTSTORE_PASSWORD = Settings.get("broker.invalid_truststore_password");

    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");

    public void testSuccessfullClientAuthentication() throws JMSException, NamingException {
        try (AutoCloseableConnection connection = this.utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
        }
    }

    public void testUnsuccessfullClientAuthentication() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getSSLConnectionBuilder().keystore(USER1_INVALID_KEYSTORE).keystorePassword(USER1_INVALID_KEYSTORE_PASSWORD).keystoreAlias(USER1_INVALID_KEYSTORE_ALIAS).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
        }
    }

    public void testHostnameVerification() throws JMSException, NamingException, InterruptedException {
        // Test that wrong hostname fails
        try (AutoCloseableConnection connection = this.utils.getSSLConnectionBuilder().hostname(IP_ADDRESS).keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            received.acknowledge();
            Assert.fail("Managed to connect with wrong hostname");
        } catch (JMSException expected) {
            // "Expected" exception ... nothing to do :-o
        }

        // Test that hostname verification can be disabled
        try (AutoCloseableConnection connection = this.utils.getSSLConnectionBuilder().hostname(IP_ADDRESS).keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).brokerOption("ssl_verify_hostname='false'").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
        }
        catch (JMSException expected) {
            Assert.fail("Didn't manage to connect with disabled hostname verification!", expected);
        }
    }

    public void testWrongServerCertificate() throws JMSException, NamingException, InterruptedException {
        // Test that with invalid truststore the client doesn't connect
        try (AutoCloseableConnection connection = this.utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).truststore(INVALID_TRUSTSTORE).truststorePassword(INVALID_TRUSTSTORE_PASSWORD).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            received.acknowledge();
            Assert.fail("Managed to connect with wrong truststore");
        }
        catch (JMSException expected) {
            // "Expected" exception ... nothing to do :-o
        }
    }

    public void testPlainOverSSLWithClientAuth() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).username(ADMIN_USERNAME).password(ADMIN_PASSWORD).brokerOption("sasl_mechs='PLAIN'").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
        }
    }

    public void testPlainOverSSL() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getSSLConnectionBuilder().username(ADMIN_USERNAME).password(ADMIN_PASSWORD).brokerOption("sasl_mechs='PLAIN'").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
        }
    }

    public void testAnonymousOverSSLWithClientAuth() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).brokerOption("sasl_mechs='ANONYMOUS'").build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
        }
    }

    public void testSignedByCannotLogin() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getSSLConnectionBuilder().keystore(USER1_SIGNED_BY_KEYSTORE).keystorePassword(USER1_SIGNED_BY_KEYSTORE_PASSWORD).keystoreAlias(USER1_SIGNED_BY_KEYSTORE_ALIAS).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        }
    }

    public void testSSLConnectionToNonSSLPort() throws JMSException, NamingException, InterruptedException {
        // Default timeout of 60 seconds would slow down the test too much
        System.setProperty("qpid.ssl_timeout", "5000");
        
        try (AutoCloseableConnection connection = this.utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).port(TCP_PORT).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        } finally {
            // Remove the property again
            System.clearProperty("qpid.ssl_timeout");
        }
    }

    public void testNonSSLConnectionToSSLPort() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getAdminConnectionBuilder().port(SSL_PORT).build()) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        }
    }
    
    public void testMaximumAllowedConnectionsOverSSL() throws JMSException, NamingException, InterruptedException {
        AbstractConnectionBuilder connectionBuilder = this.utils.getSSLConnectionBuilder().keystore(USER2_KEYSTORE).keystorePassword(USER2_KEYSTORE_PASSWORD).keystoreAlias(USER2_KEYSTORE_ALIAS);
        try (AutoCloseableConnection connection1 = connectionBuilder.build();
             AutoCloseableConnection connection2 = connectionBuilder.build();
             AutoCloseableConnection connection3 = connectionBuilder.build();
             AutoCloseableConnection connection4 = connectionBuilder.build();
             AutoCloseableConnection connection5 = connectionBuilder.build()) {
            connection1.start(); connection1.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            connection2.start(); connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            connection3.start(); connection3.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            connection4.start(); connection4.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            connection5.start(); connection5.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            try (AutoCloseableConnection connection6 = connectionBuilder.build()) {
                connection6.start(); connection6.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                Assert.fail("Managed to open 6th connection");
            } catch (JMSException expected) {
                // "Expected" exception ... nothing to do :-o
            }
        } catch (JMSException e) {
            Assert.fail("Failed to open 5 connections!");
        }
    }

}
