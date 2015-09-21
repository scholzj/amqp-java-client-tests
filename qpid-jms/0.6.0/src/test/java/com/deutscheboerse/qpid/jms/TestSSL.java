package com.deutscheboerse.qpid.jms;

import com.deutscheboerse.configuration.Settings;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

import javax.jms.*;
import javax.naming.NamingException;

import static org.junit.Assert.fail;

/**
 * Created by schojak on 02.09.2015.
 */
public class TestSSL {
    protected static String USER1_KEYSTORE = Settings.get("user1.keystore");
    protected static String USER1_KEYSTORE_PASSWORD = Settings.get("user1.keystore_password");
    protected static String USER1_KEYSTORE_ALIAS = Settings.get("user1.key_alias");
    protected static String USER1_INVALID_KEYSTORE = Settings.get("user1.invalid_keystore");
    protected static String USER1_INVALID_KEYSTORE_PASSWORD = Settings.get("user1.invalid_keystore_password");
    protected static String USER1_INVALID_KEYSTORE_ALIAS = Settings.get("user1.invalid_key_alias");
    protected static String USER1_SIGNED_BY_KEYSTORE = Settings.get("user1.signedby_keystore");
    protected static String USER1_SIGNED_BY_KEYSTORE_PASSWORD = Settings.get("user1.signedby_keystore_password");
    protected static String USER1_SIGNED_BY_KEYSTORE_ALIAS = Settings.get("user1.signedby_key_alias");
    protected static String USER2_KEYSTORE = Settings.get("user2.keystore");
    protected static String USER2_KEYSTORE_PASSWORD = Settings.get("user2.keystore_password");
    protected static String USER2_KEYSTORE_ALIAS = Settings.get("user2.key_alias");

    public static String ADMIN_USERNAME = Settings.get("admin.username");
    public static String ADMIN_PASSWORD = Settings.get("admin.password");

    protected static String IP_ADDRESS = Settings.get("broker.ip_address");
    protected static String SSL_PORT = Settings.get("broker.ssl_port");

    protected static String TRUSTSTORE = Settings.get("broker.truststore");
    protected static String TRUSTSTORE_PASSWORD = Settings.get("broker.truststore_password");
    protected static String INVALID_TRUSTSTORE = Settings.get("broker.invalid_truststore");
    protected static String INVALID_TRUSTSTORE_PASSWORD = Settings.get("broker.invalid_truststore_password");

