package com.deutscheboerse.amqp.tests;

import com.deutscheboerse.amqp.configuration.Settings;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.NamingException;

public class Failover extends BaseTest {
    private static final String USER1_KEYSTORE = Settings.getPath("user1.keystore");
    private static final String USER1_KEYSTORE_PASSWORD = Settings.get("user1.keystore_password");
    private static final String USER1_KEYSTORE_ALIAS = Settings.get("user1.key_alias");
    
    private static final String ADMIN_USERNAME = Settings.get("admin.username");
    private static final String ADMIN_PASSWORD = Settings.get("admin.password");
    
    private static final String HOSTNAME = Settings.get("broker.hostname");
    private static final String TCP_PORT = Settings.get("broker.tcp_port");
    private static final String SSL_PORT = Settings.get("broker.ssl_port");
    
    private static final String TRUSTSTORE = Settings.getPath("broker.truststore");
    private static final String TRUSTSTORE_PASSWORD = Settings.get("broker.truststore_password");
    
    private static final String RTG_QUEUE = Settings.get("routing.rtg_queue");
    
    // This doesn't test the actual failover - just connecting using the failover URI
    public void testPlainFailover() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnection("failover:(amqp://" + HOSTNAME + ":" + TCP_PORT + "?amqp.idleTimeout=0,amqp://" + HOSTNAME + ":" + TCP_PORT + "?amqp.idleTimeout=0)?failover.maxReconnectAttempts=1&failover.reconnectDelay=1000&amqp.idleTimeout=0&jms.username=" + ADMIN_USERNAME + "&jms.password=" + ADMIN_PASSWORD + "")) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
            receiver.close();
            session.close();
        }
    }
    
    // This doesn't test the actual failover - just connecting using the failover URI
    public void testPlainFailoverNested() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnection("failover:(amqp://" + HOSTNAME + ":" + TCP_PORT + ",amqp://" + HOSTNAME + ":" + TCP_PORT + ")?failover.maxReconnectAttempts=1&failover.reconnectDelay=1000&amqp.idleTimeout=0&jms.username=" + ADMIN_USERNAME + "&jms.password=" + ADMIN_PASSWORD + "&failover.nested.amqp.idleTimeout=0")) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
            receiver.close();
            session.close();
        }
    }
    
    // This doesn't test the actual failover - just connecting using the failover URI
    public void testSSLFailover() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnection("failover:(amqps://" + HOSTNAME + ":" + SSL_PORT + "?transport.keyStoreLocation=" + USER1_KEYSTORE + "&transport.trustStoreLocation=" + TRUSTSTORE + "&transport.keyStorePassword=" + USER1_KEYSTORE_PASSWORD + "&transport.trustStorePassword=" + TRUSTSTORE_PASSWORD + "&transport.keyAlias=" + USER1_KEYSTORE_ALIAS + "&amqp.idleTimeout=0)?failover.maxReconnectAttempts=1&failover.reconnectDelay=1000")) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
            receiver.close();
            session.close();
        }
    }
    
    // This doesn't test the actual failover - just connecting using the failover URI
    public void testSSLFailoverNested() throws JMSException, NamingException, InterruptedException {
        try (AutoCloseableConnection connection = this.utils.getConnection("failover:(amqps://" + HOSTNAME + ":" + SSL_PORT + ",amqps://" + HOSTNAME + ":" + SSL_PORT + ")?failover.maxReconnectAttempts=1&failover.reconnectDelay=1000&failover.nested.transport.keyStoreLocation=" + USER1_KEYSTORE + "&failover.nested.transport.trustStoreLocation=" + TRUSTSTORE + "&failover.nested.transport.keyStorePassword=" + USER1_KEYSTORE_PASSWORD + "&failover.nested.transport.trustStorePassword=" + TRUSTSTORE_PASSWORD + "&failover.nested.transport.keyAlias=" + USER1_KEYSTORE_ALIAS + "&failover.nested.amqp.idleTimeout=0")) {
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            MessageConsumer receiver = session.createConsumer(this.utils.getQueue(RTG_QUEUE));
            receiver.receive(1000);
            receiver.close();
            session.close();
        }
    }
}
