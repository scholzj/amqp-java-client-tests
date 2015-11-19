package com.deutscheboerse.amqp.utils;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.NamingException;

public interface AbstractUtils {
    AbstractConnectionBuilder getConnectionBuilder();

    AbstractConnectionBuilder getAdminConnectionBuilder() throws JMSException, NamingException;

    AbstractConnectionBuilder getSSLConnectionBuilder();

    Connection getConnection(String connURL) throws JMSException, NamingException;
    
    Destination getQueue(String queueName) throws NamingException;

    Destination getTopic(String topicName, String routingKey) throws NamingException;
    
    Destination getTopic(String topicName) throws NamingException;
}
