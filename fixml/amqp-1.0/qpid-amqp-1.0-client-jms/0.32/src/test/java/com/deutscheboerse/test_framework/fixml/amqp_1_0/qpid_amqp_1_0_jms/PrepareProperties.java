package com.deutscheboerse.test_framework.fixml.amqp_1_0.qpid_amqp_1_0_jms;

import java.io.IOException;
import java.util.Properties;

import com.deutscheboerse.test_framework.configuration.PreparePropertiesCommon;

public class PrepareProperties extends PreparePropertiesCommon
{
    public static Properties getPropertiesBackend()
    {
        Properties amqpProperties = new Properties();
        amqpProperties.setProperty("java.naming.factory.initial", "org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
        String connectionString = String.format("amqp://amqpsrv:amqpsrv@%s:%s?max-prefetch=1000&sync-publish=true",
                userProperties.getProperty("host"), Integer.parseInt(userProperties.getProperty("port")) + 10000);
        amqpProperties.setProperty("connectionfactory.connection", connectionString);
        amqpProperties.setProperty("destination.broadcastExchange", userProperties.getProperty("broadcastExchange"));
        amqpProperties.setProperty("destination.broadcastBinding", userProperties.getProperty("broadcastBinding"));
        amqpProperties.setProperty("destination.requestQueue", userProperties.getProperty("requestQueue"));
        amqpProperties.setProperty("destination.responseExchange", userProperties.getProperty("responseExchange"));
        amqpProperties.setProperty("destination.responseQueue", userProperties.getProperty("responseQueue"));
        return amqpProperties;
    }
    
    public static Properties getProperties() throws IOException
    {
        Properties amqpProperties = new Properties();
        amqpProperties.setProperty("java.naming.factory.initial", "org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
        String connectionString = String.format("amqps://:@%s:%s?clientid=App1&key-store=%s&key-store-password=%s" +
                "&trust-store=%s&trust-store-password=%s&ssl-cert-alias=%s&max-prefetch=1000&sync-publish=true",
                userProperties.getProperty("host"), userProperties.getProperty("port"), userProperties.getProperty("keystorePath"),
                userProperties.getProperty("keystorePass"), userProperties.getProperty("truststorePath"), userProperties.getProperty("truststorePass"),
                userProperties.getProperty("keystoreAlias"));
        amqpProperties.setProperty("connectionfactory.connection", connectionString);
        amqpProperties.setProperty("destination.broadcastQueue", userProperties.getProperty("broadcastQueue"));
        amqpProperties.setProperty("destination.requestExchange", userProperties.getProperty("requestExchange"));
        amqpProperties.setProperty("destination.responseQueue", userProperties.getProperty("responseQueue"));
        amqpProperties.setProperty("destination.replyAddress", getReplyToAddress());
        return amqpProperties;
    }
}
