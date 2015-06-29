package com.deutscheboerse.examples.fixml.amqp_1_0.qpid_jms;

import java.io.IOException;
import java.util.Properties;

import com.deutscheboerse.examples.test_framework.configuration.PreparePropertiesCommon;

public class PrepareProperties extends PreparePropertiesCommon
{
    public static Properties getPropertiesBackend()
    {
        Properties amqpProperties = new Properties();
        amqpProperties.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        String connectionString = String.format("amqp://%s:%s?jms.username=amqpsrv&jms.password=amqpsrv&amqp.idleTimeout=0",
                userProperties.getProperty("host"), Integer.parseInt(userProperties.getProperty("port")) + 10000);
        amqpProperties.setProperty("connectionfactory.connection", connectionString);
        amqpProperties.setProperty("topic.broadcastExchange", userProperties.getProperty("broadcastExchange"));
        amqpProperties.setProperty("queue.broadcastBinding", userProperties.getProperty("broadcastBinding"));
        amqpProperties.setProperty("queue.requestQueue", userProperties.getProperty("requestQueue"));
        amqpProperties.setProperty("topic.responseExchange", userProperties.getProperty("responseExchange"));
        amqpProperties.setProperty("queue.responseQueue", userProperties.getProperty("responseQueue"));
        return amqpProperties;
    }
    
    public static Properties getProperties() throws IOException
    {
        Properties amqpProperties = new Properties();
        amqpProperties.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        String connectionString = String.format("amqps://%s:%s?transport.keyStoreLocation=%s&transport.keyStorePassword=%s" +
                "&transport.trustStoreLocation=%s&transport.trustStorePassword=%s&transport.keyAlias=%s&amqp.idleTimeout=0",
                userProperties.getProperty("host"), userProperties.getProperty("port"), userProperties.getProperty("keystorePath"),
                userProperties.getProperty("keystorePass"), userProperties.getProperty("truststorePath"), userProperties.getProperty("truststorePass"),
                userProperties.getProperty("keystoreAlias"));
        amqpProperties.setProperty("connectionfactory.connection", connectionString);
        amqpProperties.setProperty("queue.broadcastQueue", userProperties.getProperty("broadcastQueue"));
        amqpProperties.setProperty("topic.requestExchange", userProperties.getProperty("requestExchange"));
        amqpProperties.setProperty("queue.responseQueue", userProperties.getProperty("responseQueue"));
        amqpProperties.setProperty("topic.replyAddress", getReplyToAddress());
        return amqpProperties;
    }
}