    protected static String RTG_QUEUE = Settings.get("routing.rtg_queue");

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    @Test
    public void testSuccessfullClientAuthentication() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnection(USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS);
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSException.class)
    public void testUnsuccessfullClientAuthentication() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnection(USER1_INVALID_KEYSTORE, USER1_INVALID_KEYSTORE_PASSWORD, USER1_INVALID_KEYSTORE_ALIAS);
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test
    public void testHostnameVerification() throws JMSException, NamingException, InterruptedException {
        // Test that wrong hostname fails
        try {
            Connection connection = Utils.getSSLConnection(IP_ADDRESS, SSL_PORT, USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS, TRUSTSTORE, TRUSTSTORE_PASSWORD, "");
            connection.start();
            Session session = null;
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            received.acknowledge();

            session.close();
            connection.close();

            fail("Managed to connect with wrong hostname");
        }
        catch (JMSException e)
        {
            // pass
        }

        // Test that hostname verification can be disabled
        try {
            Connection connection = Utils.getSSLConnection(IP_ADDRESS, SSL_PORT, USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS, TRUSTSTORE, TRUSTSTORE_PASSWORD, "transport.verifyHost=false");
            connection.start();
            Session session = null;
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);

            session.close();
            connection.close();
        }
        catch (JMSException e)
        {
            fail("Didn't managed to connect with disabled hostname verification!");
        }
    }

    @Test
    public void testWrongServerCertificate() throws JMSException, NamingException, InterruptedException {
        // Test that with invalid truststore the client doesn't connect
        try {
            Connection connection = Utils.getSSLConnection(USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS, INVALID_TRUSTSTORE, INVALID_TRUSTSTORE_PASSWORD);
            connection.start();
            Session session = null;
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            received.acknowledge();

            session.close();
            connection.close();

            fail("Managed to connect with wrong truststore");
        }
        catch (JMSException e)
        {
            // pass
        }

        // Test the "trust all" option
        try {
            Connection connection = Utils.getSSLConnection(USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS, INVALID_TRUSTSTORE, INVALID_TRUSTSTORE_PASSWORD, "transport.trustAll=True");
            connection.start();
            Session session = null;
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
            Message received = receiver.receive(1000);
            received.acknowledge();

            session.close();
            connection.close();
        }
        catch (JMSException e)
        {
            fail("Didn't managed to connect with disabled server certificate validation!");
        }
    }

    @Test(expected = JMSException.class)
    public void testSSLv3() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnection(USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS, TRUSTSTORE, TRUSTSTORE_PASSWORD, "transport.enabledProtocols=SSLv3&transport.disabledProtocols=null");
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSException.class)
    public void testSSLv2() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnection(USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS, TRUSTSTORE, TRUSTSTORE_PASSWORD, "transport.enabledProtocols=SSLv2Hello,SSLv3&transport.disabledProtocols=null");
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test
    public void testTLSv1() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnection(USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS, TRUSTSTORE, TRUSTSTORE_PASSWORD, "transport.enabledProtocols=TLSv1");
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test
    public void testCipherSuite3DES() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnection(USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS, TRUSTSTORE, TRUSTSTORE_PASSWORD, "transport.enabledCipherSuites=SSL_RSA_WITH_3DES_EDE_CBC_SHA");
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test
    public void testCipherSuiteAES128() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnection(USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS, TRUSTSTORE, TRUSTSTORE_PASSWORD, "transport.enabledCipherSuites=TLS_RSA_WITH_AES_128_CBC_SHA");
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);

        session.close();
        connection.close();
    }

    // JCA Unlimited Strength policy files are needed for AES256
    // http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
    @Test
    public void testCipherSuiteAES256() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnection(USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS, TRUSTSTORE, TRUSTSTORE_PASSWORD, "transport.enabledCipherSuites=TLS_RSA_WITH_AES_256_CBC_SHA");
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testPlainOverSSLWithClientAuth() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnection(USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS, TRUSTSTORE, TRUSTSTORE_PASSWORD, "jms.username=" + ADMIN_USERNAME + "&jms.password=" + ADMIN_PASSWORD + "&amqp.saslMechanisms=PLAIN");
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSException.class)
    public void testPlainOverSSL() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getConnection("amqps://cbgd03:11234?jms.username=admin&jms.password=admin&transport.trustStoreLocation=c:/opt/!_AMQP/IdeaProjects/amqp-tests/qpid-jms-client-0.5/src/main/resources/cbgd03-1234.truststore&transport.trustStorePassword=123456&amqp.saslMechanisms=PLAIN");
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testAnonymousOverSSLWithClientAuth() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnection(USER1_KEYSTORE, USER1_KEYSTORE_PASSWORD, USER1_KEYSTORE_ALIAS, TRUSTSTORE, TRUSTSTORE_PASSWORD, "amqp.saslMechanisms=ANONYMOUS");
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        Message received = receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSException.class)
    public void testSignedByCannotLogin() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnection(USER1_SIGNED_BY_KEYSTORE, USER1_SIGNED_BY_KEYSTORE_PASSWORD, USER1_SIGNED_BY_KEYSTORE_ALIAS);
        connection.start();
        Session session = null;
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        session.close();
        connection.close();
    }

    @Test
    public void testMaximumAllowedConnectionsOverSSL() throws JMSException, NamingException, InterruptedException {
        try {
            Connection connection = Utils.getSSLConnection(USER2_KEYSTORE, USER2_KEYSTORE_PASSWORD, USER2_KEYSTORE_ALIAS);
            connection.start();
            Connection connection2 = Utils.getSSLConnection(USER2_KEYSTORE, USER2_KEYSTORE_PASSWORD, USER2_KEYSTORE_ALIAS);
            connection2.start();
            Connection connection3 = Utils.getSSLConnection(USER2_KEYSTORE, USER2_KEYSTORE_PASSWORD, USER2_KEYSTORE_ALIAS);
            connection3.start();
            Connection connection4 = Utils.getSSLConnection(USER2_KEYSTORE, USER2_KEYSTORE_PASSWORD, USER2_KEYSTORE_ALIAS);
            connection4.start();
            Connection connection5 = Utils.getSSLConnection(USER2_KEYSTORE, USER2_KEYSTORE_PASSWORD, USER2_KEYSTORE_ALIAS);
            connection5.start();

            try
            {
                Connection connection6 = Utils.getSSLConnection(USER2_KEYSTORE, USER2_KEYSTORE_PASSWORD, USER2_KEYSTORE_ALIAS);
                connection6.start();
                fail("Managed to open 6th connection");
            }
            catch (JMSException e)
            {
                // pass
            }

            connection.close();
            connection2.close();
            connection3.close();
            connection4.close();
            connection5.close();
        }
        catch (JMSException e)
        {
            fail("Failed to open 5 connections!");
        }
    }
}
