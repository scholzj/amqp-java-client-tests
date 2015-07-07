package com.deutscheboerse.examples.fixml.amqp_0_10.qpid_jms;

import java.io.IOException;
import java.util.Properties;

import com.deutscheboerse.examples.test_framework.configuration.PreparePropertiesCommon;

public class PrepareProperties extends PreparePropertiesCommon
{
    public static Properties getPropertiesBackend()
    {
        Properties amqpProperties = new Properties();
        amqpProperties.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        String connectionString = String.format("amqp://amqpsrv:amqpsrv@App1/?brokerlist='tcp://%s:%s'",
                userProperties.getProperty("host"), Integer.parseInt(userProperties.getProperty("port")) + 10000);
        amqpProperties.setProperty("connectionfactory.connection", connectionString);
        amqpProperties.setProperty("destination.broadcastExchange", "broadcast/" + userProperties.getProperty("broadcastBinding") + "; { node: { type: topic }, assert: never }");
        amqpProperties.setProperty("destination.requestQueue", userProperties.getProperty("requestQueue"));
        amqpProperties.setProperty("destination.responseExchange", userProperties.getProperty("responseExchange") + "/" + userProperties.getProperty("responseQueue") + ".response_queue; { node: { type: topic }, assert: never }");
        amqpProperties.setProperty("destination.responseQueue", userProperties.getProperty("responseQueue") + ".response_queue; { node: { type: queue }, mode: consume, assert: never }");
        amqpProperties.setProperty("destination.replyAddress", getReplyToAddressWithQuotes());
        return amqpProperties;
    }
    
    public static Properties getProperties() throws IOException
    {
        Properties amqpProperties = new Properties();
        amqpProperties.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
        String connectionString = String.format("amqp://:@App1/?brokerlist='tcp://%s:%s?ssl='true'&key_store='%s'&key_store_password='%s'" +
                "&trust_store='%s'&trust_store_password='%s'&ssl_cert_alias='%s'&sasl_mechs='EXTERNAL''",
                userProperties.getProperty("host"), userProperties.getProperty("port"), userProperties.getProperty("keystorePath"),
                userProperties.getProperty("keystorePass"), userProperties.getProperty("truststorePath"), userProperties.getProperty("truststorePass"),
                userProperties.getProperty("keystoreAlias"));
        amqpProperties.setProperty("connectionfactory.connection", connectionString);
        amqpProperties.setProperty("destination.broadcastQueue", userProperties.getProperty("broadcastQueue") + "; { node: { type: queue }, create: never, mode: consume, assert: never }");
        amqpProperties.setProperty("destination.requestExchange", userProperties.getProperty("requestExchange") + "; { node: { type: topic }, create: never }");
        amqpProperties.setProperty("destination.responseQueue", userProperties.getProperty("responseQueue") + ".response_queue; {create: receiver, assert: never, node: " +
                "{ type: queue, x-declare: { auto-delete: true, exclusive: false, arguments: {'qpid.policy_type': ring, 'qpid.max_count': 1000, 'qpid.max_size': 1000000}}, " +
                "x-bindings: [{exchange: 'response', queue: '" + userProperties.getProperty("responseQueue") + ".response_queue', key: '" + userProperties.getProperty("responseQueue") + ".response_queue'}]}}");
        amqpProperties.setProperty("destination.replyAddress", getReplyToAddressWithoutQuotes());
        return amqpProperties;
    }
    
    public static String getReplyToAddressWithoutQuotes()
    {
        return userProperties.getProperty("responseExchange") + "/" + userProperties.getProperty("responseQueue") + ".response_queue; { create: receiver, node: {type: topic } }";
    }
    
    public static String getReplyToAddressWithQuotes()
    {
        return "'" + userProperties.getProperty("responseExchange") + "'/'" + userProperties.getProperty("responseQueue") + ".response_queue'; {\n  'create': 'receiver',\n  'node': {\n    'type': 'topic'\n  }\n}";
    }
}
