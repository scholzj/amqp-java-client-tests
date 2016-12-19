package com.deutscheboerse.amqp.qpid_amqp_0_10_jms_6_1_1.utils;

import com.deutscheboerse.amqp.configuration.Settings;
import com.deutscheboerse.amqp.utils.AbstractConnectionBuilder;
import com.deutscheboerse.amqp.utils.AbstractUtils;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

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
    public Connection getConnection(String connURL) throws JMSException, NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        props.setProperty("connectionfactory.connection", connURL);

        InitialContext ctx = new InitialContext(props);
        ConnectionFactory fact = (ConnectionFactory) ctx.lookup("connection");

        return fact.createConnection();
    }

    @Override
    public Destination getQueue(String queueName) throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        props.setProperty("destination.queue", queueName + "; { node: { type: queue }, create: never, assert: never }");

        InitialContext ctx = new InitialContext(props);

        return (Destination)ctx.lookup("queue");
    }

    @Override
    public Destination getTopic(String topicName, String routingKey) throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        props.setProperty("destination.queue", topicName + "/" + routingKey + "; { node: { type: topic }, create: never, assert: never }");

        InitialContext ctx = new InitialContext(props);

        return (Destination)ctx.lookup("queue");
    }

    @Override
    public Destination getTopic(String topicName) throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        props.setProperty("destination.topic", topicName + "; { node: { type: topic }, create: never, assert: never }");

        InitialContext ctx = new InitialContext(props);

        return (Destination)ctx.lookup("topic");
    }

    @Override
    public Destination getDestinationFromAddress(String address) throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        props.setProperty("destination.address", address);

        InitialContext ctx = new InitialContext(props);

        return (Destination)ctx.lookup("address");
    }
}
