package com.deutscheboerse.amqp.qpid_jms_0_44_0.utils;

import com.deutscheboerse.amqp.configuration.Settings;
import com.deutscheboerse.amqp.utils.AbstractConnectionBuilder;
import com.deutscheboerse.amqp.utils.AbstractUtils;
import com.deutscheboerse.amqp.utils.AutoCloseableConnection;

import java.util.Properties;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Utils implements AbstractUtils {
    public static final String HOSTNAME = Settings.get("broker.hostname");
    public static final String ADMIN_USERNAME = Settings.get("admin.username");
    public static final String ADMIN_PASSWORD = Settings.get("admin.password");
    public static final String TRUSTSTORE = Settings.getPath("broker.truststore");
    public static final String TRUSTSTORE_PASSWORD = Settings.get("broker.truststore_password");
    
    @Override
    public AbstractConnectionBuilder getConnectionBuilder() {
        return new ConnectionBuilder().hostname(HOSTNAME);
    }
    
    @Override
    public AbstractConnectionBuilder getAdminConnectionBuilder() throws JMSException, NamingException {
        return getConnectionBuilder().username(ADMIN_USERNAME).password(ADMIN_PASSWORD);
    }
    
    @Override
    public AbstractConnectionBuilder getSSLConnectionBuilder() {
        return getConnectionBuilder().ssl().truststore(TRUSTSTORE).truststorePassword(TRUSTSTORE_PASSWORD);
    }
    
    @Override
    public AutoCloseableConnection getConnection(String connURL) throws JMSException, NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        props.setProperty("connectionfactory.connection", connURL);
        
        InitialContext ctx = new InitialContext(props);
        ConnectionFactory fact = (ConnectionFactory) ctx.lookup("connection");
        
        return new AutoCloseableConnection(fact.createConnection());
    }
    
    @Override
    public Queue getQueue(String queueName) throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        props.setProperty("queue.queue", queueName);
        
        InitialContext ctx = new InitialContext(props);
        
        return (Queue)ctx.lookup("queue");
    }
    
    @Override
    public Topic getTopic(String queueName) throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        props.setProperty("topic.topic", queueName);
        
        InitialContext ctx = new InitialContext(props);
        
        return (Topic)ctx.lookup("topic");
    }
}
