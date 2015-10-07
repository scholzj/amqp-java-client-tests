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

    @BeforeClass
    public static void prepare() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }

    @Test
    public void testSuccessfullClientAuthentication() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSException.class)
    public void testUnsuccessfullClientAuthentication() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_INVALID_KEYSTORE).keystorePassword(USER1_INVALID_KEYSTORE_PASSWORD).keystoreAlias(USER1_INVALID_KEYSTORE_ALIAS).build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test
    public void testHostnameVerification() throws JMSException, NamingException, InterruptedException {
        // Test that wrong hostname fails
        try {
            Connection connection = Utils.getSSLConnectionBuilder().hostname(IP_ADDRESS).port(SSL_PORT).keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).build();
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

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
            Connection connection = Utils.getSSLConnectionBuilder().hostname(IP_ADDRESS).port(SSL_PORT).keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).option("transport.verifyHost=false").build();
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);

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
            Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).truststore(INVALID_TRUSTSTORE).truststorePassword(INVALID_TRUSTSTORE_PASSWORD).build();
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

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
            Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).truststore(INVALID_TRUSTSTORE).truststorePassword(INVALID_TRUSTSTORE_PASSWORD).option("transport.trustAll=True").build();
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

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
        Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).option("transport.enabledProtocols=SSLv3").option("transport.disabledProtocols=null").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSException.class)
    public void testSSLv2() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).option("transport.enabledProtocols=SSLv2Hello,SSLv3").option("transport.disabledProtocols=null").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test
    public void testTLSv1() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).option("transport.enabledProtocols=TLSv1").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test
    public void testCipherSuite3DES() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).option("transport.enabledCipherSuites=SSL_RSA_WITH_3DES_EDE_CBC_SHA").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test
    public void testCipherSuiteAES128() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).option("transport.enabledCipherSuites=TLS_RSA_WITH_AES_128_CBC_SHA").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    // JCA Unlimited Strength policy files are needed for AES256
    // http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
    @Test
    public void testCipherSuiteAES256() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).option("transport.enabledCipherSuites=TLS_RSA_WITH_AES_256_CBC_SHA").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testPlainOverSSLWithClientAuth() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).username(ADMIN_USERNAME).password(ADMIN_PASSWORD).option("amqp.saslMechanisms=PLAIN").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSException.class)
    public void testPlainOverSSL() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnectionBuilder().username(ADMIN_USERNAME).password(ADMIN_PASSWORD).option("amqp.saslMechanisms=PLAIN").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSSecurityException.class)
    public void testAnonymousOverSSLWithClientAuth() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).option("amqp.saslMechanisms=ANONYMOUS").build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        MessageConsumer receiver = session.createConsumer(Utils.getQueue(RTG_QUEUE));
        receiver.receive(1000);

        session.close();
        connection.close();
    }

    @Test(expected = JMSException.class)
    public void testSignedByCannotLogin() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_SIGNED_BY_KEYSTORE).keystorePassword(USER1_SIGNED_BY_KEYSTORE_PASSWORD).keystoreAlias(USER1_SIGNED_BY_KEYSTORE_ALIAS).build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        session.close();
        connection.close();
    }

    @Test(expected = JMSException.class)
    public void testSSLConnectionToNonSSLPort() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getSSLConnectionBuilder().keystore(USER1_KEYSTORE).keystorePassword(USER1_KEYSTORE_PASSWORD).keystoreAlias(USER1_KEYSTORE_ALIAS).port(TCP_PORT).build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        session.close();
        connection.close();
    }

    @Test(expected = JMSException.class)
    public void testNonSSLConnectionToSSLPort() throws JMSException, NamingException, InterruptedException {
        Connection connection = Utils.getAdminConnectionBuilder().port(SSL_PORT).build();
        connection.start();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        session.close();
        connection.close();
    }

    @Test
    public void testMaximumAllowedConnectionsOverSSL() throws JMSException, NamingException, InterruptedException {
        try {
            Connection connection = Utils.getSSLConnectionBuilder().keystore(USER2_KEYSTORE).keystorePassword(USER2_KEYSTORE_PASSWORD).keystoreAlias(USER2_KEYSTORE_ALIAS).build();
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Connection connection2 = Utils.getSSLConnectionBuilder().keystore(USER2_KEYSTORE).keystorePassword(USER2_KEYSTORE_PASSWORD).keystoreAlias(USER2_KEYSTORE_ALIAS).build();
            connection2.start();
            Session session2 = connection2.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Connection connection3 = Utils.getSSLConnectionBuilder().keystore(USER2_KEYSTORE).keystorePassword(USER2_KEYSTORE_PASSWORD).keystoreAlias(USER2_KEYSTORE_ALIAS).build();
            connection3.start();
            Session session3 = connection3.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Connection connection4 = Utils.getSSLConnectionBuilder().keystore(USER2_KEYSTORE).keystorePassword(USER2_KEYSTORE_PASSWORD).keystoreAlias(USER2_KEYSTORE_ALIAS).build();
            connection4.start();
            Session session4 = connection4.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Connection connection5 = Utils.getSSLConnectionBuilder().keystore(USER2_KEYSTORE).keystorePassword(USER2_KEYSTORE_PASSWORD).keystoreAlias(USER2_KEYSTORE_ALIAS).build();
            connection5.start();
            Session session5 = connection5.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            try
            {
                Connection connection6 = Utils.getSSLConnectionBuilder().keystore(USER2_KEYSTORE).keystorePassword(USER2_KEYSTORE_PASSWORD).keystoreAlias(USER2_KEYSTORE_ALIAS).build();
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
