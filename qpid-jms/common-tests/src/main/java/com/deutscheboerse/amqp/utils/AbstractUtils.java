package com.deutscheboerse.amqp.utils;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.NamingException;

public interface AbstractUtils {
    AbstractConnectionBuilder getConnectionBuilder();

    AbstractConnectionBuilder getAdminConnectionBuilder() throws JMSException, NamingException;

    AbstractConnectionBuilder getSSLConnectionBuilder();

    AutoCloseableConnection getConnection(String connURL) throws JMSException, NamingException;
    
    Queue getQueue(String queueName) throws NamingException;

    Topic getTopic(String topicName) throws NamingException;
}
